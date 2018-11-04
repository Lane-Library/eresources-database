package edu.stanford.irt.eresources;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ryanmax
 */
public final class TextParserHelper {

    public static final int THIS_YEAR = LocalDate.now(ZoneId.of("America/Los_Angeles")).getYear();

    protected static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss")
            .toFormatter();

    private static final Pattern ACCEPTED_YEAR_PATTERN = Pattern.compile("^\\d[\\d|u]{3}$");

    private static final Pattern DIGIT_OR_X_PATTERN = Pattern.compile("[^\\dxX]+");

    private static final String EMPTY = "";

    private static final Pattern MONTH_ABR_PATTERN = Pattern.compile("\\b[A-Za-z]{3}\\b");

    private static final int MONTH_PATTERN_MAX = 4;

    private static final Pattern PAGES_START_END_PATTERN = Pattern.compile(".*\\b(\\w+)\\- ?(\\w+)\\b.*");

    private static final String SPACE = " ";

    private static final Pattern WORD_BOUNDARY_PATTERN = Pattern.compile("\\b");

    private static final Pattern ZERO_PAD_PATTERN = Pattern.compile("^0+");

    private TextParserHelper() {
        // empty private constructor
    }

    public static void appendMaybeAddComma(final StringBuilder sb, final String string) {
        if (string != null && string.length() > 0) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(string);
        }
    }

    /**
     * ISBNs and ISSNs should only have digits or Xs. Ignore all but the first space-separated component of string.
     *
     * @param isbn
     *            or isbn
     * @return cleaned string
     */
    public static String cleanIsxn(final String isxn) {
        if (!isxn.isEmpty()) {
            return DIGIT_OR_X_PATTERN.matcher(isxn.split(" ")[0]).replaceAll("");
        }
        return isxn;
    }

    /**
     * Expand textual variants from a string month. Handles digit, 3 character abbreviations and fully spelled out month
     * strings. Designed to expand from NCBI's PubDate/Month element:
     * https://www.nlm.nih.gov/bsd/licensee/elements_descriptions.html#pubdate
     *
     * @param month
     * @return Between 3 and 4 representations of month (eg. "5 05 May", "12 Dec December", "7 07 Jul July")
     */
    public static String explodeMonth(final String month) {
        if (null == month || month.isEmpty()) {
            return EMPTY;
        }
        StringBuilder fmt = new StringBuilder();
        while (fmt.length() < month.length() && fmt.length() < MONTH_PATTERN_MAX) {
            fmt.append('M');
        }
        Set<String> months = new LinkedHashSet<>();
        try {
            DateTimeFormatter dtf = new DateTimeFormatterBuilder().appendPattern(fmt.toString()).toFormatter();
            Month m = Month.from(dtf.parse(month));
            int digit = m.getValue();
            months.add(Integer.toString(digit));
            if (m.compareTo(Month.OCTOBER) < 0) {
                months.add(String.format("%02d", digit));
            }
            months.add(m.getDisplayName(TextStyle.SHORT, Locale.US));
            months.add(m.getDisplayName(TextStyle.FULL, Locale.US));
        } catch (DateTimeParseException e) {
            // ok if can't parse month
        }
        return months.stream().collect(Collectors.joining(SPACE));
    }

    /**
     * Expand month abbreviations into textual variants. Designed for NCBI's MedlineDate element:
     * https://www.nlm.nih.gov/bsd/licensee/elements_descriptions.html#medlinedate
     *
     * @param text
     * @return
     */
    public static String explodeMonthAbbreviations(final String text) {
        if (null == text || text.isEmpty()) {
            return EMPTY;
        }
        Set<String> months = new LinkedHashSet<>();
        Matcher m = MONTH_ABR_PATTERN.matcher(text);
        while (m.find()) {
            months.add(explodeMonth(m.group()));
        }
        return months.stream().collect(Collectors.joining(SPACE));
    }

    /**
     * remove trailing periods, some probably should have them but voyager puts them on everything :-(
     *
     * @param text
     *            with possible period at end
     * @return text w/o period at end
     */
    public static String maybeStripTrailingPeriod(final String string) {
        int lastPeriod = string.lastIndexOf('.');
        if (lastPeriod >= 0) {
            int lastPosition = string.length() - 1;
            if (lastPeriod == lastPosition) {
                return string.substring(0, lastPosition);
            }
        }
        return string;
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
        if (null != pages && !pages.isEmpty()) {
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
        }
        return EMPTY;
    }

    /**
     * parse a year from string, replacing "u" and 9999 appropriately
     *
     * @param year
     *            incoming year
     * @return parsed year
     */
    public static String parseYear(final String year) {
        String parsedYear = null;
        Matcher yearMatcher = ACCEPTED_YEAR_PATTERN.matcher(year);
        if (yearMatcher.matches()) {
            parsedYear = year;
            if ("9999".equals(year)) {
                parsedYear = Integer.toString(THIS_YEAR);
            } else if (year.contains("u") || year.contains("|")) {
                int estimate = Integer.parseInt(year.replace('u', '5').replace('|', '5'));
                if (estimate > THIS_YEAR) {
                    estimate = THIS_YEAR;
                }
                parsedYear = Integer.toString(estimate);
            }
        }
        return parsedYear;
    }

    /**
     * strip "/ " from string
     *
     * @param string
     *            with slash and/or space
     * @return string w/o slash and space
     */
    public static StringBuilder removeTrailingSlashAndSpace(final StringBuilder sb) {
        while (sb.length() > 0 && (sb.lastIndexOf("/") == sb.length() - 1 || sb.lastIndexOf(" ") == sb.length() - 1)) {
            sb.setLength(sb.length() - 1);
        }
        return sb;
    }

    /**
     * Capitalize all the space-separated words in a {@code String}
     *
     * @param string
     *            needing caps
     * @return capitalized string
     */
    public static final String toTitleCase(final String string) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            boolean needsCap = false;
            if (i == 0 || (i > 0 && ' ' == string.charAt(i - 1))) {
                needsCap = true;
            }
            if (needsCap) {
                sb.append(Character.toTitleCase(c));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
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
