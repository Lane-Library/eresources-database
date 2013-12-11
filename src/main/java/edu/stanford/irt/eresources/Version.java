package edu.stanford.irt.eresources;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Version {

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

    public String getAdditionalText() {
        StringBuilder sb = new StringBuilder(" ");
        if (this.summaryHoldings != null) {
            sb.append(this.summaryHoldings);
        }
        maybeAppend(sb, this.dates);
        maybeAppend(sb, this.publisher);
        maybeAppend(sb, this.description);
        if (this.links != null && this.links.size() > 0) {
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

    public String getDates() {
        return this.dates;
    }

    public String getDescription() {
        return this.description;
    }

    public Collection<Link> getLinks() {
        if (null == this.links) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(this.links);
    }

    public String getPublisher() {
        return this.publisher;
    }

    public Collection<String> getSubsets() {
        if (null == this.subsets) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(this.subsets);
    }

    public String getSummaryHoldings() {
        return this.summaryHoldings;
    }

    public boolean getHasGetPasswordLink() {
        return this.hasGetPasswordLink;
    }

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
