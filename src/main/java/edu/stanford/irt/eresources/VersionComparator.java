package edu.stanford.irt.eresources;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.stanford.irt.eresources.marc.MarcLink;
import edu.stanford.irt.eresources.marc.MarcVersion;

/**
 * @author ryanmax
 */
public class VersionComparator implements Comparator<Version>, Serializable {

    private static final ZoneId AMERICA_LA = ZoneId.of("America/Los_Angeles");

    private static final Pattern CLOSED_DATE_PATTERN = Pattern.compile("(\\d{4})\\-(\\d{4})\\.");

    private static final List<String> FAVORED_PUBLISHERS = Arrays.asList("sciencedirect", "wiley", "springer",
            "highwire", "ovid", "nature", "liebert", "informaworld", "karger", "pubmed central");

    private static final int MAX_PUBLISHER_SCORE = 10;

    private static final int MIN_SCORE = -99;

    private static final Pattern OPEN_DATE_PATTERN = Pattern.compile(".*(\\d{4})\\-");

    private static final long serialVersionUID = 1L;

    private static final int THIS_YEAR = ZonedDateTime.now(AMERICA_LA).getYear();

    @Override
    public int compare(final Version v1, final Version v2) {
        int score1 = calculateHoldingsScore(v1);
        int score2 = calculateHoldingsScore(v2);
        int yearsCovered1 = getYearsCovered(v1);
        int yearsCovered2 = getYearsCovered(v2);
        // factor in years covered only if available in both versions
        if (yearsCovered1 != -1 && yearsCovered2 != -1) {
            score1 = score1 + yearsCovered1;
            score2 = score2 + yearsCovered2;
        }
        if (score1 != score2) {
            return score2 - score1;
        }
        // only factor in publisher score if holding scores are equal
        score1 = calculatePublisherScore(v1);
        score2 = calculatePublisherScore(v2);
        if (score1 != score2) {
            return score2 - score1;
        }
        return 1;
    }

    private int calculateAdditionalTextScore(final String additionalText, final int score) {
        int calculatedScore = score;
        if (additionalText != null) {
            if (additionalText.contains("delayed")) {
                calculatedScore--;
            }
            if (additionalText.equalsIgnoreCase("current edition")) {
                calculatedScore++;
            }
        }
        return calculatedScore;
    }

    private int calculateDatesScore(final String dates, final int score) {
        int calculatedScore = score;
        if (dates != null) {
            if (dates.endsWith("-")) {
                calculatedScore++;
            } else if (dates.endsWith(".")) {
                calculatedScore--;
            }
        }
        return calculatedScore;
    }

    /**
     * Calculate sorting score for version based on:
     *
     * <pre>
     * ++ dates or summaryHoldings (866 ^v) end in "-"
     * ++ additionalText (866 ^z) is "current edition"
     * -- additionalText (866 ^z) has "delayed" in it
     * -- first link label is "Impact Factor"
     * -- has period at end of dates or summaryHoldings
     * -- catalog links (print)
     *    catalog links before impact factor (case 112189: records with only an impact factor link)
     * </pre>
     *
     * @param version
     * @return score
     */
    private int calculateHoldingsScore(final Version version) {
        List<Link> links = version.getLinks();
        int score = 0;
        if (links.isEmpty()) {
            score = Integer.MIN_VALUE;
        } else if (firstLinkIsCatalogLink(version)) {
            score = MIN_SCORE + 1;
        } else if ("Impact Factor".equals(links.get(0).getLabel())) {
            score = MIN_SCORE;
        } else {
            score = calculateLinkScore(links.get(0), score);
            score = calculateSummaryHoldingsScore(version.getSummaryHoldings(), score);
            score = calculateDatesScore(version.getDates(), score);
            score = calculateAdditionalTextScore(version.getAdditionalText(), score);
            // make sure installed software product description is first:
            score = calculateInstalledSoftwareScore(links.get(0).getLabel(), score);
        }
        return score;
    }

    private int calculateInstalledSoftwareScore(final String linkLabel, final int score) {
        int calculatedScore = score;
        if (score == 0 && "Product Description".equalsIgnoreCase(linkLabel)) {
            calculatedScore = 1;
        }
        return calculatedScore;
    }

    /**
     * Examine and score a {@code Link}. Related resource links (856 42) should be down-sorted. Related resource links
     * (856 40) should be up-sorted. See case LANEWEB-10642
     * 
     * @param link
     *            link to exam and score
     * @param score
     *            pre-examination score
     * @return calculated score
     */
    private int calculateLinkScore(final Link link, final int score) {
        int calculatedScore = score;
        if (link.isResourceLink()) {
            calculatedScore++;
        } else if (link.isRelatedResourceLink())
            calculatedScore--;
        return calculatedScore;
    }

    /**
     * Calculate score for select list of publishers
     *
     * @param version
     * @return score
     */
    private int calculatePublisherScore(final Version version) {
        int score = 1;
        String publisher = version.getPublisher();
        if (publisher != null) {
            publisher = publisher.toLowerCase(Locale.US);
            if (FAVORED_PUBLISHERS.contains(publisher)) {
                score = score + (MAX_PUBLISHER_SCORE - FAVORED_PUBLISHERS.indexOf(publisher));
            }
        }
        return score;
    }

    private int calculateSummaryHoldingsScore(final String summaryHoldings, final int score) {
        int calculatedScore = score;
        if (summaryHoldings != null) {
            if (summaryHoldings.endsWith("-") || summaryHoldings.startsWith("v. 1-")) {
                calculatedScore++;
            } else if (summaryHoldings.endsWith(".")) {
                calculatedScore--;
            }
        }
        return calculatedScore;
    }

    private boolean firstLinkIsCatalogLink(final Version version) {
        if (!version.getLinks().isEmpty()) {
            String url = version.getLinks().get(0).getUrl();
            if (null != url) {
                return url.startsWith("http://lmldb.stanford.edu/cgi-bin/Pwebrecon.cgi?BBID=");
            }
        }
        return false;
    }

    private int getYearsCovered(final Version version) {
        String dates = version.getDates();
        if (dates != null) {
            Matcher closedMatcher = CLOSED_DATE_PATTERN.matcher(dates);
            Matcher openMatcher = OPEN_DATE_PATTERN.matcher(dates);
            if (closedMatcher.matches()) {
                int date1 = Integer.parseInt(closedMatcher.group(1));
                int date2 = Integer.parseInt(closedMatcher.group(closedMatcher.groupCount()));
                return date2 - date1;
            } else if (openMatcher.matches()) {
                return THIS_YEAR - Integer.parseInt(openMatcher.group(1));
            }
        }
        return -1;
    }
}
