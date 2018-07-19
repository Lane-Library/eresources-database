package edu.stanford.irt.eresources.marc;

import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

/**
 *
 */
public class SulMarcLink extends MarcLink {

    private Field field;

    public SulMarcLink(final Field field, final Version version) {
        super(field, version);
        this.field = field;
    }

    @Override
    public String getLabel() {
        String l = this.field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'q')
                .map(Subfield::getData).findFirst().orElse(null);
        if (l == null) {
            // SUL generally has 2 subfield z's
            // first is "Available to Stanford-affiliated users at:"
            // second if more meaningful version, publisher, etc.
            l = this.field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'z')
                    .map(Subfield::getData).reduce((a, b) -> b).orElse(null);
        }
        if (l != null && (l.indexOf('(') == 0) && (l.indexOf(')') == l.length() - 1) && (l.length() > 2)) {
            l = l.substring(1, l.length() - 1);
        }
        return l;
    }
}