/**
 * 
 */
package edu.stanford.irt.eresources;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.irt.eresources.impl.LinkImpl;

/**
 * @author ceyates
 */
public class AuthHistoryEresourceBuilder extends DefaultHandler implements EresourceBuilder {

    private String subfield0359;

    private String catalogLinkLabel;

    private String code;

    private StringBuilder content = new StringBuilder();

    private HistoryDatabaseEresource currentEresource;

    private LinkImpl currentLink;

    private StringBuilder currentText = new StringBuilder();

    private DatabaseVersion currentVersion;

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private EresourceHandler eresourceHandler;

    private boolean hasPreferredTitle;

    private String ind1;

    private String ind2;

    private StringBuilder preferredTitle = new StringBuilder();

    private String q;

    private boolean recordHasError;

    private String tag;

    private StringBuilder title = new StringBuilder();

    private String z;

    private boolean isNoProxy;

    @Override
    public void characters(final char[] chars, final int start, final int length) throws SAXException {
        this.currentText.append(chars, start, length);
        if (checkSaveContent()) {
            this.content.append(chars, start, length).append(' ');
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        if ("record".equals(name)) {
            DatabaseVersion version = new DatabaseVersion();
            LinkImpl link = new LinkImpl();
            link.setLabel(this.catalogLinkLabel + " record");
            link.setUrl("http://cifdb.stanford.edu/cgi-bin/Pwebrecon.cgi?DB=local&Search_Arg=0359+" + this.subfield0359
                    + "&Search_Code=CMD*&CNT=10");
            version.addLink(link);
            version.setProxy(false);
            this.currentEresource.addVersion(version);
            this.currentEresource.setKeywords(this.content.toString());
            if (!this.recordHasError) {
                this.eresourceHandler.handleEresource(this.currentEresource);
                if (this.hasPreferredTitle) {
                    this.currentEresource.setTitle(this.preferredTitle.toString());
                    this.hasPreferredTitle = false;
                    this.preferredTitle.setLength(0);
                    this.eresourceHandler.handleEresource(this.currentEresource);
                }
            } else {
                this.recordHasError = false;
            }
            this.isNoProxy = false;
            this.content.setLength(0);
        } else {
            try {
                handleBibData(name);
                handleMfhdData(name);
            } catch (EresourceDatabaseException e) {
                this.recordHasError = true;
            }
        }
    }

    public void setEresourceHandler(final EresourceHandler eresourceHandler) {
        this.eresourceHandler = eresourceHandler;
    }

    @Override
    public void startElement(final String uri, final String localName, final String name, final Attributes atts)
            throws SAXException {
        this.currentText.setLength(0);
        if ("record".equals(name)) {
            this.currentEresource = new HistoryDatabaseEresource();
            this.currentEresource.setRecordType("auth");
            this.currentVersion = new DatabaseVersion();
            this.subfield0359 = null;
            this.catalogLinkLabel = "catalog";
        }
        if ("subfield".equals(name)) {
            this.code = atts.getValue("code");
        } else if ("datafield".equals(name)) {
            this.tag = atts.getValue("tag");
            this.ind1 = atts.getValue("ind1");
            this.ind2 = atts.getValue("ind2");
            if ("856".equals(this.tag)) {
                this.currentLink = new LinkImpl();
                this.q = null;
                this.z = null;
            }
        } else if ("controlfield".equals(name)) {
            this.tag = atts.getValue("tag");
        }
    }

    // Bibliographic
    // 010-099
    // Retain only, 020, 022, 030, 035
    // 100-899 [note that non-Roman script occurs in 880]
    // 900-999
    // Retain only: 901, 902, 903, 907^x, 907^y, 941, 942, 943 [907^x&y will
    // eventually be changed into 655 values]
    private boolean checkSaveContent() {
        try {
            int tagNumber = Integer.parseInt(this.tag);
            return ((tagNumber >= 100) && (tagNumber < 900)) || (tagNumber == 20) || (tagNumber == 22) || (tagNumber == 30)
                    || (tagNumber == 35) || ((tagNumber >= 901) && (tagNumber <= 903))
                    || ((tagNumber >= 941) && (tagNumber <= 943)) || ((tagNumber == 907) && ("xy".indexOf(this.code) > -1));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void handleBibControlfield() {
        if ("001".equals(this.tag)) {
            this.currentEresource.setRecordId(Integer.parseInt(this.currentText.toString()));
        } else if ("005".equals(this.tag)) {
            try {
                this.currentEresource.setUpdated(this.dateFormat.parse(this.currentText.toString()));
            } catch (ParseException e) {
                throw new EresourceDatabaseException(e);
            }
        }
    }

    private void handleBibData(final String name) {
        if ("subfield".equals(name)) {
            handleBibSubfield();
        } else if ("controlfield".equals(name)) {
            handleBibControlfield();
        } else if ("datafield".equals(name)) {
            handleBibDatafield();
        }
    }

    private void handleBibDatafield() {
        if ("245".equals(this.tag) && (null == this.currentEresource.getTitle())) {
            if ("0".equals(this.ind2)) {
                this.currentEresource.setTitle(this.title.toString());
            } else {
                this.currentEresource.setTitle(this.title.substring(Integer.parseInt(this.ind2)));
            }
            this.title.setLength(0);
        } else if ("249".equals(this.tag) && (!this.hasPreferredTitle)) {
            this.hasPreferredTitle = true;
        }
    }

    private void handleBibSubfield() {
        if ("655".equals(this.tag) && "a".equals(this.code)) {
            String type = this.currentText.toString().toLowerCase();
            if ((type.indexOf("subset") != 0) && !"internet resource".equals(type)) {
                this.currentEresource.addType(type);
                if ((type.indexOf("person") == 0) || "peoples".equals(type)) {
                    this.currentEresource.addType("people");
                }
            }
            if (type.indexOf("person") == 0) {
                this.catalogLinkLabel = "person";
            } else if ("organization".equals(type)) {
                this.catalogLinkLabel = "organization";
            } else if ("event".equals(type)) {
                this.catalogLinkLabel = "event";
            }
        } else if ("650".equals(this.tag) && "a".equals(this.code) && "4".equals(this.ind1) && ("27".indexOf(this.ind2) > -1)) {
            String mesh = this.currentText.toString().toLowerCase();
            this.currentEresource.addMeshTerm(mesh);
        } else if ("245".equals(this.tag) && ("anpq".indexOf(this.code) > -1)) {
            if (this.title.length() > 0) {
                this.title.append(' ');
            }
            this.title.append(this.currentText);
        } else if ("249".equals(this.tag) && (!this.hasPreferredTitle)) {
            if ("anpq".indexOf(this.code) > -1) {
                if (this.preferredTitle.length() > 0) {
                    this.preferredTitle.append(' ');
                }
                this.preferredTitle.append(this.currentText);
            }
        } else if ("035".equals(this.tag) && "9".equals(this.code)) {
            this.subfield0359 = this.currentText.toString();
        }
    }

    private void handleMfhdData(final String name) {
        if ("subfield".equals(name)) {
            handleMfhdSubfield();
        } else if ("datafield".equals(name)) {
            handleMfhdDatafield();
        }
    }

    private void handleMfhdDatafield() {
        if ("856".equals(this.tag)) {
            String label = null;
            if (null != this.q) {
                label = this.q;
            } else if (null != this.z) {
                label = this.z;
            }
            if (null != label) {
                if ((label.indexOf('(') == 0) && (label.indexOf(')') == label.length() - 1) && (label.length() > 2)) {
                    label = label.substring(1, label.length() - 1);
                }
                this.currentLink.setLabel(label);
            }
            this.currentVersion.addLink(this.currentLink);
        }
    }

    private void handleMfhdSubfield() {
        if ("655".equals(this.tag) && "a".equals(this.code) && (this.currentText.indexOf("Subset, ") == 0)) {
            String subset = this.currentText.toString().substring(8).toLowerCase();
            if ("noproxy".equals(subset)) {
                this.isNoProxy = true;
            }
        }
        // else if ("844".equals(this.tag) && "a".equals(this.code)) {
        // this.currentVersion.setPublisher(this.currentText.toString());
        // } else if ("866".equals(this.tag)) {
        // if ("v".equals(this.code)) {
        // String holdings = this.currentText.toString();
        // holdings = holdings.replaceAll(" =", "");
        // this.currentVersion.setSummaryHoldings(holdings);
        // } else if ("y".equals(this.code)) {
        // this.currentVersion.setDates(this.currentText.toString());
        // } else if ("z".equals(this.code)) {
        // this.currentVersion.setDescription(this.currentText.toString());
        // }
        // }
        else if ("856".equals(this.tag)) {
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
