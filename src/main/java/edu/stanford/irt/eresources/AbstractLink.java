package edu.stanford.irt.eresources;

public abstract class AbstractLink implements Link {

    protected String getAdditionalText(final String instruction, final String additionalText, final String publisher) {
        StringBuilder sb = new StringBuilder();
        if (publisher != null && !publisher.isEmpty()) {
            sb.append(publisher);
        }
        if (additionalText != null && !additionalText.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(additionalText);
        }
        if (instruction != null && !instruction.isEmpty()) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(instruction);
        }
        return sb.length() == 0 ? null : sb.toString();
    }

    protected String getLinkText(final String label, final Version version) {
        StringBuilder sb = new StringBuilder();
        if ("impact factor".equalsIgnoreCase(label)) {
            sb.append("Impact Factor");
        } else {
            String holdingsAndDates = version.getHoldingsAndDates();
            if (holdingsAndDates != null && version.getLinks().size() == 1) {
                sb.append(holdingsAndDates);
            } else {
                if (label != null) {
                    sb.append(label);
                }
            }
            if (sb.length() == 0) {
                sb.append(label);
            }
        }
        return sb.toString();
    }
}
