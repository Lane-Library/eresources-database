package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.marc.CatalogLink.Type;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

/**
 * MarcVersion encapsulates a holding record.
 */
public class MarcVersion extends MARCRecordSupport implements Version {

    private static final Pattern BOOK_OR_VIDEO = Pattern.compile("^(Book|Video).*");

    private static final Pattern SPACE_EQUALS = Pattern.compile(" =");

    private static final String createLocationUrlFromRecordId(final String recordId) {
        if (recordId != null && recordId.matches("^\\d+$")) {
            return "/view/bib/" + recordId;
        }
        return null;
    }

    private Record bib;

    private Eresource eresource;

    private Record holding;

    private String locationName;

    private HTTPLaneLocationsService locationsService;

    private String locationUrl;

    public MarcVersion(final Record holding, final Record bib, final Eresource eresource,
            final HTTPLaneLocationsService locationsService) {
        this.holding = holding;
        this.bib = bib;
        this.eresource = eresource;
        this.locationsService = locationsService;
    }

    @Override
    public String getAdditionalText() {
        String additionalText = null;
        List<Field> fields = getFields(this.holding, "866").toList();
        if (fields.size() > 1) {
            additionalText = "";
        } else if (fields.size() == 1) {
            additionalText = fields.get(0).getSubfields().stream().filter((final Subfield s) -> 'z' == s.getCode())
                    .map(Subfield::getData).findFirst().orElse(null);
        }
        // LANEWEB-10982: show 931 notes but remove those referring to "Related Title Browse"
        String f931a = getSubfieldData(this.holding, "931", "a").collect(Collectors.joining(" "));
        if (!f931a.isBlank() && !f931a.toLowerCase().contains("use the \"related title browse\" index")) {
            additionalText = (null == additionalText) ? f931a : additionalText + " " + f931a;
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
            dates = getSubfieldData(getFields(this.bib, "260|264"), "c").findFirst().orElse("");
            dates = TextParserHelper.maybeStripTrailingPeriod(dates);
            dates = TextParserHelper.maybeStripTrailingUnbalancedBracket(dates);
        }
        return dates;
    }

    @Override
    public int[] getItemCount() {
        int[] itemCount = new int[3];
        int total = MARCRecordSupport.getSubfieldData(this.holding, "888", "t").findFirst().map(Integer::parseInt)
                .orElse(0);
        int available = MARCRecordSupport.getSubfieldData(this.holding, "888", "a").findFirst().map(Integer::parseInt)
                .orElse(0);
        int out = MARCRecordSupport.getSubfieldData(this.holding, "888", "c").findFirst().map(Integer::parseInt)
                .orElse(0);
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
        links.addAll(getFields(this.holding, "856").map((final Field f) -> new MarcLink(f, version)).toList());
        return links;
    }

    @Override
    public String getLocationCode() {
        // 852 ^9 is set when all items have a different location than holding record (catalog library does this)
        String itemLoc = getSubfieldData(this.holding, "852", "9").findFirst().orElse(null);
        if (null != itemLoc) {
            return itemLoc;
        }
        return getSubfieldData(this.holding, "852", "b").findFirst().orElse("");
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
        publisher = getSubfieldData(this.holding, "856", "y").findFirst().orElse(null);
        // remove this? 2024-09-05 perhaps after FOLIO holdings migration?
        if (null == publisher) {
            publisher = getSubfieldData(this.holding, "844", "a").findFirst().orElse(null);
        }
        // both publisher and MarcLink.getLabel() can use 856 ^y; do not duplicate publisher string
        // MarcLink.getLinkText() could be a candidate instead?
        String firstLinkLabel = this.getLinks().stream().map(Link::getLabel).filter(Objects::nonNull)
                .findFirst().orElse("");
        if (firstLinkLabel.equals(publisher)) {
            publisher = null;
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
    public boolean isProxy() {
        return getSubfieldData(this.holding, "655", "a").noneMatch("subset, noproxy"::equalsIgnoreCase);
    }

    private boolean hasLinks() {
        return getFields(this.holding, "856").count() > 0;
    }

    private boolean isBassett() {
        return this.eresource.getRecordId().equals("254573")
                || getSubfieldData(getFields(this.bib, "773"), "w").anyMatch("L254573"::equals);
    }

    private boolean isNoItemsPrintBibAndHasParentRelationship() {
        boolean hasLinks = this.getLinks().stream().anyMatch(
                (final Link l) -> null != l.getUrl() && !l.getUrl().contains("searchworks.stanford.edu/view/"));
        return this.eresource.getItemCount()[0] == 0 && !hasLinks
                && getSubfieldData(getFields(this.bib, "772|773|787|830"), "w").findAny().isPresent() && !isBassett();
    }

    private boolean needToAddBibDates(final Eresource eresource) {
        return null == getSummaryHoldings() && eresource.getPublicationText().isEmpty()
                && BOOK_OR_VIDEO.matcher(eresource.getPrimaryType()).matches();
    }

    // LANEWEB-10855
    // given a list of control numbers, find the best parent linking record
    // delpriore gave priority as:
    // - parent with items (doesn't work for digital parents)
    // - highest parent control number (not sure why)
    // default to first
    private String orderParentLinkingRecords(final List<String> cns) {
        List<Integer> recordIds = new ArrayList<>();
        for (String cn : cns) {
            Integer recordId = TextParserHelper.recordIdFromLaneControlNumber(cn);
            if (null != recordId) {
                // in Voyager days, this checked if the parent record had bibItems and returned the recordId if true
                // it used the getBibsItemCount of an itemService
                // parent item counts from FOLIO has not been implemented and no one has complained
                recordIds.add(recordId);
            }
        }
        if (!recordIds.isEmpty()) {
            Collections.sort(recordIds);
            return recordIds.get(recordIds.size() - 1).toString();
        }
        return null;
    }

    private void setLocationDataForRelatedRecord() {
        String parentRecordId = orderParentLinkingRecords(getSubfieldData(this.bib, "772", "w").toList());
        if (null != parentRecordId) {
            this.locationName = getSubfieldData(this.bib, "772", "abtdg").collect(Collectors.joining(" "));
            this.locationUrl = createLocationUrlFromRecordId(parentRecordId);
        }
        parentRecordId = orderParentLinkingRecords(getSubfieldData(this.bib, "773", "w").toList());
        if (null != parentRecordId) {
            this.locationName = this.eresource.getPublicationText();
            this.locationUrl = createLocationUrlFromRecordId(parentRecordId);
        }
        parentRecordId = orderParentLinkingRecords(getSubfieldData(this.bib, "787", "w").toList());
        if (null != parentRecordId) {
            this.locationName = getSubfieldData(this.bib, "787", "etdn").collect(Collectors.joining(" "));
            this.locationUrl = createLocationUrlFromRecordId(parentRecordId);
        }
        parentRecordId = orderParentLinkingRecords(getSubfieldData(this.bib, "830", "w").toList());
        if (null != parentRecordId) {
            this.locationName = getSubfieldData(this.bib, "830", "adv").collect(Collectors.joining(" "));
            this.locationUrl = createLocationUrlFromRecordId(parentRecordId);
        }
    }
}
