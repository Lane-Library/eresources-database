package edu.stanford.irt.eresources.marc.sfx;

import java.util.ArrayList;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.EresourceConstants;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.marc.KeywordsStrategy;
import edu.stanford.irt.eresources.marc.sul.SulMarcEresource;
import edu.stanford.irt.eresources.marc.sul.SulMarcVersion;
import edu.stanford.irt.eresources.marc.type.TypeFactory;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.lcsh.LcshMapManager;

public class SfxMarcEresource extends SulMarcEresource {

    public SfxMarcEresource(Record marcRecord, KeywordsStrategy keywordsStrategy, LcshMapManager lcshMapManager) {
        super(marcRecord, keywordsStrategy, lcshMapManager);
    }

    private static final Logger log = LoggerFactory.getLogger(SfxMarcEresource.class);

    @Override
    public String getRecordType() {
        return "sfx";
    }

    @Override
    public String getPrimaryType() {
        // assume everything is a book?
        return "Book Digital";
    }

    @Override
    public Collection<String> getTypes() {
        // assume everything is a book?
        Collection<String> types = new ArrayList<>();
        types.add("Book");
        types.add(getPrimaryType());
        return types;
    }

    // @Override
    // public String getPrimaryType() {
    //     if (getSubfieldData(this.marcRecord, "020", "a").findFirst().isPresent()) {
    //         this.primaryType = "Book Digital";
    //     }
    //     if (this.primaryType == null) {
    //         this.primaryType = TypeFactory.getPrimaryType(this.marcRecord);
    //     }
    //     return this.primaryType;
    // }

    @Override
    public String getRecordId() {
        return getSubfieldData(this.marcRecord, "090", "a").findFirst().orElse(null);
    }

    @Override
    protected Version createVersion(final Record holdingsRecord) {
        return new SfxMarcVersion(holdingsRecord, this);
    }

}
