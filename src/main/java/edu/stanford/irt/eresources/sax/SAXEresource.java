package edu.stanford.irt.eresources.sax;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.DateParser;
import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.LanguageMap;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.VersionComparator;

public class SAXEresource implements Eresource {

    private static final Comparator<Version> COMPARATOR = new VersionComparator();

    private static final String ENG = "English";

    private static final LanguageMap LANGUAGE_MAP = new LanguageMap();

    private static final String SEMICOLON_SPACE = "; ";

    private Collection<String> abbreviatedTitles = new HashSet<>();

    private Collection<String> alternativeTitles = new HashSet<>();

    private Collection<String> broadMeshTerms = new HashSet<>();

    private String date;

    private String description;

    private String id;

    private boolean isCore = false;

    private boolean isDigital = false;

    private boolean isLaneConnex = false;

    private Collection<String> issns = new HashSet<>();

    private String keywords;

    private Collection<String> meshTerms = new HashSet<>();

    private String primaryType;

    private Collection<String> publicationAuthors = new LinkedList<>();

    private Collection<String> publicationAuthorsFacetable = new HashSet<>();

    private String publicationAuthorsText;

    private String publicationDate;

    private String publicationIssue;

    private Collection<String> publicationLanguages = new HashSet<>();

    private String publicationPages;

    private String publicationText;

    private String publicationTitle;

    private Collection<String> publicationTypes = new HashSet<>();

    private String publicationVolume;

    private String recordId;

    private String recordType;

    private String shortTitle;

    private String sortTitle;

    private String title;

    private Collection<String> types = new HashSet<>();

    private LocalDateTime updated;

    private Set<Version> versions = new TreeSet<>(COMPARATOR);

    private int year;

    public void addAbbreviatedTitle(final String title) {
        this.abbreviatedTitles.add(title);
    }

    public void addAlternativeTitle(final String title) {
        this.alternativeTitles.add(title);
    }

    public void addBroadMeshTerm(final String meshTerm) {
        this.broadMeshTerms.add(meshTerm);
    }

    public void addIssn(final String issn) {
        this.issns.add(issn);
    }

    public void addMeshTerm(final String meshTerm) {
        this.meshTerms.add(meshTerm);
    }

    public void addPublicationAuthor(final String author) {
        this.publicationAuthors.add(author);
    }

    public void addPublicationAuthorFacetable(final String author) {
        this.publicationAuthorsFacetable.add(author);
    }

    public void addPublicationLanguage(final String publicationLanguage) {
        this.publicationLanguages.add(LANGUAGE_MAP.getLanguage(publicationLanguage.toLowerCase(Locale.US)));
    }

    public void addPublicationType(final String publicationType) {
        this.publicationTypes.add(publicationType);
    }

    public void addType(final String type) {
        this.types.add(type);
    }

    public void addVersion(final Version version) {
        if (!version.getLinks().isEmpty()) {
            this.versions.add(version);
        }
    }

    @Override
    public Collection<String> getAbbreviatedTitles() {
        return new HashSet<>(this.abbreviatedTitles);
    }

    @Override
    public Collection<String> getAlternativeTitles() {
        return new HashSet<>(this.alternativeTitles);
    }

    @Override
    public Collection<String> getBroadMeshTerms() {
        return new HashSet<>(this.broadMeshTerms);
    }

