package edu.stanford.irt.eresources.marc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.Version;

public class AuthMarcEresource extends AbstractMarcEresource {

    public static final int THIS_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    private static final Pattern ACCEPTED_YEAR_PATTERN = Pattern.compile("^(\\d[\\d|u]{3}|Continuing)$");

    private static final String AUTH_TYPE = "auth";

    private static final int[] ITEMS = new int[] { 0, 0 };

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private Record record;

    public AuthMarcEresource(final Record record, final String keywords) {
        super(record, keywords);
        this.record = record;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public int[] getItemCount() {
        return ITEMS;
    }

    @Override
    public String getRecordType() {
        return AUTH_TYPE;
    }

    @Override
    public Date getUpdated() {
        try {
            return this.dateFormat.parse(((ControlField) this.record.getVariableField("005")).getData());
        } catch (ParseException e) {
            throw new EresourceException(e);
        }
    }

    @Override
    public List<Version> getVersions() {
        MarcVersion version = new MarcVersion(this.record);
        if (version.getLinks().size() > 0) {
            return Collections.<Version> singletonList(new MarcVersion(this.record));
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public int getYear() {
        int year = 0;
        for (VariableField field : this.record.getVariableFields("943")) {
            boolean hasEndDate = false;
            Subfield subfieldb = ((DataField) field).getSubfield('b');
            if (subfieldb != null) {
                String endDate = parseYear(subfieldb.getData());
                if (endDate != null) {
                    year = Integer.parseInt(endDate);
                    hasEndDate = true;
                }
            }
            if (!hasEndDate) {
                Subfield subfielda = ((DataField) field).getSubfield('a');
                if (subfielda != null) {
                    String beginDate = parseYear(subfielda.getData());
                    if (beginDate != null) {
                        year = Integer.parseInt(beginDate);
                    }
                }
            }
        }
        return year;
    }

    @Override
    public boolean isCore() {
        return false;
    }

    @Override
    protected void addPrimaryType(final Collection<String> t) {
        t.add("other");
    }

    @Override
    protected String getPrintOrDigital() {
        return null;
    }

    @Override
    protected String getRealPrimaryType(final String type) {
        return "Other";
    }

    private String parseYear(final String year) {
        Matcher yearMatcher = ACCEPTED_YEAR_PATTERN.matcher(year);
        if (yearMatcher.matches()) {
            if ("Continuing".equals(year)) {
                return Integer.toString(THIS_YEAR);
            }
            return year.replace('u', '5');
        }
        return null;
    }
}
