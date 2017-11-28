package edu.stanford.irt.eresources.marc;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Subfield;

public class AuthMarcEresource extends BibMarcEresource {

    private static final int[] NO_ITEMS = new int[2];

    private Record record;

    public AuthMarcEresource(final Record record, final String keywords,
            final TypeFactory typeFactory) {
        super(Collections.singletonList(record), keywords, NO_ITEMS, typeFactory);
        this.record = record;
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
        if (this.record.getFields()
                .stream()
                .anyMatch(f -> "856".equals(f.getTag()))) {
            return Collections.singletonList(new MarcVersion(this.record, this.record, this));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public int getYear() {
        int year;
        Optional<String> subfield943b = getFieldStream(this.record, "943")
                .flatMap(f -> f.getSubfields().stream())
                .filter(s -> s.getCode() == 'b')
                .map(Subfield::getData)
                .findFirst();
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
