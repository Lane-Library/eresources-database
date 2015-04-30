package edu.stanford.irt.eresources;

public abstract class AbstractVersion implements Version {

    @Override
    public String getHoldingsAndDates() {
        StringBuilder sb = new StringBuilder();
        String summaryHoldings = getSummaryHoldings();
        String dates = getDates();
        if (summaryHoldings != null) {
            sb.append(summaryHoldings);
        }
        if (dates != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(dates);
        }
        return sb.length() == 0 ? null : sb.toString();
    }
}
