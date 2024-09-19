package edu.stanford.irt.eresources.marc.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.EresourceConstants;
import edu.stanford.irt.eresources.TextParserHelper;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.FolioRecord;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class TypeFactory extends MARCRecordSupport {

    private static final Set<String> ALLOWED_TYPES = new HashSet<>();

    private static final String[] ALLOWED_TYPES_INITIALIZER = { EresourceConstants.ARTICLE, "Atlases, Pictorial",
            EresourceConstants.AUDIO, "Bassett", EresourceConstants.BOOK, "Book Digital", "Book Print",
            "Calculators, Formulas, Algorithms", EresourceConstants.DATABASE, "Dataset", EresourceConstants.EQUIPMENT,
            "Exam Prep", EresourceConstants.IMAGE, EresourceConstants.JOURNAL, "Journal Digital", "Journal Print",
            "Lane Class", "Lane Guide", "Lane Web Page", "Print", EresourceConstants.SOFTWARE, "Statistics",
            EresourceConstants.VIDEO };

    private static final Pattern BEGIN_OR_END_BRACKET_MAYBE_SPACE_COLON = Pattern.compile("(^\\[)|(\\][ :/]{0,3}+$)");

    private static final Map<String, String> COMPOSITE_TYPES = new HashMap<>();

    private static final String[][] COMPOSITE_TYPES_INITIALIZER = { { EresourceConstants.ARTICLE, "Articles" },
            { EresourceConstants.AUDIO, "Sound Recording", "Sound Recordings" },
            { EresourceConstants.BOOK, "Book Sets", "Books" },
            { "Calculators, Formulas, Algorithms", "Decision Support Techniques", "Calculators, Clinical",
                    "Algorithms" },
            { EresourceConstants.DATABASE, "Databases" }, { "Dataset", "Datasets" },
            { "Exam Prep", "Examination Questions", "Outlines", "Problems", "Study Guides" },
            { EresourceConstants.IMAGE, "Graphics" },
            { EresourceConstants.JOURNAL, "Periodicals", "Newspapers", "Journal/Periodical", "Newspaper" },
            { EresourceConstants.SOFTWARE, "Software, Biocomputational", "Software, Educational",
                    "Software, Statistical", "Software/Multimedia" },
            { EresourceConstants.VIDEO, "Digital Video", "Digital Video, Local", "Digital Video, Local, Public" } };

    private static final Map<String, String> PRIMARY_TYPES = new HashMap<>();

    private static final Pattern SFX_LINK = Pattern.compile("^https?://library.stanford.edu/sfx.*",
            Pattern.CASE_INSENSITIVE);

    private static final Pattern SUPPLEMENTAL_LINK = Pattern
            .compile(".*(table of contents|abstract|description|sample text|finding aid).*", Pattern.CASE_INSENSITIVE);
    static {
        Collections.addAll(ALLOWED_TYPES, ALLOWED_TYPES_INITIALIZER);
        for (String[] element : COMPOSITE_TYPES_INITIALIZER) {
            for (int j = 1; j < element.length; j++) {
                COMPOSITE_TYPES.put(element[j], element[0]);
            }
        }
        PRIMARY_TYPES.put("articles", EresourceConstants.ARTICLE);
        PRIMARY_TYPES.put("book", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("books", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("book sets", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("cartographic materials", EresourceConstants.OTHER);
        PRIMARY_TYPES.put("collections", EresourceConstants.COLLECTION);
        PRIMARY_TYPES.put("components", EresourceConstants.COMPONENT);
        PRIMARY_TYPES.put("computer files", EresourceConstants.SOFTWARE);
        PRIMARY_TYPES.put("database", EresourceConstants.DATABASE);
        PRIMARY_TYPES.put("databases", EresourceConstants.DATABASE);
        PRIMARY_TYPES.put("documents", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("journal", EresourceConstants.JOURNAL);
        PRIMARY_TYPES.put("leaflets", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("music recording", EresourceConstants.AUDIO);
        PRIMARY_TYPES.put("pamphlets", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("periodicals", EresourceConstants.JOURNAL);
        PRIMARY_TYPES.put("search engines", EresourceConstants.DATABASE);
        PRIMARY_TYPES.put("serials", EresourceConstants.SERIAL);
        PRIMARY_TYPES.put("sound recording", EresourceConstants.AUDIO);
        PRIMARY_TYPES.put("sound recordings", EresourceConstants.AUDIO);
        PRIMARY_TYPES.put("video", EresourceConstants.VIDEO);
        PRIMARY_TYPES.put("videorecording", EresourceConstants.VIDEO);
        PRIMARY_TYPES.put("visual materials", EresourceConstants.VISUAL_MATERIAL);
    }

    public static String getPrimaryType(final FolioRecord folioRecord) {
        for (Map<String, Object> item : folioRecord.getItems()) {
            // the LANE-EQUIP location is likely best but can't hurt to include equipment material types as well?
            // folio material types: library equipment \d, av equipment \d
            if (((String) item.get("materialType")).contains("equipment") || item.toString().contains("LANE-EQUIP")) {
                return EresourceConstants.EQUIPMENT;
            }
        }
        // not sure where to get other types from Folio instance records
        return null;
    }

    public static String getPrimaryType(final Record marcRecord) {
        // Lane will be moving away from 655s, but use if still present
        String primaryType = getSubfieldData(getFields(marcRecord, "655")
                .filter((final Field f) -> '4' == f.getIndicator1() && '7' == f.getIndicator2()), "a").findFirst()
                        .orElse(EresourceConstants.OTHER);
        Collection<String> types = getRawTypes(marcRecord);
        if (EresourceConstants.OTHER.equals(primaryType) && !types.isEmpty()) {
            primaryType = types.iterator().next();
        }
        primaryType = PRIMARY_TYPES.get(primaryType.toLowerCase(Locale.US));
        String type;
        if (primaryType == null) {
            type = EresourceConstants.OTHER;
            if (types.contains(EresourceConstants.EQUIPMENT)) {
                type = EresourceConstants.EQUIPMENT;
            }
        } else if (EresourceConstants.BOOK.equals(primaryType)) {
            type = EresourceConstants.BOOK + EresourceConstants.SPACE + getPrintOrDigital(marcRecord);
        } else if (EresourceConstants.SERIAL.equals(primaryType)) {
            type = getPrimaryTypeFromSerial(types, marcRecord);
        } else if (EresourceConstants.JOURNAL.equals(primaryType)) {
            type = EresourceConstants.JOURNAL + EresourceConstants.SPACE + getPrintOrDigital(marcRecord);
        } else if (EresourceConstants.COMPONENT.equals(primaryType)) {
            type = getPrimaryTypeFromComponent(types);
        } else if (EresourceConstants.VISUAL_MATERIAL.equals(primaryType)) {
            type = getPrimaryTypeFromVisualMaterial(types);
        } else {
            type = primaryType;
        }
        return type;
    }

    public static List<String> getTypes(final FolioRecord folioRecord) {
        if (EresourceConstants.EQUIPMENT.equals(getPrimaryType(folioRecord))) {
            return Collections.singletonList(EresourceConstants.EQUIPMENT);
        }
        // not sure where to get other types from Folio instance records
        return Collections.emptyList();
    }

    public static List<String> getTypes(final Record marcRecord) {
        List<String> types = new ArrayList<>();
        String pType = getPrimaryType(marcRecord);
        if (!EresourceConstants.OTHER.equals(pType)) {
            types.add(pType);
        }
        if (pType.startsWith(EresourceConstants.BOOK) || pType.startsWith(EresourceConstants.JOURNAL)) {
            types.add(pType.split(" ")[0]);
        }
        for (String type : getRawTypes(marcRecord)) {
            if (!types.contains(type)) {
                types.add(type);
            }
        }
        return types.stream().filter(ALLOWED_TYPES::contains).toList();
    }

    private static String getCompositeType(final String type) {
        if (COMPOSITE_TYPES.containsKey(type)) {
            return COMPOSITE_TYPES.get(type);
        }
        return type;
    }

    private static String getPrimaryTypeFromComponent(final Collection<String> rawTypes) {
        String type = EresourceConstants.OTHER;
        if (rawTypes.contains(EresourceConstants.ARTICLE)) {
            type = EresourceConstants.ARTICLE;
        }
        return type;
    }

    private static String getPrimaryTypeFromSerial(final Collection<String> rawTypes, final Record marcRecord) {
        String type = EresourceConstants.JOURNAL + EresourceConstants.SPACE + getPrintOrDigital(marcRecord);
        if (rawTypes.contains(EresourceConstants.BOOK)) {
            type = EresourceConstants.BOOK + EresourceConstants.SPACE + getPrintOrDigital(marcRecord);
        } else if (rawTypes.contains(EresourceConstants.DATABASE)) {
            type = EresourceConstants.DATABASE;
        }
        return type;
    }

    private static String getPrimaryTypeFromVisualMaterial(final Collection<String> rawTypes) {
        String type = EresourceConstants.IMAGE;
        if (rawTypes.contains(EresourceConstants.VIDEO)) {
            type = EresourceConstants.VIDEO;
        }
        return type;
    }

    private static String getPrintOrDigital(final Record marcRecord) {
        // 245 $h for older Lane data, 338 $a for RDA records
        if (getSubfieldData(marcRecord, "245", "h").anyMatch((final String s) -> s.toLowerCase().contains("digital"))
                || getSubfieldData(marcRecord, "338", "a")
                        .anyMatch((final String s) -> s.toLowerCase().contains("online resource"))) {
            return "Digital";
        }
        // 8/9 56 fields with $u are found in SUL instance MARC
        List<Field> linkFields = getFieldsWild(marcRecord, "[8|9]56").filter(
                (final Field f) -> f.getSubfields().stream().anyMatch((final Subfield sf) -> sf.getCode() == 'u'))
                .toList();
        int digitalLinks = (int) linkFields.stream().filter(TypeFactory::isDigitalLink).count();
        if (digitalLinks > 0) {
            return "Digital";
        }
        return "Print";
    }

    private static Collection<String> getRawTypes(final Record marcRecord) {
        // order is preserved here and matters for primary type assignment
        // 245 ^h seems most visible/obvious, so assign it first
        Set<String> rawTypes = new LinkedHashSet<>();
        rawTypes.addAll(getSubfieldData(marcRecord, "245", "h").map(TextParserHelper::maybeStripTrailingPeriod)
                .map((final String s) -> BEGIN_OR_END_BRACKET_MAYBE_SPACE_COLON.matcher(s).replaceAll(""))
                .filter((final String s) -> !s.equalsIgnoreCase("digital"))
                .filter((final String s) -> !s.equalsIgnoreCase("print"))
                .filter((final String s) -> !s.equalsIgnoreCase("print/digital"))
                .filter((final String s) -> !s.equalsIgnoreCase("electronic resource"))
                .map(TextParserHelper::toTitleCase)
                .collect(Collectors.toSet()));
        if (getSubfieldData(marcRecord, "245", "h").anyMatch((final String s) -> s.contains("videorecording"))) {
            rawTypes.add("Video");
        }
        for (String type : SulTypeFactoryHelper.getTypes(marcRecord)) {
            rawTypes.add(getCompositeType(type));
        }
        List<Field> fields655 = getFields(marcRecord, "655").toList();
        rawTypes.addAll(getSubfieldData(fields655.stream(), "a").map(TextParserHelper::maybeStripTrailingPeriod)
                .collect(Collectors.toSet()));
        List<Field> fields6xx = getFieldsWild(marcRecord, "6..").toList();
        rawTypes.addAll(getSubfieldData(fields6xx.stream(), "v").map(TextParserHelper::maybeStripTrailingPeriod)
                .collect(Collectors.toSet()));
        rawTypes.addAll(
                getSubfieldData(marcRecord, "999", "t").map(TextParserHelper::toTitleCase).collect(Collectors.toSet()));
        if (getSubfieldData(marcRecord, "035", "a").anyMatch((final String s) -> s.startsWith("(Bassett)"))) {
            rawTypes.add("Bassett");
        }
        return rawTypes.stream().map(TypeFactory::getCompositeType)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    // logic from:
    // https://github.com/sul-dlss/searchworks_traject_indexer/blob/master/lib/marc_links.rb#L122
    private static boolean isDigitalLink(final Field field) {
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
