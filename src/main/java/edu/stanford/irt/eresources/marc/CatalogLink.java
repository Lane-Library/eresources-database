package edu.stanford.irt.eresources.marc;

import java.util.List;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

public class CatalogLink implements Link {

    private String additionalText = null;

    private String label = "Lane Catalog Record";

    private String url;

    private Version version;

    public CatalogLink(final String recordId, final Version version) {
        this.url = "http://lmldb.stanford.edu/cgi-bin/Pwebrecon.cgi?BBID=" + recordId;
        this.version = version;
    }

    @Override
    public String getAdditionalText() {
        return this.additionalText;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getLinkText() {
        StringBuilder sb = new StringBuilder();
        if ("impact factor".equalsIgnoreCase(this.label)) {
            sb.append("Impact Factor");
        } else {
            appendHoldingsAndDates(sb);
        }
        return sb.toString();
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public void setVersion(final Version version) {
        // not implemented
    }

    private void appendHoldingsAndDates(final StringBuilder sb) {
        String holdingsAndDates = this.version.getHoldingsAndDates();
        List<Link> links = this.version.getLinks();
        if (holdingsAndDates != null && links != null && links.size() == 1) {
            sb.append(holdingsAndDates);
        } else {
            if (this.label != null) {
                sb.append(this.label);
            }
        }
        if (sb.length() == 0) {
            sb.append(this.label);
        }
    }
}
