/**
 *
 */
package edu.stanford.irt.eresources.sax;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.ItemCount;

/**
 * @author ceyates
 */
public class MARCEresourceBuilder extends DefaultHandler implements EresourceBuilder {

    protected enum RecordTypes {
        AUTH, BIB, MFHD
    }

    protected static final int THIS_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    private static final Pattern ACCEPTED_YEAR_PATTERN = Pattern.compile("^\\d[\\d|u]{3}$");

    private static final String CONTROLFIELD = "controlfield";

    private static final String DATAFIELD = "datafield";

    private static final int LEADER_BYTE_6 = 6;

    private static final String MULTI_SPACES = "\\s\\s+";

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

    protected StringBuilder dateForPrintSummaryHoldings = new StringBuilder();

    protected StringBuilder description505 = new StringBuilder();

    protected StringBuilder description520 = new StringBuilder();

    protected StringBuilder editionOrVersion = new StringBuilder();

    protected EresourceHandler eresourceHandler;

    protected boolean hasAbbreviatedTitle;

    protected String ind1;

    protected String ind2;

    protected List<String> preferredTitles = new ArrayList<>();

    protected String q;

    protected RecordTypes recordType;

    protected ReservesTextAugmentation reservesTextAugmentation;

    protected String tag = "000";

    protected String tagAndCode = "0000";

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
    }

    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        if ("leader".equals(name)) {
            if ("uvxy".indexOf(this.currentText.charAt(LEADER_BYTE_6)) > -1) {
                this.recordType = RecordTypes.MFHD;
                this.currentVersion = new SAXVersion();
            } else {
                this.recordType = RecordTypes.BIB;
                if ('q' == this.currentText.charAt(LEADER_BYTE_6)) {
                    this.recordType = RecordTypes.AUTH;
                    this.currentVersion = new SAXVersion();
                }
                if (null != this.currentEresource) {
                    this.currentEresource.setUpdated(this.updated);
                    this.updated = null;
                    this.currentEresource.setItemCount(this.itemCount.itemCount(this.currentEresource.getRecordId()));
                    handlePreviousRecord();
                }
                this.currentEresource = new SAXEresource();
                this.currentEresource.setRecordType(this.recordType.toString().toLowerCase(Locale.US));
            }
        } else if (RECORD.equals(name)) {
            String recordId = Integer.toString(this.currentEresource.getRecordId());
            if (this.recordType == RecordTypes.MFHD) {
                if (this.currentVersion.getLinks().isEmpty()) {
                    SAXLink link = new SAXLink();
                    link.setLabel("Lane Catalog Record");
                    link.setUrl("http://lmldb.stanford.edu/cgi-bin/Pwebrecon.cgi?BBID=" + recordId);
                    this.currentVersion.addLink(link);
                }
                maybeAddBibDates();
                this.currentEresource.addVersion(this.currentVersion);
                StringBuilder combinedKeywords = new StringBuilder();
                combinedKeywords.append(this.currentEresource.getKeywords());
                combinedKeywords.append(' ');
                combinedKeywords.append(this.content.toString().replaceAll(MULTI_SPACES, " "));
                this.currentEresource.setKeywords(combinedKeywords.toString());
                this.content.setLength(0);
            } else if (this.recordType == RecordTypes.AUTH) {
                this.currentEresource.addVersion(this.currentVersion);
                this.currentEresource.setKeywords(this.content.toString().replaceAll(MULTI_SPACES, " "));
                this.content.setLength(0);
            } else if (this.recordType == RecordTypes.BIB) {
                if (this.description520.length() > 0) {
                    this.currentEresource.setDescription(this.description520.toString());
                } else if (this.description505.length() > 0) {
                    this.currentEresource.setDescription(this.description505.toString());
                }
                this.description520.setLength(0);
                this.description505.setLength(0);
                StringBuilder combinedKeywords = new StringBuilder();
                combinedKeywords.append(this.content.toString().replaceAll(MULTI_SPACES, " "));
                combinedKeywords.append(' ');
                combinedKeywords.append(this.reservesTextAugmentation.getReservesAugmentations(recordId));
                this.currentEresource.setKeywords(combinedKeywords.toString());
                this.content.setLength(0);
            }
        } else if (this.recordType == RecordTypes.BIB) {
            handleBibData(name);
        } else if (this.recordType == RecordTypes.MFHD) {
            handleMfhdData(name);
        } else if (this.recordType == RecordTypes.AUTH) {
            // auths get mfhd handling so links are extracted
            handleAuthData(name);
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

    public void setReservesTextAugmentation(final ReservesTextAugmentation reservesTextAugmentation) {
        this.reservesTextAugmentation = reservesTextAugmentation;
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
            this.tagAndCode = this.tag + this.code;
        } else if (DATAFIELD.equals(name)) {
            this.tag = atts.getValue("tag");
            this.ind1 = atts.getValue("ind1");
            this.ind2 = atts.getValue("ind2");
            if ((this.recordType == RecordTypes.AUTH || this.recordType == RecordTypes.MFHD)
                    && "856".equals(this.tag)) {
                this.currentLink = new SAXLink();
                this.q = null;
                this.z = null;
            }
        } else if (CONTROLFIELD.equals(name)) {
            this.tag = atts.getValue("tag");
        } else if (RECORD.equals(name)) {
            this.recordType = null;
            this.countOf866 = 0;
            this.countOf773 = 0;
        }
    }

    protected void createCustomTypes(final SAXEresource eresource) {
        Collection<String> types = eresource.getTypes();
        String keywords = eresource.getKeywords().toLowerCase(Locale.US);
        if (types.contains("Software, Installed")) {
            if (keywords.contains("subset, biotools")) {
                eresource.addType("Biotools Software, Installed");
            }
            if (types.contains("Statistics")) {
                eresource.addType("Statistics Software, Installed");
            }
        }
    }

    protected void handleAuthData(final String name) {
        // treat auths like bibs with minor exceptions
        handleBibData(name);
        if (SUBFIELD.equals(name)) {
            handleAuthSubfield();
        }
    }

    protected void handleAuthSubfield() {
        if ("943".equals(this.tag) && "b".equals(this.code)) {
            String type = this.currentText.toString();
            if ("continuing".equalsIgnoreCase(type)) {
                this.currentEresource.setYear(THIS_YEAR);
            } else {
                this.currentEresource.setYear(Integer.parseInt(type));
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
        if ("655a".equals(this.tagAndCode)) {
            String type = this.currentText.toString();
            type = maybeStripTrailingPeriod(type);
            this.currentEresource.addType(type);
            if ("Core Material".equalsIgnoreCase(type)) {
                this.currentEresource.setIsCore(true);
            } else if ("LaneConnex".equalsIgnoreCase(type)) {
                this.currentEresource.setIsLaneConnex(true);
            }
            if ("4".equals(this.ind1) && "7".equals(this.ind2)) {
                this.currentEresource.setPrimaryType(type);
            }
        } else if (("650a".equals(this.tagAndCode) && "2356".indexOf(this.ind2) > -1)
                || ("651a".equals(this.tagAndCode) && "7".equals(this.ind2))) {
            String mesh = maybeStripTrailingPeriod(this.currentText.toString());
            this.currentEresource.addMeshTerm(mesh);
            if ("42".equals(this.ind1 + this.ind2)) {
                this.currentEresource.addBroadMeshTerm(mesh);
            }
        } else if ("245".equals(this.tag) && (null == this.currentEresource.getTitle())) {
            if ("abnpq".indexOf(this.code) > -1) {
                String data = this.currentText.toString();
                if ("b".equals(this.code)) {
                    // remove trailing slash from subtitle (subfield b)
                    data = SPACE_SLASH.matcher(data).replaceFirst("");
                    // prepend a space colon to subtitles if missing (we don't include ^h in title)
                    if (this.title.indexOf(":") != this.title.length() - 1) {
                        this.title.append(" :");
                    }
                }
                if (this.title.length() > 0) {
                    this.title.append(' ');
                }
                this.title.append(data);
            } else if ("c".equals(this.code)) {
                this.currentEresource.setPublicationAuthorsText(this.currentText.toString());
            } else if ("h".equals(this.code) && this.currentText.toString().contains("digital")) {
                this.currentEresource.setIsDigital(true);
            } else if ("h".equals(this.code) && this.currentText.toString().contains("videorecording")) {
                this.currentEresource.addType("Video");
            }
        } else if ("246".equals(this.tag)) {
            String data = this.currentText.toString();
            if ("i".equals(this.code) && "Acronym/initialism:".equalsIgnoreCase(data)) {
                this.hasAbbreviatedTitle = true;
            } else if ("a".equals(this.code)) {
                this.currentEresource.addAlternativeTitle(data);
                if (this.hasAbbreviatedTitle) {
                    this.currentEresource.addAbbreviatedTitle(data);
                    this.hasAbbreviatedTitle = false;
                }
            }
        } else if ("149a".equals(this.tagAndCode)) {
            this.currentEresource.setShortTitle(this.currentText.toString());
        } else if ((this.tag.matches("(130|210|247)")) && "a".equals(this.code)) {
            this.currentEresource.addAlternativeTitle(this.currentText.toString());
        } else if ("249a".equals(this.tagAndCode)) {
            this.preferredTitles.add(this.currentText.toString());
        } else if ("250a".equals(this.tagAndCode)) {
            this.editionOrVersion.append(". ");
            this.editionOrVersion.append(this.currentText);
        } else if ("035a".equals(this.tagAndCode) && this.currentText.indexOf("(Bassett)") == 0) {
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
        if ("6500".equals(this.tagAndCode)) {
            String authText = this.authTextAugmentation.getAuthAugmentations(this.currentText.toString());
            if (authText != null && authText.length() > 0) {
                this.content.append(' ').append(authText).append(' ');
            }
        }
        if ("100a".equals(this.tagAndCode) || "700a".equals(this.tagAndCode)) {
            String auth = this.currentText.toString().replaceFirst(",$", "");
            if (auth.endsWith(".") && !auth.matches(".* \\w\\.")) {
                auth = auth.substring(0, auth.length() - 1);
            }
            // case 115239: don't include journal editors
            if (!("700".equals(this.tag) && this.currentEresource.getPrimaryType().startsWith("Journal"))) {
                this.currentEresource.addPublicationAuthor(auth);
            }
        }
        // 100 or 600 or 700 ^0
        if (this.tagAndCode.matches("[167]000")) {
            String authText = this.authTextAugmentation.getAuthAugmentations(this.currentText.toString());
            if (authText != null && authText.length() > 0) {
                this.content.append(' ').append(authText).append(' ');
            }
        }
        if ("041".equals(this.tag)) {
            this.currentEresource.addPublicationLanguage(this.currentText.toString());
        }
        if ("830a".equals(this.tagAndCode)) {
            String suba = this.currentText.toString().toLowerCase(Locale.US);
            if (suba.contains("stanford") && suba.contains("grand rounds")) {
                this.currentEresource.addType("Grand Rounds");
            }
        }
        if ("773".equals(this.tag)) {
            if (this.countOf773 == 0) {
                if ("d".indexOf(this.code) > -1) {
                    this.currentEresource.setDate(this.currentText.toString().replaceAll("(:|;)", " "));
                }
                if ("tp".indexOf(this.code) > -1) {
                    this.currentEresource.setPublicationTitle(this.currentText.toString());
                }
            } else if (this.countOf773 > 0 && "t".equals(this.code)) {
                this.currentEresource.setPublicationText(
                        this.currentEresource.getPublicationText() + "; " + this.currentText.toString());
            }
            if ("dg".indexOf(this.code) > -1) {
                this.currentEresource.setPublicationText(
                        this.currentEresource.getPublicationText() + " " + this.currentText.toString());
            }
            if ("w".equals(this.code)) {
                this.countOf773++;
            }
        }
        if ("149d".equals(this.tagAndCode)
                || ("260c".equals(this.tagAndCode) && this.dateForPrintSummaryHoldings.length() == 0)) {
            this.dateForPrintSummaryHoldings.append(this.currentText);
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
        if ("844a".equals(this.tagAndCode)) {
            this.currentVersion.setPublisher(this.currentText.toString());
        } else if (this.countOf866 == 0 && "866".equals(this.tag)) {
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
            } else if (null == this.z && "z".equals(this.code)) {
                this.z = this.currentText.toString();
            } else if ("u".equals(this.code)) {
                this.currentLink.setUrl(this.currentText.toString());
            } else if ("i".equals(this.code)) {
                maybeSetAdditionalText(this.currentLink, this.currentText.toString());
            }
        }
    }

    protected void maybeAddCatalogLink() {
        // by default do nothing
    }

    protected void maybeSetAdditionalText(final SAXLink link, final String instruction) {
        // case 112154, 101898
        if (!"click link above for location/circulation status.".equalsIgnoreCase(instruction)) {
            link.setInstruction(instruction);
        }
    }

    // Holdings
    // 852, 866
    // Bibliographic
    // 010-099
    // Retain only, 020, 022, 030, 035
    // 100-899 [note that non-Roman script occurs in 880]
    // 900-999
    // Retain only: 901, 902, 903, 907^x, 907^y, 941, 942, 943 [907^x&y will
    // eventually be changed into 655 values]
    private boolean checkSaveContent() {
        int tagNumber = Integer.parseInt(this.tag);
        if (this.recordType == RecordTypes.MFHD) {
            return tagNumber == 852 || tagNumber == 866;
        } else if (this.recordType == RecordTypes.BIB) {
            return (tagNumber >= 100 && tagNumber < 900)
                    || "020 022 030 035 901 902 903 941 942 943".indexOf(this.tag) != -1
                    || (tagNumber == 907 && "xy".indexOf(this.code) > -1);
        } else if (this.recordType == RecordTypes.AUTH && tagNumber >= 100 && tagNumber <= 943) {
            return true;
        }
        return false;
    }

    private void handleBibControlfield() {
        if ("001".equals(this.tag)) {
            this.currentEresource.setId(this.currentEresource.getRecordType() + "-" + this.currentText.toString());
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
            this.currentEresource.setTitle(this.title.toString());
            if ("0".equals(this.ind2)) {
                this.currentEresource.setSortTitle(this.title.toString());
            } else {
                try {
                    this.currentEresource.setSortTitle(this.title.substring(Integer.parseInt(this.ind2)));
                } catch (StringIndexOutOfBoundsException | NumberFormatException e) {
                    LoggerFactory.getLogger(getClass()).warn(
                            "can't strip non-filing from title using ind2; eresource: {}", this.currentEresource, e);
                    this.currentEresource.setSortTitle(this.title.toString());
                }
            }
            this.title.setLength(0);
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
        this.dateForPrintSummaryHoldings.setLength(0);
        this.eresourceHandler.handleEresource(this.currentEresource);
        if (!this.preferredTitles.isEmpty()) {
            try {
                int cloned = 0;
                for (String preferredTitle : this.preferredTitles) {
                    cloned++;
                    SAXEresource clone = (SAXEresource) this.currentEresource.clone();
                    clone.setTitle(preferredTitle);
                    clone.setSortTitle(preferredTitle);
                    clone.setId(this.currentEresource.getId() + "-clone-" + cloned);
                    this.eresourceHandler.handleEresource(clone);
                }
                this.preferredTitles.clear();
            } catch (CloneNotSupportedException e) {
                throw new EresourceDatabaseException(e);
            }
        }
    }

    // pull date from bib when not present elsewhere; apply sparingly because it can be inaccurate for journal
    // holdings, repetitive on articles/chapters, unnecessary for impact factors
    private void maybeAddBibDates() {
        if (null == this.currentVersion.getDates() && null == this.currentVersion.getSummaryHoldings()
                && this.currentEresource.getPublicationText().isEmpty()
                && !"impact factor".equalsIgnoreCase(this.currentVersion.getLinks().get(0).getLabel())
                && this.currentEresource.getPrimaryType().matches("^(Book|Video).*")) {
            this.currentVersion.setDates(this.dateForPrintSummaryHoldings.toString());
            this.currentEresource.setKeywords(this.currentEresource.getKeywords() + " bibdatez");
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
        String parsedYear = null;
        Matcher yearMatcher = ACCEPTED_YEAR_PATTERN.matcher(year);
        if (yearMatcher.matches()) {
            parsedYear = year;
            if ("9999".equals(year)) {
                parsedYear = Integer.toString(THIS_YEAR);
            } else if (year.contains("u")) {
                int estimate = Integer.parseInt(year.replace('u', '5'));
                if (estimate > THIS_YEAR) {
                    estimate = THIS_YEAR;
                }
                parsedYear = Integer.toString(estimate);
            }
        }
        return parsedYear;
    }
}
