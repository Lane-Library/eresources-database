package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.FolioRecord;

/**
 * An Eresource that encapsulates the marc Record and folioHoldings from which
 * it is derived.
 */
public class LaneMarcEresourceFolioHoldings extends AbstractMarcEresource {

    private List<Map<String, Object>> folioHoldings;

    private FolioRecord folioRecord;

    public LaneMarcEresourceFolioHoldings(final FolioRecord folioRecord, final KeywordsStrategy keywordsStrategy,
            final HTTPLaneLocationsService locationsService) {
        this.folioRecord = folioRecord;
        this.marcRecord = folioRecord.getInstanceMarc();
        this.folioHoldings = folioRecord.getHoldings();
        this.keywordsStrategy = keywordsStrategy;
        this.locationsService = locationsService;
    }

    @Override
    public List<Version> getVersions() {
        if (this.versions == null) {
            Collection<Version> versionSet = new TreeSet<>(COMPARATOR);
            for (Map<String, ?> holding : this.folioHoldings) {
                Version version = new FolioVersion(this.folioRecord, holding, this, this.locationsService);
                if (!version.getLinks().isEmpty()) {
                    versionSet.add(version);
                }
            }
            this.versions = Collections.unmodifiableList(new ArrayList<>(versionSet));
        }
        return new ArrayList<>(this.versions);
    }

    @Override
    public String getKeywords() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.keywordsStrategy.getKeywords(this.marcRecord));
        sb.append(this.keywordsStrategy.getKeywordsFromFolioHoldings(folioHoldings));
        return sb.toString();
    }

}
