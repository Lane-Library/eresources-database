package edu.stanford.irt.eresources.marc;

import java.util.List;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

/**
 * A Link that encapsulates the DataField from which it is derived.
 */
public class MarcLink implements Link {

    private Field field;

    private Version version;

    public MarcLink(final Field field, final Version version) {
        this.field = field;
        this.version = version;
    }

    @Override
    public String getAdditionalText() {
        Subfield i = this.field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'i')
                .reduce((final Subfield a, final Subfield b) -> b).orElse(null);
        String text = i != null ? i.getData() : null;
        if ("click link above for location/circulation status.".equalsIgnoreCase(text)) {
            text = null;
        }
        return text;
    }

    @Override
    public String getLabel() {
        String l = this.field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'q')
                .map(Subfield::getData).findFirst().orElse(null);
        if (l == null) {
            l = this.field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'z')
                    .map(Subfield::getData).findFirst().orElse(null);
        } else if (l.startsWith("(") && l.endsWith(")") && !"()".equals(l)) {
            l = l.substring(1, l.length() - 1);
        }
        return l;
    }

    @Override
    public String getLinkText() {
        StringBuilder sb = new StringBuilder();
        String l = getLabel();
        if ("impact factor".equalsIgnoreCase(l)) {
            sb.append("Impact Factor");
        } else {
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
        }
        return sb.toString();
    }

    @Override
    public String getUrl() {
        return this.field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'u')
                .map(Subfield::getData).findFirst().orElse(null);
    }

    /**
     * A related resource link (856 42) will be down-sorted by version comparator. See case LANEWEB-10642
     * @return has 856 42
     */
    @Override
    public boolean isRelatedResourceLink() {
        return '4' == this.field.getIndicator1() && '2' == this.field.getIndicator2();
    }

    /**
     * A related resource link (856 40) will be up-sorted by version comparator. See case LANEWEB-10642
     * @return has 856 40
     */
    @Override
    public boolean isResourceLink() {
        return '4' == this.field.getIndicator1() && '0' == this.field.getIndicator2();
    }

    @Override
    public void setVersion(final Version version) {
        // not implemented
    }
}
