package edu.stanford.irt.eresources;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Eresource implements Cloneable {

    private static final Set<String> ALLOWED_TYPES = new HashSet<String>();

    private static final String[] ALLOWED_TYPES_INITIALIZER = { "article", "cc", "database", "book", "ej",
            "atlases, pictorial", "redwood software, installed", "duck software, installed",
            "stone software, installed", "m051 software, installed", "lksc-student software, installed",
            "lksc-public software, installed", "software, installed", "software", "statistics", "video", "graphic",
            "lanesite", "print", "bassett", "statistics software, installed", "biotools software, installed" };

    private static final Comparator<Version> COMPARATOR = new VersionComparator();

    private static final Map<String, String> COMPOSITE_TYPES = new HashMap<String, String>();

    private static final String[][] COMPOSITE_TYPES_INITIALIZER = {
            { "ej", "periodical", "newspaper", "periodicals", "newspapers" },
            { "cc", "decision support techniques", "calculators, clinical", "algorithms" },
            { "video", "digital video", "digital video, local", "digital video, local, public", "digital videos",
                    "digital videos, local", "digital videos, local, public" },
            { "book", "book set", "book sets", "books" }, { "database", "databases" }, { "graphic", "graphics" } };

    private static final String ENG = "eng";

    private static final LanguageMap LANGUAGE_MAP = new LanguageMap();
    static {
        for (String type : ALLOWED_TYPES_INITIALIZER) {
            ALLOWED_TYPES.add(type);
        }
        for (String[] element : COMPOSITE_TYPES_INITIALIZER) {
            for (int j = 1; j < element.length; j++) {
                COMPOSITE_TYPES.put(element[j], element[0]);
            }
        }
    }

    private String description;

    private boolean isClone = false;

    private boolean isCore = false;

    private String keywords;

    private Collection<String> meshTerms;

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

    private Collection<String> types;

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
            if (this.types == null) {
                this.types = new HashSet<String>();
            }
            this.types.add(typeToAdd);
        }
    }

    public void addVersion(final Version version) {
        if (this.versions == null) {
            this.versions = new TreeSet<Version>(COMPARATOR);
        }
        this.versions.add(version);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Eresource clone = (Eresource) super.clone();
        clone.isClone = true;
        return clone;
    }

    protected String getCompositeType(final String type) {
        if (COMPOSITE_TYPES.containsKey(type)) {
            return COMPOSITE_TYPES.get(type);
        }
        return type;
    }

    public String getDescription() {
        return this.description;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public Collection<String> getMeshTerms() {
        if (null == this.meshTerms) {
            return Collections.emptySet();
        }
        return this.meshTerms;
    }

    public Collection<String> getPublicationAuthors() {
        if (null == this.publicationAuthors) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(this.publicationAuthors);
    }

    public String getPublicationAuthorsText() {
        return this.publicationAuthorsText;
    }

    public String getPublicationDate() {
        return this.publicationDate;
    }

    public String getPublicationIssue() {
        return this.publicationIssue;
    }

    public Collection<String> getPublicationLanguages() {
        if (null == this.publicationLanguages) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(this.publicationLanguages);
    }

    public String getPublicationPages() {
        return this.publicationPages;
    }

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

    public String getPublicationTitle() {
        return this.publicationTitle;
    }

    public Collection<String> getPublicationTypes() {
        if (null == this.publicationTypes) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(this.publicationTypes);
    }

    public String getPublicationVolume() {
        return this.publicationVolume;
    }

    public int getRecordId() {
        return this.recordId;
    }

    public String getRecordType() {
        return this.recordType;
    }

    public String getTitle() {
        return this.title;
    }

    public Collection<String> getTypes() {
        if (null == this.types) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(this.types);
    }

    public Date getUpdated() {
        return new Date(this.updated.getTime());
    }

    public Collection<Version> getVersions() {
        if (this.versions == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(this.versions);
    }

    public int getYear() {
        return this.year;
    }

    protected boolean isAllowable(final String type) {
        return ALLOWED_TYPES.contains(type);
    }

    public boolean isClone() {
        return this.isClone;
    }

    public boolean isCore() {
        return this.isCore;
    }

    public boolean isEnglish() {
        if (null != this.publicationLanguages) {
            return this.publicationLanguages.contains(ENG);
        }
        return false;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setIsCore(final boolean isCore) {
        this.isCore = isCore;
    }

    public void setKeywords(final String keywords) {
        this.keywords = keywords;
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
}
