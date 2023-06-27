package edu.stanford.irt.eresources.marc;

import java.util.List;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

public class CatalogLink implements Link {

    public enum Type {
        BIB, SUL
    }

    private static final String BASE_URL = "https://searchworks.stanford.edu/view/";

    private String label;

    private String url;

    private Version version;

    public CatalogLink(final Type type, final String recordId, final Version version) {
        // raw FOLIO instance hrid is not stored in Solr, only the numeric portion is stored as recordId
        // idiosyncratic rules around SearchWorks/FOLIO ID prefixes:
        // - "in" for all Folio-created records (SUL or Lane) -->
        // - strip "a" from migrated SUL records -->
        // - "L" from migrated Lane records -->
        String prefix = "";
        if (null != recordId && recordId.startsWith("000")) {
            prefix = "in";
        } else if (type.equals(Type.BIB)) {
            prefix = "L";
        }
        this.label = "SU Catalog (SearchWorks)";
        if (type.equals(Type.BIB)) {
            this.label = "Lane Record in SearchWorks";
        }
        this.url = BASE_URL + prefix + recordId;
        this.version = version;
    }

    @Override
    public String getAdditionalText() {
        return null;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getLinkText() {
        StringBuilder sb = new StringBuilder();
        appendHoldingsAndDates(sb);
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
