package edu.stanford.irt.eresources;

import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PubmedFtpDataFetcher implements DataFetcher {

    private static final Logger LOG = LoggerFactory.getLogger(PubmedFtpDataFetcher.class);

    private String basePath;

    private String ftpHost;

    private String ftpPath;

    @Override
    public void getUpdateFiles() {
        PubmedFtpFileFilter filter = new PubmedFtpFileFilter(this.basePath);
        FTPClient client = new FTPClient();
        FileOutputStream fos = null;
        try {
            LOG.info("connecting to ftp host: " + this.ftpHost);
            client.connect(this.ftpHost);
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.enterLocalPassiveMode();
            client.changeWorkingDirectory(this.ftpPath);
            for (FTPFile file : client.listFiles(".", filter)) {
                fos = new FileOutputStream(this.basePath + "/" + file.getName());
                LOG.info("fetching: " + file);
                if (client.retrieveFile(file.getName(), fos)) {
                    fos.close();
                }
            }
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                client.disconnect();
            } catch (IOException e) {
                LOG.error("couldn't disconnect", e);
            }
        }
    }

    public void setBasePath(final String basePath) {
        if (null == basePath) {
            throw new IllegalArgumentException("null basePath");
        }
        this.basePath = basePath;
    }

    public void setFtpHost(final String ftpHost) {
        if (null == ftpHost) {
            throw new IllegalArgumentException("null ftpHost");
        }
        this.ftpHost = ftpHost;
    }

    public void setFtpPath(final String ftpPath) {
        if (null == ftpPath) {
            throw new IllegalArgumentException("null ftpPath");
        }
        this.ftpPath = ftpPath;
    }
}
