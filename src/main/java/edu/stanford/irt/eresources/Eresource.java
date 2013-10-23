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
        Eresource clone = (Eresource) super.clone();
        clone.isClone = true;
        return clone;
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
        return this.updated;
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

    public boolean isClone() {
        return this.isClone;
    }

    public boolean isCore() {
        return this.isCore;
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
        this.updated = updated;
    }

    public void setYear(final int year) {
        this.year = year;
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
