package edu.stanford.irt.eresources.sax;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.irt.eresources.Version;

public class SAXVersion implements Version {

    private String additionalText;

    private String dates;

    private boolean hasGetPasswordLink = false;

    private boolean isProxy = true;

    private List<Link> links;

    private String publisher;

    private String summaryHoldings;

    public void addLink(final Link link) {
        if (null == this.links) {
            this.links = new LinkedList<>();
        }
        this.links.add(link);
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Version#getDescription()
     */
    @Override
    public String getAdditionalText() {
        return this.additionalText;
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Version#getDates()
     */
    @Override
    public String getDates() {
        return this.dates;
    }

    public boolean getHasGetPasswordLink() {
        return hasGetPasswordLink();
    }

    @Override
    public String getHoldingsAndDates() {
        StringBuilder sb = new StringBuilder();
        if (this.summaryHoldings != null) {
            sb.append(this.summaryHoldings);
        }
        TextParserHelper.appendMaybeAddComma(sb, this.dates);
        return sb.length() == 0 ? null : sb.toString();
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Version#getLinks()
     */
    @Override
    public List<Link> getLinks() {
        if (null == this.links) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(this.links);
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Version#getPublisher()
     */
    @Override
    public String getPublisher() {
        return this.publisher;
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Version#getSummaryHoldings()
     */
    @Override
    public String getSummaryHoldings() {
        return this.summaryHoldings;
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Version#hasGetPasswordLink()
     */
    @Override
    public boolean hasGetPasswordLink() {
        return this.hasGetPasswordLink;
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Version#isProxy()
     */
    @Override
    public boolean isProxy() {
        return this.isProxy;
    }

    public void setAdditionalText(final String additionalText) {
        this.additionalText = additionalText;
    }

    public void setDates(final String dates) {
        this.dates = dates;
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
}
