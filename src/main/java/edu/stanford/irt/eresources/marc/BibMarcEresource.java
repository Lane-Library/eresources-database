package edu.stanford.irt.eresources.marc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

import com.ibm.icu.text.Normalizer;

import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.VersionComparator;

/**
 * An Eresource that encapsulates the marc Records from which it is derived.
 */
public class BibMarcEresource extends AbstractMarcEresource {

    public static final int THIS_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    private static final Pattern ACCEPTED_YEAR_PATTERN = Pattern.compile("^\\d[\\d|u]{3}$");

    private static final String BIB_TYPE = "bib";

    private static final String[][][] CUSTOM_TYPES = { { { "periodical", "newspaper" }, { "ej" } },
            { { "decision support techniques", "calculators, clinical", "algorithms" }, { "cc" } },
            { { "digital video", "digital video, local" }, { "video" } }, { { "book set" }, { "book" } } };

    private static final Pattern WHITESPACE = Pattern.compile("\\s*");

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private List<Record> holdings;

    private int[] items;

    private Record record;

    private List<Version> versions;

    public BibMarcEresource(final List<Record> recordList, final String keywords, final int[] items) {
        super(recordList.get(0), keywords);
        this.record = recordList.get(0);
        this.holdings = recordList.subList(1, recordList.size());
        this.items = items;
    }

    @Override
    public String getDescription() {
        String description = null;
        List<VariableField> fields = this.record.getVariableFields("520");
        if (fields.size() == 0) {
            fields = this.record.getVariableFields("505");
        }
        if (fields.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (VariableField field : fields) {
                for (Subfield subfield : ((DataField) field).getSubfields()) {
                    append(sb, Normalizer.compose(subfield.getData(), false));
                }
            }
            description = sb.toString();
        }
        return description;
    }

    @Override
    public int[] getItemCount() {
        return this.items;
    }

    @Override
    public String getRecordType() {
        return BIB_TYPE;
    }

    public String getType() {
        return BIB_TYPE;
    }

    @Override
    public Date getUpdated() {
        try {
            Date updated = this.dateFormat.parse(((ControlField) this.record.getVariableField("005")).getData());
            for (Record holding : this.holdings) {
                Date holdingUpdated = this.dateFormat.parse(((ControlField) holding.getVariableField("005")).getData());
                if (holdingUpdated.compareTo(updated) > 0) {
                    updated = holdingUpdated;
                }
            }
            return updated;
        } catch (ParseException e) {
            throw new EresourceException(e);
        }
    }

    @Override
    public List<Version> getVersions() {
        if (this.versions == null) {
            Collection<Version> versions = new TreeSet<Version>(new VersionComparator());
            for (Record holding : this.holdings) {
                Version version = createVersion(holding);
                if (version.getLinks().size() > 0) {
                    versions.add(version);
                }
            }
            this.versions = Collections.unmodifiableList(new ArrayList<Version>(versions));
        }
        return this.versions;
    }

    @Override
    public int getYear() {
        int year = 0;
        String dateField = ((ControlField) this.record.getVariableField("008")).getData();
        String endDate = parseYear(dateField.substring(11, 15));
        if (endDate != null) {
            year = Integer.parseInt(endDate);
        } else {
            String beginDate = parseYear(dateField.substring(7, 11));
            if (beginDate != null) {
                year = Integer.parseInt(beginDate);
            }
        }
        return year;
    }

    @Override
    public boolean isCore() {
        boolean isCore = false;
        Iterator<VariableField> it = this.record.getVariableFields("655").iterator();
        while (it.hasNext() && !isCore) {
            isCore = "core material".equalsIgnoreCase(MarcTextUtil.getSubfieldData((DataField) it.next(), 'a'));
        }
        return isCore;
    }

    @Override
    protected void addCustomTypes(final Collection<String> types) {
        for (String[][] element : CUSTOM_TYPES) {
            for (int j = 0; j < element[0].length; j++) {
                if (types.contains(element[0][j])) {
                    types.add(element[1][0]);
                    break;
                }
            }
        }
        Collection<String> subsets = getAllSubsets();
        if (types.contains("software, installed")) {
            handleInstalledSoftware(types, subsets);
        }
        if (subsets.contains("biotools")) {
            types.add("software");
        }
        if (isBassettRecord()) {
            types.add("bassett");
        }
    }

