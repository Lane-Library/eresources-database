package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.marc4j.marc.Record;

import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.VersionComparator;


public class PrintMarcEresource extends BibMarcMarcEresource {

    @Override
    public String getRecordType() {
        return "print";
    }

    public PrintMarcEresource(Record record, List<Record> holdings, String keywords, int[] items) {
        super(record, holdings, keywords, items);
    }

    @Override
    protected List<Version> doVersions() {
        SortedSet<Version> versions = new TreeSet<Version>(new VersionComparator());
        versions.addAll(super.doVersions());
        versions.add(getCatalogVersion());
        return new ArrayList<Version>(versions);
    }

    @Override
    protected Collection<String> doTypes() {
        Collection<String> types = super.doTypes();
        types.add("print");
        return types;
    }

    private Version getCatalogVersion() {
        return new CatalogVersion(getRecordId());
    }
}