    @Override
    public String getDate() {
        if (null == this.date || "0".equals(this.date) || this.date.isEmpty()) {
            if (null != this.publicationDate) {
                this.date = DateParser.parseDate(this.publicationDate);
            } else if (this.year > 0) {
                this.date = DateParser.parseDate(Integer.toString(this.year));
            }
        }
        return this.date;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Collection<String> getIssns() {
        return new HashSet<>(
                this.issns.stream().map(String::trim).map(TextParserHelper::cleanIsxn).collect(Collectors.toSet()));
    }

    @Override
    public String getKeywords() {
        return this.keywords;
    }

    @Override
    public Collection<String> getMeshTerms() {
        return new HashSet<>(this.meshTerms);
    }

    @Override
    public String getPrimaryType() {
        return this.primaryType;
    }

    @Override
    public Collection<String> getPublicationAuthors() {
        return Collections.unmodifiableCollection(this.publicationAuthors);
    }

    public Collection<String> getPublicationAuthorsFacetable() {
        return Collections.unmodifiableCollection(this.publicationAuthorsFacetable);
    }

    @Override
    public String getPublicationAuthorsText() {
        if (null == this.publicationAuthorsText) {
            this.publicationAuthorsText = buildPublicationAuthorsText();
        }
        return this.publicationAuthorsText;
    }

    @Override
    public String getPublicationDate() {
        return this.publicationDate;
    }

    @Override
    public String getPublicationIssue() {
        return this.publicationIssue;
    }

    @Override
    public Collection<String> getPublicationLanguages() {
        return Collections.unmodifiableCollection(this.publicationLanguages);
    }

    @Override
    public String getPublicationPages() {
        return this.publicationPages;
    }

    @Override
    public String getPublicationText() {
        if (null == this.publicationText) {
            this.publicationText = buildPublicationText();
        }
        return this.publicationText;
    }

    @Override
    public String getPublicationTitle() {
        return this.publicationTitle;
    }

    @Override
    public Collection<String> getPublicationTypes() {
        return Collections.unmodifiableCollection(this.publicationTypes);
    }

    @Override
    public String getPublicationVolume() {
        return this.publicationVolume;
    }

    @Override
    public String getRecordId() {
        return this.recordId;
    }

    @Override
    public String getRecordType() {
        return this.recordType;
    }

    @Override
    public String getShortTitle() {
        return this.shortTitle;
    }

    @Override
    public String getSortTitle() {
        return this.sortTitle;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public Collection<String> getTypes() {
        return Collections.unmodifiableCollection(this.types);
    }

    @Override
    public LocalDateTime getUpdated() {
        return this.updated;
    }

    @Override
    public Collection<Version> getVersions() {
        return Collections.unmodifiableCollection(this.versions);
    }

    @Override
    public int getYear() {
        return this.year;
    }

    @Override
    public boolean isCore() {
        return this.isCore;
    }

    public boolean isDigital() {
        return this.isDigital;
    }

    @Override
    public boolean isEnglish() {
        return this.publicationLanguages.contains(ENG);
    }

    @Override
    public boolean isLaneConnex() {
        return this.isLaneConnex;
    }

    public void setDate(final String date) {
        this.date = DateParser.parseDate(date);
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public void setIsCore(final boolean isCore) {
        this.isCore = isCore;
    }

    public void setIsDigital(final boolean isDigital) {
        this.isDigital = isDigital;
    }

    public void setIsLaneConnex(final boolean isLaneConnex) {
        this.isLaneConnex = isLaneConnex;
    }

    public void setKeywords(final String keywords) {
        this.keywords = keywords;
    }

    public void setPrimaryType(final String type) {
        this.primaryType = type;
    }

    public void setPublicationAuthorsText(final String authorsText) {
        this.publicationAuthorsText = authorsText;
    }

    public void setPublicationDate(final String date) {
        this.publicationDate = date;
    }

    public void setPublicationIssue(final String publicationIssue) {
        this.publicationIssue = publicationIssue;
    }

    public void setPublicationPages(final String pages) {
        this.publicationPages = pages;
    }

    public void setPublicationText(final String publicationText) {
        this.publicationText = publicationText;
    }

    public void setPublicationTitle(final String publicationTitle) {
        this.publicationTitle = publicationTitle;
    }

    public void setPublicationVolume(final String publicationVolume) {
        this.publicationVolume = publicationVolume;
    }

    public void setRecordId(final String recordId) {
        this.recordId = recordId;
    }

    public void setRecordType(final String recordType) {
        this.recordType = recordType;
    }

    public void setShortTitle(final String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public void setSortTitle(final String sortTitle) {
        this.sortTitle = sortTitle;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setUpdated(final LocalDateTime updated) {
        this.updated = updated;
    }

    public void setYear(final int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return new StringBuilder(this.recordType).append(':').append(this.recordId).append(' ').append(this.title)
                .toString();
    }

    private String buildPublicationAuthorsText() {
        StringBuilder sb = new StringBuilder();
        for (String auth : this.publicationAuthors) {
            sb.append(auth).append(SEMICOLON_SPACE);
        }
        if (sb.toString().endsWith(SEMICOLON_SPACE)) {
            sb.delete(sb.length() - SEMICOLON_SPACE.length(), sb.length());
        }
        if (sb.length() > 0 && !sb.toString().endsWith(".")) {
            sb.append('.');
        }
        return sb.toString();
    }

    private String buildPublicationText() {
        StringBuilder sb = new StringBuilder();
        if (this.publicationTitle != null) {
            sb.append(this.publicationTitle).append(". ");
            if (this.publicationDate != null) {
                sb.append(this.publicationDate);
            }
            if (this.publicationVolume != null && this.publicationVolume.length() > 0) {
                sb.append(';').append(this.publicationVolume);
            }
            if (this.publicationIssue != null && this.publicationIssue.length() > 0) {
                sb.append('(').append(this.publicationIssue).append(')');
            }
            if (this.publicationPages != null && this.publicationPages.length() > 0) {
                sb.append(':').append(this.publicationPages).append('.');
            }
        }
        return sb.toString();
    }
}
