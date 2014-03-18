/**
 * 
 */
package edu.stanford.irt.eresources;

import java.util.Collection;

/**
 * @author ceyates
 */
public class PrintMARCEresourceBuilder extends MARCEresourceBuilder {

    @Override
    protected void createCustomTypes(final Eresource eresource) {
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
        Link link = new Link();
        link.setLabel("Lane Catalog record");
        link.setUrl("http://lmldb.stanford.edu/cgi-bin/Pwebrecon.cgi?BBID=" + this.currentEresource.getRecordId());
        this.currentVersion.addLink(link);
        this.currentVersion.addLink(link);
    }

    @Override
    protected void maybeAddSubset(final String subset) {
    }

    @Override
    protected void setRecordType() {
        this.currentEresource.setRecordType("print");
    }
}
