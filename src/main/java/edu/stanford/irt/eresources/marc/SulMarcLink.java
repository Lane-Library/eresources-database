package edu.stanford.irt.eresources.marc;

import java.util.regex.Pattern;

import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

/**
 *
 */
public class SulMarcLink extends MarcLink {

    private static final Pattern SU_AFFIL_AT = Pattern
            .compile("(available[ -]?to[ -]?stanford[ -]?affiliated[ -]?users)[ -]?at[:;.]?", Pattern.CASE_INSENSITIVE);

    private static final Pattern SUL_PROXY_PREFIX = Pattern
            .compile("^https?://stanford\\.idm\\.oclc\\.org/login\\?url=", Pattern.CASE_INSENSITIVE);

    private static final int TWO = 2;

    private Field field;

    private SulMarcVersion version;

    public SulMarcLink(final Field field, final Version version) {
        super(field, version);
        this.field = field;
        this.version = (SulMarcVersion) version;
    }

    @Override
    public String getLabel() {
        String l = this.field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'q')
                .map(Subfield::getData).findFirst().orElse(null);
        if (l == null) {
            // SUL generally has 2 subfield z's
            // first is "Available to Stanford-affiliated users at:"
            // last is more meaningful version, publisher, etc.
            l = this.field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'z')
                    .map(Subfield::getData).reduce((a, b) -> b).orElse(null);
        }
        if (l != null && (l.indexOf('(') == 0) && (l.indexOf(')') == l.length() - 1) && (l.length() > TWO)) {
            l = l.substring(1, l.length() - 1);
        }
        // strip " at:" from "available to Stanford users"
        if (l != null && SU_AFFIL_AT.matcher(l).matches()) {
            l = SU_AFFIL_AT.matcher(l).replaceFirst("$1");
        }
        return l;
    }

    @Override
    public String getUrl() {
        // strip SUL proxy prefix from links
        String url = super.getUrl();
        if (null != url && SUL_PROXY_PREFIX.matcher(url).find()) {
            this.version.setIsProxy(true);
            url = SUL_PROXY_PREFIX.matcher(url).replaceFirst("");
        }
        return url;
    }
}
