package edu.stanford.irt.eresources.pubmed;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.DataFetcher;
import edu.stanford.irt.eresources.EresourceDatabaseException;

public class PubmedFtpDataFetcher implements DataFetcher {

    private static final Logger log = LoggerFactory.getLogger(PubmedFtpDataFetcher.class);

    private String basePath;

    private String ftpHost;

    private String ftpPass;

    private String ftpPath;

    private String ftpUser;

    public PubmedFtpDataFetcher(final String basePathname, final String ftpHostname, final String ftpPathname,
            final String ftpUsername, final String ftpPassword) {
        this.basePath = basePathname;
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
        PubmedFtpFileFilter filter = new PubmedFtpFileFilter(this.basePath);
        FTPClient client = new FTPClient();
        try {
            log.info("connecting to ftp host: {}", this.ftpHost);
            client.connect(this.ftpHost);
            client.login(this.ftpUser, this.ftpPass);
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.enterLocalPassiveMode();
            client.changeWorkingDirectory(this.ftpPath);
            for (FTPFile file : client.listFiles(".", filter)) {
                fetchFile(client, file);
            }
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        } finally {
            try {
                client.disconnect();
            } catch (IOException e) {
                log.error("couldn't disconnect", e);
            }
        }
    }

    private void fetchFile(final FTPClient client, final FTPFile file) {
        try (FileOutputStream fos = new FileOutputStream(this.basePath + "/" + file.getName())) {
            log.info("fetching: {}", file);
            if (!client.retrieveFile(file.getName(), fos)) {
                log.error("couldn't fetch file: {}", file);
            }
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }
}
