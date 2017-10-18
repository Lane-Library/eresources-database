package edu.stanford.irt.eresources.sax;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceConstants;
import edu.stanford.irt.eresources.LanguageMap;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.VersionComparator;

public class SAXEresource implements Cloneable, Eresource {

    private static final Set<String> ALLOWED_TYPES = new HashSet<>();

    private static final String[] ALLOWED_TYPES_INITIALIZER = { EresourceConstants.ARTICLE, "Atlases, Pictorial",
            EresourceConstants.AUDIO, "Bassett", "Biotools Software, Installed", EresourceConstants.BOOK,
            EresourceConstants.CHAPTER, "Calculators, Formulas, Algorithms", EresourceConstants.DATABASE, "Dataset",
            "Exam Prep", "Grand Rounds", EresourceConstants.IMAGE, EresourceConstants.JOURNAL, "Lane Class",
            "Lane Web Page", "Mobile", "Print", EresourceConstants.SOFTWARE, "Software, Installed",
            "Statistics Software, Installed", "Statistics", EresourceConstants.VIDEO, EresourceConstants.WEBSITE };

    private static final Comparator<Version> COMPARATOR = new VersionComparator();

    private static final Map<String, String> COMPOSITE_TYPES = new HashMap<>();

    private static final String[][] COMPOSITE_TYPES_INITIALIZER = { { EresourceConstants.ARTICLE, "Articles" },
            { EresourceConstants.AUDIO, "Sound Recordings" }, { EresourceConstants.BOOK, "Book Sets", "Books" },
            { EresourceConstants.CHAPTER, "Chapters" },
            { "Calculators, Formulas, Algorithms", "Decision Support Techniques", "Calculators, Clinical",
                    "Algorithms" },
            { EresourceConstants.DATABASE, "Databases" }, { "Dataset", "Datasets" },
            { "Exam Prep", "Examination Questions", "Outlines", "Problems", "Study Guides" },
            { EresourceConstants.IMAGE, "Graphics" }, { EresourceConstants.JOURNAL, "Periodicals", "Newspapers" },
            { "Mobile", "Subset, Mobile" },
            { EresourceConstants.SOFTWARE, "Software, Biocomputational", "Software, Educational",
                    "Software, Statistical" },
            { EresourceConstants.VIDEO, "Digital Video", "Digital Video, Local", "Digital Video, Local, Public" },
            { EresourceConstants.WEBSITE, "Websites" } };

    private static final String ENG = "English";

    private static final LanguageMap LANGUAGE_MAP = new LanguageMap();

    private static final Map<String, String> PRIMARY_TYPES = new HashMap<>();

