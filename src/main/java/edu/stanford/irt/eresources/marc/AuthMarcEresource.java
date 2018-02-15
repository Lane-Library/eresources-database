package edu.stanford.irt.eresources.marc;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;

public class AuthMarcEresource extends BibMarcEresource {

    private static final int EMPTY_ITEM_COUNT_SIZE = 2;

    private static final int[] NO_ITEMS = new int[EMPTY_ITEM_COUNT_SIZE];

    private Record record;

    public AuthMarcEresource(final Record record, final KeywordsStrategy keywordsStrategy,
            final TypeFactory typeFactory) {
        super(Collections.singletonList(record), keywordsStrategy, null, typeFactory);
        this.record = record;
    }

    @Override
    public int[] getItemCount() {
        return NO_ITEMS;
    }

    @Override
    public Collection<String> getMeshTerms() {
        Collection<String> terms = super.getMeshTerms();
        if (terms == null) {
            return Collections.emptySet();
        }
        return terms;
    }

    @Override
    public String getRecordType() {
        return "auth";
    }

    @Override
    public List<Version> getVersions() {
        if (this.record.getFields().stream().anyMatch((final Field f) -> "856".equals(f.getTag()))) {
            return Collections.singletonList(new MarcVersion(this.record, this.record, this));
        }
        return Collections.emptyList();
    }

    @Override
    public int getYear() {
        int year;
        Optional<String> subfield943b = getSubfieldData(this.record, "943", "b").findFirst();
        if (subfield943b.isPresent()) {
            String value = subfield943b.get();
            if ("continuing".equalsIgnoreCase(value)) {
                year = THIS_YEAR;
            } else {
                year = Integer.parseInt(value);
            }
        } else {
            year = 0;
        }
        return year;
    }
}
