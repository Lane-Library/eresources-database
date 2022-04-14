package edu.stanford.irt.eresources;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public interface Version {

    String getAdditionalText();

    default String getCallnumber() {
        return null;
    }

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

    // no need to write zero values to solr
    @JsonInclude(Include.NON_DEFAULT)
    default int[] getItemCount() {
        return new int[0];
    }

    List<Link> getLinks();

    default String getLocationName() {
        return null;
    }

    default String getLocationUrl() {
        return null;
    }

    String getPublisher();

    String getSummaryHoldings();

    boolean isProxy();
}
