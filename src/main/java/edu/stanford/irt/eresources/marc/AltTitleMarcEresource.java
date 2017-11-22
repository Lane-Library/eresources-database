package edu.stanford.irt.eresources.marc;

import java.util.List;
import java.util.stream.Collectors;

import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

// TODO: create a TitleStrategy class
public class AltTitleMarcEresource extends BibMarcEresource {

    private int item;

    private Record record;

    public AltTitleMarcEresource(final List<Record> recordList, final String keywords, final TypeFactory typeFactory,
            final int[] itemCount, final int item) {
        super(recordList, keywords, itemCount, typeFactory);
        this.record = recordList.get(0);
        this.item = item;
    }

    @Override
    public String getId() {
        return super.getId() + "-clone-" + this.item;
    }

    @Override
    public String getSortTitle() {
        return getTitle();
    }

    @Override
    public String getTitle() {
        Field field = this.record.getFields().stream().filter(f -> "249".equals(f.getTag())).collect(Collectors.toList()).get(this.item - 1);
        return field.getSubfields().stream().filter(s -> s.getCode() == 'a').map(Subfield::getData).findFirst().orElse(null);
    }

    @Override
    public boolean isClone() {
        return true;
    }
}
