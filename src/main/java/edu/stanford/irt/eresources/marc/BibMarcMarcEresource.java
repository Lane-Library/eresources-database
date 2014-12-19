package edu.stanford.irt.eresources.marc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
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
public class BibMarcMarcEresource extends AbstractMarcEresource {

    public static final int THIS_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    private static final Pattern ACCEPTED_YEAR_PATTERN = Pattern.compile("^\\d[\\d|u]{3}$");

    private static final String BIB_TYPE = "bib";

    private static final String[][][] CUSTOM_TYPES = { { { "periodical", "newspaper" }, { "ej" } },
        { { "decision support techniques", "calculators, clinical", "algorithms" }, { "cc" } },
        { { "digital video", "digital video, local" }, { "video" } }, { { "book set" }, { "book" } } };

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private List<Record> holdings;

    private int[] items;

    private Record record;

    public BibMarcMarcEresource(final Record record, final List<Record> holdings, final String keywords,
            final int[] items) {
        super(keywords);
        if (record == null) {
            throw new EresourceException("null record");
        }
        if (holdings == null) {
            throw new EresourceException("null holdings");
        }
        this.record = record;
        this.holdings = holdings;
        this.items = items;
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
    protected String doDescription() {
        String d = null;
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
            d = sb.toString();
        }
        return d;
    }

    @Override
    protected int doId() {
        return Integer.parseInt(this.record.getControlNumber());
    }

    @Override
    protected boolean doIsCore() {
        boolean i = false;
        Iterator<VariableField> it = this.record.getVariableFields("655").iterator();
        while (it.hasNext() && !i) {
            i = "core material".equalsIgnoreCase(getSubfieldData((DataField) it.next(), 'a'));
        }
        return i;
    }

    @Override
    protected Collection<String> doMeshTerms() {
        Collection<String> m = new HashSet<String>();
        for (VariableField field : this.record.getVariableFields("650")) {
            if (((DataField) field).getIndicator1() == '4' && "237".indexOf(((DataField) field).getIndicator2()) > -1) {
                m.add(getSubfieldData((DataField) field, 'a').toLowerCase());
            }
        }
        return m;
    }

    @Override
    protected String doPrimaryType() {
        String primaryType = "";
        for (VariableField field : this.record.getVariableFields("655")) {
            DataField datafield = (DataField) field;
            if (datafield.getIndicator1() == '4' && datafield.getIndicator2() == '7') {
                primaryType = datafield.getSubfield('a').getData();
            }
        }
        // remove trailing periods, some probably should have them but
        // voyager puts them on everything :-(
        int lastPeriod = primaryType.lastIndexOf('.');
        if (lastPeriod >= 0) {
            int lastPosition = primaryType.length() - 1;
            if (lastPeriod == lastPosition) {
                primaryType = primaryType.substring(0, lastPosition);
            }
        }
        return primaryType.toLowerCase();
    }

    @Override
    protected String doTitle() {
        StringBuilder sb = new StringBuilder();
        DataField field245 = (DataField) this.record.getVariableField("245");
        for (Subfield subfield : field245.getSubfields()) {
            if ("anpq".indexOf(subfield.getCode()) > -1) {
                append(sb, Normalizer.compose(subfield.getData(), false));
            } else if (subfield.getCode() == 'b') {
                String data = subfield.getData();
                int lengthLessTwo = data.length() - 2;
                if (data.lastIndexOf(" /") == lengthLessTwo) {
                    data = data.substring(0, lengthLessTwo);
                }
                append(sb, Normalizer.compose(data, false));
            }
        }
        DataField field250 = (DataField) this.record.getVariableField("250");
        String edition = getSubfieldData(field250, 'a');
        if (edition != null) {
            sb.append(". ").append(edition);
        }
        int offset = field245.getIndicator2() - 48;
        return sb.toString().substring(offset);
    }

    @Override
    protected Collection<String> doTypes() {
        Collection<String> t = new HashSet<String>();
        for (VariableField field : this.record.getVariableFields("655")) {
            String type = getSubfieldData((DataField) field, 'a').toLowerCase();
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
        addCustomTypes(t);
        return t;
    }

    @Override
    protected Date doUpdated() {
        Date d;
        try {
            d = this.dateFormat.parse(((ControlField) this.record.getVariableField("005")).getData());
        } catch (ParseException e) {
            throw new EresourceException(e);
        }
        for (Record holding : this.holdings) {
            Date holdingUpdated;
            try {
                holdingUpdated = this.dateFormat.parse(((ControlField) holding.getVariableField("005")).getData());
            } catch (ParseException e) {
                throw new EresourceException(e);
            }
            if (holdingUpdated.compareTo(d) > 0) {
                d = holdingUpdated;
            }
        }
        return d;
    }

    @Override
    protected List<Version> doVersions() {
        Collection<Version> versions = new TreeSet<Version>(new VersionComparator());
        for (Record holding : this.holdings) {
            Version version = new MarcVersion(holding);
            if (version.getLinks().size() > 0) {
                versions.add(new MarcVersion(holding));
            }
        }
        return new ArrayList<Version>(versions);
    }

    @Override
    protected int doYear() {
        int y = 0;
        String currentText = ((ControlField) this.record.getVariableField("008")).getData();
        String endDate = parseYear(currentText.substring(11, 15));
        String beginDate = parseYear(currentText.substring(7, 11));
        if (null != endDate) {
            y = Integer.parseInt(endDate);
        } else if (null != beginDate) {
            y = Integer.parseInt(beginDate);
        }
        return y;
    }

    private void addCustomTypes(final Collection<String> types) {
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
            String value = getSubfieldData((DataField) it.next(), 'a');
            isBassett = value != null && value.indexOf("Bassett") > -1;
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
