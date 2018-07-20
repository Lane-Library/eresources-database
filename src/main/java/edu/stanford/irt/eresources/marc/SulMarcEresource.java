package edu.stanford.irt.eresources.marc;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.irt.eresources.Version;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class SulMarcEresource extends AbstractMarcEresource {

    private SulTypeFactory sulTypeFactory;

    public SulMarcEresource(final Record record, final KeywordsStrategy keywordsStrategy,
            final SulTypeFactory typeFactory) {
        this.record = record;
        this.keywordsStrategy = keywordsStrategy;
        this.sulTypeFactory = typeFactory;
    }

    @Override
    public Collection<String> getAbbreviatedTitles() {
        return getSubfieldData(getFields(this.record, "246").filter((final Field f) -> {
            Subfield i = f.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'i').findFirst()
                    .orElse(null);
            Subfield a = f.getSubfields().stream().filter((final Subfield s) -> s.getCode() == 'a').findFirst()
                    .orElse(null);
            return i != null && a != null && "Also known as:".equalsIgnoreCase(i.getData());
        }), "a").collect(Collectors.toSet());
    }

    @Override
    public Collection<String> getBroadMeshTerms() {
        // SUL unlikely to have 650 42 ^a
        return getSubfieldData(getFields(this.record, "650")
                .filter((final Field f) -> f.getIndicator1() == '4' && f.getIndicator2() == '2'), "a")
                        .map(TextParserHelper::maybeStripTrailingPeriod).collect(Collectors.toSet());
    }

    @Override
    public int[] getItemCount() {
        return new int[2];
    }

    @Override
    public String getKeywords() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.keywordsStrategy.getKeywords(this.record));
        return sb.toString();
    }

    @Override
    public Collection<String> getMeshTerms() {
        // doubtful SUL will have these
        return getSubfieldData(getFields(this.record, "650|651")
                .filter((final Field f) -> ("650".equals(f.getTag()) && "2356".indexOf(f.getIndicator2()) > -1)
                        || ("651".equals(f.getTag()) && f.getIndicator2() == '7')),
                "a").map(TextParserHelper::maybeStripTrailingPeriod).collect(Collectors.toSet());
    }

    @Override
    public String getPrimaryType() {
        if (this.primaryType == null) {
            this.primaryType = this.sulTypeFactory.getPrimaryType(this.record);
        }
        return this.primaryType;
    }

    @Override
    public int getRecordId() {
        int i;
        String f001 = getFields(this.record, "001").map(Field::getData).findFirst().orElse("0").replaceAll("\\D", "");
        try {
            i = Integer.parseInt(f001);
        } catch (NumberFormatException e) {
            i = 0;
        }
        return i;
    }

    @Override
    public String getRecordType() {
        return "sul";
    }

    @Override
    public String getShortTitle() {
        String title = getSubfieldData(this.record, "222", "a").findFirst().orElse(null);
        if (null == title) {
            title = getSubfieldData(this.record, "245", "a").findFirst().orElse(null);
        }
        return title;
    }

    @Override
    public Collection<String> getTypes() {
        if (this.types == null) {
            this.types = this.sulTypeFactory.getTypes(this.record);
        }
        return new ArrayList<>(this.types);
    }

    @Override
    public LocalDateTime getUpdated() {
        try {
            return LocalDateTime.parse(getFields(this.record, "005").map(Field::getData).findFirst().orElse(null),
                    FORMATTER);
        } catch (DateTimeParseException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    @Override
    public List<Version> getVersions() {
        if (this.versions == null) {
            Collection<Version> versionSet = new TreeSet<>(COMPARATOR);
            Version version = createVersion(this.record);
            if (!version.getLinks().isEmpty()) {
                versionSet.add(version);
            }
            this.versions = Collections.unmodifiableList(new ArrayList<>(versionSet));
        }
        return new ArrayList<>(this.versions);
    }

    @Override
    public boolean isCore() {
        return false;
    }

    @Override
    public boolean isLaneConnex() {
        return false;
    }

    @Override
    protected Version createVersion(final Record record) {
        return new SulMarcVersion(record, this);
    }
}
