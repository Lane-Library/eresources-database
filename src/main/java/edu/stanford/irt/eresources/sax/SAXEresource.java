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

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.VersionComparator;

public class SAXEresource implements Cloneable, Eresource {

    private static final Set<String> ALLOWED_TYPES = new HashSet<String>();

    private static final String[] ALLOWED_TYPES_INITIALIZER = { "cc", "database", "book", "ej", "atlases, pictorial",
            "redwood software, installed", "duck software, installed", "stone software, installed",
            "m051 software, installed", "lksc-student software, installed", "lksc-public software, installed",
            "software, installed", "software", "statistics", "video", "graphic", "lanesite", "print", "bassett",
            "statistics software, installed", "biotools software, installed", "laneclass", "lanepage" };

    private static final Comparator<Version> COMPARATOR = new VersionComparator();

    private static final Map<String, String> COMPOSITE_TYPES = new HashMap<String, String>();

    private static final String[][] COMPOSITE_TYPES_INITIALIZER = {
            { "ej", "periodical", "newspaper", "periodicals", "newspapers" },
            { "cc", "decision support techniques", "calculators, clinical", "algorithms" },
            { "video", "digital video", "digital video, local", "digital video, local, public", "digital videos",
                    "digital videos, local", "digital videos, local, public" },
            { "book", "book set", "book sets", "books" }, { "database", "databases" }, { "graphic", "graphics" } };

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
            PRIMARY_TYPES.put("book", "book");
            PRIMARY_TYPES.put("books", "book");
            PRIMARY_TYPES.put("book set", "book");
            PRIMARY_TYPES.put("book sets", "book");
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
            PRIMARY_TYPES.put("document", "book");
            PRIMARY_TYPES.put("documents", "book");
            PRIMARY_TYPES.put("laneclass", "Lane Class");
            PRIMARY_TYPES.put("lanepage", "Lane Webpage");
            PRIMARY_TYPES.put("leaflet", "book");
            PRIMARY_TYPES.put("leaflets", "book");
            PRIMARY_TYPES.put("pamphlet", "book");
            PRIMARY_TYPES.put("pamphlets", "book");
            PRIMARY_TYPES.put("periodical", "Digital Journal");
            PRIMARY_TYPES.put("periodicals", "Digital Journal");
            PRIMARY_TYPES.put("search engine", "Database");
            PRIMARY_TYPES.put("search engines", "Database");
            PRIMARY_TYPES.put("serial", "Digital Journal");
            PRIMARY_TYPES.put("serials", "Digital Journal");
            PRIMARY_TYPES.put("sound recording", "Audio");
            PRIMARY_TYPES.put("sound recordings", "Audio");
            PRIMARY_TYPES.put("visual material", "visual material");
            PRIMARY_TYPES.put("visual materials", "visual material");
            PRIMARY_TYPES.put("website", "Website");
            PRIMARY_TYPES.put("websites", "Website");
    }

    private int[] count = new int[] { 0, 0 };

    private String description;

    private boolean isClone = false;

    private boolean isCore = false;

    private String keywords;

    private Collection<String> meshTerms;

    private String primaryType;

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
        String type = null;
        if (this.primaryType == null) {
            type = "Other";
        } else if ("book".equals(this.primaryType)) {
            if ("print".equals(this.recordType)) {
                type = "Print Book";
            } else {
                type = "Digital Book";
            }
        } else if ("visual material".equals(this.primaryType)) {
            if (this.types.contains("video")) {
                type = "Video";
            } else {
                type = "Image";
            }
        } else {
            type = this.primaryType;
        }
        return type;
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
        if (null == this.types) {
            return Collections.emptySet();
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

    public void setDescription(final String description) {
        this.description = description;
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

    public void setPrimaryType(final String type) {
        this.primaryType = PRIMARY_TYPES.get(type);
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
