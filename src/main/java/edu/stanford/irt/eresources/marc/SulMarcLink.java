package edu.stanford.irt.eresources.marc;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

/**
 *
 */
public class SulMarcLink extends MarcLink {

    private static final Pattern SU_AFFIL_AT = Pattern.compile(
            "(available[ -]?to[ -]?stanford[ -]?affiliated[ -]?users)([ -]?at)?[:;.]?", Pattern.CASE_INSENSITIVE);

    private static final Pattern SUL_PROXY_PREFIX = Pattern
            .compile("^https?://stanford\\.idm\\.oclc\\.org/login\\?url=", Pattern.CASE_INSENSITIVE);

    private Field field;

    private SulMarcVersion version;

    public SulMarcLink(final Field field, final Version version) {
        super(field, version);
        this.field = field;
        this.version = (SulMarcVersion) version;
    }

    @Override
    public String getLabel() {
        StringBuilder sb = new StringBuilder();
        List<String> strings = this.field.getSubfields().stream()
                .filter((final Subfield s) -> (s.getCode() == '3' || s.getCode() == 'z' || s.getCode() == 'y'))
                .map(Subfield::getData).collect(Collectors.toList());
        for (String string : strings) {
            if (SU_AFFIL_AT.matcher(string).matches()) {
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
        String l = sb.toString().trim();
        // empty returns null for consistency with MarcLink
        if (l.isEmpty()) {
            return null;
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
