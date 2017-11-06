package edu.stanford.irt.eresources;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * extract complete page endings from page statements containing hyphens (ranges) to allow users to search by complete
 * page range
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
 * @author ryanmax
 */
public final class PagesParser {

    private static final String EMPTY = "";

    private static final Pattern START_END_PATTERN = Pattern.compile(".*\\b(\\w+)\\- ?(\\w+)\\b.*");

    private PagesParser() {
        // empty private constructor
    }

    /**
     * extract page endings when pages contain a range
     *
     * @param pages
     * @return full page ending
     */
    public static String parseEndPages(final String pages) {
        Matcher m = START_END_PATTERN.matcher(pages);
        if (m.matches()) {
            String start = m.group(1).trim();
            String end = m.group(m.groupCount()).trim();
            int baseLength = start.length() - end.length();
            if (baseLength > 0) {
                String base = start.substring(0, baseLength);
                return base + end;
            }
        }
        return EMPTY;
    }
}
