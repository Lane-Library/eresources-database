package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

/**
 * MarcVersion encapsulates a holding record.
 */
public class MarcVersion extends AbstractMARCRecordObject implements Version {

    private static final Pattern PATTERN = Pattern.compile(" =");

    private Record bib;

    private Eresource eresource;

    private Record holding;

    public MarcVersion(final Record holding, final Record bib, final Eresource eresource) {
        this.holding = holding;
        this.bib = bib;
        this.eresource = eresource;
    }

    @Override
    public String getAdditionalText() {
        String additionalText = null;
        List<Field> fields = this.holding.getFields()
                .stream()
                .filter(f -> "866".equals(f.getTag()))
                .collect(Collectors.toList());
        if (fields.size() > 1) {
            additionalText = "";
        } else if (fields.size() == 1) {
            additionalText = fields.get(0)
                    .getSubfields().stream()
                    .filter(s -> 'z' == s.getCode())
                    .map(Subfield::getData)
                    .findFirst()
                    .orElse(null);
        }
        return additionalText;
    }

    @Override
    public String getDates() {
        String dates = getSubfieldData(this.holding.getFields()
                .stream()
                .filter(f -> "866".equals(f.getTag()))
                .findFirst()
                .orElse(null), 'y');
        if (dates == null && needToAddBibDates(this.eresource)) {
            dates = this.bib.getFields()
                    .stream()
                    .filter(f -> "149".equals(f.getTag()))
                    .flatMap(f -> f.getSubfields().stream())
                    .filter(s -> s.getCode() == 'd')
                    .map(Subfield::getData)
                    .findFirst()
                    .orElse(null);
            if (dates == null) {
                dates = this.bib.getFields()
                        .stream()
                        .filter(f -> "260".equals(f.getTag()))
                        .flatMap(f -> f.getSubfields().stream())
                        .filter(s -> s.getCode() == 'c')
                        .map(Subfield::getData)
                        .findFirst().orElse("");
            }
        }
        return dates;
    }

    public boolean getHasGetPasswordLink() {
        return hasGetPasswordLink();
    }

    @Override
    public String getHoldingsAndDates() {
        StringBuilder sb = new StringBuilder();
        if (getSummaryHoldings() != null) {
            sb.append(getSummaryHoldings());
        }
        maybeAppend(sb, getDates());
        return sb.length() == 0 ? null : sb.toString();
    }

    @Override
    public List<Link> getLinks() {
        List<Link> links = new ArrayList<>();
        boolean has856 = this.holding.getFields()
                .stream()
                .filter(f -> "856".equals(f.getTag()))
                .count() > 0;
        if (!has856) {
            links.add(new CatalogLink(this.bib.getFields()
                    .stream()
                    .filter(f -> "001".equals(f.getTag()))
                    .map(f -> f.getData())
                    .findFirst()
                    .orElse(null), this));
        }
        for (Field field : this.holding.getFields()) {
            if ("856".equals(field.getTag())
                    && !"http://lane.stanford.edu/secure/ejpw.html".equals(getSubfieldData(field, 'u'))) {
                links.add(new MarcLink(field, this));
            }
        }
        return links;
    }

    @Override
    public String getPublisher() {
        return getSubfieldData(
                this.holding.getFields()
                .stream()
                .filter(f -> "844".equals(f.getTag()))
                .findFirst()
                .orElse(null), 'a');
    }

    @Override
    public String getSummaryHoldings() {
        String value = getSubfieldData(
                this.holding.getFields()
                .stream()
                .filter(f -> "866".equals(f.getTag()))
                .findFirst()
                .orElse(null), 'v');
        if (value != null) {
            value = PATTERN.matcher(value).replaceAll("");
        }
        return value;
    }

    @Override
    public boolean hasGetPasswordLink() {
        return this.holding.getFields()
                .stream()
                .filter(f -> "856".equals(f.getTag()))
                .flatMap(f -> f.getSubfields().stream())
                .filter(s -> s.getCode() == 'u')
                .map(Subfield::getData)
                .anyMatch("http://lane.stanford.edu/secure/ejpw.html"::equals);
    }

    @Override
    public boolean isProxy() {
        return true;
    }

    private void maybeAppend(final StringBuilder sb, final String string) {
        if (string != null && string.length() > 0) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(string);
        }
    }

    private boolean needToAddBibDates(final Eresource eresource) {
        return null == getSummaryHoldings() && eresource.getPublicationText().isEmpty()
                && eresource.getPrimaryType().matches("^(Book|Video).*")
                && !getLinks().stream().anyMatch(l -> "impact factor".equalsIgnoreCase(l.getLabel()));
    }
}
