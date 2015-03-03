package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.VersionComparator;

public class PrintMarcEresource extends BibMarcEresource {

    public PrintMarcEresource(final List<Record> recordList, final String keywords, final int[] items) {
        super(recordList, keywords, items);
    }

    @Override
    public String getRecordType() {
        return "print";
    }

    @Override
    protected Collection<String> doTypes() {
        Collection<String> types = super.doTypes();
        types.add("print");
        return types;
    }

    @Override
    protected List<Version> doVersions() {
        SortedSet<Version> versions = new TreeSet<Version>(new VersionComparator());
        versions.addAll(super.doVersions());
        versions.add(getCatalogVersion());
        return new ArrayList<Version>(versions);
    }

    @Override
    protected Version createVersion(Record record) {
        return new PrintMarcVersion(record);
    }

    private Version getCatalogVersion() {
        return new CatalogVersion(getRecordId());
    }

    @Override
    protected String getPrintOrDigital() {
        return "Print";
    }
}
