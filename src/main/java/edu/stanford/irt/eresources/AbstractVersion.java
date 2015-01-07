package edu.stanford.irt.eresources;

import java.util.List;


public abstract class AbstractVersion implements Version {

    protected String getAdditionalText(final Version version) {
        StringBuilder sb = new StringBuilder(" ");
        String summaryHoldings = version.getSummaryHoldings();
        if (summaryHoldings != null) {
            sb.append(summaryHoldings);
        }
        maybeAppend(sb, version.getDates());
        maybeAppend(sb, version.getPublisher());
        maybeAppend(sb, version.getDescription());
        List<Link> l = version.getLinks();
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

    private void maybeAppend(final StringBuilder sb, final String string) {
        if (string != null && string.length() > 0) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(string);
        }
    }
}
