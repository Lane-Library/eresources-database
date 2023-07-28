package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.marc.CatalogLink.Type;
import edu.stanford.lane.catalog.FolioRecord;

/**
 * FolioVersion encapsulates a folio holding record (from /inventory-hierarchy/items-and-holdings, not
 * /holdings-storage/holdings).
 */
public class FolioVersion implements Version {

    private Eresource eresource;

    private Map<String, ?> folioHolding;

    private String locationName;

    private HTTPLaneLocationsService locationsService;

    private String locationUrl;

    public FolioVersion(final Map<String, ?> folioHolding, final Eresource eresource,
            final HTTPLaneLocationsService locationsService) {
        this.folioHolding = folioHolding;
        this.eresource = eresource;
        this.locationsService = locationsService;
    }

    @Override
    public String getAdditionalText() {
        // 866 ^z not mapped to holdings, so return nothing?
        // for print example of this, see NYTimes L89316
        return null;
    }

    @Override
    public String getCallnumber() {
        return ((Map<String, String>) this.folioHolding.get("callNumber")).get("callNumber");
    }

    @Override
    public String getDates() {
        List<Map<?, ?>> statements = (List<Map<?, ?>>) this.folioHolding.get("holdingsStatements");
        return statements.stream().map((final Map m) -> (String) m.get("statement")).collect(Collectors.joining("; "));
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
        List<Map<?, ?>> electronicAccesses = (List<Map<?, ?>>) this.folioHolding.get("electronicAccess");
        links.addAll(electronicAccesses.stream().map((final Map ea) -> new FolioLink(ea, version))
                .collect(Collectors.toList()));
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
        // not sure where to get this? callnumber with a specific scheme type?
        return null;
    }

    @Override
    public String getSummaryHoldings() {
        // holdingsStatements ... same as getDates so use that for now
        return this.getDates();
    }

    @Override
    public boolean isProxy() {
        // determine a way to store and retrieve this from folio holdings?
        return true;
    }

    private boolean hasLinks() {
        return !((List<?>) this.folioHolding.get("electronicAccess")).isEmpty();
    }
}
