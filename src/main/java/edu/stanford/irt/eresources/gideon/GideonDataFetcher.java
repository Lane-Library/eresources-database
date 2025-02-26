package edu.stanford.irt.eresources.gideon;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import edu.stanford.irt.eresources.DataFetcher;
import edu.stanford.irt.eresources.EresourceDatabaseException;

public class GideonDataFetcher implements DataFetcher {

    private String host;

    private String hostPublicKey;

    private String localDirectory;

    private String password;

    private String user;

    // protected for unit testing
    protected JSch jsch = new JSch();



    public GideonDataFetcher(final String host, final String hostPublicKey, final String user, final String password,
            final String localDirectory) {
        this.host = host;
        this.hostPublicKey = hostPublicKey;
        this.user = user;
        this.password = password;
        this.localDirectory = localDirectory;
    }

    @Override
    public void getUpdateFiles() {
        // make localDirectory if not already present
        // then delete all old content before fetching new
        File localDir = new File(this.localDirectory);
        localDir.mkdirs();
        try {
            FileUtils.cleanDirectory(localDir);
            FileUtils.writeStringToFile(new File(localDir + "/hostPublicKey"), this.hostPublicKey,
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new EresourceDatabaseException("problem writing to localDirectory", e);
        }
        Session jschSession = null;
        ChannelSftp channelSftp = null;
        try {
            this.jsch.setKnownHosts(localDir + "/hostPublicKey");
            jschSession = jsch.getSession(this.user, this.host, 22);
            jschSession.setConfig("StrictHostKeyChecking", "yes");
            jschSession.setPassword(this.password);
            jschSession.connect();
            channelSftp = (ChannelSftp) jschSession.openChannel("sftp");
            channelSftp.connect();
            channelSftp.get("gideon_feeds_complete*.xml.gz", this.localDirectory);
        } catch (SftpException | JSchException e) {
            throw new EresourceDatabaseException("problem fetching gideon files from remote server", e);
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
    }
}
