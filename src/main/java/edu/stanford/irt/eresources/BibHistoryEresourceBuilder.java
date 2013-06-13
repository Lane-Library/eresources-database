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

/**
 * @author ceyates
 */
public class BibHistoryEresourceBuilder extends DefaultHandler implements EresourceBuilder {

    private String subfield0359;

//    private int field866Count;

    private String code;

    private StringBuilder content = new StringBuilder();

    private HistoryDatabaseEresource currentEresource;
    
    private DatabaseVersion currentVersion;

    private DatabaseLink currentLink;

    private StringBuilder currentText = new StringBuilder();

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private EresourceHandler eresourceHandler;

    private boolean hasPreferredTitle;

    private String ind1;

    private String ind2;

    private StringBuilder preferredTitle = new StringBuilder();

    private String q;

    private boolean recordHasError = false;

    private String tag;

    private StringBuilder title = new StringBuilder();

    private String titleDates;

    private String z;

//    private boolean isNoProxy;

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
            DatabaseLink link = new DatabaseLink();
            link.setLabel("catalog record");
            link.setUrl("http://lmldb.stanford.edu/cgi-bin/Pwebrecon.cgi?DB=local&Search_Arg=0359+" + this.subfield0359
                    + "&Search_Code=CMD*&CNT=10");
            this.currentVersion.addLink(link);
            this.currentEresource.setKeywords(this.content.toString());
            if (!this.recordHasError) {
                this.eresourceHandler.handleEresource(this.currentEresource);
                if (this.hasPreferredTitle) {
                    this.currentEresource.setTitle(this.preferredTitle.toString());
                    this.hasPreferredTitle = false;
                    this.preferredTitle.setLength(0);
                    this.eresourceHandler.handleEresource(this.currentEresource);
                }
            }
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
            this.currentVersion = new DatabaseVersion();
            this.currentEresource.addVersion(this.currentVersion);
            this.currentEresource.setRecordType("bib");
            this.subfield0359 = null;
            this.titleDates = null;
//            this.isNoProxy = false;
            this.content.setLength(0);
            this.recordHasError = false;
        }
        if ("subfield".equals(name)) {
            this.code = atts.getValue("code");
        } else if ("datafield".equals(name)) {
            this.tag = atts.getValue("tag");
            this.ind1 = atts.getValue("ind1");
            this.ind2 = atts.getValue("ind2");
            if ("856".equals(this.tag)) {
                this.currentLink = new DatabaseLink();
                this.q = null;
                this.z = null;
            }
        } else if ("controlfield".equals(name)) {
            this.tag = atts.getValue("tag");
        } else if ("record".equals(name)) {
//            this.field866Count = 0;
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
        } else if ("008".equals(this.tag)) {
            char field008_06 = this.currentText.charAt(6);
            String date1 = this.currentText.substring(7, 11).replaceAll("[u#y| ]", "?");
            String date2 = this.currentText.substring(11, 15).replaceAll("[u#y| ]", "?");
            if ("dikmq".indexOf(field008_06) > -1) {
                this.titleDates = date1 + '-' + date2;
            } else if ('c' == field008_06) {
                this.titleDates = date1 + '-';
            } else if ('s' == field008_06) {
                this.titleDates = date1;
            } else if ('r' == field008_06) {
                this.titleDates = date2;
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

    // <xsl:variable name="_008-06"
    // select="substring(preceding-sibling::slim:controlfield[@tag='008'],7,1)"/>
    // <xsl:variable name="date-1"
    // select="translate(substring(preceding-sibling::slim:controlfield[@tag='008'],8,4),'u#y|
    // ','?????')"/>
    // <xsl:variable name="date-2"
    // select="translate(substring(preceding-sibling::slim:controlfield[@tag='008'],12,4),'u#y|
    // ','?????')"/>
    // <xsl:variable name="date-string">
    // <xsl:choose>
    // <xsl:when test="contains('dikmq',$_008-06)"><xsl:value-of
    // select="concat($date-1,'-',$date-2)"/></xsl:when>
    // <xsl:when test="$_008-06='c'"><xsl:value-of
    // select="concat($date-1,'-')"/></xsl:when>
    // <xsl:when test="$_008-06='s'"><xsl:value-of select="$date-1"/></xsl:when>
    // <xsl:when test="$_008-06='r'"><xsl:value-of select="$date-2"/></xsl:when>
    // </xsl:choose>
    // </xsl:variable>
    private void handleBibDatafield() {
        if ("245".equals(this.tag) && (null == this.currentEresource.getTitle())) {
            if (null != this.titleDates) {
                this.title.append(' ').append(this.titleDates);
            }
            if ("0".equals(this.ind2)) {
                this.currentEresource.setTitle(this.title.toString());
            } else {
                try {
                    this.currentEresource.setTitle(this.title.substring(Integer.parseInt(this.ind2)));
                } catch (NumberFormatException e) {
                    this.currentEresource.setTitle(this.title.toString());
                }
            }
            this.title.setLength(0);
        } else if ("249".equals(this.tag) && (!this.hasPreferredTitle)) {
            this.hasPreferredTitle = true;
        }
    }

    // <xsl:template name="lane-types">
    // <xsl:if test="slim:datafield[@tag='655' and slim:subfield/text() =
    // 'Book'] or
    // slim:datafield[@tag='655' and slim:subfield/text() = 'Pamphlet'] or
    // slim:datafield[@tag='655' and slim:subfield/text() = 'Leaflet']">
    // <type>books</type>
    // </xsl:if>
    // <xsl:if test="slim:datafield[@tag='655' and
    // starts-with(slim:subfield/text(), 'Video')] or
    // slim:datafield[@tag='655' and slim:subfield/text() = 'Motion Picture']">
    // <type>movie</type>
    // </xsl:if>
    // </xsl:template>
    private void handleBibSubfield() {
        if ("655".equals(this.tag) && "a".equals(this.code)) {
            String type = this.currentText.toString().toLowerCase();
            if ((type.indexOf("subset") != 0) && !"internet resource".equals(type)) {
                this.currentEresource.addType(type);
                if ("motion picure".equals(type) || (type.indexOf("video") > -1)) {
                    this.currentEresource.addType("movie");
                } else if ("book set".equals(type) || "pamphlet".equals(type) || "leaflet".equals(type)) {
                    this.currentEresource.addType("book");
                }
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
//        } else if ("866".equals(this.tag) && (++this.field866Count > 1)) {
//            this.currentVersion.setDescription("");
        }
    }

    private void handleMfhdSubfield() {
//        if ("655".equals(this.tag) && "a".equals(this.code) && (this.currentText.indexOf("Subset, ") == 0)) {
//            String subset = this.currentText.toString().substring(8).toLowerCase();
//            if ("noproxy".equals(subset)) {
//                this.isNoProxy = true;
//            }
//        }
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
        if ("856".equals(this.tag)) {
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
