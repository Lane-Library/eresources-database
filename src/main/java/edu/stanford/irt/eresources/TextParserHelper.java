package edu.stanford.irt.eresources;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
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

    private static final Pattern ACCEPTED_YEAR_PATTERN = Pattern.compile("^\\d[\\d|u]{3}$");

    private static final Pattern DOI_PATTERN = Pattern.compile("\\b(?:doi:)?(10\\.[^ ]+)(?: +doi)?\\b");

    private static final String EMPTY = "";

    private static final Pattern LANE_CONTROL_NUMBER = Pattern.compile("^[LQZ]\\d+$");

    private static final Pattern MONTH_ABR_PATTERN = Pattern.compile("\\b[A-Za-z]{3}\\b");

    private static final int MONTH_PATTERN_MAX = 4;

    private static final String[] NO_CAP_WORDS = { "a", "and", "as", "at", "but", "by", "for", "from", "if", "in",
            "into", "like", "nor", "of", "off", "on", "once", "onto", "or", "over", "so", "than", "that", "the", "to",
            "upon", "when", "with", "yet" };

    private static final int ORCID_MAX_LENGTH = 19;

    private static final Pattern ORCID_PATTERN = Pattern.compile("(\\b(?:\\d{4}[\\- ]){3,}\\d{3}[\\dXx]\\b)");

    private static final Pattern PAGES_START_END_PATTERN = Pattern.compile(".*\\b(\\w+)\\- ?(\\w+)\\b.*");

    private static final String SPACE = " ";

    private static final Pattern WORD_BOUNDARY_PATTERN = Pattern.compile("\\b");

    private static final Pattern ZERO_PAD_PATTERN = Pattern.compile("^0+");

    public static void appendMaybeAddComma(final StringBuilder sb, final String string) {
        if (string != null && string.length() > 0) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(string);
        }
    }

    /**
     * strip negatives from IDs created from hashCode
     *
     * @param hashCode
     *            int used as an ID
     * @return cleaned ID
     */
    public static String cleanId(final int hashCode) {
        return Integer.toString(hashCode).replace("-", "");
    }

    /**
     * ORCID data from PubMed is dirty. Extract a valid ORCID if possible, otherwise just return entire string.
     *
     * @param orcidString
     *            incoming ORCID
     * @return cleaned ORCID where possible
     */
    public static String cleanOrcid(final String orcidString) {
        Matcher m = ORCID_PATTERN.matcher(orcidString);
        if (m.find() && ORCID_MAX_LENGTH == m.group(1).length()) {
            return m.group(1).replace(' ', '-').toUpperCase(Locale.US);
        }
        return orcidString;
    }

    /**
     * Expand textual variants from a string month. Handles digit, 3 character abbreviations and fully spelled out month
     * strings. Designed to expand from NCBI's PubDate/Month element:
     * https://www.nlm.nih.gov/bsd/licensee/elements_descriptions.html#pubdate
     *
     * @param month
     *            string month
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
     *            string with months
     * @return all month variants
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
     * Extract DOIs from a string of text
     *
     * @param text
     *            incoming string with possible DOIs
     * @return list of DOIs (first will be most authoritative for PubMed)
     */
    public static List<String> extractDois(final String text) {
        List<String> dois = new ArrayList<>();
        Matcher m = DOI_PATTERN.matcher(text);
        while (m.find()) {
            if (m.group(0).contains("doi")) {
                dois.add(m.group(1));
            }
        }
        return dois;
    }

    /**
     * remove trailing periods, some probably should have them but voyager puts them on everything :-(
     *
     * @param string
     *            text with possible period at end
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
     * remove unbalanced ending bracket; useful for 260 ^c
     *
     * @param string
     *            text with possible unbalanced bracket at end
     * @return text w/o bracket at end
     */
    public static String maybeStripTrailingUnbalancedBracket(final String string) {
        int lastBracket = string.lastIndexOf(']');
        if (lastBracket >= 0 && !string.contains("[")) {
            int lastPosition = string.length() - 1;
            if (lastBracket == lastPosition) {
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
     *            string containing pages
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
                int estimate = Integer.parseInt(year.replace('u', '0').replace('|', '0'));
                if (estimate > THIS_YEAR) {
                    estimate = THIS_YEAR;
                }
                parsedYear = Integer.toString(estimate);
            }
        }
        return parsedYear;
    }

    public static final Integer recordIdFromLaneControlNumber(final String cn) {
        if (null != cn && LANE_CONTROL_NUMBER.matcher(cn).matches()) {
            return Integer.valueOf(cn.substring(1));
        }
        return null;
    }

    /**
     * strip "/ " from {@code StringBuilder}
     *
     * @param sb
     *            {@code StringBuilder} with slash and/or space
     * @return string w/o slash and space
     */
    public static StringBuilder removeTrailingSlashAndSpace(final StringBuilder sb) {
        while (sb.length() > 0 && (sb.lastIndexOf("/") == sb.length() - 1 || sb.lastIndexOf(" ") == sb.length() - 1)) {
            sb.setLength(sb.length() - 1);
        }
        return sb;
    }

    /**
     * Capitalize all the space-separated words in a {@code String}, ignoring some English words like articles and
     * prepositions.
     *
     * @param string
     *            needing caps
     * @return capitalized string
     */
    public static String toTitleCase(final String string) {
        if (string == null || string.trim().isEmpty()) {
            return string;
        }
        String title = string.trim();
        StringBuilder sb = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : title.toCharArray()) {
            if (Character.isWhitespace(c)) {
                capitalizeNext = true;
                sb.append(c);
            } else if (capitalizeNext) {
                sb.append(Character.toTitleCase(c));
                capitalizeNext = false;
            } else {
                sb.append(c);
            }
        }
        String titleCased = sb.toString();
        for (String preposition : NO_CAP_WORDS) {
            titleCased = titleCased.replaceAll("(?i)(?<!^)\\b" + preposition + "\\b", preposition);
        }
        // Preserve original casing for words like iPhone, e-Anatomy, aBIOTECH, etc.
        String[] words = titleCased.split("\\s+");
        String[] originalWords = string.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            if (originalWords[i].matches("^[a-z][A-Z].*") || originalWords[i].matches("^[a-zA-Z]-.*")) {
                words[i] = originalWords[i];
            }
        }
        titleCased = String.join(" ", words);
        //remove finial period
        if (titleCased.endsWith(".")) {
            titleCased = titleCased.substring(0, titleCased.length() - 1);
        }
        return titleCased;
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

    private TextParserHelper() {
        // empty private constructor
    }
}
