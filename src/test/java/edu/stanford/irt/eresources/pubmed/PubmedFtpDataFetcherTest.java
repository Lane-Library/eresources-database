package edu.stanford.irt.eresources.pubmed;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.xbib.io.ftp.client.FTP;
import org.xbib.io.ftp.client.FTPClient;
import org.xbib.io.ftp.client.FTPFile;
import org.xbib.io.ftp.client.FTPFileFilter;

import edu.stanford.irt.eresources.EresourceDatabaseException;

class PubmedFtpDataFetcherTest {

    PubmedFtpDataFetcher fetcher;

    FTPClient ftpClient;

    FTPFile ftpFile;

    FTPFileFilter ftpFileFilter;

    FTPFile[] ftpFiles;

    String tempFile = "eresources-unit-test-file.tmp";

    @BeforeEach
    void setUp() {
        this.ftpClient = mock(FTPClient.class);
        this.ftpFileFilter = mock(PubmedFtpFileFilter.class);
        this.fetcher = new PubmedFtpDataFetcher("/tmp", this.ftpClient, this.ftpFileFilter, "ftpHostname",
                "ftpPathname", "ftpUsername", "ftpPassword");
        this.ftpFile = mock(FTPFile.class);
        this.ftpFiles = new FTPFile[1];
        this.ftpFiles[0] = this.ftpFile;
    }

    @AfterEach
    void tearDown() {
        new File("/tmp/" + this.tempFile).delete();
    }

    @Test
    final void testGetUpdateFiles() throws Exception {
        this.ftpClient.connect("ftpHostname");
        expectLastCall();
        expect(this.ftpClient.login("ftpUsername", "ftpPassword")).andReturn(true);
        expect(this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE)).andReturn(true);
        this.ftpClient.enterLocalPassiveMode();
        expectLastCall();
        expect(this.ftpClient.changeWorkingDirectory("ftpPathname")).andReturn(true);
        expect(this.ftpClient.listFiles(isA(String.class), isA(PubmedFtpFileFilter.class))).andReturn(this.ftpFiles);
        expect(this.ftpFile.getName()).andReturn(this.tempFile).times(2);
        expect(this.ftpClient.retrieveFile(isA(String.class), isA(FileOutputStream.class))).andReturn(true);
        this.ftpClient.disconnect();
        expectLastCall();
        replay(this.ftpClient, this.ftpFileFilter, this.ftpFile);
        this.fetcher.getUpdateFiles();
        verify(this.ftpClient, this.ftpFileFilter, this.ftpFile);
    }

    @Test
    final void testGetUpdateFilesConnectionExpection() throws Exception {
        this.ftpClient.connect("ftpHostname");
        expectLastCall().andThrow(new SocketException("oops")).atLeastOnce();
        expect(this.ftpClient.getLocalPort()).andReturn(0).atLeastOnce();
        expect(this.ftpClient.getPassivePort()).andReturn(0).atLeastOnce();
        expect(this.ftpClient.getRemotePort()).andReturn(0).atLeastOnce();
        this.ftpClient.disconnect();
        expectLastCall().atLeastOnce();
        replay(this.ftpClient);
        assertThrows(EresourceDatabaseException.class, () -> {
            this.fetcher.getUpdateFiles();
            verify(this.ftpClient);
        });

    }

    @Test
    final void testGetUpdateFilesFtpFileException() throws Exception {
        this.ftpClient.connect("ftpHostname");
        expectLastCall().atLeastOnce();
        expect(this.ftpClient.login("ftpUsername", "ftpPassword")).andReturn(true).atLeastOnce();
        expect(this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE)).andReturn(true).atLeastOnce();
        this.ftpClient.enterLocalPassiveMode();
        expectLastCall().atLeastOnce();
        expect(this.ftpClient.changeWorkingDirectory("ftpPathname")).andReturn(true).atLeastOnce();
        expect(this.ftpClient.listFiles(isA(String.class), isA(PubmedFtpFileFilter.class))).andReturn(this.ftpFiles)
                .atLeastOnce();
        expect(this.ftpFile.getName()).andReturn(this.tempFile).atLeastOnce();
        expect(this.ftpClient.retrieveFile(isA(String.class), isA(FileOutputStream.class)))
                .andThrow(new IOException("oops")).atLeastOnce();
        this.ftpClient.disconnect();
        expectLastCall().atLeastOnce();
        replay(this.ftpClient, this.ftpFileFilter, this.ftpFile);
        assertThrows(EresourceDatabaseException.class, () -> {
            this.fetcher.getUpdateFiles();
            verify(this.ftpClient, this.ftpFileFilter, this.ftpFile);
        });
    }

    @Test
    final void testGetUpdateFilesFtpFileNotFound() throws Exception {
        this.ftpClient.connect("ftpHostname");
        expectLastCall();
        expect(this.ftpClient.login("ftpUsername", "ftpPassword")).andReturn(true);
        expect(this.ftpClient.setFileType(FTP.BINARY_FILE_TYPE)).andReturn(true);
        this.ftpClient.enterLocalPassiveMode();
        expectLastCall();
        expect(this.ftpClient.changeWorkingDirectory("ftpPathname")).andReturn(true);
        expect(this.ftpClient.listFiles(isA(String.class), isA(PubmedFtpFileFilter.class))).andReturn(this.ftpFiles);
        expect(this.ftpFile.getName()).andReturn(this.tempFile).times(2);
        expect(this.ftpClient.retrieveFile(isA(String.class), isA(FileOutputStream.class))).andReturn(false);
        this.ftpClient.disconnect();
        expectLastCall().andThrow(new IOException());
        replay(this.ftpClient, this.ftpFileFilter, this.ftpFile);
        this.fetcher.getUpdateFiles();
        verify(this.ftpClient, this.ftpFileFilter, this.ftpFile);
    }
}
