package edu.stanford.irt.eresources.marc;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

import com.ibm.icu.text.Normalizer;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.Version;

public abstract class AbstractMarcEresource implements Eresource {
    private static final Pattern SPACE_SLASH = Pattern.compile(" /");

    private static final Set<String> ALLOWED_TYPES = new HashSet<String>();

    private static final String[] ALLOWED_TYPES_INITIALIZER = { "cc", "database", "book", "ej", "atlases, pictorial",
            "redwood software, installed", "duck software, installed", "stone software, installed",
            "m051 software, installed", "lksc-student software, installed", "lksc-public software, installed",
            "software, installed", "software", "statistics", "video", "graphic", "lanesite", "print", "bassett",
            "statistics software, installed", "biotools software, installed", "laneclass", "lanepage", "catalog" };

    private static final Map<String, String> COMPOSITE_TYPES = new HashMap<String, String>();

    private static final String[][] COMPOSITE_TYPES_INITIALIZER = {
            { "ej", "periodical", "newspaper", "periodicals", "newspapers" },
            { "cc", "decision support techniques", "calculators, clinical", "algorithms" },
            { "video", "digital video", "digital video, local", "digital video, local, public", "digital videos",
                    "digital videos, local", "digital videos, local, public" },
            { "book", "book set", "book sets", "books" }, { "database", "databases" }, { "graphic", "graphics" } };

    private static final Map<String, String> PRIMARY_TYPES = new HashMap<String, String>();

    private static final int[] NOITEMS = new int[] {0,0};
    static {
        for (String type : ALLOWED_TYPES_INITIALIZER) {
            ALLOWED_TYPES.add(type);
        }
        for (String[] element : COMPOSITE_TYPES_INITIALIZER) {
            for (int j = 1; j < element.length; j++) {
                COMPOSITE_TYPES.put(element[j], element[0]);
            }
        }
        PRIMARY_TYPES.put("", "Other");
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
        PRIMARY_TYPES.put("periodical", "journal");
        PRIMARY_TYPES.put("periodicals", "journal");
        PRIMARY_TYPES.put("search engine", "Database");
        PRIMARY_TYPES.put("search engines", "Database");
        PRIMARY_TYPES.put("serial", "serial");
        PRIMARY_TYPES.put("serials", "serial");
        PRIMARY_TYPES.put("sound recording", "Audio");
        PRIMARY_TYPES.put("sound recordings", "Audio");
        PRIMARY_TYPES.put("visual material", "visual material");
        PRIMARY_TYPES.put("visual materials", "visual material");
        PRIMARY_TYPES.put("website", "Website");
        PRIMARY_TYPES.put("websites", "Website");
    }

    private String description;

    private boolean descriptionDone;

    private int id;

    private boolean isCore;

    private boolean isCoreDone;

    private String keywords;

    private Collection<String> meshTerms;

    private boolean meshTermsDone;

    private String primaryType;

    private Record record;

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

    private boolean primaryTypeDone;

    private String initialPrimaryType;

    private Collection<String> initialTypes;

    public AbstractMarcEresource(final Record record, final String keywords) {
        this.record = record;
        this.keywords = keywords;
    }

    @Override
    public String getAuthor() {
        String author = null;
        DataField field245 = (DataField) this.record.getVariableField("245");
        if (field245 != null) {
            Subfield subfieldc = field245.getSubfield('c');
            if (subfieldc != null) {
                author =  Normalizer.compose(subfieldc.getData(), false);
            }
        }
        return author;
    }

