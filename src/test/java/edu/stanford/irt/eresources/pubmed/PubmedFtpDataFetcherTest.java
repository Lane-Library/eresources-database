package edu.stanford.irt.eresources.pubmed;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xbib.io.ftp.client.FTP;
import org.xbib.io.ftp.client.FTPClient;
import org.xbib.io.ftp.client.FTPFile;
import org.xbib.io.ftp.client.FTPFileFilter;

import edu.stanford.irt.eresources.EresourceDatabaseException;

public class PubmedFtpDataFetcherTest {

    PubmedFtpDataFetcher fetcher;

    FTPClient ftpClient;

    FTPFile ftpFile;

    FTPFileFilter ftpFileFilter;

    FTPFile[] ftpFiles;

    @Before
    public void setUp() throws Exception {
        this.ftpClient = mock(FTPClient.class);
        this.ftpFileFilter = mock(PubmedFtpFileFilter.class);
        this.fetcher = new PubmedFtpDataFetcher("/tmp", this.ftpClient, this.ftpFileFilter, "ftpHostname",
                "ftpPathname", "ftpUsername", "ftpPassword");
        this.ftpFile = mock(FTPFile.class);
        this.ftpFiles = new FTPFile[1];
        this.ftpFiles[0] = this.ftpFile;
    }

    @After
    public void tearDown() throws Exception {
        new File("/tmp/foo").delete();
    }

    @Test
    public final void testGetUpdateFiles() throws Exception {
        this.ftpClient.connect("ftpHostname");
        expectLastCall();
        expect(this.ftpClient.login("ftpUsername", "ftpPassword")).andReturn(true);
        expect(this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE)).andReturn(true);
        this.ftpClient.enterLocalPassiveMode();
        expectLastCall();
        expect(this.ftpClient.changeWorkingDirectory("ftpPathname")).andReturn(true);
        expect(this.ftpClient.listFiles(isA(String.class), isA(PubmedFtpFileFilter.class))).andReturn(this.ftpFiles);
        expect(this.ftpFile.getName()).andReturn("foo").times(2);
        expect(this.ftpClient.retrieveFile(isA(String.class), isA(FileOutputStream.class))).andReturn(true);
        this.ftpClient.disconnect();
        expectLastCall();
        replay(this.ftpClient, this.ftpFileFilter, this.ftpFile);
        this.fetcher.getUpdateFiles();
        verify(this.ftpClient, this.ftpFileFilter, this.ftpFile);
    }

    @Test(expected = EresourceDatabaseException.class)
    public final void testGetUpdateFilesConnectionExpection() throws Exception {
        this.ftpClient.connect("ftpHostname");
        expectLastCall().andThrow(new SocketException("oops"));
        this.ftpClient.disconnect();
        expectLastCall();
        replay(this.ftpClient);
        this.fetcher.getUpdateFiles();
        verify(this.ftpClient);
    }

    @Test(expected = EresourceDatabaseException.class)
    public final void testGetUpdateFilesFtpFileException() throws Exception {
        this.ftpClient.connect("ftpHostname");
        expectLastCall();
        expect(this.ftpClient.login("ftpUsername", "ftpPassword")).andReturn(true);
        expect(this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE)).andReturn(true);
        this.ftpClient.enterLocalPassiveMode();
        expectLastCall();
        expect(this.ftpClient.changeWorkingDirectory("ftpPathname")).andReturn(true);
        expect(this.ftpClient.listFiles(isA(String.class), isA(PubmedFtpFileFilter.class))).andReturn(this.ftpFiles);
        expect(this.ftpFile.getName()).andReturn("foo").times(2);
        expect(this.ftpClient.retrieveFile(isA(String.class), isA(FileOutputStream.class)))
                .andThrow(new IOException("oops"));
        this.ftpClient.disconnect();
        expectLastCall();
        replay(this.ftpClient, this.ftpFileFilter, this.ftpFile);
        this.fetcher.getUpdateFiles();
        verify(this.ftpClient, this.ftpFileFilter, this.ftpFile);
    }

    @Test
    public final void testGetUpdateFilesFtpFileNotFound() throws Exception {
        this.ftpClient.connect("ftpHostname");
        expectLastCall();
        expect(this.ftpClient.login("ftpUsername", "ftpPassword")).andReturn(true);
        expect(this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE)).andReturn(true);
        this.ftpClient.enterLocalPassiveMode();
        expectLastCall();
        expect(this.ftpClient.changeWorkingDirectory("ftpPathname")).andReturn(true);
        expect(this.ftpClient.listFiles(isA(String.class), isA(PubmedFtpFileFilter.class))).andReturn(this.ftpFiles);
        expect(this.ftpFile.getName()).andReturn("foo").times(2);
        expect(this.ftpClient.retrieveFile(isA(String.class), isA(FileOutputStream.class))).andReturn(false);
        this.ftpClient.disconnect();
        expectLastCall().andThrow(new IOException());
        replay(this.ftpClient, this.ftpFileFilter, this.ftpFile);
        this.fetcher.getUpdateFiles();
        verify(this.ftpClient, this.ftpFileFilter, this.ftpFile);
    }
}
