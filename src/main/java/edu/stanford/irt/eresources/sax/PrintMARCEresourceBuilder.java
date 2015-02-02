package edu.stanford.irt.eresources.sax;

import java.util.Collection;

public class PrintMARCEresourceBuilder extends MARCEresourceBuilder {

    @Override
    protected void maybeSetInstruction(SAXLink link, String instruction) {
        // do nothing
    }

    @Override
    protected void createCustomTypes(final SAXEresource eresource) {
        this.currentEresource.addType("Print");
        Collection<String> types = eresource.getTypes();
        if (types.contains("Periodical") || types.contains("Newspaper") || types.contains("Periodicals")
                || types.contains("Newspapers")) {
            eresource.addType("Journal");
        }
        if (types.contains("Decision Support Techniques") || types.contains("Calculators, Clinical")
                || types.contains("Algorithms")) {
            eresource.addType("Clinical Decision Tools");
        }
        if (types.contains("Digital Video") || types.contains("Digital Video, Local")
                || types.contains("Digital Video, Local, Public") || types.contains("Digital Videos")
                || types.contains("Digital Videos, Local") || types.contains("Digital Videos, Local, Public")) {
            eresource.addType("Video");
        }
        if (types.contains("Book Set") || types.contains("Book Sets") || types.contains("Books")) {
            eresource.addType("Book");
        }
        if (types.contains("Databases")) {
            eresource.addType("Database");
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
