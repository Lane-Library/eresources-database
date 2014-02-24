package edu.stanford.irt.eresources;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SolrPubmedUpdate extends SolrLoader {

    private String basePath;

    private String ftpHost;

    private String ftpPass;

    private String ftpPath;

    private String ftpUser;

    private Logger log = LoggerFactory.getLogger(getClass());

    private SolrServer solrServer;

    private void getFtpUpdateFiles() {
        PubmedFtpFileFilter filter = new PubmedFtpFileFilter(this.basePath);
        FTPClient client = new FTPClient();
        FileOutputStream fos = null;
        try {
            this.log.info("connecting to ftp host: " + this.ftpHost);
            client.connect(this.ftpHost);
            client.login(this.ftpUser, this.ftpPass);
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.changeWorkingDirectory(this.ftpPath);
            for (FTPFile file : client.listFiles(".", filter)) {
                fos = new FileOutputStream(this.basePath + "/" + file.getName());
                this.log.info("fetching: " + file);
                if (client.retrieveFile(file.getName(), fos)) {
                    fos.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                client.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected Date getUpdatedDate() {
        SolrQuery query = new SolrQuery();
        query.setQuery("recordType:pubmed");
        query.add("sort", "updated desc");
        QueryResponse rsp = null;
        try {
            rsp = this.solrServer.query(query);
        } catch (SolrServerException e) {
            throw new EresourceDatabaseException(e);
        }
        SolrDocumentList rdocs = rsp.getResults();
        Date updated;
        if (rdocs.isEmpty()) {
            updated = new Date(0);
        } else {
            SolrDocument firstResult = rdocs.get(0);
            updated = (Date) firstResult.getFieldValue("updated");
        }
        return updated;
    }

    public void init() {
        getFtpUpdateFiles();
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

    public void setFtpPass(final String ftpPass) {
        if (null == ftpPass) {
            throw new IllegalArgumentException("null ftpPass");
        }
        this.ftpPass = ftpPass;
    }

    public void setFtpPath(final String ftpPath) {
        if (null == ftpPath) {
            throw new IllegalArgumentException("null ftpPath");
        }
        this.ftpPath = ftpPath;
    }

    public void setFtpUser(final String ftpUser) {
        if (null == ftpUser) {
            throw new IllegalArgumentException("null ftpUser");
        }
        this.ftpUser = ftpUser;
    }

    public void setSolrServer(final SolrServer solrServer) {
        this.solrServer = solrServer;
    }
}
