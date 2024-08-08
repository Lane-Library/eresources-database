package edu.stanford.irt.eresources.marc.sfx;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.marc.sul.SulMarcVersion;
import edu.stanford.lane.catalog.Record;

/**
 * MarcVersion encapsulates a holding record.
 */
public class SfxMarcVersion extends SulMarcVersion {

    private Eresource eresource;

    public SfxMarcVersion(final Record bib, final Eresource eresource) {
        super(bib, eresource);
        this.eresource = eresource;
    }

    @Override
    // SFX export puts object ID in 866 ^z and we don't want that to show in UI
    // SFX 866 ^x contains target name like "Elsevier SD ScienceDirect Available
    // Journals:Full Text"
    // neither is helpful for display
    public String getAdditionalText() {
        return null;
    }

    @Override
    public String getDates() {
        int year = this.eresource.getYear();
        if (year > 0) {
            return Integer.toString(year);
        }
        return null;
    }

}
