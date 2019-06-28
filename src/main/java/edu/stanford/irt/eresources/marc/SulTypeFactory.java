package edu.stanford.irt.eresources.marc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import edu.stanford.irt.eresources.EresourceConstants;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class SulTypeFactory extends MARCRecordSupport {

    private static final Set<String> ALLOWED_TYPES = new HashSet<>();

    private static final Pattern BEGIN_OR_END_BRACKET_MAYBE_SPACE_COLON = Pattern.compile("(^\\[|\\]( :)?$)");

    private static final Map<String, String> COMPOSITE_TYPES = new HashMap<>();

    private static final String[][] COMPOSITE_TYPES_INITIALIZER = { { EresourceConstants.AUDIO, "Sound Recording" },
            { EresourceConstants.BOOK, "Book Sets", "Books" }, { EresourceConstants.DATABASE, "Databases" },
            { "Dataset", "Datasets" }, { EresourceConstants.IMAGE, "Graphics" },
            { EresourceConstants.JOURNAL, "Journal/Periodical", "Newspaper" },
            { EresourceConstants.SOFTWARE, "Software/Multimedia" } };

    private static final Map<String, String> PRIMARY_TYPES = new HashMap<>();

    private static final Pattern SFX_LINK = Pattern.compile("^https?://library.stanford.edu/sfx.*",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern SUPPLEMENTAL_LINK = Pattern
            .compile(".*(table of contents|abstract|description|sample text|finding aid).*", Pattern.CASE_INSENSITIVE);
    static {
        for (String type : TypeFactory.ALLOWED_TYPES_INITIALIZER) {
            ALLOWED_TYPES.add(type);
        }
        for (String[] element : COMPOSITE_TYPES_INITIALIZER) {
            for (int j = 1; j < element.length; j++) {
                COMPOSITE_TYPES.put(element[j], element[0]);
            }
        }
        // plurals tend to come from 6xx ^v
        PRIMARY_TYPES.put("book", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("database", EresourceConstants.DATABASE);
        PRIMARY_TYPES.put("databases", EresourceConstants.DATABASE);
        PRIMARY_TYPES.put("journal", EresourceConstants.JOURNAL);
        PRIMARY_TYPES.put("periodicals", EresourceConstants.JOURNAL);
        PRIMARY_TYPES.put("sound recording", EresourceConstants.AUDIO);
        PRIMARY_TYPES.put("music recording", EresourceConstants.AUDIO);
        PRIMARY_TYPES.put("video", EresourceConstants.VIDEO);
    }

    private static Stream<Field> getFieldsWild(final Record record, final String tagString) {
        return record.getFields().stream().filter((final Field f) -> f.getTag().matches(tagString));
    }

    public String getPrimaryType(final Record record) {
        String primaryType = EresourceConstants.OTHER;
        List<String> types = getTypes(record);
        if (!types.isEmpty()) {
            primaryType = types.get(0);
        }
        primaryType = PRIMARY_TYPES.get(primaryType.toLowerCase(Locale.US));
        String type;
        if (primaryType == null) {
            type = EresourceConstants.OTHER;
        } else if (EresourceConstants.BOOK.equals(primaryType)) {
            type = EresourceConstants.BOOK + EresourceConstants.SPACE + getPrintOrDigital(record);
        } else if (EresourceConstants.JOURNAL.equals(primaryType)) {
            type = EresourceConstants.JOURNAL + EresourceConstants.SPACE + getPrintOrDigital(record);
        } else {
            type = primaryType;
        }
        return type;
    }

    public List<String> getTypes(final Record record) {
        List<String> types = new ArrayList<>();
        for (String type : SulTypeFactoryHelper.getTypes(record)) {
            types.add(getCompositeType(type));
        }
        for (String type : getRawTypes(record)) {
            if (!types.contains(type)) {
                types.add(type);
            }
        }
        return types;
    }

    private String getCompositeType(final String type) {
        if (COMPOSITE_TYPES.containsKey(type)) {
            return COMPOSITE_TYPES.get(type);
        }
        return type;
    }

    private String getPrintOrDigital(final Record record) {
        List<Field> linkFields = getFieldsWild(record, "[8|9]56").filter(
                (final Field f) -> f.getSubfields().stream().anyMatch((final Subfield sf) -> sf.getCode() == 'u'))
                .collect(Collectors.toList());
        int digitalLinks = (int) linkFields.stream().filter(this::isDigitalLink).count();
        if (digitalLinks > 0) {
            return "Digital";
        }
        return "Print";
    }

    private Collection<String> getRawTypes(final Record record) {
        Set<String> rawTypes = new HashSet<>();
        List<Field> fields655 = getFields(record, "655").collect(Collectors.toList());
        rawTypes.addAll(getSubfieldData(fields655.stream(), "a").map(TextParserHelper::maybeStripTrailingPeriod)
                .collect(Collectors.toSet()));
        List<Field> fields6xx = getFieldsWild(record, "6..").collect(Collectors.toList());
        rawTypes.addAll(getSubfieldData(fields6xx.stream(), "v").map(TextParserHelper::maybeStripTrailingPeriod)
                .collect(Collectors.toSet()));
        rawTypes.addAll(getSubfieldData(record, "245", "h").map(TextParserHelper::maybeStripTrailingPeriod)
                .map((final String s) -> BEGIN_OR_END_BRACKET_MAYBE_SPACE_COLON.matcher(s).replaceAll(""))
                .map(TextParserHelper::toTitleCase).collect(Collectors.toSet()));
        rawTypes.addAll(
                getSubfieldData(record, "999", "t").map(TextParserHelper::toTitleCase).collect(Collectors.toSet()));
        return rawTypes.stream().map(this::getCompositeType).filter(ALLOWED_TYPES::contains)
                .collect(Collectors.toSet());
    }

    // logic from:
    // https://github.com/sul-dlss/searchworks_traject_indexer/blob/master/lib/marc_links.rb#L122
    private boolean isDigitalLink(final Field field) {
        char ind2 = field.getIndicator2();
        if (" 2".indexOf(ind2) > -1) {
            return false;
        }
        for (Subfield sf : field.getSubfields()) {
            if ('u' == sf.getCode() && SFX_LINK.matcher(sf.getData()).matches()) {
                return true;
            }
            if ("01".indexOf(ind2) > -1 && "3z".indexOf(sf.getCode()) > -1
                    && SUPPLEMENTAL_LINK.matcher(sf.getData()).matches()) {
                return false;
            }
        }
        return true;
    }
}
