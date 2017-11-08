package edu.stanford.irt.eresources;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ryanmax
 */
public final class TextParserHelper {

    private static final String EMPTY = "";

    private static final Pattern PAGES_START_END_PATTERN = Pattern.compile(".*\\b(\\w+)\\- ?(\\w+)\\b.*");

    private static final Pattern WORD_BOUNDARY_PATTERN = Pattern.compile("\\b");

    private static final Pattern ZERO_PAD_PATTERN = Pattern.compile("^0+");

    private TextParserHelper() {
        // empty private constructor
    }

    /**
     * extract complete page endings from page statements containing hyphens (ranges) to allow users to search by
     * complete page range
     *
     * <pre>
        examples from https://www.nlm.nih.gov/bsd/licensee/elements_descriptions.html :
        304- 10
        335-6
        1199-201
        24-32, 64
        31-7 cntd
        176-8 concl
        iii-viii
        XC-CIII
        P32- 4
        32P-35P
        suppl 111-2
        E101-6
        44; discussion 44-8
        925; author reply 925- 6
        129e1- 4
        10.1-8
     * </pre>
     *
     * @param pages
     * @return full page range
     */
    public static String parseEndPages(final String pages) {
        Matcher m = PAGES_START_END_PATTERN.matcher(pages);
        if (m.matches()) {
            String start = m.group(1).trim();
            String end = m.group(m.groupCount()).trim();
            int baseLength = start.length() - end.length();
            if (baseLength > 0) {
                String base = start.substring(0, baseLength);
                StringBuilder sb = new StringBuilder(start);
                sb.append('-');
                sb.append(base);
                sb.append(end);
                return sb.toString();
            }
        }
        return EMPTY;
    }

    /**
     * Find and unpad zero-padded text; returns unpadded text only
     *
     * @param text
     *            with possible zero-padding
     * @return only unpadded text
     */
    public static String unpadZeroPadded(final String text) {
        StringBuilder sb = new StringBuilder();
        for (String s : WORD_BOUNDARY_PATTERN.split(text)) {
            if (s.indexOf('0') == 0) {
                sb.append(ZERO_PAD_PATTERN.matcher(s).replaceFirst(EMPTY));
                sb.append(' ');
            }
        }
        return sb.toString().trim();
    }
}