    private static final String SEMICOLON_SPACE = "; ";
    static {
        for (String type : ALLOWED_TYPES_INITIALIZER) {
            ALLOWED_TYPES.add(type);
        }
        for (String[] element : COMPOSITE_TYPES_INITIALIZER) {
            for (int j = 1; j < element.length; j++) {
                COMPOSITE_TYPES.put(element[j], element[0]);
            }
        }
        PRIMARY_TYPES.put("articles", EresourceConstants.ARTICLE);
        PRIMARY_TYPES.put("books", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("book sets", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("cartographic materials", EresourceConstants.OTHER);
        PRIMARY_TYPES.put("collections", EresourceConstants.COLLECTION);
        PRIMARY_TYPES.put("components", EresourceConstants.COMPONENT);
        PRIMARY_TYPES.put("computer files", EresourceConstants.SOFTWARE);
        PRIMARY_TYPES.put("databases", EresourceConstants.DATABASE);
        PRIMARY_TYPES.put("documents", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("laneclass", "Lane Class");
        PRIMARY_TYPES.put("lanepage", "Lane Web Page");
        PRIMARY_TYPES.put("leaflets", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("pamphlets", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("periodicals", EresourceConstants.JOURNAL);
        PRIMARY_TYPES.put("search engines", EresourceConstants.DATABASE);
        PRIMARY_TYPES.put("serials", EresourceConstants.SERIAL);
        PRIMARY_TYPES.put("sound recordings", EresourceConstants.AUDIO);
        PRIMARY_TYPES.put("visual materials", EresourceConstants.VISUAL_MATERIAL);
        PRIMARY_TYPES.put("websites", EresourceConstants.WEBSITE);
        // authority types: keep?
        PRIMARY_TYPES.put("events", "Event");
        PRIMARY_TYPES.put("persons", EresourceConstants.PERSON);
        PRIMARY_TYPES.put("persons, female", EresourceConstants.PERSON);
        PRIMARY_TYPES.put("persons, male", EresourceConstants.PERSON);
        PRIMARY_TYPES.put("jurisdictions, subdivisions", EresourceConstants.ORGANIZATION);
        PRIMARY_TYPES.put("organizations", EresourceConstants.ORGANIZATION);
        PRIMARY_TYPES.put("organizations, subdivisions", EresourceConstants.ORGANIZATION);
    }

    private Collection<String> abbreviatedTitles;

    private Collection<String> alternativeTitles;

    private int[] count = new int[] { 0, 0 };

    private String date;

    private String description;

    private String id;

    private boolean isClone = false;

    private boolean isCore = false;

    private boolean isDigital = false;

    private boolean isLaneConnex = false;

    private String keywords;

    private Collection<String> meshTerms;

    private String primaryType;

    private String printOrDigital;

    private Collection<String> publicationAuthors = new ArrayList<>();

    private String publicationAuthorsText;

    private String publicationDate;

    private String publicationIssue;

    private Collection<String> publicationLanguages;

    private String publicationPages;

    private String publicationText;

    private String publicationTitle;

    private Collection<String> publicationTypes;

    private String publicationVolume;

    private int recordId;

    private String recordType;

    private String shortTitle;

    private String sortTitle;

    private String title;

    private Collection<String> types = new HashSet<>();

    private Date updated;

    private Set<Version> versions;

    private int year;

    public void addAbbreviatedTitle(final String title) {
        if (null == this.abbreviatedTitles) {
            this.abbreviatedTitles = new HashSet<>();
        }
        this.abbreviatedTitles.add(title);
    }

    public void addAlternativeTitle(final String title) {
        if (null == this.alternativeTitles) {
            this.alternativeTitles = new HashSet<>();
        }
        this.alternativeTitles.add(title);
    }

    public void addMeshTerm(final String meshTerm) {
        if (null == this.meshTerms) {
            this.meshTerms = new HashSet<>();
        }
        this.meshTerms.add(meshTerm);
    }

    public void addPublicationAuthor(final String author) {
        this.publicationAuthors.add(author);
    }

    public void addPublicationLanguage(final String publicationLanguage) {
        if (this.publicationLanguages == null) {
            this.publicationLanguages = new HashSet<>();
        }
        this.publicationLanguages.add(LANGUAGE_MAP.getLanguage(publicationLanguage.toLowerCase(Locale.US)));
    }

    public void addPublicationType(final String publicationType) {
        if (this.publicationTypes == null) {
            this.publicationTypes = new HashSet<>();
        }
        this.publicationTypes.add(publicationType);
    }

    public void addType(final String type) {
        String typeToAdd = getCompositeType(type);
        if (isAllowable(typeToAdd)) {
            this.types.add(typeToAdd);
        }
    }

    public void addVersion(final Version version) {
        if (this.versions == null) {
            this.versions = new TreeSet<>(COMPARATOR);
        }
        if (!version.getLinks().isEmpty()) {
            this.versions.add(version);
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        SAXEresource clone = (SAXEresource) super.clone();
        clone.isClone = true;
        return clone;
    }

    @Override
    public Collection<String> getAbbreviatedTitles() {
        if (null == this.abbreviatedTitles) {
            return Collections.emptySet();
        }
        return this.abbreviatedTitles;
    }

    @Override
    public Collection<String> getAlternativeTitles() {
        if (null == this.alternativeTitles) {
            return Collections.emptySet();
        }
        return this.alternativeTitles;
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

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Eresource#getDescription()
     */
    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getId() {
        return this.id;
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Eresource#getItemCount()
     */
    @Override
    public int[] getItemCount() {
        return this.count;
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Eresource#getKeywords()
     */
    @Override
    public String getKeywords() {
        return this.keywords;
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Eresource#getMeshTerms()
     */
    @Override
    public Collection<String> getMeshTerms() {
        if (null == this.meshTerms) {
            return Collections.emptySet();
        }
        return this.meshTerms;
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Eresource#getPrimaryType()
     */
    @Override
    public String getPrimaryType() {
        String type;
        if (this.primaryType == null) {
            type = EresourceConstants.OTHER;
        } else if (EresourceConstants.BOOK.equals(this.primaryType)) {
            type = EresourceConstants.BOOK + EresourceConstants.SPACE + getPrintOrDigital();
        } else if (EresourceConstants.JOURNAL.equals(this.primaryType)) {
            type = EresourceConstants.JOURNAL + EresourceConstants.SPACE + getPrintOrDigital();
        } else if (EresourceConstants.SERIAL.equals(this.primaryType)) {
            if (this.types.contains(EresourceConstants.BOOK)) {
                type = EresourceConstants.BOOK + EresourceConstants.SPACE + getPrintOrDigital();
            } else if (this.types.contains(EresourceConstants.DATABASE)) {
                type = EresourceConstants.DATABASE;
            } else {
                type = EresourceConstants.JOURNAL + EresourceConstants.SPACE + getPrintOrDigital();
            }
        } else if ("Component".equals(this.primaryType)) {
            if (this.types.contains(EresourceConstants.ARTICLE) && this.types.contains(EresourceConstants.CHAPTER)) {
                type = "Article/Chapter";
            } else if (this.types.contains(EresourceConstants.ARTICLE)) {
                type = EresourceConstants.ARTICLE;
            } else if (this.types.contains(EresourceConstants.CHAPTER)) {
                type = EresourceConstants.CHAPTER;
            } else {
                type = EresourceConstants.OTHER;
            }
        } else if (EresourceConstants.VISUAL_MATERIAL.equals(this.primaryType)) {
            if (this.types.contains(EresourceConstants.VIDEO)) {
                type = EresourceConstants.VIDEO;
            } else {
                type = EresourceConstants.IMAGE;
            }
        } else {
            type = this.primaryType;
        }
        return type;
    }

    @Override
    public Collection<String> getPublicationAuthors() {
        if (null == this.publicationAuthors) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(this.publicationAuthors);
    }

    @Override
    public String getPublicationAuthorsText() {
        if (null == this.publicationAuthorsText) {
            this.publicationAuthorsText = buildPublicationAuthorsText();
        }
        return this.publicationAuthorsText;
    }

    public String getPublicationDate() {
        return this.publicationDate;
    }

    public String getPublicationIssue() {
        return this.publicationIssue;
    }

    @Override
    public Collection<String> getPublicationLanguages() {
        if (null == this.publicationLanguages) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(this.publicationLanguages);
    }

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
        if (null == this.publicationTypes) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(this.publicationTypes);
    }

    public String getPublicationVolume() {
        return this.publicationVolume;
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Eresource#getRecordId()
     */
    @Override
    public int getRecordId() {
        return this.recordId;
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Eresource#getRecordType()
     */
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

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Eresource#getTitle()
     */
    @Override
    public String getTitle() {
        return this.title;
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Eresource#getTypes()
     */
    @Override
    public Collection<String> getTypes() {
        String pType = getPrimaryType();
        if (!EresourceConstants.OTHER.equals(pType) && !"Article/Chapter".equals(pType)) {
            this.types.add(pType);
        }
        if (pType.startsWith(EresourceConstants.BOOK) || pType.startsWith(EresourceConstants.JOURNAL)) {
            this.types.add(pType.split(" ")[0]);
        }
        return Collections.unmodifiableCollection(this.types);
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Eresource#getUpdated()
     */
    @Override
    public Date getUpdated() {
        return new Date(this.updated.getTime());
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Eresource#getVersions()
     */
    @Override
    public Collection<Version> getVersions() {
        if (this.versions == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(this.versions);
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Eresource#getYear()
     */
    @Override
    public int getYear() {
        return this.year;
    }

    @Override
    public boolean isClone() {
        return this.isClone;
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Eresource#isCore()
     */
    @Override
    public boolean isCore() {
        return this.isCore;
    }

    public boolean isDigital() {
        return this.isDigital;
    }

    @Override
    public boolean isEnglish() {
        if (null != this.publicationLanguages) {
            return this.publicationLanguages.contains(ENG);
        }
        return false;
    }

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

    public void setItemCount(final int[] count) {
        this.count = count;
    }

    public void setKeywords(final String keywords) {
        this.keywords = keywords;
    }

    public void setPrimaryType(final String type) {
        this.primaryType = PRIMARY_TYPES.get(type.toLowerCase(Locale.US));
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

    public void setRecordId(final int recordId) {
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

    public void setUpdated(final Date updated) {
        this.updated = new Date(updated.getTime());
    }

    public void setYear(final int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return new StringBuilder(this.recordType).append(':').append(this.recordId).append(' ').append(this.title)
                .toString();
    }

    protected String getCompositeType(final String type) {
        if (COMPOSITE_TYPES.containsKey(type)) {
            return COMPOSITE_TYPES.get(type);
        }
        return type;
    }

    protected boolean isAllowable(final String type) {
        return ALLOWED_TYPES.contains(type);
    }

    private String buildPublicationAuthorsText() {
        StringBuilder sb = new StringBuilder();
        for (String auth : this.publicationAuthors) {
            sb.append(auth).append(SEMICOLON_SPACE);
        }
        if (sb.toString().endsWith(SEMICOLON_SPACE)) {
            sb.delete(sb.length() - 2, sb.length());
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

    private String getPrintOrDigital() {
        if (null != this.printOrDigital) {
            return this.printOrDigital;
        }
        this.printOrDigital = "Print";
        if (this.isDigital) {
            this.printOrDigital = "Digital";
        }
        return this.printOrDigital;
    }
}
