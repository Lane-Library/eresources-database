package edu.stanford.irt.eresources;


public abstract class AbstractLink implements Link {

    protected String getAdditionalText(final String instruction, final String publisher) {
        StringBuilder sb = new StringBuilder();
        if (instruction != null) {
            sb.append(" ").append(instruction);
        }
        if (publisher != null) {
            sb.append(" ").append(publisher);
        }
        return sb.toString();
    }

    protected String getLinkText(final String label, final Version version) {
        StringBuilder sb = new StringBuilder();
        if ("impact factor".equalsIgnoreCase(label)) {
            sb.append("Impact Factor");
        } else {
            String summaryHoldings = version.getSummaryHoldings();
            if (summaryHoldings != null && version.getLinks().size() == 1) {
                sb.append(summaryHoldings);
                String dates = version.getDates();
                if (dates != null && dates.length() > 0) {
                    sb.append(", ").append(dates);
                }
            } else {
                if (label != null) {
                    sb.append(label);
                }
            }
            if (sb.length() == 0) {
                sb.append(label);
            }
            String description = version.getDescription();
            if (description != null && description.length() > 0) {
                sb.append(" ").append(description);
            }
        }
        return sb.toString();
    }
}
