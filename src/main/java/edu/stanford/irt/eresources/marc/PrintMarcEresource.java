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
    public Collection<String> getTypes() {
        Collection<String> types = super.getTypes();
        types.add("print");
        return types;
    }

    @Override
    public List<Version> getVersions() {
        SortedSet<Version> versions = new TreeSet<Version>(new VersionComparator());
        versions.addAll(super.getVersions());
        versions.add(getCatalogVersion());
        return new ArrayList<Version>(versions);
    }

    @Override
    protected Version createVersion(final Record record) {
        return new PrintMarcVersion(record);
    }

    @Override
    protected String getPrintOrDigital() {
        return "Print";
    }

    private Version getCatalogVersion() {
        return new CatalogVersion(getRecordId());
    }
}
