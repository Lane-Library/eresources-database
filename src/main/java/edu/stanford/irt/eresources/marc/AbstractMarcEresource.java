package edu.stanford.irt.eresources.marc;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

import com.ibm.icu.text.Normalizer;

import edu.stanford.irt.eresources.Eresource;

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

    private String keywords;

    private Record record;

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
    public int[] getItemCount() {
        return NOITEMS;
    }

    @Override
    public String getKeywords() {
        return this.keywords;
    }

    @Override
    public String getPrimaryType() {
        return getRealPrimaryType(getInitialPrimaryType());
    }

    @Override
    public Collection<String> getMeshTerms() {
        Collection<String> m = new HashSet<String>();
        for (VariableField field : this.record.getVariableFields("650")) {
            if (((DataField) field).getIndicator1() == '4' && "237".indexOf(((DataField) field).getIndicator2()) > -1) {
                m.add(MarcTextUtil.getSubfieldData((DataField) field, 'a').toLowerCase());
            }
        }
        return m;
    }

    @Override
    public int getRecordId() {
        return Integer.parseInt(this.record.getControlNumber());
    }

    @Override
    public String getTitle() {
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

    @Override
    public Collection<String> getTypes() {
        Collection<String> t = getInitialTypes();
        addCustomTypes(t);
        addPrimaryType(t);
        return t;
    }

    @Override
    public boolean isClone() {
        return false;
    }

    @Override
    public String toString() {
        return getRecordType() + ":" + getRecordId() + " " + getTitle();
    }

    protected void addCustomTypes(final Collection<String> types) {
        // do nothing by default
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

    protected String getCompositeType(final String type) {
        return COMPOSITE_TYPES.get(type);
    }

    protected boolean isAllowedType(final String type) {
        return ALLOWED_TYPES.contains(type);
    }

    protected abstract String getPrintOrDigital();
}
