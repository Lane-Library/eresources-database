package edu.stanford.irt.eresources.marc;

import java.util.List;
import java.util.Map;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

/**
 * A Link that encapsulates the Folio Electronic Access object from which it is derived.
 */
public class FolioLink implements Link {

    private Map<String, String> folioElectronicAccess;

    private Version version;

    public FolioLink(final Map<String, String> electronicAccess, final Version version) {
        this.folioElectronicAccess = electronicAccess;
        this.version = version;
    }

    @Override
    public String getAdditionalText() {
        return this.folioElectronicAccess.get("publicNote");
    }

    @Override
    public String getLabel() {
        return this.folioElectronicAccess.get("linkText");
    }

    @Override
    public String getLinkText() {
        StringBuilder sb = new StringBuilder();
        String l = getLabel();
        String holdingsAndDates = this.version.getHoldingsAndDates();
        List<Link> links = this.version.getLinks();
        if (holdingsAndDates != null && links != null && links.size() == 1) {
            sb.append(holdingsAndDates);
        } else {
            if (l != null) {
                sb.append(l);
            }
        }
        if (sb.length() == 0) {
            sb.append(l);
        }
        return sb.toString();
    }

    @Override
    public String getUrl() {
        return this.folioElectronicAccess.get("uri");
    }

    /**
     * A related resource link (856 42) will be down-sorted by version comparator. See case LANEWEB-10642
     *
     * @return has 856 42
     */
    @Override
    public boolean isRelatedResourceLink() {
        return "Related resource".equals(this.folioElectronicAccess.get("name"));
    }

    /**
     * A related resource link (856 40) will be up-sorted by version comparator. See case LANEWEB-10642
     *
     * @return has 856 40
     */
    @Override
    public boolean isResourceLink() {
        String name = this.folioElectronicAccess.get("name");
        // not sure if Resource or Version of resource is correct here?
        return "Version of resource".equals(name) || "Resource".endsWith(name);
    }

    @Override
    public void setVersion(final Version version) {
        // not implemented
    }
}
