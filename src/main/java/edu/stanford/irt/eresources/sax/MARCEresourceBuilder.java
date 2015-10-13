/**
 *
 */
package edu.stanford.irt.eresources.sax;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.ItemCount;
import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

/**
 * @author ceyates
 */
public class MARCEresourceBuilder extends DefaultHandler implements EresourceBuilder {

    protected static final int THIS_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    private static final Pattern ACCEPTED_YEAR_PATTERN = Pattern.compile("^\\d[\\d|u]{3}$");

    private static final String BIOTOOLS = "biotools";

    private static final String CONTROLFIELD = "controlfield";

    private static final String DATAFIELD = "datafield";

    private static final String RECORD = "record";

    private static final Pattern SPACE_SLASH = Pattern.compile(" /");

    private static final String SUBFIELD = "subfield";

    protected AuthTextAugmentation authTextAugmentation;

    protected String code;

    protected StringBuilder content = new StringBuilder();

    protected SAXEresource currentEresource;

    protected SAXLink currentLink;

    protected StringBuilder currentText = new StringBuilder();

    protected SAXVersion currentVersion;

    protected StringBuilder description505 = new StringBuilder();

    protected StringBuilder description520 = new StringBuilder();

    protected StringBuilder editionOrVersion = new StringBuilder();

    protected EresourceHandler eresourceHandler;

    protected boolean hasPreferredTitle = false;

    protected String ind1;

    protected String ind2;

    protected boolean isBib;

    protected boolean isMfhd;

    protected StringBuilder preferredTitle = new StringBuilder();

    protected String q;

    protected boolean recordHasError = false;

    protected String tag;

    protected StringBuilder title = new StringBuilder();

    protected Date updated;

    protected String z;

    private int countOf773;

