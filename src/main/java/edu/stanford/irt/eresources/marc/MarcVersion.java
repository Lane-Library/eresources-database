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
public class MarcVersion extends MARCRecordSupport implements Version {

    private static final Pattern BOOK_OR_VIDEO = Pattern.compile("^(Book|Video).*");

    private static final Pattern SPACE_EQUALS = Pattern.compile(" =");

    private Record bib;

    private Eresource eresource;

    private Record holding;

    public MarcVersion(final Record holding, final Record bib, final Eresource eresource) {
        this.holding = holding;
        this.bib = bib;
        this.eresource = eresource;
    }

    private static boolean isGetPassword856(final Field field) {
        return field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'u').map(Subfield::getData)
                .anyMatch("http://lane.stanford.edu/secure/ejpw.html"::equals);
    }

    @Override
    public String getAdditionalText() {
        String additionalText = null;
        List<Field> fields = getFields(this.holding, "866").collect(Collectors.toList());
        if (fields.size() > 1) {
            additionalText = "";
        } else if (fields.size() == 1) {
            additionalText = fields.get(0).getSubfields().stream().filter((final Subfield s) -> 'z' == s.getCode())
                    .map(Subfield::getData).findFirst().orElse(null);
        }
        return additionalText;
    }

    @Override
    public String getDates() {
        Field field = getFields(this.holding, "866").findFirst().orElse(null);
        String dates = null;
        if (field != null) {
            dates = field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'y')
                    .map(Subfield::getData).findFirst().orElse(null);
        }
        if (dates == null && needToAddBibDates(this.eresource)) {
            dates = getSubfieldData(this.bib, "149", "d").findFirst().orElse(null);
            if (dates == null) {
                dates = getSubfieldData(this.bib, "260", "c").findFirst().orElse("");
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
        boolean has856 = getFields(this.holding, "856").count() > 0;
        if (!has856) {
            links.add(new CatalogLink(getFields(this.bib, "001").map(Field::getData).findFirst().orElse(null), this));
        }
        Version version = this;
        links.addAll(getFields(this.holding, "856").filter((final Field f) -> !isGetPassword856(f))
                .map((final Field f) -> new MarcLink(f, version)).collect(Collectors.toList()));
        return links;
    }

    @Override
    public String getPublisher() {
        String publisher = null;
        Field field = getFields(this.holding, "844").findFirst().orElse(null);
        if (field != null) {
            publisher = field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'a')
                    .map(Subfield::getData).findFirst().orElse(null);
        }
        return publisher;
    }

    @Override
    public String getSummaryHoldings() {
        String value = null;
        Field field = getFields(this.holding, "866").findFirst().orElse(null);
        if (field != null) {
            value = field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'v')
                    .map(Subfield::getData).map((final String s) -> SPACE_EQUALS.matcher(s).replaceAll("")).findFirst()
                    .orElse(null);
        }
        return value;
    }

    @Override
    public boolean hasGetPasswordLink() {
        return getSubfieldData(this.holding, "856", "u").anyMatch("http://lane.stanford.edu/secure/ejpw.html"::equals);
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
                && BOOK_OR_VIDEO.matcher(eresource.getPrimaryType()).matches()
                && !getLinks().stream().anyMatch((final Link l) -> "impact factor".equalsIgnoreCase(l.getLabel()));
    }
}
