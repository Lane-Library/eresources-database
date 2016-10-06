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
        if (name.endsWith(".xml.gz") || name.endsWith(".xml")) {
            name = name.replaceFirst("\\.xml(\\.gz)?", "");
            return !this.processedFiles.contains(name);
        }
        return false;
    }

    private List<String> getProcessedFiles(final File directory) {
        List<String> fileList = new LinkedList<String>();
        File[] files = directory.listFiles();
        if (null != files) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    fileList.add(file.getName().replaceFirst("\\.xml(\\.gz)?", ""));
                }
            }
        }
        return fileList;
    }
}
