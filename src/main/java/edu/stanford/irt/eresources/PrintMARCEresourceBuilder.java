/**
 * 
 */
package edu.stanford.irt.eresources;

import java.util.Collection;

import org.xml.sax.SAXException;

/**
 * @author ceyates
 */
public class PrintMARCEresourceBuilder extends MARCEresourceBuilder {

    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        if ("leader".equals(name)) {
            if ("uvxy".indexOf(this.currentText.charAt(6)) > -1) {
                this.isMfhd = true;
                this.isBib = false;
                this.currentVersion = new Version();
            } else {
                this.isBib = true;
                this.isMfhd = false;
                if (null != this.currentEresource) {
                    this.currentEresource.setUpdated(this.updated);
                    this.updated = null;
                    createCustomTypes(this.currentEresource);
                    if (!this.recordHasError) {
                        this.eresourceHandler.handleEresource(this.currentEresource);
                        if (this.hasPreferredTitle) {
                            try {
                                Eresource clone = (Eresource) this.currentEresource.clone();
                                clone.setTitle(this.preferredTitle.toString());
                                this.hasPreferredTitle = false;
                                this.preferredTitle.setLength(0);
                                this.eresourceHandler.handleEresource(clone);
                            } catch (CloneNotSupportedException e) {
                                throw new EresourceDatabaseException(e);
                            }
                        }
                    } else {
                        this.recordHasError = false;
                    }
                }
                this.currentEresource = new Eresource();
                this.currentEresource.setRecordType("print");
            }
        } else if ("record".equals(name)) {
            if (this.isMfhd) {
                Link link = new Link();
                link.setLabel("Lane Catalog record");
                link.setUrl("http://lmldb.stanford.edu/cgi-bin/Pwebrecon.cgi?BBID="
                        + this.currentEresource.getRecordId());
                this.currentVersion.addLink(link);
                this.currentEresource.addVersion(this.currentVersion);
            } else if (this.isBib) {
                if (this.description520.length() > 0) {
                    this.currentEresource.setDescription(this.description520.toString());
                } else if (this.description505.length() > 0) {
                    this.currentEresource.setDescription(this.description505.toString());
                }
                this.description520.setLength(0);
                this.description505.setLength(0);
                this.currentEresource.setKeywords(this.content.toString());
                this.content.setLength(0);
            }
        } else if (this.isBib) {
            handleBibData(name);
        } else if (this.isMfhd) {
            handleMfhdData(name);
        }
    }

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
    protected void handleMfhdSubfield() {
        if ("655".equals(this.tag) && "a".equals(this.code) && (this.currentText.indexOf("Subset, ") == 0)) {
            String subset = this.currentText.toString().substring(8).toLowerCase();
            if ("proxy".equals(subset)) {
                this.currentVersion.setProxy(true);
            } else if ("noproxy".equals(subset)) {
                this.currentVersion.setProxy(false);
            }
        } else if ("844".equals(this.tag) && "a".equals(this.code)) {
            this.currentVersion.setPublisher(this.currentText.toString());
        } else if ("866".equals(this.tag)) {
            if ("v".equals(this.code)) {
                String holdings = this.currentText.toString();
                holdings = holdings.replaceAll(" =", "");
                this.currentVersion.setSummaryHoldings(holdings);
            } else if ("y".equals(this.code)) {
                this.currentVersion.setDates(this.currentText.toString());
            } else if ("z".equals(this.code)) {
                this.currentVersion.setDescription(this.currentText.toString());
            }
        } else if ("856".equals(this.tag)) {
            if ("q".equals(this.code) && (null == this.q)) {
                this.q = this.currentText.toString();
            } else if ("z".equals(this.code) && (null == this.z)) {
                this.z = this.currentText.toString();
            } else if ("u".equals(this.code)) {
                this.currentLink.setUrl(this.currentText.toString());
            } else if ("i".equals(this.code)) {
                this.currentLink.setInstruction(this.currentText.toString());
            }
        }
    }
}
