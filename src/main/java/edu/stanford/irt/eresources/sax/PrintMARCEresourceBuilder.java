/**
 * 
 */
package edu.stanford.irt.eresources.sax;

import java.util.Collection;

/**
 * @author ceyates
 */
public class PrintMARCEresourceBuilder extends MARCEresourceBuilder {

    @Override
    protected void createCustomTypes(final SAXEresource sAXEresource) {
        this.currentEresource.addType("print");
        Collection<String> types = sAXEresource.getTypes();
        if (types.contains("periodical") || types.contains("newspaper") || types.contains("periodicals")
                || types.contains("newspapers")) {
            sAXEresource.addType("ej");
        }
        if (types.contains("decision support techniques") || types.contains("calculators, clinical")
                || types.contains("algorithms")) {
            sAXEresource.addType("cc");
        }
        if (types.contains("digital video") || types.contains("digital video, local")
                || types.contains("digital video, local, public") || types.contains("digital videos")
                || types.contains("digital videos, local") || types.contains("digital videos, local, public")) {
            sAXEresource.addType("video");
        }
        if (types.contains("book set") || types.contains("book sets") || types.contains("books")) {
            sAXEresource.addType("book");
        }
        if (types.contains("databases")) {
            sAXEresource.addType("database");
        }
    }

    @Override
    protected void maybeAddCatalogLink() {
        SAXLink link = new SAXLink();
        link.setLabel("Lane Catalog record");
        link.setUrl("http://lmldb.stanford.edu/cgi-bin/Pwebrecon.cgi?BBID=" + this.currentEresource.getRecordId());
        this.currentVersion.addLink(link);
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
