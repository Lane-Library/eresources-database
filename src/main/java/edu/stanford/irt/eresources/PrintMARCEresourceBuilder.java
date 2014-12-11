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
        this.currentEresource.addType("Print");
        Collection<String> types = eresource.getTypes();
        if (types.contains("Periodicals")
                || types.contains("Newspapers")) {
            eresource.addType("Journal");
        }
        if (types.contains("Decision Support Techniques") || types.contains("Calculators, Clinical")
                || types.contains("Algorithms")) {
            eresource.addType("Clinical Decision Tools");
        }
        if (types.contains("Digital Video") || types.contains("Digital Video, Local")
                || types.contains("Digital Video, Local, Public")) {
            eresource.addType("Video");
        }
        if (types.contains("Book Sets") || types.contains("Books")) {
            eresource.addType("Book");
        }
        if (types.contains("Databases")) {
            eresource.addType("Database");
        }
    }

    @Override
    protected void maybeAddCatalogLink() {
        Link link = new Link();
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
