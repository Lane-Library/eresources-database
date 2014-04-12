package edu.stanford.irt.eresources.marc;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.VersionComparator;

public abstract class AbstractMarcEresource extends AbstractMarcComponent implements Eresource {

    private static final Set<String> ALLOWED_TYPES = new HashSet<String>();

    private static final String[] ALLOWED_TYPES_INITIALIZER = { "cc", "database", "book", "ej", "atlases, pictorial",
            "redwood software, installed", "duck software, installed", "stone software, installed",
            "m051 software, installed", "lksc-student software, installed", "lksc-public software, installed",
            "software, installed", "software", "statistics", "video", "graphic", "lanesite", "print", "bassett",
            "statistics software, installed", "biotools software, installed" };

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
    
    protected boolean isAllowedType(String type) {
        return ALLOWED_TYPES.contains(type);
    }
    
    protected String getCompositeType(String type) {
        return COMPOSITE_TYPES.get(type);
    }

    private String description;

    private boolean descriptionDone;

    private int id;

    private boolean idDone;

    private boolean isCore;

    private boolean isCoreDone;

    private List<Version> versions;

    private boolean versionsDone;

    private Collection<String> meshTerms;

    private boolean meshTermsDone;

    private String title;

    private boolean titleDone;

    private Collection<String> types;

    private boolean typesDone;

    private Date updated;

    private boolean updatedDone;

    private int year;

    private boolean yearDone;

    private String keywords;
    
    public AbstractMarcEresource(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        if (!this.descriptionDone) {
            this.description = doDescription();
            this.descriptionDone = true;
        }
        return this.description;
    }

    public int getId() {
        if (!this.idDone) {
            this.id = doId();
            this.idDone = true;
        }
        return this.id;
    }

    public String getKeywords() {
        return this.keywords;
    }

    public Collection<Version> getVersions() {
        if (!this.versionsDone) {
            this.versions = doVersions();
            this.versionsDone = true;
        }
        return this.versions;
    }

    public Collection<String> getMeshTerms() {
        if (!this.meshTermsDone) {
            this.meshTerms = doMeshTerms();
            this.meshTermsDone = true;
        }
        return this.meshTerms;
    }

    public String getTitle() {
        if (!this.titleDone) {
            this.title = doTitle();
            this.titleDone = true;
        }
        return this.title;
    }

    public Collection<String> getTypes() {
        if (!this.typesDone) {
            this.types = doTypes();
            this.typesDone = true;
        }
        return this.types;
    }

    public Date getUpdated() {
        if (!this.updatedDone) {
            this.updated = doUpdated();
            this.updatedDone = true;
        }
        return this.updated;
    }

    public int getYear() {
        if (!this.yearDone) {
            this.year = doYear();
            this.yearDone = true;
        }
        return this.year;
    }

    public boolean isCore() {
        if (!this.isCoreDone) {
            this.isCore = doIsCore();
            this.isCoreDone = true;
        }
        return this.isCore;
    }

    @Override
    public String toString() {
        return getTitle();
    }
    
    @Override
    public int getRecordId() {
        if (this.id == 0) {
            this.id = doId();
        }
        return this.id;
    }

    protected abstract String doDescription();

    protected abstract int doId();

    protected abstract boolean doIsCore();

    protected abstract List<Version> doVersions();

    protected abstract Collection<String> doMeshTerms();

    protected abstract String doTitle();

    protected abstract Collection<String> doTypes();

    protected abstract Date doUpdated();

    protected abstract int doYear();

    @Override
    public boolean isClone() {
        // TODO Auto-generated method stub
        return false;
    }
}