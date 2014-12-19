package edu.stanford.irt.eresources.marc;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.Version;

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

    private static final int[] NOITEMS = new int[] { 0, 0 };
    static {
        for (String type : ALLOWED_TYPES_INITIALIZER) {
            ALLOWED_TYPES.add(type);
        }
        for (String[] element : COMPOSITE_TYPES_INITIALIZER) {
            for (int j = 1; j < element.length; j++) {
                COMPOSITE_TYPES.put(element[j], element[0]);
            }
        }
    }    private static final Map<String, String> PRIMARY_TYPES = new HashMap<String, String>();

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

    private String description;

    private boolean descriptionDone;

    private int id;

    private boolean idDone;

    private boolean isCore;

    private boolean isCoreDone;

    private String keywords;

    private Collection<String> meshTerms;

    private boolean meshTermsDone;

    private String primaryType;

    private String title;

    private boolean titleDone;

    private Collection<String> types;

    private boolean typesDone;

    private Date updated;

    private boolean updatedDone;

    private List<Version> versions;

    private boolean versionsDone;

    private int year;

    private boolean yearDone;

    public AbstractMarcEresource(final String keywords) {
        this.keywords = keywords;
    }

    @Override
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

    @Override
    public int[] getItemCount() {
        return NOITEMS;
    }

    @Override
    public String getKeywords() {
        return this.keywords;
    }

    @Override
    public Collection<String> getMeshTerms() {
        if (!this.meshTermsDone) {
            this.meshTerms = doMeshTerms();
            this.meshTermsDone = true;
        }
        return this.meshTerms;
    }

    @Override
    public String getPrimaryType() {
        if (this.primaryType == null) {
            this.primaryType = PRIMARY_TYPES.get(doPrimaryType());
            if (this.primaryType == null) {
                this.primaryType = "";
            }
        }
        return this.primaryType;
    }

    @Override
    public int getRecordId() {
        if (this.id == 0) {
            this.id = doId();
        }
        return this.id;
    }

    @Override
    public String getTitle() {
        if (!this.titleDone) {
            this.title = doTitle();
            this.titleDone = true;
        }
        return this.title;
    }

    @Override
    public Collection<String> getTypes() {
        if (!this.typesDone) {
            this.types = doTypes();
            this.typesDone = true;
        }
        return this.types;
    }

    @Override
    public Date getUpdated() {
        if (!this.updatedDone) {
            this.updated = doUpdated();
            this.updatedDone = true;
        }
        return this.updated;
    }

    @Override
    public Collection<Version> getVersions() {
        if (!this.versionsDone) {
            this.versions = doVersions();
            this.versionsDone = true;
        }
        return this.versions;
    }

    @Override
    public int getYear() {
        if (!this.yearDone) {
            this.year = doYear();
            this.yearDone = true;
        }
        return this.year;
    }

    @Override
    public boolean isClone() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
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

    protected abstract String doDescription();

    protected abstract int doId();

    protected abstract boolean doIsCore();

    protected abstract Collection<String> doMeshTerms();

    protected abstract String doPrimaryType();

    protected abstract String doTitle();

    protected abstract Collection<String> doTypes();

    protected abstract Date doUpdated();

    protected abstract List<Version> doVersions();

    protected abstract int doYear();

    protected String getCompositeType(final String type) {
        return COMPOSITE_TYPES.get(type);
    }

    protected boolean isAllowedType(final String type) {
        return ALLOWED_TYPES.contains(type);
    }
}