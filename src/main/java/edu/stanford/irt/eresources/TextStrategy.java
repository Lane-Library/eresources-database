package edu.stanford.irt.eresources;

import java.util.List;

public class TextStrategy {

    public String getAdditionalText(final String instruction, final String publisher) {
        StringBuilder sb = new StringBuilder();
        if (instruction != null) {
            sb.append(" ").append(instruction);
        }
        if (publisher != null) {
            sb.append(" ").append(publisher);
        }
        return sb.toString();
    }

    public String getAdditionalText(final Version version) {
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

    public String getLinkText(final String label, final Version version) {
        StringBuilder sb = new StringBuilder();
        if ("impact factor".equalsIgnoreCase(label)) {
            sb.append("Impact Factor");
        } else {
            String summaryHoldings = version.getSummaryHoldings();
            if (summaryHoldings != null && version.getLinks().size() == 1) {
                sb.append(summaryHoldings);
                String dates = version.getDates();
                if (dates != null && dates.length() > 0) {
                    sb.append(", ").append(dates);
                }
            } else {
                if (label != null) {
                    sb.append(label);
                }
            }
            if (sb.length() == 0) {
                sb.append(label);
            }
            String description = version.getDescription();
            if (description != null && description.length() > 0) {
                sb.append(" ").append(description);
            }
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
