package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.marc4j.marc.VariableField;

import edu.stanford.irt.eresources.AbstractVersion;
import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

/**
 * MarcVersion encapsulates a holding record.
 */
public class MarcVersion extends AbstractVersion {
    
    private static final Pattern PATTERN = Pattern.compile(" =");

    private String additionalText;

    private boolean hasGetPassword = false;

    private ArrayList<Link> links;

    private Record record;

    public MarcVersion(final Record record) {
        if (record == null) {
            throw new EresourceException("null record");
        }
        this.record = record;
    }

    @Override
    public String getAdditionalText() {
        if (this.additionalText == null) {
            this.additionalText = createAdditionalText();
        }
        return this.additionalText;
    }

    @Override
    public String getDates() {
        return MarcTextUtil.getSubfieldData((DataField) this.record.getVariableField("866"), 'y');
    }

    @Override
    public String getDescription() {
        String description = null;
        List<VariableField> fields = this.record.getVariableFields("866");
        if (fields.size() > 1) {
            description = "";
        } else if (fields.size() == 1) {
            DataField field = (DataField) fields.get(0);
            // TODO: review getting the last one if multiple, this is was SAX processing did
            for (Subfield subfield : field.getSubfields('z')) {
                description = subfield.getData();
            }
        }
        return description;
    }

    @Override
    public List<Link> getLinks() {
        if (this.links == null) {
            setupLinks();
        }
        return this.links;
    }

    @Override
    public String getPublisher() {
        return MarcTextUtil.getSubfieldData((DataField) this.record.getVariableField("844"), 'a');
    }

    @Override
    public String getSummaryHoldings() {
        String value = MarcTextUtil.getSubfieldData((DataField) this.record.getVariableField("866"), 'v');
        if (value != null) {
            value = PATTERN.matcher(value).replaceAll("");
        }
        return value;
    }

    @Override
    public boolean hasGetPasswordLink() {
        if (this.links == null) {
            setupLinks();
        }
        return this.hasGetPassword;
    }

    @Override
    public boolean isProxy() {
        boolean isProxy = true;
        Iterator<VariableField> it = this.record.getVariableFields("655").iterator();
        while (isProxy && it.hasNext()) {
            isProxy = !"subset, noproxy".equalsIgnoreCase(MarcTextUtil.getSubfieldData((DataField) it.next(), 'a'));
        }
        return isProxy;
    }

    @Override
    public String toString() {
        return this.record.toString();
    }

    protected Link createLink(final DataField field, final Version version) {
        return new MarcLink(field, this);
    }

    private void setupLinks() {
        this.links = new ArrayList<Link>();
        for (VariableField field : this.record.getVariableFields("856")) {
            if ("http://lane.stanford.edu/secure/ejpw.html"
                    .equals(MarcTextUtil.getSubfieldData((DataField) field, 'u'))) {
                this.hasGetPassword = true;
            } else {
                this.links.add(createLink((DataField) field, this));
            }
        }
    }
}
