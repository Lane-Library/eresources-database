package edu.stanford.irt.eresources.sax;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

public class SAXVersion implements Version {

    private static final Set<String> ALLOWED_SUBSETS = new HashSet<String>();

    private static final String[] ALLOWED_SUBSETS_INITIALIZER = { "mobile applications", "pda tools",
        "mobile resources", "biotools" };
    static {
        for (String subset : ALLOWED_SUBSETS_INITIALIZER) {
            ALLOWED_SUBSETS.add(subset);
        }
    }

    private String dates;

    private String description;

    private boolean hasGetPasswordLink = false;

    private boolean isProxy = true;

    private List<Link> links;

    private String publisher;

    private Set<String> subsets;

    private String summaryHoldings;

    public void addLink(final Link link) {
        if (null == this.links) {
            this.links = new LinkedList<Link>();
        }
        this.links.add(link);
    }

    public void addSubset(final String subset) {
        if (ALLOWED_SUBSETS.contains(subset)) {
            if (null == this.subsets) {
                this.subsets = new HashSet<String>();
            }
            this.subsets.add(subset);
        }
    }

    @Override
    public String getAdditionalText() {
        StringBuilder sb = new StringBuilder(" ");
        if (this.summaryHoldings != null) {
            sb.append(this.summaryHoldings);
        }
        maybeAppend(sb, this.dates);
        maybeAppend(sb, this.publisher);
        maybeAppend(sb, this.description);
        if (this.links != null && !this.links.isEmpty()) {
            Link firstLink = this.links.get(0);
            String label = firstLink.getLabel();
            if (sb.length() == 1 && label != null) {
                sb.append(label);
            }
            String instruction = firstLink.getInstruction();
            if (instruction != null) {
                if (sb.length() > 1) {
                    sb.append(", ");
                }
                sb.append(instruction);
            }
        }
        if (sb.length() > 1) {
            sb.append(" ");
        }
        return sb.toString();
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Version#getDates()
     */
    @Override
    public String getDates() {
        return this.dates;
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Version#getDescription()
     */
    @Override
    public String getDescription() {
        return this.description;
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
     * @see edu.stanford.irt.eresources.Version#getSubsets()
     */
    @Override
    public Collection<String> getSubsets() {
        if (null == this.subsets) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(this.subsets);
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
