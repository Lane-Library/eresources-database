package edu.stanford.irt.eresources;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

public class PubmedFtpFileFilter implements FTPFileFilter {

    private String basePath;

    private List<String> processedFiles;

    public PubmedFtpFileFilter(final String basePath) {
        this.basePath = basePath;
        this.processedFiles = getProcessedFiles(new File(this.basePath));
    }

    @Override
    public boolean accept(final FTPFile file) {
        String name = file.getName();
        return name.endsWith(".xml.gz") && !this.processedFiles.contains(name);
    }

    private List<String> getProcessedFiles(final File directory) {
        List<String> fileList = new LinkedList<String>();
        for (File file : directory.listFiles()) {
            if (!file.isDirectory()) {
                fileList.add(file.getName());
            }
        }
        return fileList;
    }
}
