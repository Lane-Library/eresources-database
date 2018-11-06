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
import edu.stanford.irt.eresources.SulSolrCatalogRecordService;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;

public class SulTypeFactory extends MARCRecordSupport {

    private static final Set<String> ALLOWED_TYPES = new HashSet<>();

    private static final Pattern BEGIN_OR_END_BRACKET_MAYBE_SPACE_COLON = Pattern.compile("(^\\[|\\]( :)?$)");

    private static final Map<String, String> COMPOSITE_TYPES = new HashMap<>();

    private static final String[][] COMPOSITE_TYPES_INITIALIZER = { { EresourceConstants.AUDIO, "Sound Recording" },
            { EresourceConstants.BOOK, "Book Sets", "Books" }, { EresourceConstants.DATABASE, "Databases" },
            { "Dataset", "Datasets" }, { EresourceConstants.IMAGE, "Graphics" },
            { EresourceConstants.JOURNAL, "Journal/Periodical", "Newspaper" },
            { EresourceConstants.SOFTWARE, "Software/Multimedia" } };

    private static final Pattern NOT_DIGIT = Pattern.compile("\\D");

    private static final Map<String, String> PRIMARY_TYPES = new HashMap<>();
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
        PRIMARY_TYPES.put("journal", EresourceConstants.JOURNAL);
        PRIMARY_TYPES.put("databases", EresourceConstants.DATABASE);
        PRIMARY_TYPES.put("periodicals", EresourceConstants.JOURNAL);
    }

    private SulSolrCatalogRecordService catalogRecordService;

    public SulTypeFactory(final SulSolrCatalogRecordService catalogRecordService) {
        this.catalogRecordService = catalogRecordService;
    }

    private static Stream<Field> getFieldsWild(final Record record, final String tagString) {
        return record.getFields().stream().filter((final Field f) -> f.getTag().startsWith(tagString));
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
        String f001 = NOT_DIGIT.matcher(getFields(record, "001").map(Field::getData).findFirst().orElse("0"))
                .replaceAll("");
        for (String type : this.catalogRecordService.getRecordFormats(f001)) {
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
        // may need to extend to exclude loc.gov or include google books?
        boolean isDigital = getSubfieldData(record, "856", "u").count() > 0;
        if (isDigital) {
            return "Digital";
        }
        return "Print";
    }

    private Collection<String> getRawTypes(final Record record) {
        Set<String> rawTypes = new HashSet<>();
        List<Field> fields655 = getFields(record, "655").collect(Collectors.toList());
        rawTypes.addAll(getSubfieldData(fields655.stream(), "a").map(TextParserHelper::maybeStripTrailingPeriod)
                .collect(Collectors.toSet()));
        List<Field> fields6xx = getFieldsWild(record, "6").collect(Collectors.toList());
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
}
