package edu.stanford.irt.eresources;

import java.util.List;

public interface Version {

    String getAdditionalText();

    String getDates();

    default String getHoldingsAndDates() {
        StringBuilder sb = new StringBuilder();
        String summaryHoldings = getSummaryHoldings();
        if (summaryHoldings != null) {
            sb.append(summaryHoldings);
        }
        TextParserHelper.appendMaybeAddComma(sb, getDates());
        return sb.length() == 0 ? null : sb.toString();
    }

    List<Link> getLinks();

    String getPublisher();

    String getSummaryHoldings();

    boolean hasGetPasswordLink();

    boolean isProxy();
}
