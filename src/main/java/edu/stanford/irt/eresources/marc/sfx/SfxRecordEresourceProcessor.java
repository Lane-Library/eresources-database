package edu.stanford.irt.eresources.marc.sfx;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.validator.routines.ISBNValidator;

import edu.stanford.irt.eresources.AbstractEresourceProcessor;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.marc.KeywordsStrategy;
import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.irt.eresources.marc.RecordCollectionFactory;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.RecordCollection;
import edu.stanford.lane.catalog.TextHelper;
import edu.stanford.lane.lcsh.LcshMapManager;

public class SfxRecordEresourceProcessor extends AbstractEresourceProcessor {

    private EresourceHandler eresourceHandler;

    private KeywordsStrategy keywordsStrategy;

    private LcshMapManager lcshMapManager = new LcshMapManager();

    private RecordCollectionFactory recordCollectionFactory;

    private LaneDedupAugmentation laneDedupAugmentation;

    public SfxRecordEresourceProcessor(final EresourceHandler eresourceHandler, final KeywordsStrategy keywordsStrategy,
            final RecordCollectionFactory recordCollectionFactory, final LaneDedupAugmentation laneDedupAugmentation) {
        this.recordCollectionFactory = recordCollectionFactory;
        this.eresourceHandler = eresourceHandler;
        this.keywordsStrategy = keywordsStrategy;
        this.laneDedupAugmentation = laneDedupAugmentation;
    }

    @Override
    public void process() {
        RecordCollection rc = this.recordCollectionFactory.newRecordCollection(getStartTime());
        while (rc.hasNext()) {
            Record mr = rc.next();
            if (null != mr && hasIsbn(mr) && !isDuplicate(mr)) {
                this.eresourceHandler
                        .handleEresource(new SfxMarcEresource(mr, this.keywordsStrategy, this.lcshMapManager));
            }
        }
    }

    private boolean hasIsbn(final Record marcRecord) {
        return MARCRecordSupport.getFields(marcRecord, "020").count() > 0;
    }

    private boolean isDuplicate(final Record marcRecord) {
        Set<String> keys = new HashSet<>();
        for (String isbn : MARCRecordSupport.getSubfieldData(marcRecord, "020", "a").map(String::trim)
                .map(TextHelper::cleanIsxn).filter((final String s) -> !s.isEmpty()).collect(Collectors.toSet())) {
            keys.add(LaneDedupAugmentation.KEY_ISBN + LaneDedupAugmentation.SEPARATOR + isbn);
            if (isbn.length() == 10) {
                keys.add(LaneDedupAugmentation.KEY_ISBN + LaneDedupAugmentation.SEPARATOR
                        + ISBNValidator.getInstance().convertToISBN13(isbn));
            }
        }
        for (String entry : keys) {
            if (this.laneDedupAugmentation.isDuplicate(entry)) {
                return true;
            }
        }
        return false;
    }

}
