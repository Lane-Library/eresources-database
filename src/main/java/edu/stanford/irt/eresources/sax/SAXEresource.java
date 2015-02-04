package edu.stanford.irt.eresources.sax;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.LanguageMap;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.VersionComparator;

public class SAXEresource implements Cloneable, Eresource {
    
    private static final Pattern WHITESPACE = Pattern.compile("\\s*");

    private static final Set<String> ALLOWED_TYPES = new HashSet<String>();

    private static final String[] ALLOWED_TYPES_INITIALIZER = { "Article", "Clinical Decision Tools", "Database",
        "Book", "Journal", "Atlases, Pictorial", "Software, Installed - Redwood Room",
        "Software, Installed - Duck Room", "Software, Installed - Stone Room", "Software, Installed - M051",
        "Software, Installed - LKSC Student", "Software, Installed - LKSC Public", "Software, Installed",
        "Software", "Statistics", "Video", "Graphic", "Lane Class", "Lane Web Page", "Print", "Bassett",
        "Statistics Software, Installed", "Biotools Software, Installed", "Catalog" };

    private static final Comparator<Version> COMPARATOR = new VersionComparator();

    private static final Map<String, String> COMPOSITE_TYPES = new HashMap<String, String>();

    private static final String[][] COMPOSITE_TYPES_INITIALIZER = { { "Journal", "Periodicals", "Newspapers" },
        { "Clinical Decision Tools", "Decision Support Techniques", "Calculators, Clinical", "Algorithms" },
        { "Video", "Digital Video", "Digital Video, Local", "Digital Video, Local, Public" },
        { "Book", "Book Sets", "Books" }, { "Database", "Databases" }, { "Graphic", "Graphics" }, { "Article", "Articles" } };

    private static final String ENG = "English";

    private static final LanguageMap LANGUAGE_MAP = new LanguageMap();

    private static final Map<String, String> PRIMARY_TYPES = new HashMap<String, String>();
    static {
        for (String type : ALLOWED_TYPES_INITIALIZER) {
            ALLOWED_TYPES.add(type);
        }
        for (String[] element : COMPOSITE_TYPES_INITIALIZER) {
            for (int j = 1; j < element.length; j++) {
                COMPOSITE_TYPES.put(element[j], element[0]);
            }
        }
            PRIMARY_TYPES.put("article", "Article");
            PRIMARY_TYPES.put("articles", "Article");
            PRIMARY_TYPES.put("book", "Book");
            PRIMARY_TYPES.put("books", "Book");
            PRIMARY_TYPES.put("book set", "Book");
            PRIMARY_TYPES.put("book sets", "Book");
            PRIMARY_TYPES.put("cartographic material", "Other");
            PRIMARY_TYPES.put("cartographic materials", "Other");
            PRIMARY_TYPES.put("collection", "Database");
            PRIMARY_TYPES.put("collections", "Database");
            PRIMARY_TYPES.put("component", "Other");
            PRIMARY_TYPES.put("components", "Other");
            PRIMARY_TYPES.put("computer file", "Software");
            PRIMARY_TYPES.put("computer files", "Software");
            PRIMARY_TYPES.put("database", "Database");
            PRIMARY_TYPES.put("databases", "Database");
            PRIMARY_TYPES.put("document", "Book");
            PRIMARY_TYPES.put("documents", "Book");
            PRIMARY_TYPES.put("laneclass", "Lane Class");
            PRIMARY_TYPES.put("lanepage", "Lane Webpage");
            PRIMARY_TYPES.put("leaflet", "Book");
            PRIMARY_TYPES.put("leaflets", "Book");
            PRIMARY_TYPES.put("pamphlet", "Book");
            PRIMARY_TYPES.put("pamphlets", "Book");
            PRIMARY_TYPES.put("periodical", "Journal");
            PRIMARY_TYPES.put("periodicals", "Journal");
            PRIMARY_TYPES.put("search engine", "Database");
            PRIMARY_TYPES.put("search engines", "Database");
            PRIMARY_TYPES.put("serial", "Journal");
            PRIMARY_TYPES.put("serials", "Journal");
            PRIMARY_TYPES.put("sound recording", "Audio");
            PRIMARY_TYPES.put("sound recordings", "Audio");
            PRIMARY_TYPES.put("visual material", "Visual Material");
            PRIMARY_TYPES.put("visual materials", "Visual Material");
            PRIMARY_TYPES.put("website", "Website");
            PRIMARY_TYPES.put("websites", "Website");
    }

