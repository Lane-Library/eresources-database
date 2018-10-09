package edu.stanford.irt.eresources.pubmed;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.xbib.io.ftp.client.FTPFile;
import org.xbib.io.ftp.client.FTPFileFilter;

public class PubmedFtpFileFilter implements FTPFileFilter {

    private static final Pattern DOT_XML_MAYBE_DOT_GZ = Pattern.compile("\\.xml(\\.gz)?");

    private Set<String> processedFiles;

    public PubmedFtpFileFilter(final String basePath) {
        this.processedFiles = getProcessedFiles(new File(basePath));
    }

    @Override
    public boolean accept(final FTPFile file) {
        String name = file.getName();
        if (name.endsWith(".xml.gz") || name.endsWith(".xml")) {
            name = DOT_XML_MAYBE_DOT_GZ.matcher(name).replaceFirst("");
            return !this.processedFiles.contains(name);
        }
        return false;
    }

    private Set<String> getProcessedFiles(final File directory) {
        Set<String> fileList = new HashSet<>();
        File[] files = directory.listFiles();
        if (null != files) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    fileList.add(DOT_XML_MAYBE_DOT_GZ.matcher(file.getName()).replaceFirst(""));
                }
            }
        }
        return fileList;
    }
}
