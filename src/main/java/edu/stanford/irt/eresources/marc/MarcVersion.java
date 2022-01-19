package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.ItemService;
import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

/**
 * MarcVersion encapsulates a holding record.
 */
public class MarcVersion extends MARCRecordSupport implements Version {

    private static final Pattern BOOK_OR_VIDEO = Pattern.compile("^(Book|Video).*");

    private static final Pattern SPACE_EQUALS = Pattern.compile(" =");

    private static final String createLocationUrlFromControlNumber(final String cn) {
        StringBuilder sb = new StringBuilder("/view/bib/");
        Integer recordId = TextParserHelper.recordIdFromLaneControlNumber(cn);
        if (recordId != null) {
            sb.append(recordId);
            return sb.toString();
        }
        return null;
    }

    private static boolean isGetPassword856(final Field field) {
        return field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'u').map(Subfield::getData)
                .anyMatch("http://lane.stanford.edu/secure/ejpw.html"::equals);
    }

    private Record bib;

    private Eresource eresource;

    private Record holding;

    private ItemService itemService;

    private String locationName;

    private HTTPLaneLocationsService locationsService;

    private String locationUrl;

    public MarcVersion(final Record holding, final Record bib, final Eresource eresource, final ItemService itemService,
            final HTTPLaneLocationsService locationsService) {
        this.holding = holding;
        this.bib = bib;
        this.eresource = eresource;
        this.itemService = itemService;
        this.locationsService = locationsService;
    }

    @Override
    public String getAdditionalText() {
        String additionalText = null;
        List<Field> fields = getFields(this.holding, "866").collect(Collectors.toList());
        if (fields.size() > 1) {
            additionalText = "";
        } else if (fields.size() == 1) {
            additionalText = fields.get(0).getSubfields().stream().filter((final Subfield s) -> 'z' == s.getCode())
                    .map(Subfield::getData).findFirst().orElse(null);
        }
        return additionalText;
    }

    @Override
    public String getCallnumber() {
        String cn = null;
        Field field = getFields(this.holding, "852").findFirst().orElse(null);
        if (field != null && !hasLinks()) {
            cn = field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'h' || s.getCode() == 'i')
                    .map(Subfield::getData).collect(Collectors.joining(" "));
            if (!cn.trim().isEmpty()) {
                return cn;
            }
        }
        return cn;
    }

    @Override
    public String getDates() {
        Field field = getFields(this.holding, "866").findFirst().orElse(null);
        String dates = null;
        if (field != null) {
            dates = field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'y')
                    .map(Subfield::getData).findFirst().orElse(null);
        }
        if (dates == null && needToAddBibDates(this.eresource)) {
            dates = getSubfieldData(this.bib, "149", "d").findFirst().orElse(null);
            if (dates == null) {
                dates = getSubfieldData(this.bib, "260", "c").findFirst().orElse("");
            }
        }
        return dates;
    }

    // required for ObjectMapper to write field
    public boolean getHasGetPasswordLink() {
        return hasGetPasswordLink();
    }

    @Override
    public int[] getItemCount() {
        int[] counts = null;
        if (null != this.itemService) {
            counts = this.itemService.getHoldingsItemCount().itemCount(MARCRecordSupport.getRecordId(this.holding));
            if (counts[0] > 0) {
                return counts;
            }
        }
        return Version.super.getItemCount();
    }

    @Override
    public List<Link> getLinks() {
        List<Link> links = new ArrayList<>();
        if (!hasLinks()) {
            links.add(new CatalogLink(getFields(this.bib, "001").map(Field::getData).findFirst().orElse(null), this,
                    "http://lmldb.stanford.edu/cgi-bin/Pwebrecon.cgi?BBID=", "Lane Catalog Record"));
        }
        Version version = this;
        links.addAll(getFields(this.holding, "856").filter((final Field f) -> !isGetPassword856(f))
                .map((final Field f) -> new MarcLink(f, version)).collect(Collectors.toList()));
        return links;
    }

    @Override
    public String getLocationName() {
        if (null != this.locationName) {
            return this.locationName;
        }
        String name = null;
        if (null != this.locationsService) {
            String locCode = getLocationCode();
            if (isNoItemsPrintBibAndHasParentRelationship()) {
                setLocationDataForRelatedRecord();
                name = this.locationName;
            } else {
                name = this.locationsService.getLocationName(locCode);
            }
        }
        return name;
    }

    @Override
    public String getLocationUrl() {
        if (null != this.locationUrl) {
            return this.locationUrl;
        }
        String url = null;
        if (null != this.locationsService) {
            String locCode = getLocationCode();
            if (isNoItemsPrintBibAndHasParentRelationship()) {
                setLocationDataForRelatedRecord();
                url = this.locationUrl;
            } else {
                url = this.locationsService.getLocationUrl(locCode);
            }
        }
        return url;
    }

    @Override
    public String getPublisher() {
        String publisher = null;
        Field field = getFields(this.holding, "844").findFirst().orElse(null);
        if (field != null) {
            publisher = field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'a')
                    .map(Subfield::getData).findFirst().orElse(null);
        }
        return publisher;
    }

    @Override
    public String getSummaryHoldings() {
        String value = null;
        Field field = getFields(this.holding, "866").findFirst().orElse(null);
        if (field != null) {
            value = field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'v')
                    .map(Subfield::getData).map((final String s) -> SPACE_EQUALS.matcher(s).replaceAll("")).findFirst()
                    .orElse(null);
        }
        return value;
    }

    @Override
    public boolean hasGetPasswordLink() {
        return getSubfieldData(this.holding, "856", "u").anyMatch("http://lane.stanford.edu/secure/ejpw.html"::equals);
    }

    @Override
    public boolean isProxy() {
        return getSubfieldData(this.holding, "655", "a").noneMatch("subset, noproxy"::equalsIgnoreCase);
    }

    private String getLocationCode() {
        int holdingsId = MARCRecordSupport.getRecordId(this.holding);
        if (null != this.locationsService
                && this.locationsService.getTemporaryHoldingLocations().containsKey(holdingsId)) {
            return this.locationsService.getTemporaryHoldingLocations().get(holdingsId);
        }
        return getSubfieldData(this.holding, "852", "b").findFirst().orElse("");
    }

    private boolean hasLinks() {
        return getFields(this.holding, "856").count() > 0;
    }

    private boolean isNoItemsPrintBibAndHasParentRelationship() {
        return this.eresource.getItemCount()[0] == 0
                && !getSubfieldData(getFields(this.bib, "856"), "u").findAny().isPresent()
                && getSubfieldData(getFields(this.bib, "773|787|830"), "w").findAny().isPresent();
    }

    private boolean needToAddBibDates(final Eresource eresource) {
        return null == getSummaryHoldings() && eresource.getPublicationText().isEmpty()
                && BOOK_OR_VIDEO.matcher(eresource.getPrimaryType()).matches()
                && getLinks().stream().noneMatch((final Link l) -> "impact factor".equalsIgnoreCase(l.getLabel()));
    }

    private boolean parentHasBibItems(final String controlNumber) {
        Integer recordId = TextParserHelper.recordIdFromLaneControlNumber(controlNumber);
        if (null == recordId) {
            System.out.println("linking error: rec = " + this.eresource.getRecordId() + "; ^w = " + controlNumber);
        }
        return recordId != null && this.itemService.getBibsItemCount().itemCount(recordId)[0] > 0;
    }

    private void setLocationDataForRelatedRecord() {
        String parentRecordId = getSubfieldData(this.bib, "773", "w").filter(this::parentHasBibItems).findFirst()
                .orElse(null);
        if (null != parentRecordId) {
            this.locationName = this.eresource.getPublicationText();
            this.locationUrl = createLocationUrlFromControlNumber(parentRecordId);
        }
        parentRecordId = getSubfieldData(this.bib, "787", "w").filter(this::parentHasBibItems).findFirst().orElse(null);
        if (null != parentRecordId) {
            this.locationName = getSubfieldData(this.bib, "787", "etdn").collect(Collectors.joining(" "));
            this.locationUrl = createLocationUrlFromControlNumber(parentRecordId);
        }
        parentRecordId = getSubfieldData(this.bib, "830", "w").filter(this::parentHasBibItems).findFirst().orElse(null);
        if (null != parentRecordId) {
            this.locationName = getSubfieldData(this.bib, "830", "adv").collect(Collectors.joining(" "));
            this.locationUrl = createLocationUrlFromControlNumber(parentRecordId);
        }
    }
}
