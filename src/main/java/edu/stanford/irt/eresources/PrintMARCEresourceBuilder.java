/**
 * 
 */
package edu.stanford.irt.eresources;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * @author ceyates
 */
public class PrintMARCEresourceBuilder extends DefaultHandler implements EresourceBuilder {

    public static final int THIS_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    private static final Pattern ACCEPTED_YEAR_PATTERN = Pattern.compile("^\\d[\\d|u]{3}$");

    private int _866Count;

    private AuthTextAugmentation authTextAugmentation;

    private String code;

    private StringBuilder content = new StringBuilder();

    private DatabaseEresource currentEresource;

    private DatabaseLink currentLink;

    private StringBuilder currentText = new StringBuilder();

    private DatabaseVersion currentVersion;

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private StringBuilder description505 = new StringBuilder();

    private StringBuilder description520 = new StringBuilder();

    private StringBuilder editionOrVersion = new StringBuilder();

    private EresourceHandler eresourceHandler;

    private boolean hasPreferredTitle = false;

    private String ind1;

    private String ind2;

    private boolean isBib;

    private boolean isMfhd;

    private StringBuilder preferredTitle = new StringBuilder();

    private String q;

    private boolean recordHasError = false;

    private String tag;

    private StringBuilder title = new StringBuilder();

    private Date updated;

    private String z;

    @Override
    public void characters(final char[] chars, final int start, final int length) throws SAXException {
        this.currentText.append(chars, start, length);
        if (checkSaveContent()) {
            this.content.append(chars, start, length).append(' ');
        }
    }

