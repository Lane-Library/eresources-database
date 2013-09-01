package edu.stanford.irt.eresources;

public class Link {

    private String instruction;

    private String label;

    private String url;

    private Version version;

    public String getInstruction() {
        return this.instruction;
    }

    public String getLabel() {
        return this.label;
    }

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

    public String getUrl() {
        return this.url;
    }

    public void setInstruction(final String instruction) {
        this.instruction = instruction;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    void setVersion(final Version version) {
        this.version = version;
    }
}
