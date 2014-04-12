package edu.stanford.irt.eresources.marc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.marc4j.marc.ControlField;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

import com.ibm.icu.text.Normalizer;

import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;


public class AuthMarcEresource extends AbstractMarcEresource {
    
    private static final String AUTH_TYPE = "auth";
    private Record record;


    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    
    public AuthMarcEresource(Record record, String keywords) {
        super(keywords);
        this.record = record;
    }

    public String getType() {
        return AUTH_TYPE;
    }

    @Override
    protected String doDescription() {
        return null;
    }

    @Override
    protected int doId() {
        return Integer.parseInt(this.record.getControlNumber());
    }

    @Override
    protected boolean doIsCore() {
        return false;
    }

    @Override
    protected List<Version> doVersions() {
        return Collections.<Version>singletonList(new MarcVersion(this.record));
    }

    @Override
    protected Collection<String> doMeshTerms() {
        Collection<String> m = new LinkedList<String>();
        for (VariableField field : this.record.getVariableFields("650")) {
            if (((DataField) field).getIndicator1() == '4' && "27".indexOf(((DataField) field).getIndicator2()) > -1) {
                m.add(getSubfieldData((DataField) field, 'a').toLowerCase());
            }
        }
        return m;
    }

    @Override
    protected String doTitle() {
        StringBuilder sb = new StringBuilder();
        DataField field245 = (DataField) this.record.getVariableField("245");
        for (Subfield subfield : field245.getSubfields()) {
            if ("anpq".indexOf(subfield.getCode()) > -1) {
                append(sb, Normalizer.compose(subfield.getData(), false));
            }
        }
        int offset = field245.getIndicator2() - '0';
        return sb.toString().substring(offset);
    }

    @Override
    protected Collection<String> doTypes() {
        Collection<String> t = new LinkedList<String>();
        for (VariableField field : this.record.getVariableFields("655")) {
            String type = getSubfieldData((DataField) field, 'a').toLowerCase();
            if (!"laneconnex".equals(type) && !"internet resource".equals(type) && type.indexOf("subset") != 0) {
                // remove trailing periods, some probably should have them but
                // voyager puts them on everything :-(
                int lastPeriod = type.lastIndexOf('.');
                if (lastPeriod >= 0) {
                    int lastPosition = type.length() - 1;
                    if (lastPeriod == lastPosition) {
                        type = type.substring(0, lastPosition);
                    }
                }
                t.add(type);
            }
        }
        return t;
    }

    @Override
    protected Date doUpdated() {
        try {
            return this.dateFormat.parse(((ControlField) this.record.getVariableField("005")).getData());
        } catch (ParseException e) {
            throw new EresourceException(e);
        }
    }

    @Override
    protected int doYear() {
        return 0;
    }

    @Override
    public String getRecordType() {
        return AUTH_TYPE;
    }
}
