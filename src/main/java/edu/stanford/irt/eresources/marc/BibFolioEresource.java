package edu.stanford.irt.eresources.marc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.LanguageMap;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.VersionComparator;
import edu.stanford.lane.catalog.FolioRecord;

/**
 * An Eresource that encapsulates the Folio Records from which it is derived.
 */
public class BibFolioEresource implements Eresource {

    private static final Comparator<Version> COMPARATOR = new VersionComparator();

    private static final LanguageMap LANGUAGE_MAP = new LanguageMap();

    private FolioRecord folioRecord;

    private List<Version> versions;

    protected HTTPLaneLocationsService locationsService;

    public BibFolioEresource(final FolioRecord folioRecord, final HTTPLaneLocationsService locationsService) {
        this.folioRecord = folioRecord;
        this.locationsService = locationsService;
    }

    @Override
    public Collection<String> getAbbreviatedTitles() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getAlternativeTitles() {
        return this.folioRecord.jsonContext().read("$.instance.alternativeTitles.*.alternativeTitle");
    }

    @Override
    public Collection<String> getBroadMeshTerms() {
        return Collections.emptyList();
    }

    @Override
    public String getDate() {
        return this.folioRecord.jsonContext().read("$.instance.publication[0].dateOfPublication");
    }

    @Override
    public String getDescription() {
        List<String> notes = this.folioRecord.jsonContext().read("$.instance.notes.[?(@.staffOnly==false)].note");
        return notes.stream().collect(Collectors.joining(" "));
    }

    @Override
    public String getId() {
        return getRecordType() + "-" + getRecordId();
    }

    @Override
    public int[] getItemCount() {
        int[] itemCount = new int[2];
        itemCount[0] = this.folioRecord.getItems().size();
        itemCount[1] = (int) this.folioRecord.getItems().stream()
                .filter((final Map<String, Object> m) -> "Available".equals(m.get("status"))).count();
        return itemCount;
    }

    @Override
    public String getKeywords() {
        // too much data?
        // problem that it ignores both reserves and authority augmentables?
        return this.folioRecord.toString();
    }

    @Override
    public Collection<String> getMeshTerms() {
        return this.folioRecord.jsonContext().read("$.instance.subjects.*");
    }

    @Override
    public String getPrimaryType() {
        // not sure where to get this from instance record
        return null;
    }

    @Override
    public Collection<String> getPublicationAuthors() {
        return this.folioRecord.jsonContext().read("$.instance.contributors.*name");
    }

    @Override
    public String getPublicationAuthorsText() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getPublicationAuthors().stream().collect(Collectors.joining("; ")));
        if (sb.length() > 0 && !sb.toString().endsWith(".")) {
            sb.append('.');
        }
        return sb.toString();
    }

    @Override
    public String getPublicationDate() {
        return null;
    }

    @Override
    public String getPublicationIssue() {
        return null;
    }

    @Override
    public Collection<String> getPublicationLanguages() {
        List<String> languages = this.folioRecord.jsonContext().read("$.instance.languages.*");
        return languages.stream().map(String::toLowerCase).map(LANGUAGE_MAP::getLanguage).collect(Collectors.toSet());
    }

    @Override
    public String getPublicationPages() {
        return null;
    }

    @Override
    public String getPublicationText() {
        return null;
    }

    @Override
    public String getPublicationTitle() {
        return null;
    }

    @Override
    public Collection<String> getPublicationTypes() {
        return Collections.emptySet();
    }

    @Override
    public String getPublicationVolume() {
        return null;
    }

    @Override
    public String getRecordId() {
        return this.folioRecord.jsonContext().read("$.instance.hrid", String.class).replaceAll("[^\\d]", "");
    }

    @Override
    public String getRecordType() {
        return "bib";
    }

    @Override
    public String getShortTitle() {
        // no 149 ^a equivalent but returning null could mess up relevance
        return this.getSortTitle();
    }

    @Override
    public String getSortTitle() {
        return this.folioRecord.jsonContext().read("$.instance.indexTitle");
    }

    @Override
    public String getTitle() {
        return this.folioRecord.jsonContext().read("$.instance.title");
    }

    @Override
    public Collection<String> getTypes() {
        // not sure where to get this from Folio instance records
        return Collections.emptyList();
    }

    @Override
    public LocalDateTime getUpdated() {
        // TODO is this even used?
        return null;
    }

    @Override
    public List<Version> getVersions() {
        if (this.versions == null) {
            Collection<Version> versionSet = new TreeSet<>(COMPARATOR);
            for (Map<String, ?> holding : this.folioRecord.getHoldings()) {
                Version version = new FolioVersion(holding, this, this.locationsService);
                if (!version.getLinks().isEmpty()) {
                    versionSet.add(version);
                }
            }
            this.versions = Collections.unmodifiableList(new ArrayList<>(versionSet));
        }
        return new ArrayList<>(this.versions);
    }

    @Override
    public int getYear() {
        String date = this.getDate();
        if (null != date && date.length() >= 4) {
            return Integer.parseInt(date.substring(0, 4));
        }
        return 0;
    }

    @Override
    public boolean isEnglish() {
        return this.getPublicationLanguages().contains("English");
    }
}