    @Override
    public String getDescription() {
        if (!this.descriptionDone) {
            this.description = doDescription();
            this.descriptionDone = true;
        }
        return this.description;
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
        if (!this.primaryTypeDone) {
            this.primaryType = doPrimaryType();
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
        return getRecordType() + ":" + getRecordId() + " " + getTitle();
    }

    protected void addCustomTypes(final Collection<String> types) {
        // do nothing by default
    }

    protected abstract String doDescription();

    protected abstract int doId();

    protected abstract boolean doIsCore();

    protected abstract Collection<String> doMeshTerms();

    protected String doPrimaryType() {
        return getRealPrimaryType(getInitialPrimaryType());
    }
    
    protected String getInitialPrimaryType() {
        if (this.initialPrimaryType == null) {
        String type = "";
        for (VariableField field : this.record.getVariableFields("655")) {
            DataField datafield = (DataField) field;
            if (datafield.getIndicator1() == '4' && datafield.getIndicator2() == '7') {
                type = datafield.getSubfield('a').getData();
            }
        }
        // remove trailing periods, some probably should have them but
        // voyager puts them on everything :-(
        int lastPeriod = type.lastIndexOf('.');
        if (lastPeriod >= 0) {
            int lastPosition = type.length() - 1;
            if (lastPeriod == lastPosition) {
                type = type.substring(0, lastPosition);
            }
        }
        this.initialPrimaryType = type.toLowerCase();
        }
        return this.initialPrimaryType;
    }

    
    protected String getRealPrimaryType(String type) {
        return type;
    }
    
    protected String getMappedPrimaryType(String type) {
        if (PRIMARY_TYPES.containsKey(type)) {
            return PRIMARY_TYPES.get(type);
        } else {
            return type;
        }
        
    }

    protected String doTitle() {
        StringBuilder sb = new StringBuilder();
        DataField field245 = (DataField) this.record.getVariableField("245");
        for (Subfield subfield : field245.getSubfields()) {
            char code = subfield.getCode();
            if ("anpq".indexOf(code) > -1) {
                append(sb, Normalizer.compose(subfield.getData(), false));
            } else if (code == 'b') {
                String data = subfield.getData();
                data = SPACE_SLASH.matcher(data).replaceFirst("");
                append(sb, Normalizer.compose(data, false));
            }
        }
        DataField field250 = (DataField) this.record.getVariableField("250");
        String edition = MarcTextUtil.getSubfieldData(field250, 'a');
        if (edition != null) {
            sb.append(". ").append(edition);
        }
        int offset = field245.getIndicator2() - 48;
        return sb.toString().substring(offset);
    }

    protected Collection<String> doTypes() {
        Collection<String> t = getInitialTypes();
        addCustomTypes(t);
        addPrimaryType(t);
        return t;
    }
    
    protected Collection<String> getInitialTypes() {
        if (this.initialTypes == null) {
        Collection<String> t = new HashSet<String>();
        t.add("catalog");
        for (VariableField field : this.record.getVariableFields("655")) {
            String type = MarcTextUtil.getSubfieldData((DataField) field, 'a').toLowerCase();
            // remove trailing periods, some probably should have them but
            // voyager puts them on everything :-(
            int lastPeriod = type.lastIndexOf('.');
            if (lastPeriod >= 0) {
                int lastPosition = type.length() - 1;
                if (lastPeriod == lastPosition) {
                    type = type.substring(0, lastPosition);
                }
            }
            String composite = getCompositeType(type);
            if (composite != null) {
                t.add(composite);
            } else if (isAllowedType(type)) {
                t.add(type);
            }
        }
        this.initialTypes = t;
        }
        return this.initialTypes;
    }

    protected void addPrimaryType(Collection<String> t) {
        t.add(getInitialPrimaryType());
    }

    protected void append(final StringBuilder sb, final String value) {
        if (value != null) {
            if (sb.length() > 0) {
                sb.append(' ');
            }
            sb.append(value);
        }
    }

    protected abstract Date doUpdated();

    protected abstract List<Version> doVersions();

    protected abstract int doYear();

    protected String getCompositeType(final String type) {
        return COMPOSITE_TYPES.get(type);
    }

    protected boolean isAllowedType(final String type) {
        return ALLOWED_TYPES.contains(type);
    }

    protected abstract String getPrintOrDigital();
}
