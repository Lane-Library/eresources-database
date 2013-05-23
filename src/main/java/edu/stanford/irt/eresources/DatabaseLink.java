package edu.stanford.irt.eresources;

import edu.stanford.irt.eresources.impl.LinkImpl;


public class DatabaseLink extends LinkImpl {
    
    private Version version;

    public String getLinkText() {
        StringBuilder sb = new StringBuilder();
        if ("impact factor".equalsIgnoreCase(getLabel())) {
            sb.append("Impact Factor");
        } else {
            String summaryHoldings = this.version.getSummaryHoldings();
            if (summaryHoldings != null && this.version.getLinks().size() == 1) {
                sb.append(summaryHoldings);
                String dates = this.version.getDates();
                if (dates != null && dates.length() > 0) {
                    sb.append(", ").append(dates);
                }
            } else {
                if (getLabel() != null) {
                    sb.append(getLabel());
                }
            }
            if (sb.length() == 0) {
                sb.append(getLabel());
            }
            String description = this.version.getDescription();
            if (description != null && description.length() > 0) {
                sb.append(" ").append(description);
            }
        }
        return sb.toString();
    }
    
    void setVersion(Version version) {
        this.version = version;
    }
}
