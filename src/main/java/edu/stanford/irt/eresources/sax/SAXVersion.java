package edu.stanford.irt.eresources.sax;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.stanford.irt.eresources.AbstractVersion;
import edu.stanford.irt.eresources.Link;

public class SAXVersion extends AbstractVersion {

    private String additionalText;

    private String dates;

    private String description;

    private boolean hasGetPasswordLink = false;

    private boolean isProxy = true;

    private List<Link> links;

    private String publisher;

    private String summaryHoldings;

    public void addLink(final Link link) {
        if (null == this.links) {
            this.links = new ArrayList<Link>();
        }
        this.links.add(link);
    }

    @Override
    public String getAdditionalText() {
        if (this.additionalText == null) {
            this.additionalText = createAdditionalText();
        }
        return this.additionalText;
    }

    @Override
    public String getDates() {
        return this.dates;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getHoldingsAndDates() {
        StringBuilder sb = new StringBuilder(" ");
        if (this.summaryHoldings != null) {
            sb.append(this.summaryHoldings);
        }
        maybeAppend(sb, this.dates);
        return sb.toString();
    }

    @Override
    public List<Link> getLinks() {
        if (null == this.links) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this.links);
    }

    @Override
    public String getPublisher() {
        return this.publisher;
    }

    @Override
    public String getSummaryHoldings() {
        return this.summaryHoldings;
    }

    @Override
    public boolean hasGetPasswordLink() {
        return this.hasGetPasswordLink;
    }

    @Override
    public boolean isProxy() {
        return this.isProxy;
    }

    public void setDates(final String dates) {
        this.dates = dates;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setHasGetPasswordLink(final boolean hasGetPasswordLink) {
        this.hasGetPasswordLink = hasGetPasswordLink;
    }

    public void setProxy(final boolean isProxy) {
        this.isProxy = isProxy;
    }

    public void setPublisher(final String publisher) {
        this.publisher = publisher;
    }

    public void setSummaryHoldings(final String summaryHoldings) {
        this.summaryHoldings = summaryHoldings;
    }

    private void maybeAppend(final StringBuilder sb, final String string) {
        if (string != null && string.length() > 0) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(string);
        }
    }
}
