package edu.stanford.irt.eresources;

import java.util.List;

public interface Version {

    public default String getHoldingsAndDates() {
        StringBuilder sb = new StringBuilder();
        if (getSummaryHoldings() != null) {
            sb.append(getSummaryHoldings());
        }
        TextParserHelper.appendMaybeAddComma(sb, getDates());
        return sb.length() == 0 ? null : sb.toString();
    }

    String getAdditionalText();

    String getDates();

    List<Link> getLinks();

    String getPublisher();

    String getSummaryHoldings();

    boolean hasGetPasswordLink();

    boolean isProxy();
}
