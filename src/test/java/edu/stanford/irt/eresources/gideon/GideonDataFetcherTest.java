package edu.stanford.irt.eresources.gideon;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import edu.stanford.irt.eresources.EresourceDatabaseException;

class GideonDataFetcherTest {

    private static final String HOST = "test.host";

    private static final String HOST_PUBLIC_KEY = "testPublicKey";

    private static final String PASSWORD = "testPassword";

    private static final String USER = "testUser";

    private ChannelSftp channelSftp;

    private GideonDataFetcher dataFetcher;

    private JSch jsch;

    private String localDirectory;

    private Session session;

    @BeforeEach
    void setUp() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "gideonDataFetcherTest");
        tempDir.mkdirs();
        this.localDirectory = tempDir.getAbsolutePath();
        this.dataFetcher = new GideonDataFetcher(HOST, HOST_PUBLIC_KEY, USER, PASSWORD, this.localDirectory);
        this.jsch = createMock(JSch.class);
        this.dataFetcher.jsch = this.jsch;
        this.session = createMock(Session.class);
        this.channelSftp = createMock(ChannelSftp.class);
    }

    @Test
    void testGetUpdateFiles() throws Exception {
        this.jsch.setKnownHosts(isA(String.class));
        expectLastCall();
        expect(this.jsch.getSession(USER, HOST, 22)).andReturn(this.session);
        this.session.setConfig("StrictHostKeyChecking", "yes");
        expectLastCall();
        this.session.setPassword(isA(String.class));
        expectLastCall();
        this.session.connect();
        expectLastCall();
        expect(this.session.openChannel("sftp")).andReturn(this.channelSftp);
        this.channelSftp.connect();
        expectLastCall();
        this.channelSftp.get(isA(String.class), isA(String.class));
        expectLastCall();
        this.channelSftp.disconnect();
        expectLastCall();
        this.session.disconnect();
        expectLastCall();
        replay(this.jsch, this.session, this.channelSftp);
        this.dataFetcher.getUpdateFiles();
        verify(this.jsch, this.session, this.channelSftp);
    }

    @Test
    @Disabled("failing on GitLb CI/CD")
    void testGetUpdateFilesIOException() {
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "another");
        tempDir.mkdirs();
        String parentTemp = tempDir.getParent();
        this.dataFetcher = new GideonDataFetcher(HOST, HOST_PUBLIC_KEY, USER, PASSWORD, parentTemp);
        EresourceDatabaseException exception = assertThrows(EresourceDatabaseException.class, () -> {
            this.dataFetcher.getUpdateFiles();
        });
        assertNotNull(exception);
        assertEquals("problem writing to localDirectory", exception.getMessage());
    }

    @Test
    void testGetUpdateFilesJSchException() throws Exception {
        this.jsch.setKnownHosts(isA(String.class));
        expectLastCall().andThrow(new JSchException("oops!"));
        replay(this.jsch);
        EresourceDatabaseException exception = assertThrows(EresourceDatabaseException.class, () -> {
            this.dataFetcher.getUpdateFiles();
        });
        assertNotNull(exception);
        assertEquals("problem fetching gideon files from remote server", exception.getMessage());
        assertEquals(JSchException.class, exception.getCause().getClass());
        verify(this.jsch);
    }

    @Test
    void testGetUpdateFilesSftpException() throws Exception {
        this.jsch.setKnownHosts(isA(String.class));
        expectLastCall();
        expect(this.jsch.getSession(USER, HOST, 22)).andReturn(this.session);
        this.session.setConfig("StrictHostKeyChecking", "yes");
        expectLastCall();
        this.session.setPassword(isA(String.class));
        expectLastCall();
        this.session.connect();
        expectLastCall();
        expect(this.session.openChannel("sftp")).andReturn(this.channelSftp);
        this.channelSftp.connect();
        expectLastCall();
        this.channelSftp.get(isA(String.class), isA(String.class));
        expectLastCall().andThrow(new SftpException(0, "oops!"));
        this.channelSftp.disconnect();
        expectLastCall();
        this.session.disconnect();
        expectLastCall();
        replay(this.jsch, this.session, this.channelSftp);
        EresourceDatabaseException exception = assertThrows(EresourceDatabaseException.class, () -> {
            this.dataFetcher.getUpdateFiles();
        });
        assertNotNull(exception);
        assertEquals("problem fetching gideon files from remote server", exception.getMessage());
        assertEquals(SftpException.class, exception.getCause().getClass());
        verify(this.jsch, this.session, this.channelSftp);
    }
}