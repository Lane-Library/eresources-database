package edu.stanford.irt.eresources.pubmed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xbib.io.ftp.client.FTP;
import org.xbib.io.ftp.client.FTPClient;
import org.xbib.io.ftp.client.FTPFile;
import org.xbib.io.ftp.client.FTPFileFilter;

import edu.stanford.irt.eresources.DataFetcher;
import edu.stanford.irt.eresources.EresourceDatabaseException;

public class PubmedFtpDataFetcher implements DataFetcher {

    private static final Logger log = LoggerFactory.getLogger(PubmedFtpDataFetcher.class);

    private static final int MAX_ATTEMPTS = 10;

    private String basePath;

    private FTPClient ftpClient;

    private FTPFileFilter ftpFileFilter;

    private String ftpHost;

    private String ftpPass;

    private String ftpPath;

    private String ftpUser;

    private int tries;

    public PubmedFtpDataFetcher(final String basePathname, final FTPClient ftpClient, final FTPFileFilter ftpFileFilter,
            final String ftpHostname, final String ftpPathname, final String ftpUsername, final String ftpPassword) {
        this.basePath = basePathname;
        this.ftpClient = ftpClient;
        this.ftpFileFilter = ftpFileFilter;
        this.ftpHost = ftpHostname;
        this.ftpPath = ftpPathname;
        this.ftpUser = ftpUsername;
        this.ftpPass = ftpPassword;
        File dir = new File(this.basePath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IllegalArgumentException("missing and can't create " + this.basePath);
        }
    }

    @Override
    public void getUpdateFiles() {
        try {
            log.info("connecting to ftp host: {}", this.ftpHost);
            this.ftpClient.connect(this.ftpHost);
            this.ftpClient.login(this.ftpUser, this.ftpPass);
            this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            this.ftpClient.enterLocalPassiveMode();
            this.ftpClient.changeWorkingDirectory(this.ftpPath);
            for (FTPFile file : this.ftpClient.listFiles(".", this.ftpFileFilter)) {
                fetchFile(this.ftpClient, file);
            }
        } catch (IOException e) {
            log.info("FTP problem; passsive port: {}; local port: {}; remote port: {}", this.ftpClient.getPassivePort(),
                    this.ftpClient.getLocalPort(), this.ftpClient.getRemotePort(), e);
            if (this.tries < MAX_ATTEMPTS) {
                this.tries++;
                getUpdateFiles();
            } else {
                log.error("max FTP connection attempts reached ... giving up");
                throw new EresourceDatabaseException(e);
            }
        } finally {
            try {
                this.ftpClient.disconnect();
            } catch (IOException e) {
                log.error("couldn't disconnect", e);
            }
        }
    }

    private void fetchFile(final FTPClient client, final FTPFile file) {
        File localFile = new File(this.basePath, file.getName());
        try (FileOutputStream fos = new FileOutputStream(localFile)) {
            log.info("fetching: {}", file);
            if (!client.retrieveFile(file.getName(), fos)) {
                log.info("failed to fetch file: {}", file);
            }
        } catch (IOException e) {
            try {
                log.info("status of attempt to delete {}: {}", localFile.getAbsolutePath(),
                        Files.deleteIfExists(localFile.toPath()));
            } catch (IOException e1) {
                log.error("problem deleting {}", localFile.getAbsolutePath(), e1);
            }
            // reset file filter so we don't fetch successfully downloaded files again
            this.ftpFileFilter = new PubmedFtpFileFilter(this.basePath);
            if (this.tries < MAX_ATTEMPTS) {
                this.tries++;
                getUpdateFiles();
            } else {
                log.error("max attempts to fetch file {} reached ... giving up", file.getName());
                throw new EresourceDatabaseException(e);
            }
        }
    }
}
