package edu.stanford.irt.eresources.marc;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.EresourceConstants;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;

public class TypeFactory extends MARCRecordSupport {

    private static final Set<String> ALLOWED_TYPES = new HashSet<>();

    private static final String[] ALLOWED_TYPES_INITIALIZER = { EresourceConstants.ARTICLE, "Atlases, Pictorial",
            EresourceConstants.AUDIO, "Bassett", EresourceConstants.BOOK, "Calculators, Formulas, Algorithms",
            EresourceConstants.DATABASE, "Dataset", EresourceConstants.EQUIPMENT, "Exam Prep",
            EresourceConstants.IMAGE, EresourceConstants.JOURNAL, "Lane Class", "Lane Guide", "Lane Web Page", "Print",
            EresourceConstants.SOFTWARE, "Statistics", EresourceConstants.VIDEO };

    private static final Map<String, String> COMPOSITE_TYPES = new HashMap<>();

    private static final String[][] COMPOSITE_TYPES_INITIALIZER = { { EresourceConstants.ARTICLE, "Articles" },
            { EresourceConstants.AUDIO, "Sound Recordings" }, { EresourceConstants.BOOK, "Book Sets", "Books" },
            { "Calculators, Formulas, Algorithms", "Decision Support Techniques", "Calculators, Clinical",
                    "Algorithms" },
            { EresourceConstants.DATABASE, "Databases" }, { "Dataset", "Datasets" },
            { "Exam Prep", "Examination Questions", "Outlines", "Problems", "Study Guides" },
            { EresourceConstants.IMAGE, "Graphics" }, { EresourceConstants.JOURNAL, "Periodicals", "Newspapers" },
            { EresourceConstants.SOFTWARE, "Software, Biocomputational", "Software, Educational",
                    "Software, Statistical" },
            { EresourceConstants.VIDEO, "Digital Video", "Digital Video, Local", "Digital Video, Local, Public" } };

