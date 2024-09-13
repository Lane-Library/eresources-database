package edu.stanford.irt.eresources.marc;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

/**
 * A Link that encapsulates the Folio Electronic Access object from which it is
 * derived.
 */
public class FolioLink implements Link {

    protected static final Pattern SU_AFFIL_AT = Pattern.compile(
            "(available[ -]?to[ -]?stanford[ -]?affiliated[ -]?users)([ -]?at)?[:;.]?", Pattern.CASE_INSENSITIVE);

    private Map<String, String> folioElectronicAccess;

    private Version version;

    public FolioLink(final Map<String, String> electronicAccess, final Version version) {
        this.folioElectronicAccess = electronicAccess;
        this.version = version;
    }

    @Override
    public String getAdditionalText() {
        String text = this.folioElectronicAccess.get("publicNote");
        return maybeRemoveAffiliation(text);
    }

    private String maybeRemoveAffiliation(final String text) {
        String reString = text;
        if (reString != null) {
            reString = SU_AFFIL_AT.matcher(text).replaceAll("").trim();
            if (reString.isBlank()) {
                return null;
            }
        }
        return reString;
    }

    @Override
    public String getLabel() {
        String label = this.folioElectronicAccess.get("materialsSpecification");
        if (label == null) {
            label = this.folioElectronicAccess.get("linkText");
        } else if (label.startsWith("(") && label.endsWith(")") && !"()".equals(label)) {
            label = label.substring(1, label.length() - 1);
        }
        return label;
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
     * A related resource link (856 42) will be down-sorted by version
     * comparator. See case LANEWEB-10642
     *
     * @return has 856 42
     */
    @Override
    public boolean isRelatedResourceLink() {
        return "Related resource".equals(this.folioElectronicAccess.get("name"));
    }

    /**
     * A related resource link (856 40) will be up-sorted by version comparator.
     * See case LANEWEB-10642
     *
     * @return has 856 40
     */
    @Override
    public boolean isResourceLink() {
        return "Resource".equals(this.folioElectronicAccess.get("name"));
    }

}
