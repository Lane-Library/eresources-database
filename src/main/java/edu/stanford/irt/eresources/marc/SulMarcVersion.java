package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

/**
 * MarcVersion encapsulates a holding record.
 */
public class SulMarcVersion extends MARCRecordSupport implements Version {

    private static final Pattern BOOK_OR_VIDEO = Pattern.compile("^(Book|Video).*");

    private static final Pattern SPACE_EQUALS = Pattern.compile(" =");

    private Record bib;

    private Eresource eresource;

    public SulMarcVersion(final Record bib, final Eresource eresource) {
        this.bib = bib;
        this.eresource = eresource;
    }

    @Override
    public String getAdditionalText() {
        String additionalText = null;
        // doubt 866 ^z present in sul data
        List<Field> fields = getFields(this.bib, "866").collect(Collectors.toList());
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
        // doubt 866 ^y present in sul data
        Field field = getFields(this.bib, "866").findFirst().orElse(null);
        String dates = null;
        if (field != null) {
            dates = field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'y')
                    .map(Subfield::getData).findFirst().orElse(null);
        }
        if (dates == null && needToAddBibDates(this.eresource)) {
            // 260c or 008 date1
            // add 264c?
            dates = getSubfieldData(this.bib, "260", "c").findFirst()
                    .orElse(Integer.toString(this.eresource.getYear()));
        }
        return dates;
    }

    @Override
    public String getHoldingsAndDates() {
        StringBuilder sb = new StringBuilder();
        if (getSummaryHoldings() != null) {
            sb.append(getSummaryHoldings());
        }
        TextParserHelper.appendMaybeAddComma(sb, getDates());
        return sb.length() == 0 ? null : sb.toString();
    }

    @Override
    public List<Link> getLinks() {
        List<Link> links = new ArrayList<>();
        Version version = this;
        links.addAll(getFields(this.bib, "956").map((final Field f) -> new SulMarcLink(f, version))
                .collect(Collectors.toList()));
        if (links.isEmpty()) {
            links.addAll(getFields(this.bib, "856").map((final Field f) -> new SulMarcLink(f, version))
                    .collect(Collectors.toList()));
        }
        if (links.isEmpty()) {
            links.add(new CatalogLink(Integer.toString(this.eresource.getRecordId()), this,
                    "https://searchworks.stanford.edu/view/", "SU Catalog (SearchWorks)"));
        }
        return links;
    }

    @Override
    public String getPublisher() {
        // doubt 844 is in SUL data
        String publisher = null;
        Field field = getFields(this.bib, "844").findFirst().orElse(null);
        if (field != null) {
            publisher = field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'a')
                    .map(Subfield::getData).findFirst().orElse(null);
        }
        return publisher;
    }

    @Override
    public String getSummaryHoldings() {
        String value = null;
        Field field = getFields(this.bib, "362").findFirst().orElse(null);
        if (field != null) {
            value = field.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'a')
                    .map(Subfield::getData).map((final String s) -> SPACE_EQUALS.matcher(s).replaceAll("")).findFirst()
                    .orElse(null);
        }
        return value;
    }

    @Override
    public boolean hasGetPasswordLink() {
        return false;
    }

    @Override
    public boolean isProxy() {
        return true;
    }

    private boolean needToAddBibDates(final Eresource eresource) {
        return null == getSummaryHoldings() && eresource.getPublicationText().isEmpty()
                && BOOK_OR_VIDEO.matcher(eresource.getPrimaryType()).matches();
    }
}
