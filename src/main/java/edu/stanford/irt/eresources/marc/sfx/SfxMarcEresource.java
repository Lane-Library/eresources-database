package edu.stanford.irt.eresources.marc.sfx;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.marc.KeywordsStrategy;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.irt.eresources.marc.sul.SulMarcEresource;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;
import edu.stanford.lane.lcsh.LcshMapManager;

public class SfxMarcEresource extends SulMarcEresource {

    private LcshMapManager lcshMapManager;

    public SfxMarcEresource(Record marcRecord, KeywordsStrategy keywordsStrategy, LcshMapManager lcshMapManager) {
        super(marcRecord, keywordsStrategy, lcshMapManager);
        this.lcshMapManager = lcshMapManager;
    }

    @Override
    public Collection<String> getMeshTerms() {
        // pull in category info as "MeSH"; likely not super helpful but could help relevance a little?
        Collection<String> mesh = getSubfieldData(getFields(this.marcRecord, "650"), "a")
                .map(TextParserHelper::maybeStripTrailingPeriod).collect(Collectors.toSet());
        MARCRecordSupport.getFields(this.marcRecord, "650").forEach((final Field f) -> {
            StringBuilder sb = new StringBuilder();
            f.getSubfields().stream().filter((final Subfield sf) -> "ax".indexOf(sf.getCode()) > -1)
                    .forEach((final Subfield sf) -> {
                        if ('x' == sf.getCode()) {
                            sb.append("--");
                        }
                        sb.append(TextParserHelper.maybeStripTrailingPeriod(sf.getData()));
                    });
            mesh.addAll(this.lcshMapManager.getMeshForHeading(sb.toString()));
        });
        return mesh;
    }

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

    @Override
    public String getRecordId() {
        return getSubfieldData(this.marcRecord, "090", "a").findFirst().orElse(null);
    }

    @Override
    protected Version createVersion(final Record holdingsRecord) {
        return new SfxMarcVersion(holdingsRecord, this);
    }

}
