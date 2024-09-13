package edu.stanford.irt.eresources.marc.sul;

import java.util.List;
import java.util.regex.Pattern;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

/**
 *
 */
public class SulMarcLink implements Link {

    private static final Pattern FOUR_DIGITS = Pattern.compile(".*\\b\\d{4}\\b.*");

    private static final Pattern SU_AFFIL_AT = Pattern.compile(
            "(available[ -]?to[ -]?stanford[ -]?affiliated[ -]?users)([ -]?at)?[:;.]?", Pattern.CASE_INSENSITIVE);

    private static final Pattern SUL_PROXY_PREFIX = Pattern
            .compile("^https?://stanford\\.idm\\.oclc\\.org/login\\?url=", Pattern.CASE_INSENSITIVE);

    private Field field;

    private SulMarcVersion version;

    public SulMarcLink(final Field field, final Version version) {
        this.field = field;
        this.version = (SulMarcVersion) version;
    }

    @Override
    public String getAdditionalText() {
        Subfield i = this.field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'i')
                .reduce((final Subfield a, final Subfield b) -> b).orElse(null);
        return i != null ? i.getData() : null;
    }

    @Override
    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        List<String> strings = this.field.getSubfields().stream()
                .filter((final Subfield s) -> (s.getCode() == '3' || s.getCode() == 'z' || s.getCode() == 'y'))
                .map(Subfield::getData).toList();
        for (String string : strings) {
            if (SU_AFFIL_AT.matcher(string).matches()) {
                this.version.setIsProxy(true);
                if (strings.size() > 1) {
                    // don't add "available to Stanford users" if there are more strings
                    string = "";
                } else {
                    // strip " at:" from "available to Stanford users"
                    string = SU_AFFIL_AT.matcher(string).replaceFirst("$1");
                }
            }
            sb.append(string).append(' ');
        }
        return sb.toString().trim();
    }

    @Override
    public String getLinkText() {
        StringBuilder sb = new StringBuilder();
        String l = getLabel();
        if (null != l) {
            sb.append(l);
        }
        String holdingsAndDates = this.version.getHoldingsAndDates();
        // since dates appear on links instead of the main record in laneweb, add dates when not already present
        if (holdingsAndDates != null && !FOUR_DIGITS.matcher(sb.toString()).matches()) {
            TextParserHelper.appendMaybeAddComma(sb, holdingsAndDates);
        }
        return sb.toString();
    }

    @Override
    public String getUrl() {
        // strip SUL proxy prefix from links
        String url = this.field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'u')
                .map(Subfield::getData).findFirst().orElse(null);
        if (null != url && SUL_PROXY_PREFIX.matcher(url).find()) {
            // remove setting proxy here? SUL uses SU_AFFIL_AT to toggle proxy prefix
            // but as of 2023-06-15 there were still hundreds of proxy prefixes in SUL records
            this.version.setIsProxy(true);
            url = SUL_PROXY_PREFIX.matcher(url).replaceFirst("");
        }
        return url;
    }

    /**
     * A related resource link (856 42) will be down-sorted by version comparator. See case LANEWEB-10642
     *
     * @return has 856 42
     */
    @Override
    public boolean isRelatedResourceLink() {
        return '4' == this.field.getIndicator1() && '2' == this.field.getIndicator2();
    }

    /**
     * A related resource link (856 40) will be up-sorted by version comparator. See case LANEWEB-10642
     *
     * @return has 856 40
     */
    @Override
    public boolean isResourceLink() {
        return '4' == this.field.getIndicator1() && '0' == this.field.getIndicator2();
    }

}