    private static final Map<String, String> PRIMARY_TYPES = new HashMap<>();
    static {
        Collections.addAll(ALLOWED_TYPES, getAllowedTypesInitializer());
        for (String[] element : COMPOSITE_TYPES_INITIALIZER) {
            for (int j = 1; j < element.length; j++) {
                COMPOSITE_TYPES.put(element[j], element[0]);
            }
        }
        PRIMARY_TYPES.put("articles", EresourceConstants.ARTICLE);
        PRIMARY_TYPES.put("books", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("book sets", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("cartographic materials", EresourceConstants.OTHER);
        PRIMARY_TYPES.put("collections", EresourceConstants.COLLECTION);
        PRIMARY_TYPES.put("components", EresourceConstants.COMPONENT);
        PRIMARY_TYPES.put("computer files", EresourceConstants.SOFTWARE);
        PRIMARY_TYPES.put("databases", EresourceConstants.DATABASE);
        PRIMARY_TYPES.put("documents", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("leaflets", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("pamphlets", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("periodicals", EresourceConstants.JOURNAL);
        PRIMARY_TYPES.put("search engines", EresourceConstants.DATABASE);
        PRIMARY_TYPES.put("serials", EresourceConstants.SERIAL);
        PRIMARY_TYPES.put("sound recordings", EresourceConstants.AUDIO);
        PRIMARY_TYPES.put("visual materials", EresourceConstants.VISUAL_MATERIAL);
    }

    public static String[] getAllowedTypesInitializer() {
        return ALLOWED_TYPES_INITIALIZER;
    }

    public String getPrimaryType(final Record marcRecord) {
        String primaryType = getSubfieldData(getFields(marcRecord, "655")
                .filter((final Field f) -> '4' == f.getIndicator1() && '7' == f.getIndicator2()), "a").findFirst()
                        .orElse("");
        primaryType = PRIMARY_TYPES.get(primaryType.toLowerCase(Locale.US));
        String pType;
        Collection<String> rawTypes = getRawTypes(marcRecord);
        if (primaryType == null) {
            pType = EresourceConstants.OTHER;
            if (rawTypes.contains(EresourceConstants.EQUIPMENT)) {
                pType = EresourceConstants.EQUIPMENT;
            }
        } else if (EresourceConstants.BOOK.equals(primaryType)) {
            pType = EresourceConstants.BOOK + EresourceConstants.SPACE + getPrintOrDigital(marcRecord);
        } else if (EresourceConstants.SERIAL.equals(primaryType)) {
            pType = getPrimaryTypeFromSerial(rawTypes, marcRecord);
        } else if (EresourceConstants.COMPONENT.equals(primaryType)) {
            pType = getPrimaryTypeFromComponent(rawTypes);
        } else if (EresourceConstants.VISUAL_MATERIAL.equals(primaryType)) {
            pType = getPrimaryTypeFromVisualMaterial(rawTypes);
        } else {
            pType = primaryType;
        }
        return pType;
    }

    public Collection<String> getTypes(final Record marcRecord) {
        String pType = getPrimaryType(marcRecord);
        Collection<String> rawTypes = getRawTypes(marcRecord);
        if (!EresourceConstants.OTHER.equals(pType)) {
            rawTypes.add(pType);
        }
        if (pType.startsWith(EresourceConstants.BOOK) || pType.startsWith(EresourceConstants.JOURNAL)) {
            rawTypes.add(pType.split(" ")[0]);
        }
        return rawTypes;
    }

    private String getCompositeType(final String type) {
        if (COMPOSITE_TYPES.containsKey(type)) {
            return COMPOSITE_TYPES.get(type);
        }
        return type;
    }

    private String getPrimaryTypeFromComponent(final Collection<String> rawTypes) {
        String type = EresourceConstants.OTHER;
        if (rawTypes.contains(EresourceConstants.ARTICLE)) {
            type = EresourceConstants.ARTICLE;
        }
        return type;
    }

    private String getPrimaryTypeFromSerial(final Collection<String> rawTypes, final Record marcRecord) {
        String type = EresourceConstants.JOURNAL + EresourceConstants.SPACE + getPrintOrDigital(marcRecord);
        if (rawTypes.contains(EresourceConstants.BOOK)) {
            type = EresourceConstants.BOOK + EresourceConstants.SPACE + getPrintOrDigital(marcRecord);
        } else if (rawTypes.contains(EresourceConstants.DATABASE)) {
            type = EresourceConstants.DATABASE;
        }
        return type;
    }

    private String getPrimaryTypeFromVisualMaterial(final Collection<String> rawTypes) {
        String type = EresourceConstants.IMAGE;
        if (rawTypes.contains(EresourceConstants.VIDEO)) {
            type = EresourceConstants.VIDEO;
        }
        return type;
    }

    private String getPrintOrDigital(final Record marcRecord) {
        boolean isDigital = getSubfieldData(marcRecord, "245", "h").anyMatch((final String s) -> s.contains("digital"));
        if (isDigital) {
            return "Digital";
        }
        return "Print";
    }

    private Collection<String> getRawTypes(final Record marcRecord) {
        Set<String> rawTypes = new HashSet<>();
        List<Field> fields655 = getFields(marcRecord, "655").collect(Collectors.toList());
        rawTypes.addAll(getSubfieldData(fields655.stream(), "a").collect(Collectors.toSet()));
        if (getSubfieldData(marcRecord, "245", "h").anyMatch((final String s) -> s.contains("videorecording"))) {
            rawTypes.add("Video");
        }
        if (getSubfieldData(marcRecord, "035", "a").anyMatch((final String s) -> s.startsWith("(Bassett)"))) {
            rawTypes.add("Bassett");
        }
        if (rawTypes.contains("Objects") && rawTypes.contains("Subset, Circbib")) {
            rawTypes.add("Equipment");
        }
        return rawTypes.stream().map(this::getCompositeType).filter(ALLOWED_TYPES::contains)
                .collect(Collectors.toSet());
    }
}