    private int[] count = new int[] { 0, 0 };

    private String description;

    private String doi;

    private boolean isClone = false;

    private boolean isCore = false;

    private String keywords;

    private Collection<String> meshTerms;

    private String pmid;

    private String primaryType;

    private Collection<String> publicationAuthors;

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

    private String title;

    private Collection<String> types = new HashSet<String>();

    private Date updated;

    private Set<Version> versions;

    private int year;

    public void addMeshTerm(final String meshTerm) {
        if (null == this.meshTerms) {
            this.meshTerms = new HashSet<String>();
        }
        this.meshTerms.add(meshTerm);
    }

    public void addPublicationAuthor(final String author) {
        if (this.publicationAuthors == null) {
            this.publicationAuthors = new HashSet<String>();
        }
        this.publicationAuthors.add(author);
    }

    public void addPublicationLanguage(final String publicationLanguage) {
        if (this.publicationLanguages == null) {
            this.publicationLanguages = new HashSet<String>();
        }
        this.publicationLanguages.add(LANGUAGE_MAP.getLanguage(publicationLanguage.toLowerCase()));
    }

    public void addPublicationType(final String publicationType) {
        if (this.publicationTypes == null) {
            this.publicationTypes = new HashSet<String>();
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
            this.versions = new TreeSet<Version>(COMPARATOR);
        }
        if (version.getLinks().size() > 0) {
            this.versions.add(version);
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        SAXEresource clone = (SAXEresource) super.clone();
        clone.isClone = true;
        return clone;
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
    public String getDoi() {
        return this.doi;
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

    @Override
    public String getPmid() {
        return this.pmid;
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Eresource#getPrimaryType()
     */
    @Override
    public String getPrimaryType() {
        String type = null;
        if (this.primaryType == null) {
            type = "Other";
        } else if ("Book".equals(this.primaryType)) {
            if ("print".equals(this.recordType)) {
                type = "Print Book";
            } else {
                type = "Digital Book";
            }
        } else if ("Journal".equals(this.primaryType)) {
            if ("print".equals(this.recordType)) {
                type = "Print Journal";
            } else {
                type = "Digital Journal";
            }
        } else if ("Visual Material".equals(this.primaryType)) {
            if (this.types.contains("Video")) {
                type = "Video";
            } else {
                type = "Image";
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
        if (this.publicationText == null) {
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
            this.publicationText = sb.toString();
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

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Eresource#getTitle()
     */
    @Override
    public String getTitle() {
        if (this.title != null && this.title.length() > 512) {
            return this.title.substring(0, 511);
        }
        return this.title;
    }

    /*
     * (non-Javadoc)
     * @see edu.stanford.irt.eresources.Eresource#getTypes()
     */
    @Override
    public Collection<String> getTypes() {
        this.types.add(getPrimaryType());
//        this.types.add(WHITESPACE.matcher(getPrimaryType()).replaceAll(""));
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

    @Override
    public boolean isEnglish() {
        if (null != this.publicationLanguages) {
            return this.publicationLanguages.contains(ENG);
        }
        return false;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setDoi(final String doi) {
        this.doi = doi;
    }

    public void setIsCore(final boolean isCore) {
        this.isCore = isCore;
    }

    public void setItemCount(final int[] count) {
        this.count = count;
    }

    public void setKeywords(final String keywords) {
        this.keywords = keywords;
    }

    public void setPmid(final String pmid) {
        this.pmid = pmid;
    }

    public void setPrimaryType(final String type) {
        this.primaryType = PRIMARY_TYPES.get(type.toLowerCase());
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
}
