package edu.stanford.irt.eresources;

import java.io.File;
import java.util.Comparator;

/**
 * Alpha and numeric sort of files so PubMed updates are applied in order
 *
 * @author ryanmax
 */
public class PubmedFilenameComparator implements Comparator<File> {

    @Override
    public int compare(final File f1, final File f2) {
        String f1Alpha = getAlphasFromFile(f1);
        String f2Alpha = getAlphasFromFile(f2);
        if (f1Alpha.equals(f2Alpha)) {
            long f1Num = getDigitsFromFile(f1);
            long f2Num = getDigitsFromFile(f2);
            return Long.compare(f1Num, f2Num);
        }
        return f1.compareTo(f2);
    }

    private String getAlphasFromFile(final File file) {
        return file.getAbsolutePath().replaceAll("(\\W|\\d)", "");
    }

    private long getDigitsFromFile(final File file) {
        return Long.parseLong(file.getAbsolutePath().replaceAll("[^\\d]", ""));
    }
}