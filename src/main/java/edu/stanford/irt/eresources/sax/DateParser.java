package edu.stanford.irt.eresources.sax;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class DateParser {

    private static final Pattern EIGHT_DIGITS = Pattern.compile("\\d{8}");

    private static final String JAN_01 = "0101";

    private static final Pattern YEAR = Pattern.compile("\\d{4}");

    private static final Pattern YEAR_FIRST_ANYWHERE = Pattern.compile("\\b(\\d{4})\\b");

    private static final Pattern YEAR_MON = Pattern.compile("\\d{4} [A-Z][a-z]{2}");

    private static final Pattern YEAR_MON_DAY = Pattern.compile("\\d{4} [A-Z][a-z]{2} \\d{1,2}");

    private static final DateFormat YEAR_MONTH_DAY_FORMAT = new SimpleDateFormat("yyyy MMM dd", Locale.ENGLISH);

    private static final DateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy MMM", Locale.ENGLISH);

    private static final Pattern YEAR_NUMON = Pattern.compile("\\d{4} \\d{1,2}");

    private static final Pattern YEAR_NUMON_DAY = Pattern.compile("\\d{4} \\d{1,2} \\d{1,2}");

    private static final DateFormat YEAR_NUMONTH_DAY_FORMAT = new SimpleDateFormat("yyyy MM dd", Locale.ENGLISH);

    private static final DateFormat YEAR_NUMONTH_FORMAT = new SimpleDateFormat("yyyy MM", Locale.ENGLISH);

    private static final Pattern YEAR_SEASON = Pattern.compile("(\\d{4}) (winter|spring|summer|fall)",
            Pattern.CASE_INSENSITIVE);

    private static final String ZERO = "0";

    private DateParser() {
        // empty private constructor
    }

    public static String parseDate(final String date) {
        String parsedDate = ZERO;
        try {
            parsedDate = doDateParse(date);
        } catch (ParseException e) {
            // ok
        }
        return parsedDate;
    }

    /**
     * PubDate and MedlineDate field descriptions:
     * https://www.nlm.nih.gov/bsd/licensee/elements_descriptions.html#pubdate PubMed help:
     * "Publication dates without a month are set to January, multiple months (e.g., Oct-Dec) are set to the first month, and dates without a day are set to the first day of the month. Dates with a season are set as: winter = January, spring = April, summer = July and fall = October."
     *
     * @param date
     * @return String date in yyyyMMdd format or 0
     * @throws ParseException
     */
    private static String doDateParse(final String date) throws ParseException {
        DateFormat desiredFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
        String formattedDate = ZERO;
        String cleaned = date.replaceFirst("(.*)((?:\\-|/).*)", "$1").trim();
        if (EIGHT_DIGITS.matcher(cleaned).matches()) {
            formattedDate = cleaned;
        } else if (YEAR.matcher(cleaned).matches()) {
            formattedDate = cleaned + JAN_01;
        } else if (YEAR_MON_DAY.matcher(cleaned).matches()) {
            formattedDate = desiredFormat.format(YEAR_MONTH_DAY_FORMAT.parse(cleaned));
        } else if (YEAR_MON.matcher(cleaned).matches()) {
            formattedDate = desiredFormat.format(YEAR_MONTH_FORMAT.parse(cleaned));
        } else if (YEAR_NUMON_DAY.matcher(cleaned).matches()) {
            formattedDate = desiredFormat.format(YEAR_NUMONTH_DAY_FORMAT.parse(cleaned));
        } else if (YEAR_NUMON.matcher(cleaned).matches()) {
            formattedDate = desiredFormat.format(YEAR_NUMONTH_FORMAT.parse(cleaned));
        } else if (YEAR_SEASON.matcher(cleaned).matches()) {
            Matcher m = YEAR_SEASON.matcher(cleaned);
            m.matches();
            String year = m.group(1);
            String season = m.group(2);
            formattedDate = year + parseSeason(season);
        } else if (YEAR_FIRST_ANYWHERE.matcher(cleaned).find()) {
            Matcher m = YEAR_FIRST_ANYWHERE.matcher(cleaned);
            m.find();
            String year = m.group(1);
            formattedDate = year + JAN_01;
        }
        return formattedDate;
    }

    private static final String parseSeason(final String season) {
        String mmdd = JAN_01;
        if ("winter".equalsIgnoreCase(season)) {
            mmdd = "0101";
        } else if ("spring".equalsIgnoreCase(season)) {
            mmdd = "0401";
        } else if ("summer".equalsIgnoreCase(season)) {
            mmdd = "0701";
        } else if ("fall".equalsIgnoreCase(season)) {
            mmdd = "1001";
        }
        return mmdd;
    }
}
