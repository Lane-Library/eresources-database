package edu.stanford.irt.eresources.marc.sul;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.marc.CatalogLink;
import edu.stanford.irt.eresources.marc.CatalogLink.Type;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

/**
 * MarcVersion encapsulates a holding record.
 */
public class SulMarcVersion extends MARCRecordSupport implements Version {

    private static final Pattern SPACE_EQUALS = Pattern.compile(" =");

    private Record bib;

    private Eresource eresource;

    private boolean isProxy;

    public SulMarcVersion(final Record bib, final Eresource eresource) {
        this.bib = bib;
        this.eresource = eresource;
    }

    @Override
    public String getAdditionalText() {
        String additionalText = null;
        // doubt 866 ^z present in sul data
        List<Field> fields = getFields(this.bib, "866").toList();
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
        String dates = null;
        if (needToAddBibDates(this.eresource)) {
            if (this.eresource.getPrimaryType().startsWith("Journal")) {
                dates = getYears(this.bib);
            } else {
                // 260/4c or 008 date1
                dates = getSubfieldData(getFields(this.bib, "260|264"), "c").findFirst()
                        .orElse(Integer.toString(this.eresource.getYear()));
            }
            dates = TextParserHelper.maybeStripTrailingPeriod(dates);
            dates = TextParserHelper.maybeStripTrailingUnbalancedBracket(dates);
        }
        return dates;
    }

    @Override
    public List<Link> getLinks() {
        List<Link> links = new ArrayList<>();
        Version version = this;
        links.addAll(getFields(this.bib, "956").map((final Field f) -> new SulMarcLink(f, version))
                .toList());
        if (links.isEmpty()) {
            links.addAll(getFields(this.bib, "856").map((final Field f) -> new SulMarcLink(f, version))
                    .toList());
        }
        if (links.isEmpty()) {
            links.add(new CatalogLink(Type.SUL, this.eresource.getRecordId(), this));
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
        return getSubfieldData(this.bib, "362", "a").map((final String s) -> SPACE_EQUALS.matcher(s).replaceAll(""))
                .findFirst().orElse(null);
        // maybe second check of 866 ^a s here ... if journal?
        // is 362 just a better version of 866s?
    }

    @Override
    public boolean isProxy() {
        return this.isProxy;
    }

    public void setIsProxy(final boolean proxy) {
        this.isProxy = proxy;
    }

    private boolean needToAddBibDates(final Eresource eresource) {
        return null == getSummaryHoldings() && eresource.getPublicationText().isEmpty();
    }
}
