package edu.stanford.irt.eresources.marc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.VariableField;

import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

/**
 * MarcVersion encapsulates a holding record.
 */
public class MarcVersion extends AbstractMarcComponent implements Version {

    private static final Set<String> ALLOWED_SUBSETS = new HashSet<String>();

    private static final String[] ALLOWED_SUBSETS_INITIALIZER = { "mobile applications", "pda tools",
            "mobile resources", "biotools" };
    static {
        for (String subset : ALLOWED_SUBSETS_INITIALIZER) {
            ALLOWED_SUBSETS.add(subset);
        }
    }

    private static final String[][] CUSTOM_SUBSETS = { { "redwood room", "redwood" }, { "stone room", "stone" },
            { "duck room", "duck" }, { "m230", "m230" }, { "public kiosks", "lksc-public" },
            { "student computing", "lksc-student" } };

    private static final Pattern PATTERN = Pattern.compile(" =");

    private Record record;
    
    private boolean hasGetPassword = false;

    private LinkedList<Link> links;

    private String additionalText;

    public MarcVersion(final Record record) {
        if (record == null) {
            throw new EresourceException("null record");
        }
        this.record = record;
    }

    public String getDates() {
        return getSubfieldData((DataField) this.record.getVariableField("866"), 'y');
    }

    public String getDescription() {
        return getSubfieldData((DataField) this.record.getVariableField("866"), 'z');
    }
    
    public List<Link> getLinks() {
        if (this.links == null) {
            setupLinks();
        }
        return this.links;
    }
    
    public boolean hasGetPassword() {
        if (this.links == null) {
            setupLinks();
        }
        return this.hasGetPassword;
    }

    private void setupLinks() {
        this.links = new LinkedList<Link>();
        for (VariableField field : this.record.getVariableFields("856")) {
            if ("http://lane.stanford.edu/secure/ejpw.html".equals(getSubfieldData((DataField)field, 'u'))) {
                this.hasGetPassword = true;
            } else {
                MarcLink link = new MarcLink((DataField) field, this);
                this.links.add(link);
            }
        }
//        String summaryHoldings = getSummaryHoldings();
//        String dates = getDates();
//        String publisher = getPublisher();
//        StringBuilder sb = new StringBuilder();
//        for (int i = 0; i < this.links.size(); i++) {
//            Link oldLink = (Link) links.get(i);
//            sb.setLength(0);
//            if (this.links.size() == 1 && summaryHoldings != null) {
//                sb.append(summaryHoldings);
//                if (dates != null) {
//                    sb.append(", ").append(dates);
//                }
//            } else {
//                sb.append(oldLink.getLabel());
//            }
//            if (sb.length() == 0) {
//                sb.append(oldLink.getUrl());
//            }
//            String description = getDescription();
//            if (description != null) {
//                sb.append(" ").append(description);
//            }
//            String newLabel = sb.toString();
//            sb.setLength(0);
//            if (publisher != null) {
//                String oldInstruction = oldLink.getInstruction();
//                if (oldInstruction != null) {
//                    sb.append(oldInstruction).append(" ");
//                }
//                sb.append(publisher);
//            }
//            if (hasGetPassword()) {
//                sb.append("GETPASSWORD");
//            }
//            String newInstruction = sb.length() == 0 ? null : sb.toString();
//            AugmentedMarcLink newLink = new AugmentedMarcLink(oldLink, newLabel, newInstruction);
//            this.links.set(i, newLink);
//        }
    }

    public String getPublisher() {
        return getSubfieldData((DataField) this.record.getVariableField("844"), 'a');
    }

    public Collection<String> getSubsets() {
        Collection<String> subsets = new TreeSet<String>();
        Iterator<VariableField> it = this.record.getVariableFields("655").iterator();
        while (it.hasNext()) {
            String subset = getSubfieldData((DataField) it.next(), 'a').toLowerCase();
            if (subset.indexOf("subset, ") == 0 && !"subset, noproxy".equals(subset)) {
                subset = subset.substring(8);
                if (ALLOWED_SUBSETS.contains(subset)) {
                    subsets.add(subset);
                }
            }
        }
        addCustomSubsets(subsets);
        return subsets;
    }

    public String getSummaryHoldings() {
        String value = getSubfieldData((DataField) this.record.getVariableField("866"), 'v');
        if (value != null) {
            value = PATTERN.matcher(value).replaceAll("");
        }
        return value;
    }

    public boolean isProxy() {
        boolean isProxy = true;
        Iterator<VariableField> it = this.record.getVariableFields("655").iterator();
        while (isProxy && it.hasNext()) {
            isProxy = !"subset, noproxy".equalsIgnoreCase(getSubfieldData((DataField) it.next(), 'a'));
        }
        return isProxy;
    }

    @Override
    public String toString() {
        return this.record.toString();
    }

    private void addCustomSubsets(final Collection<String> subsets) {
        for (Link link : getLinks()) {
            String label = link.getLabel();
            if (label != null) {
                label = label.toLowerCase();
                for (String[] element : CUSTOM_SUBSETS) {
                    if (label.indexOf(element[0]) == 0) {
                        subsets.add(element[1]);
                    }
                }
            }
        }
    }

    @Override
    public String getAdditionalText() {
        if (this.additionalText == null) {
            this.additionalText = doAdditionalText();
        }
        return this.additionalText;
    }

    private String doAdditionalText() {
        StringBuilder sb = new StringBuilder(" ");
        String summaryHoldings = getSummaryHoldings();
        if (summaryHoldings != null) {
            sb.append(summaryHoldings);
        }
        maybeAppend(sb, getDates());
        maybeAppend(sb, getPublisher());
        maybeAppend(sb, getDescription());
        List<Link> l = getLinks();
        if (l != null && !l.isEmpty()) {
            Link firstLink = l.get(0);
            String label = firstLink.getLabel();
            if (sb.length() == 1 && label != null) {
                sb.append(label);
            }
            String instruction = firstLink.getInstruction();
            if (instruction != null) {
                if (sb.length() > 1) {
                    sb.append(", ");
                }
                sb.append(instruction);
            }
        }
        if (sb.length() > 1) {
            sb.append(" ");
        }
        return sb.toString();
    }

    @Override
    public boolean hasGetPasswordLink() {
        // TODO Auto-generated method stub
        return false;
    }

    private void maybeAppend(final StringBuilder sb, final String string) {
        if (string != null && string.length() > 0) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(string);
        }
    }
}
