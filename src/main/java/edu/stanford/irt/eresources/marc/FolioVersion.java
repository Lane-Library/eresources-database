package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.marc.CatalogLink.Type;
import edu.stanford.lane.catalog.FolioRecord;

/**
 * FolioVersion encapsulates a folio holding record (from /inventory-hierarchy/items-and-holdings, not
 * /holdings-storage/holdings).
 */
public class FolioVersion implements Version {

    private static final String ELECTRONIC_ACCESS = "electronicAccess";

    private static final String HOLDINGS_STATEMENTS = "holdingsStatements";

    private Eresource eresource;

    private Map<String, ?> folioHolding;

    private String locationName;

    private HTTPLaneLocationsService locationsService;

    private String locationUrl;

    private FolioRecord folioRecord;

    public FolioVersion(final FolioRecord folioRecord, final Map<String, ?> folioHolding, final Eresource eresource,
            final HTTPLaneLocationsService locationsService) {
        this.folioRecord = folioRecord;
        this.folioHolding = folioHolding;
        this.eresource = eresource;
        this.locationsService = locationsService;
    }

    @Override
    public String getAdditionalText() {
        // 866 ^z mapped to holdingsStatements.note
        // for print example of this, see NYTimes L89316
        String additionalText = null;
        List<Map<String, String>> statements = (List<Map<String, String>>) this.folioHolding.get(HOLDINGS_STATEMENTS);
        if (statements.size() > 1) {
            additionalText = "";
        } else if (statements.size() == 1) {
            additionalText = statements.get(0).get("note");
        }
        // LANEWEB-10982: show 931 notes but remove those referring to "Related
        // Title Browse"
        // 931 ^a is mapped to notes[].note when notes[].staffOnly == false
        List<Map<String, String>> notes = (List<Map<String, String>>) this.folioHolding.get("notes");
        for (Map<String, String> note : notes) {
            if (null != note && null != note.get("staffOnly") && note.get("staffOnly").equals("false")) {
                String noteText = note.get("note");
                if (null != noteText && !noteText.toLowerCase().contains("use the \"related title browse\" index")) {
                    additionalText = (null == additionalText) ? noteText : additionalText + " " + noteText;
                }
            }
        }
        return additionalText;
    }

    @Override
    public String getCallnumber() {
        return ((Map<String, String>) this.folioHolding.get("callNumber")).get("callNumber");
    }

    private boolean needToAddBibDates(final Eresource eresource) {
        return null == getSummaryHoldings() && eresource.getPublicationText().isEmpty()
                && BOOK_OR_VIDEO.matcher(eresource.getPrimaryType()).matches();
    }

    private static final Pattern BOOK_OR_VIDEO = Pattern.compile("^(Book|Video).*");

    @Override
    public String getDates() {
        String value = null;
        List<Map<String, String>> statements = (List<Map<String, String>>) this.folioHolding.get(HOLDINGS_STATEMENTS);
        if (!statements.isEmpty() && !statements.get(0).get("statement").isBlank()) {
            value = statements.get(0).get("statement");
            if (value.contains(" = ")) {
                String[] parts = value.split(" = ");
                value = parts[1];
            } else {
                value = null;
            }
        }
        // 260/4 ^c = publication.dateOfPublication
        if (null == value && needToAddBibDates(this.eresource)) {
            // jsonContext will have MARC for MARC instance and FOLIO for FOLIO
            // instance
            List<String> dates = this.folioRecord.jsonContext()
                    .read("$.instanceSource.fields[?(@['260'] || @['264'])].*.subfields.*.c");
            value = dates.stream().findFirst().orElse(null);
            value = null != value ? value
                    : this.folioRecord.jsonContext().read("$.instance.publication[0].dateOfPublication");
            value = null == value ? "" : value;
            value = TextParserHelper.maybeStripTrailingPeriod(value);
            value = TextParserHelper.maybeStripTrailingUnbalancedBracket(value);
        }
        return value;
    }

    @Override
    public int[] getItemCount() {
        int[] itemCount = new int[3];
        int total = (int) this.folioHolding.get(FolioRecord.TOTAL_ITEMS);
        int available = (int) this.folioHolding.get(FolioRecord.AVAILABLE_ITEMS);
        int out = (int) this.folioHolding.get(FolioRecord.OUT_ITEMS);
        itemCount[0] = total;
        itemCount[1] = available;
        itemCount[2] = out;
        return itemCount;
    }

    @Override
    public List<Link> getLinks() {
        List<Link> links = new ArrayList<>();
        if (!hasLinks()) {
            links.add(new CatalogLink(Type.BIB, this.eresource.getRecordId(), this));
        }
        Version version = this;
        List<Map<String, String>> electronicAccesses = (List<Map<String, String>>) this.folioHolding
                .get(ELECTRONIC_ACCESS);
        links.addAll(
                electronicAccesses.stream().map((final Map<String, String> ea) -> new FolioLink(ea, version)).toList());
        return links;
    }

    @Override
    public String getLocationCode() {
        Map<?, ?> effectiveLocation = ((Map<?, ?>) ((Map<?, ?>) this.folioHolding.get("location"))
                .get("effectiveLocation"));
        String locCode = (String) effectiveLocation.get("item-loc");
        if (null != locCode) {
            return locCode;
        }
        return (String) effectiveLocation.get("code");
    }

    @Override
    public String getLocationName() {
        if (null != this.locationName) {
            return this.locationName;
        }
        if (null != this.locationsService) {
            this.locationName = this.locationsService.getLocationName(getLocationCode());
        }
        return this.locationName;
    }

    @Override
    public String getLocationUrl() {
        if (null != this.locationUrl) {
            return this.locationUrl;
        }
        if (null != this.locationsService) {
            this.locationUrl = this.locationsService.getLocationUrl(getLocationCode());
        }
        return this.locationUrl;
    }

    @Override
    public String getPublisher() {
        String publisher = null;
        // 856 ^y is mapped to electronicAccess[0].linkText
        publisher = ((List<Map<String, String>>) this.folioHolding.get(ELECTRONIC_ACCESS)).stream()
                .map((final Map<String, String> ea) -> ea.get("linkText")).filter(Objects::nonNull).findFirst()
                .orElse(null);
        // both publisher and FolioLink.getLabel() can use 856 ^y (linkText); do
        // not duplicate publisher string
        // FolioLink.getLinkText() could be a candidate instead?
        String firstLinkLabel = this.getLinks().stream().map(Link::getLabel).filter(Objects::nonNull).findFirst()
                .orElse("");
        if (firstLinkLabel.equals(publisher)) {
            publisher = null;
        }
        return publisher;
    }

    @Override
    public String getSummaryHoldings() {
        String value = null;
        List<Map<String, String>> statements = (List<Map<String, String>>) this.folioHolding.get(HOLDINGS_STATEMENTS);
        if (!statements.isEmpty() && !statements.get(0).get("statement").isBlank()) {
            value = statements.get(0).get("statement");
            if (value.contains(" = ")) {
                String[] parts = value.split(" = ");
                value = parts[0];
            }
        }
        return value;
    }

    @Override
    public boolean isProxy() {
        // true if statisticalCodes[].code != "Lane-NoProxy"
        List<Map<String, String>> statisticalCodes = (List<Map<String, String>>) this.folioHolding
                .get("statisticalCodes");
        return statisticalCodes.stream()
                .noneMatch((final Map<String, String> sc) -> "Lane-NoProxy".equalsIgnoreCase((sc.get("code"))));
    }

    private boolean hasLinks() {
        return !((List<?>) this.folioHolding.get(ELECTRONIC_ACCESS)).isEmpty();
    }
}
