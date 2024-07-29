package edu.stanford.irt.eresources.marc.sfx;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.marc.sul.SulMarcVersion;
import edu.stanford.lane.catalog.Record;

/**
 * MarcVersion encapsulates a holding record.
 */
public class SfxMarcVersion extends SulMarcVersion {

    public SfxMarcVersion(final Record bib, final Eresource eresource) {
        super(bib, eresource);
    }

    @Override
    // SFX export puts object ID in 866 ^z and we don't want that to show in UI
    public String getAdditionalText() {
        String additionalText = null;
        // might consider using note info from SFX, assuming it's user-friendly
        // List<Field> fields = getFields(this.bib, "866").toList();
        // if (fields.size() > 1) {
        // additionalText = "";
        // } else if (fields.size() == 1) {
        // additionalText = fields.get(0).getSubfields().stream().filter((final
        // Subfield s) -> 'z' == s.getCode())
        // .map(Subfield::getData).findFirst().orElse(null);
        // }
        return additionalText;
    }

}