    @Override
    public void endDocument() {
        if (null != this.currentEresource) {
            this.currentEresource.setUpdated(this.updated);
            createCustomTypes(this.currentEresource);
            if (!this.recordHasError) {
                this.eresourceHandler.handleEresource(this.currentEresource);
                if (this.hasPreferredTitle) {
                    try {
                        DatabaseEresource clone = (DatabaseEresource) this.currentEresource.clone();
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
        try {
            this.authTextAugmentation.save();
        } catch (IOException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        if ("leader".equals(name)) {
            if ("uvxy".indexOf(this.currentText.charAt(6)) > -1) {
                this.isMfhd = true;
                this.isBib = false;
                this.currentVersion = new DatabaseVersion();
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
                                DatabaseEresource clone = (DatabaseEresource) this.currentEresource.clone();
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
                this.currentEresource = new DatabaseEresource();
                this.currentEresource.setRecordType("print");
            }
        } else if ("record".equals(name)) {
            if (this.isMfhd) {
                DatabaseLink link = new DatabaseLink();
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

    public void setAuthTextAugmentation(final AuthTextAugmentation authTextAugmentation) {
        this.authTextAugmentation = authTextAugmentation;
    }

    public void setEresourceHandler(final EresourceHandler eresourceHandler) {
        this.eresourceHandler = eresourceHandler;
    }

    @Override
    public void startDocument() {
        if (null == this.eresourceHandler) {
            throw new IllegalStateException("null eresourceHandler");
        }
    }

    @Override
    public void startElement(final String uri, final String localName, final String name, final Attributes atts)
            throws SAXException {
        this.currentText.setLength(0);
        if ("subfield".equals(name)) {
            this.code = atts.getValue("code");
        } else if ("datafield".equals(name)) {
            this.tag = atts.getValue("tag");
            this.ind1 = atts.getValue("ind1");
            this.ind2 = atts.getValue("ind2");
            if (this.isMfhd && "856".equals(this.tag)) {
                this.currentLink = new DatabaseLink();
                this.q = null;
                this.z = null;
            }
        } else if ("controlfield".equals(name)) {
            this.tag = atts.getValue("tag");
        } else if ("record".equals(name)) {
            this.isBib = false;
            this.isMfhd = false;
            this._866Count = 0;
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
        if (!this.isBib) {
            return false;
        }
        try {
            int tagNumber = Integer.parseInt(this.tag);
            return ((tagNumber >= 100) && (tagNumber < 900)) || (tagNumber == 20) || (tagNumber == 22)
                    || (tagNumber == 30) || (tagNumber == 35) || ((tagNumber >= 901) && (tagNumber <= 903))
                    || ((tagNumber >= 941) && (tagNumber <= 943))
                    || ((tagNumber == 907) && ("xy".indexOf(this.code) > -1));
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private void createCustomTypes(final DatabaseEresource eresource) {
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

    private void handleBibControlfield() {
        if ("001".equals(this.tag)) {
            this.currentEresource.setRecordId(Integer.parseInt(this.currentText.toString()));
        } else if ("005".equals(this.tag)) {
            try {
                this.updated = this.dateFormat.parse(this.currentText.toString());
            } catch (ParseException e) {
                throw new EresourceDatabaseException(e);
            }
        } else if ("008".equals(this.tag)) {
            String endDate = parseYear(this.currentText.substring(11, 15));
            String beginDate = parseYear(this.currentText.substring(7, 11));
            if (null != endDate) {
                this.currentEresource.setYear(Integer.parseInt(endDate));
            } else if (null != beginDate) {
                this.currentEresource.setYear(Integer.parseInt(beginDate));
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
        if ("245".equals(this.tag)) {
            if ("0".equals(this.ind2)) {
                this.currentEresource.setTitle(this.title.toString());
            } else {
                try {
                    this.currentEresource.setTitle(this.title.substring(Integer.parseInt(this.ind2)));
                } catch (StringIndexOutOfBoundsException e) {
                    this.currentEresource.setTitle(this.title.toString());
                }
            }
            this.title.setLength(0);
        } else if ("249".equals(this.tag) && (!this.hasPreferredTitle)) {
            this.hasPreferredTitle = true;
        } else if ("250".equals(this.tag)) {
            this.currentEresource.setTitle(this.currentEresource.getTitle() + this.editionOrVersion);
            this.editionOrVersion.setLength(0);
        }
    }

    private void handleBibSubfield() {
        if ("655".equals(this.tag) && "a".equals(this.code)) {
            String type = this.currentText.toString().toLowerCase();
            // remove trailing periods, some probably should have them but
            // voyager puts them on everything :-(
            int lastPeriod = type.lastIndexOf('.');
            if (lastPeriod >= 0) {
                int lastPosition = type.length() - 1;
                if (lastPeriod == lastPosition) {
                    type = type.substring(0, lastPosition);
                }
            }
//            if (!"laneconnex".equals(type) && !"internet resource".equals(type) && type.indexOf("subset") != 0) {
                this.currentEresource.addType(type);
                if ("core material".equals(type)) {
                    this.currentEresource.setIsCore(true);
                }
//            }
        } else if ("650".equals(this.tag) && "a".equals(this.code) && "4".equals(this.ind1)
                && ("237".indexOf(this.ind2) > -1)) {
            String mesh = this.currentText.toString().toLowerCase();
            this.currentEresource.addMeshTerm(mesh);
        } else if ("245".equals(this.tag) && (null == this.currentEresource.getTitle())) {
            if ("anpq".indexOf(this.code) > -1) {
                if (this.title.length() > 0) {
                    this.title.append(' ');
                }
                this.title.append(this.currentText);
            }
        } else if ("249".equals(this.tag) && (!this.hasPreferredTitle)) {
            if ("anpq".indexOf(this.code) > -1) {
                if (this.preferredTitle.length() > 0) {
                    this.preferredTitle.append(' ');
                }
                this.preferredTitle.append(this.currentText);
            }
        } else if ("250".equals(this.tag) && "a".equals(this.code)) {
            this.editionOrVersion.append(". ");
            this.editionOrVersion.append(this.currentText);
        } else if ("520".equals(this.tag)) {
            this.description520.append(this.currentText.toString());
        } else if ("505".equals(this.tag)) {
            this.description505.append(this.currentText.toString());
        }
        if ("650".equals(this.tag) && "a".equals(this.code)) {
            String authText = this.authTextAugmentation.getAuthAugmentations(this.currentText.toString(), this.tag);
            if (authText != null && authText.length() > 0) {
                this.content.append(' ').append(authText).append(' ');
            }
        }
        if (("100".equals(this.tag) || "600".equals(this.tag) || "700".equals(this.tag)) && "a".equals(this.code)) {
            String authText = this.authTextAugmentation.getAuthAugmentations(this.currentText.toString(), this.tag);
            if (authText != null && authText.length() > 0) {
                this.content.append(' ').append(authText).append(' ');
            }
        }
    }

    private void handleMfhdControlfield() {
        if ("005".equals(this.tag)) {
            try {
                Date mfhdDate = this.dateFormat.parse(this.currentText.toString());
                if (mfhdDate.compareTo(this.updated) > 0) {
                    this.updated = mfhdDate;
                }
            } catch (ParseException e) {
                throw new EresourceDatabaseException(e);
            }
        }
    }

    private void handleMfhdData(final String name) {
        if ("subfield".equals(name)) {
            handleMfhdSubfield();
        } else if ("controlfield".equals(name)) {
            handleMfhdControlfield();
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
            if ("get password".equalsIgnoreCase(label)) {
                this.currentVersion.setHasGetPasswordLink(true);
            } else {
                this.currentVersion.addLink(this.currentLink);
            }
        } else if ("866".equals(this.tag) && (++this._866Count > 1)) {
            this.currentVersion.setDescription("");
        }
    }

    private void handleMfhdSubfield() {
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

    private String parseYear(final String year) {
        Matcher yearMatcher = ACCEPTED_YEAR_PATTERN.matcher(year);
        if (yearMatcher.matches()) {
            if ("9999".equals(year)) {
                return Integer.toString(THIS_YEAR);
            }
            return year.replace('u', '5');
        }
        return null;
    }
}