    private int countOf866;

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private ItemCount itemCount;

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
            this.currentEresource.setItemCount(this.itemCount.itemCount(this.currentEresource.getRecordId()));
            handlePreviousRecord();
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
                this.currentVersion = new SAXVersion();
            } else {
                this.isBib = true;
                this.isMfhd = false;
                if (null != this.currentEresource) {
                    this.currentEresource.setUpdated(this.updated);
                    this.updated = null;
                    this.currentEresource.setItemCount(this.itemCount.itemCount(this.currentEresource.getRecordId()));
                    handlePreviousRecord();
                }
                this.currentEresource = new SAXEresource();
                setRecordType();
                // this.currentEresource.addType("Catalog");
            }
        } else if (RECORD.equals(name)) {
            if (this.isMfhd) {
                this.currentEresource.addVersion(this.currentVersion);
                StringBuilder combinedKeywords = new StringBuilder();
                combinedKeywords.append(this.currentEresource.getKeywords());
                combinedKeywords.append(this.content.toString().replaceAll("\\s\\s+", " "));
                this.currentEresource.setKeywords(combinedKeywords.toString());
                this.content.setLength(0);
            } else if (this.isBib) {
                if (this.description520.length() > 0) {
                    this.currentEresource.setDescription(this.description520.toString());
                } else if (this.description505.length() > 0) {
                    this.currentEresource.setDescription(this.description505.toString());
                }
                this.description520.setLength(0);
                this.description505.setLength(0);
                this.currentEresource.setKeywords(this.content.toString().replaceAll("\\s\\s+", " "));
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

    @Override
    public void setEresourceHandler(final EresourceHandler eresourceHandler) {
        this.eresourceHandler = eresourceHandler;
    }

    public void setItemCount(final ItemCount itemCount) {
        this.itemCount = itemCount;
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
        if (SUBFIELD.equals(name)) {
            this.code = atts.getValue("code");
        } else if (DATAFIELD.equals(name)) {
            this.tag = atts.getValue("tag");
            this.ind1 = atts.getValue("ind1");
            this.ind2 = atts.getValue("ind2");
            if (this.isMfhd && "856".equals(this.tag)) {
                this.currentLink = new SAXLink();
                this.q = null;
                this.z = null;
            }
        } else if (CONTROLFIELD.equals(name)) {
            this.tag = atts.getValue("tag");
        } else if (RECORD.equals(name)) {
            this.isBib = false;
            this.isMfhd = false;
            this.countOf866 = 0;
            this.countOf773 = 0;
        }
    }

    protected void createCustomTypes(final SAXEresource eresource) {
        Collection<String> types = eresource.getTypes();
        if (types.contains("Software, Installed")) {
            if (types.contains("Statistics")) {
                eresource.addType("Statistics Software, Installed");
            }
            for (Version verzion : eresource.getVersions()) {
                Version version = verzion;
                if (version.getSubsets().contains(BIOTOOLS)) {
                    eresource.addType("Biotools Software, Installed");
                }
                // software installed in various locations have the location in
                // the label
                for (Link link : version.getLinks()) {
                    String label = link.getLabel();
                    if (label.indexOf("Redwood") == 0) {
                        eresource.addType("Software, Installed - Redwood Room");
                    } else if (label.indexOf("Stone") == 0) {
                        eresource.addType("Software, Installed - Stone Room");
                    } else if (label.indexOf("Duck") == 0) {
                        eresource.addType("Software, Installed - Duck Room");
                    } else if (label.indexOf("M051") == 0) {
                        eresource.addType("Software, Installed - M051");
                    } else if (label.indexOf("Public") == 0) {
                        eresource.addType("Software, Installed - LKSC Public");
                    } else if (label.indexOf("Student") == 0) {
                        eresource.addType("Software, Installed - LKSC Student");
                    }
                }
            }
        }
    }

    protected void handleBibData(final String name) {
        if (SUBFIELD.equals(name)) {
            handleBibSubfield();
        } else if (CONTROLFIELD.equals(name)) {
            handleBibControlfield();
        } else if (DATAFIELD.equals(name)) {
            handleBibDatafield();
        }
    }

    protected void handleBibSubfield() {
        if ("655".equals(this.tag) && "a".equals(this.code)) {
            String type = this.currentText.toString();
            type = maybeStripTrailingPeriod(type);
            this.currentEresource.addType(type);
            if ("Core Material".equals(type)) {
                this.currentEresource.setIsCore(true);
            }
            if ("4".equals(this.ind1) && "7".equals(this.ind2)) {
                this.currentEresource.setPrimaryType(type);
            }
        } else if ("650".equals(this.tag) && "a".equals(this.code) && "2356".indexOf(this.ind2) > -1) {
            String mesh = maybeStripTrailingPeriod(this.currentText.toString());
            this.currentEresource.addMeshTerm(mesh);
        } else if ("651".equals(this.tag) && "a".equals(this.code) && "7".equals(this.ind2)) {
            String mesh = maybeStripTrailingPeriod(this.currentText.toString());
            this.currentEresource.addMeshTerm(mesh);
        } else if ("245".equals(this.tag) && (null == this.currentEresource.getTitle())) {
            if ("abnpq".indexOf(this.code) > -1) {
                if (this.title.length() > 0) {
                    this.title.append(' ');
                }
                if ("b".equals(this.code)) {
                    // remove trailing slash from subtitle (subfield b)
                    String data = this.currentText.toString();
                    data = SPACE_SLASH.matcher(data).replaceFirst("");
                    this.title.append(data);
                } else {
                    this.title.append(this.currentText);
                }
            } else if ("c".equals(this.code)) {
                this.currentEresource.setAuthor(this.currentText.toString());
            }
        } else if ("249".equals(this.tag) && (!this.hasPreferredTitle)) {
            if ("abnpq".indexOf(this.code) > -1) {
                if (this.preferredTitle.length() > 0) {
                    this.preferredTitle.append(' ');
                }
                if ("b".equals(this.code)) {
                    // remove trailing slash from subtitle (subfield b)
                    String data = this.currentText.toString();
                    data = SPACE_SLASH.matcher(data).replaceFirst("");
                    this.preferredTitle.append(data);
                } else {
                    this.preferredTitle.append(this.currentText);
                }
            }
        } else if ("250".equals(this.tag) && "a".equals(this.code)) {
            this.editionOrVersion.append(". ");
            this.editionOrVersion.append(this.currentText);
        } else if ("035".equals(this.tag) && "a".equals(this.code) && (this.currentText.indexOf("(Bassett)") == 0)) {
            this.currentEresource.addType("Bassett");
        } else if ("520".equals(this.tag)) {
            if (this.description520.length() > 0) {
                this.description520.append(' ');
            }
            this.description520.append(this.currentText.toString());
        } else if ("505".equals(this.tag)) {
            if (this.description505.length() > 0) {
                this.description505.append(' ');
            }
            this.description505.append(this.currentText.toString());
        }
        if ("650".equals(this.tag) && "0".equals(this.code)) {
            String authText = this.authTextAugmentation.getAuthAugmentations(this.currentText.toString());
            if (authText != null && authText.length() > 0) {
                this.content.append(' ').append(authText).append(' ');
            }
        }
        if (("100".equals(this.tag) || "700".equals(this.tag)) && "a".equals(this.code)) {
            String auth = this.currentText.toString().replaceFirst(",$", "");
            if (auth.endsWith(".") && !auth.matches(".* \\w\\.")) {
                auth = auth.substring(0, auth.length() - 1);
            }
            this.currentEresource.addPublicationAuthor(auth);
        }
        if (("100".equals(this.tag) || "600".equals(this.tag) || "700".equals(this.tag)) && "0".equals(this.code)) {
            String authText = this.authTextAugmentation.getAuthAugmentations(this.currentText.toString());
            if (authText != null && authText.length() > 0) {
                this.content.append(' ').append(authText).append(' ');
            }
        }
        if ("041".equals(this.tag)) {
            this.currentEresource.addPublicationLanguage(this.currentText.toString());
        }
        if ("830".equals(this.tag) && "a".equals(this.code)) {
            String suba = this.currentText.toString().toLowerCase();
            if (suba.contains("stanford") && suba.contains("grand rounds")) {
                this.currentEresource.addType("Grand Rounds");
            }
        }
        if ("773".equals(this.tag)) {
            if (this.countOf773 == 0) {
                if ("tp".indexOf(this.code) > -1) {
                    this.currentEresource.setPublicationTitle(this.currentText.toString());
                }
            } else if (this.countOf773 > 0) {
                if ("t".equals(this.code)) {
                    this.currentEresource.setPublicationText(this.currentEresource.getPublicationText() + "; "
                            + this.currentText.toString());
                }
            }
            if ("dg".indexOf(this.code) > -1) {
                this.currentEresource.setPublicationText(this.currentEresource.getPublicationText() + " "
                        + this.currentText.toString());
            }
            if ("w".equals(this.code)) {
                this.countOf773++;
            }
        }
    }

    protected void handleMfhdData(final String name) {
        if (SUBFIELD.equals(name)) {
            handleMfhdSubfield();
        } else if (CONTROLFIELD.equals(name)) {
            handleMfhdControlfield();
        } else if (DATAFIELD.equals(name)) {
            handleMfhdDatafield();
        }
    }

    protected void handleMfhdSubfield() {
        if ("655".equals(this.tag) && "a".equals(this.code) && (this.currentText.indexOf("Subset, ") == 0)) {
            String subset = this.currentText.toString().substring(8).toLowerCase();
            if ("proxy".equals(subset)) {
                this.currentVersion.setProxy(true);
            } else if ("noproxy".equals(subset)) {
                this.currentVersion.setProxy(false);
            } else {
                maybeAddSubset(subset);
            }
        } else if ("844".equals(this.tag) && "a".equals(this.code)) {
            this.currentVersion.setPublisher(this.currentText.toString());
        } else if ("866".equals(this.tag) && this.countOf866 == 0) {
            if ("v".equals(this.code)) {
                String holdings = this.currentText.toString();
                holdings = holdings.replaceAll(" =", "");
                this.currentVersion.setSummaryHoldings(holdings);
            } else if ("y".equals(this.code)) {
                this.currentVersion.setDates(this.currentText.toString());
            } else if ("z".equals(this.code)) {
                this.currentVersion.setAdditionalText(this.currentText.toString());
            }
        } else if ("856".equals(this.tag)) {
            if ("q".equals(this.code) && (null == this.q)) {
                this.q = this.currentText.toString();
            } else if ("z".equals(this.code) && (null == this.z)) {
                this.z = this.currentText.toString();
            } else if ("u".equals(this.code)) {
                this.currentLink.setUrl(this.currentText.toString());
            } else if ("i".equals(this.code)) {
                maybeSetAdditionalText(this.currentLink, this.currentText.toString());
            }
        }
    }

    protected void maybeAddCatalogLink() {
        if (!"bib".equals(this.currentEresource.getRecordType())) {
            return;
        }
        boolean needsLink = true;
        for (Version version : this.currentEresource.getVersions()) {
            if (version.getLinks().size() > 0) {
                needsLink = false;
            }
        }
        if (needsLink) {
            SAXLink link = new SAXLink();
            link.setLabel("Lane Catalog Record");
            link.setUrl("http://lmldb.stanford.edu/cgi-bin/Pwebrecon.cgi?BBID=" + this.currentEresource.getRecordId());
            this.currentVersion.addLink(link);
            this.currentEresource.addVersion(this.currentVersion);
        }
    }

    protected void maybeAddSubset(final String subset) {
        this.currentVersion.addSubset(subset);
        if (BIOTOOLS.equals(subset)) {
            // subset, biotools will count as type: software
            this.currentEresource.addType("Software");
        }
    }

    protected void maybeSetAdditionalText(final SAXLink link, final String instruction) {
        link.setInstruction(instruction);
    }

    protected void setRecordType() {
        this.currentEresource.setRecordType("bib");
    }

    // Holdings
    // 852
    // Bibliographic
    // 010-099
    // Retain only, 020, 022, 030, 035, 050, 060
    // 100-899 [note that non-Roman script occurs in 880]
    // 900-999
    // Retain only: 901, 902, 903, 907^x, 907^y, 941, 942, 943 [907^x&y will
    // eventually be changed into 655 values]
    private boolean checkSaveContent() {
        try {
            int tagNumber = Integer.parseInt(this.tag);
            if (this.isMfhd) {
                return tagNumber == 852;
            } else if (this.isBib) {
                return ((tagNumber >= 100) && (tagNumber < 900)) || (tagNumber == 20) || (tagNumber == 22)
                        || (tagNumber == 30) || (tagNumber == 35) || (tagNumber == 50) || (tagNumber == 60)
                        || ((tagNumber >= 901) && (tagNumber <= 903)) || ((tagNumber >= 941) && (tagNumber <= 943))
                        || ((tagNumber == 907) && ("xy".indexOf(this.code) > -1));
            }
        } catch (NumberFormatException e) {
            return false;
        }
        return false;
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
            String lang = this.currentText.substring(35, 38);
            if (null != endDate) {
                this.currentEresource.setYear(Integer.parseInt(endDate));
            } else if (null != beginDate) {
                this.currentEresource.setYear(Integer.parseInt(beginDate));
            }
            if (null != lang) {
                this.currentEresource.addPublicationLanguage(lang);
            }
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
                    LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
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
        this.currentEresource.setTitle(maybeStripTrailingSlash(this.currentEresource.getTitle()));
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
        } else if ("866".equals(this.tag) && (++this.countOf866 > 1)) {
            this.currentVersion.setAdditionalText("");
        }
    }

    private void handlePreviousRecord() {
        createCustomTypes(this.currentEresource);
        maybeAddCatalogLink();
        if (!this.recordHasError) {
            this.eresourceHandler.handleEresource(this.currentEresource);
            if (this.hasPreferredTitle) {
                try {
                    SAXEresource clone = (SAXEresource) this.currentEresource.clone();
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

    // remove trailing periods, some probably should have them but
    // voyager puts them on everything :-(
    private String maybeStripTrailingPeriod(final String string) {
        int lastPeriod = string.lastIndexOf('.');
        if (lastPeriod >= 0) {
            int lastPosition = string.length() - 1;
            if (lastPeriod == lastPosition) {
                return string.substring(0, lastPosition);
            }
        }
        return string;
    }

    // remove trailing slashes on titles
    private String maybeStripTrailingSlash(final String string) {
        if (null != string && string.endsWith("/")) {
            return string.substring(0, string.length() - 1).trim();
        }
        return string;
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
