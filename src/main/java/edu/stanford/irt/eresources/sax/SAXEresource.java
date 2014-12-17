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
        "statistics software, installed", "biotools software, installed" };

    private static final Comparator<Version> COMPARATOR = new VersionComparator();

    private static final Map<String, String> COMPOSITE_TYPES = new HashMap<String, String>();

    private static final String[][] COMPOSITE_TYPES_INITIALIZER = {
        { "ej", "periodical", "newspaper", "periodicals", "newspapers" },
        { "cc", "decision support techniques", "calculators, clinical", "algorithms" },
        { "video", "digital video", "digital video, local", "digital video, local, public", "digital videos",
            "digital videos, local", "digital videos, local, public" },
            { "book", "book set", "book sets", "books" }, { "database", "databases" }, { "graphic", "graphics" } };

    private static final Map<String, String> PRIMARY_TYPES = new HashMap<String, String>();

    private static final String[][] PRIMARY_TYPES_INITIALIZER = { { "cartographic materials", "Map" },
            { "search engine", "Search Engine" }, { "sound recordings", "Sound Recording" }, { "leaflets", "Leaflet" },
            { "documents", "Document" }, { "pamphlets", "Pamphlet" }, { "components", "Component" },
            { "websites", "Website" }, { "book sets", "Book Set" }, { "computer files", "Computer File" },
            { "databases", "Database" }, { "visual materials", "Visual Material" }, { "serials", "Serial" },
            { "books", "Book" }, { "laneclasses", "Class" }, { "lanesite", "Lane Webpage" } };
    static {
        for (String type : ALLOWED_TYPES_INITIALIZER) {
            ALLOWED_TYPES.add(type);
        }
        for (String[] element : COMPOSITE_TYPES_INITIALIZER) {
            for (int j = 1; j < element.length; j++) {
                COMPOSITE_TYPES.put(element[j], element[0]);
            }
        }
        for (String[] element : PRIMARY_TYPES_INITIALIZER) {
            PRIMARY_TYPES.put(element[0], element[1]);
        }
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
        SAXEresource clone = (SAXEresource) super.clone();
        clone.isClone = true;
        return clone;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public int[] getItemCount() {
        return this.count;
    }

    @Override
    public String getKeywords() {
        return this.keywords;
    }

    @Override
    public Collection<String> getMeshTerms() {
        if (null == this.meshTerms) {
            return Collections.emptySet();
        }
        return this.meshTerms;
    }

    @Override
    public String getPrimaryType() {
        if (this.primaryType == null) {
            return "";
        }
        return this.primaryType;
    }

    @Override
    public int getRecordId() {
        return this.recordId;
    }

    @Override
    public String getRecordType() {
        return this.recordType;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public Collection<String> getTypes() {
        if (null == this.types) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(this.types);
    }

    @Override
    public Date getUpdated() {
        return new Date(this.updated.getTime());
    }

    @Override
    public Collection<Version> getVersions() {
        if (this.versions == null) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(this.versions);
    }

    @Override
    public int getYear() {
        return this.year;
    }

    @Override
    public boolean isClone() {
        return this.isClone;
    }

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
