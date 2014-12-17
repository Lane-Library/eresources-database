package edu.stanford.irt.eresources.marc;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.irt.eresources.Link;

public class HoldingsComparator implements Comparator<MarcVersion> {

    private static final Pattern CLOSED_DATE_PATTERN = Pattern.compile("(\\d{4})\\-(\\d{4})\\.");

    private static final List<String> favoredPublishers = Arrays.asList("sciencedirect", "wiley", "springer",
            "highwire", "ovid", "nature", "liebert", "informaworld", "karger", "pubmed central");

    private static final Pattern OPEN_DATE_PATTERN = Pattern.compile(".*(\\d{4})\\-");

    private static final int THIS_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    @Override
    public int compare(final MarcVersion data1, final MarcVersion data2) {
        String dates1 = data1.getDates();
        String dates2 = data2.getDates();
        int score1 = calculateHoldingsScore(data1, dates1);
        int score2 = calculateHoldingsScore(data2, dates2);
        // factor in years covered only if available in both instances
        int yearsCovered1 = getYearsCovered(dates1);
        int yearsCovered2 = getYearsCovered(dates2);
        if (yearsCovered1 != -1 && yearsCovered2 != -1) {
            score1 = score1 + yearsCovered1;
            score2 = score2 + yearsCovered2;
        }
        if (score1 != score2) {
            return score2 - score1;
        }
        // only factor in publisher score if holding scores are equal
        score1 = calculatePublisherScore(data1.getPublisher());
        score2 = calculatePublisherScore(data2.getPublisher());
        if (score1 != score2) {
            return score2 - score1;
        }
        return 1;
    }

    /**
     * Calculate sorting score for holdings data based on:
     *
     * <pre>
     * ++ dates or summaryHoldings end in "-"
     * -- description has "delayed" in it
     * -- first link label is "Impact Factor"
     * -- has period at end of dates or summaryHoldings
     * </pre>
     *
     * @param data
     * @return score
     */
    private int calculateHoldingsScore(final MarcVersion data, final String dates) {
        int score = 0;
        String summaryHoldings = data.getSummaryHoldings();
        if (summaryHoldings != null) {
            if (summaryHoldings.endsWith("-") || summaryHoldings.startsWith("v. 1-")) {
                score++;
            } else if (summaryHoldings.endsWith(".")) {
                score--;
            }
        }
        if (dates != null) {
            if (dates.endsWith("-")) {
                score++;
            } else if (dates.endsWith(".")) {
                score--;
            }
        }
        String description = data.getDescription();
        if (description != null && description.contains("delayed")) {
            score--;
        }
        List<Link> links = data.getLinks();
        if (links.size() > 0 && "Impact Factor".equals(links.get(0).getLabel())) {
            score = -99;
        }
        return score;
    }

    /**
     * Calculate score for select list of publishers
     *
     * @param v
     * @return score
     */
    private int calculatePublisherScore(final String publisher) {
        int score = 1;
        if (publisher != null) {
            String lowerPublisher = publisher.toLowerCase();
            if (favoredPublishers.contains(lowerPublisher)) {
                score = score + (10 - favoredPublishers.indexOf(lowerPublisher));
            }
        }
        return score;
    }

    private int getYearsCovered(final String dates) {
        int score = -1;
        if (dates != null) {
            Matcher closedMatcher = CLOSED_DATE_PATTERN.matcher(dates);
            Matcher openMatcher = OPEN_DATE_PATTERN.matcher(dates);
            if (closedMatcher.matches()) {
                int date1 = Integer.parseInt(closedMatcher.group(1));
                int date2 = Integer.parseInt(closedMatcher.group(2));
                score = date2 - date1;
            } else if (openMatcher.matches()) {
                score = THIS_YEAR - Integer.parseInt(openMatcher.group(1));
            }
        }
        return score;
    }
}
