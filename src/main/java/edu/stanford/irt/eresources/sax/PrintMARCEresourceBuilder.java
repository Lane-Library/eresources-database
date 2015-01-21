package edu.stanford.irt.eresources.sax;

import java.util.Collection;

public class PrintMARCEresourceBuilder extends MARCEresourceBuilder {

    @Override
    protected void maybeSetInstruction(SAXLink link, String instruction) {
        // do nothing
    }

    @Override
    protected void createCustomTypes(final SAXEresource eresource) {
        this.currentEresource.addType("print");
        Collection<String> types = eresource.getTypes();
        if (types.contains("periodical") || types.contains("newspaper") || types.contains("periodicals")
                || types.contains("newspapers")) {
            eresource.addType("ej");
        }
        if (types.contains("decision support techniques") || types.contains("calculators, clinical")
                || types.contains("algorithms")) {
            eresource.addType("cc");
        }
        if (types.contains("digital video") || types.contains("digital video, local")
                || types.contains("digital video, local, public") || types.contains("digital videos")
                || types.contains("digital videos, local") || types.contains("digital videos, local, public")) {
            eresource.addType("video");
        }
        if (types.contains("book set") || types.contains("book sets") || types.contains("books")) {
            eresource.addType("book");
        }
        if (types.contains("databases")) {
            eresource.addType("database");
        }
    }

    @Override
    protected void maybeAddCatalogLink() {
        SAXVersion version = new SAXVersion();
        SAXLink link = new SAXLink();
        link.setLabel("Lane Catalog record");
        link.setUrl("http://lmldb.stanford.edu/cgi-bin/Pwebrecon.cgi?BBID=" + this.currentEresource.getRecordId());
        version.addLink(link);
        this.currentEresource.addVersion(version);
    }

    @Override
    protected void maybeAddSubset(final String subset) {
        // no subsets for print
    }

    @Override
    protected void setRecordType() {
        this.currentEresource.setRecordType("print");
    }
}