    @Override
    protected void addPrimaryType(final Collection<String> types) {
        String mappedPrimaryType = getMappedPrimaryType(getInitialPrimaryType());
        if ("serial".equals(mappedPrimaryType)) {
            Collection<String> initialTypes = getInitialTypes();
            if (initialTypes.contains("book")) {
                types.add("book" + getPrintOrDigital().toLowerCase());
            } else if (initialTypes.contains("database")) {
                // add nothing
            } else {
                types.add("journal");
                types.add("journal" + getPrintOrDigital().toLowerCase());
            }
        } else if ("book".equals(mappedPrimaryType)) {
            types.add("book" + getPrintOrDigital().toLowerCase());
        } else if ("visual material".equals(mappedPrimaryType)) {
            Collection<String> initialTypes = getInitialTypes();
            boolean video = false;
            for (String type : initialTypes) {
                if (type.contains("video")) {
                    video = true;
                    break;
                }
            }
            if (video) {
                types.add("video");
            } else {
                types.add("image");
            }
        } else {
            types.add(WHITESPACE.matcher(mappedPrimaryType).replaceAll("").toLowerCase());
        }
    }

    protected Version createVersion(final Record record) {
        return new MarcVersion(record);
    }

    @Override
    protected String getPrintOrDigital() {
        return "Digital";
    }

    @Override
    protected String getRealPrimaryType(final String type) {
        String mappedType = getMappedPrimaryType(type);
        if ("serial".equals(mappedType)) {
            Collection<String> initialTypes = getInitialTypes();
            if (initialTypes.contains("book")) {
                return "Book " + getPrintOrDigital();
            } else if (initialTypes.contains("database")) {
                return "Database";
            } else {
                return "Journal " + getPrintOrDigital();
            }
        } else if ("book".equals(mappedType)) {
            return "Book " + getPrintOrDigital();
        } else if ("visual material".equals(mappedType)) {
            if (getTypes().contains("video")) {
                return "Video";
            } else {
                return "Image";
            }
        } else {
            return mappedType;
        }
    }

    private Collection<String> getAllSubsets() {
        Collection<String> subsets = new ArrayList<String>();
        for (Version version : getVersions()) {
            subsets.addAll(version.getSubsets());
        }
        return subsets;
    }

    private void handleInstalledSoftware(final Collection<String> types, final Collection<String> subsets) {
        if (types.contains("statistics")) {
            types.add("statistics software, installed");
        }
        if (subsets.contains("biotools")) {
            types.add("biotools software, installed");
        }
        for (Version version : getVersions()) {
            // software installed in various locations have the location in
            // the label
            for (Link link : version.getLinks()) {
                handleLinkLabel(types, link.getLabel());
            }
        }
    }

    private void handleLinkLabel(final Collection<String> types, final String label) {
        if (label != null) {
            if (label.indexOf("Redwood") == 0) {
                types.add("redwood software, installed");
            } else if (label.indexOf("Stone") == 0) {
                types.add("stone software, installed");
            } else if (label.indexOf("Duck") == 0) {
                types.add("duck software, installed");
            } else if (label.indexOf("M051") == 0) {
                types.add("m051 software, installed");
            } else if (label.indexOf("Public") == 0) {
                types.add("lksc-public software, installed");
            } else if (label.indexOf("Student") == 0) {
                types.add("lksc-student software, installed");
            }
        }
    }

    private boolean isBassettRecord() {
        boolean isBassett = false;
        Iterator<VariableField> it = this.record.getVariableFields("035").iterator();
        while (!isBassett && it.hasNext()) {
            String value = MarcTextUtil.getSubfieldData((DataField) it.next(), 'a');
            isBassett = value.indexOf("Bassett") > -1;
        }
        return isBassett;
    }

    private String parseYear(final String year) {
        Matcher yearMatcher = ACCEPTED_YEAR_PATTERN.matcher(year);
        if (yearMatcher.matches()) {
            if ("9999".equals(year)) {
                return Integer.toString(THIS_YEAR);
            }
            return year.replace('u', '5');
        }
        return null;
    }
}
