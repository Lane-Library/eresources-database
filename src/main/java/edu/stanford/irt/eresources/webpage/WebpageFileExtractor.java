package edu.stanford.irt.eresources.webpage;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.stanford.irt.eresources.Extractor;
import edu.stanford.irt.eresources.StartDate;

public class WebpageFileExtractor implements Extractor<File> {

    private static final String[] NO_SEARCH_DIRECTORIES = { ".svn", "includes", "search", "samples", "m", "usb" };

    private String basePath;

    private List<File> files;

    private Set<String> noSearchDirectories = new HashSet<String>();

    private StartDate startDate;

    public WebpageFileExtractor(final String basePath, final StartDate startDate) {
        this.basePath = basePath;
        this.startDate = startDate;
        for (String element : NO_SEARCH_DIRECTORIES) {
            this.noSearchDirectories.add(element);
        }
    }

    @Override
    public boolean hasNext() {
        if (this.files == null) {
            long date = this.startDate.getStartDate().getTime();
            this.files = getHTMLPages(new File(this.basePath), date);
        }
        return !this.files.isEmpty();
    }

    @Override
    public File next() {
        return this.files.remove(0);
    }

    private List<File> getHTMLPages(final File directory, final long date) {
        List<File> result = new ArrayList<File>();
        File[] files = directory.listFiles(new FileFilter() {

            @Override
            public boolean accept(final File file) {
                boolean accept = false;
                String name = file.getName();
                if (file.isDirectory() && !WebpageFileExtractor.this.noSearchDirectories.contains(name)) {
                    accept = true;
                } else if (name.endsWith(".html") && file.lastModified() > date) {
                    accept = true;
                }
                return accept;
            }
        });
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    result.addAll(getHTMLPages(file, date));
                } else {
                    result.add(file);
                }
            }
        }
        return result;
    }
}
