package edu.stanford.irt.eresources.pubmed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.DataFetcher;
import edu.stanford.irt.eresources.EresourceDatabaseException;

public class PubmedFtpDataFetcher implements DataFetcher {

    private static final Logger log = LoggerFactory.getLogger(PubmedFtpDataFetcher.class);

    private String basePath;

    private FTPClient ftpClient;

    private FTPFileFilter ftpFileFilter;

    private String ftpHost;

    private String ftpPass;

    private String ftpPath;

    private String ftpUser;

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
            throw new EresourceDatabaseException(e);
        } finally {
            try {
                this.ftpClient.disconnect();
            } catch (IOException e) {
                log.error("couldn't disconnect", e);
            }
        }
    }

    private void fetchFile(final FTPClient client, final FTPFile file) {
        try (FileOutputStream fos = new FileOutputStream(new File(this.basePath, file.getName()))) {
            log.info("fetching: {}", file);
            if (!client.retrieveFile(file.getName(), fos)) {
                log.error("couldn't fetch file: {}", file);
            }
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
