package edu.stanford.irt.eresources.marc;

import java.util.Collection;
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
            EresourceConstants.AUDIO, "Bassett", "Biotools Software, Installed", EresourceConstants.BOOK,
            EresourceConstants.CHAPTER, "Calculators, Formulas, Algorithms", EresourceConstants.DATABASE, "Dataset",
            "Exam Prep", "Grand Rounds", EresourceConstants.IMAGE, EresourceConstants.JOURNAL, "Lane Class",
            "Lane Web Page", "Mobile", "Print", EresourceConstants.SOFTWARE, "Software, Installed",
            "Statistics Software, Installed", "Statistics", EresourceConstants.VIDEO, EresourceConstants.WEBSITE };

    private static final Map<String, String> COMPOSITE_TYPES = new HashMap<>();

    private static final String[][] COMPOSITE_TYPES_INITIALIZER = { { EresourceConstants.ARTICLE, "Articles" },
            { EresourceConstants.AUDIO, "Sound Recordings" }, { EresourceConstants.BOOK, "Book Sets", "Books" },
            { EresourceConstants.CHAPTER, "Chapters" },
            { "Calculators, Formulas, Algorithms", "Decision Support Techniques", "Calculators, Clinical",
                    "Algorithms" },
            { EresourceConstants.DATABASE, "Databases" }, { "Dataset", "Datasets" },
            { "Exam Prep", "Examination Questions", "Outlines", "Problems", "Study Guides" },
            { EresourceConstants.IMAGE, "Graphics" }, { EresourceConstants.JOURNAL, "Periodicals", "Newspapers" },
            { "Mobile", "Subset, Mobile" },
            { EresourceConstants.SOFTWARE, "Software, Biocomputational", "Software, Educational",
                    "Software, Statistical" },
            { EresourceConstants.VIDEO, "Digital Video", "Digital Video, Local", "Digital Video, Local, Public" },
            { EresourceConstants.WEBSITE, "Websites" } };

    private static final Map<String, String> PRIMARY_TYPES = new HashMap<>();
    static {
        for (String type : ALLOWED_TYPES_INITIALIZER) {
            ALLOWED_TYPES.add(type);
        }
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
        PRIMARY_TYPES.put("laneclass", "Lane Class");
        PRIMARY_TYPES.put("lanepage", "Lane Web Page");
        PRIMARY_TYPES.put("leaflets", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("pamphlets", EresourceConstants.BOOK);
        PRIMARY_TYPES.put("periodicals", EresourceConstants.JOURNAL);
        PRIMARY_TYPES.put("search engines", EresourceConstants.DATABASE);
        PRIMARY_TYPES.put("serials", EresourceConstants.SERIAL);
        PRIMARY_TYPES.put("sound recordings", EresourceConstants.AUDIO);
        PRIMARY_TYPES.put("visual materials", EresourceConstants.VISUAL_MATERIAL);
        PRIMARY_TYPES.put("websites", EresourceConstants.WEBSITE);
        // authority types: keep?
        PRIMARY_TYPES.put("events", "Event");
        PRIMARY_TYPES.put("persons", EresourceConstants.PERSON);
        PRIMARY_TYPES.put("persons, female", EresourceConstants.PERSON);
        PRIMARY_TYPES.put("persons, male", EresourceConstants.PERSON);
        PRIMARY_TYPES.put("jurisdictions, subdivisions", EresourceConstants.ORGANIZATION);
        PRIMARY_TYPES.put("organizations", EresourceConstants.ORGANIZATION);
        PRIMARY_TYPES.put("organizations, subdivisions", EresourceConstants.ORGANIZATION);
    }

    public String getPrimaryType(final Record record) {
        String primaryType = getSubfieldData(getFields(record, "655")
                .filter(f -> '4' == f.getIndicator1() && '7' == f.getIndicator2()), "a")
                .findFirst()
                .orElse("");
        primaryType = PRIMARY_TYPES.get(primaryType.toLowerCase(Locale.US));
        String type;
        Collection<String> rawTypes = getRawTypes(record);
        if (primaryType == null) {
            type = EresourceConstants.OTHER;
        } else if (EresourceConstants.BOOK.equals(primaryType)) {
            type = EresourceConstants.BOOK + EresourceConstants.SPACE + getPrintOrDigital(record);
        } else if (EresourceConstants.JOURNAL.equals(primaryType)) {
            type = EresourceConstants.JOURNAL + EresourceConstants.SPACE + getPrintOrDigital(record);
        } else if (EresourceConstants.SERIAL.equals(primaryType)) {
            if (rawTypes.contains(EresourceConstants.BOOK)) {
                type = EresourceConstants.BOOK + EresourceConstants.SPACE + getPrintOrDigital(record);
            } else if (rawTypes.contains(EresourceConstants.DATABASE)) {
                type = EresourceConstants.DATABASE;
            } else {
                type = EresourceConstants.JOURNAL + EresourceConstants.SPACE + getPrintOrDigital(record);
            }
        } else if ("Component".equals(primaryType)) {
            if (rawTypes.contains(EresourceConstants.ARTICLE) && rawTypes.contains(EresourceConstants.CHAPTER)) {
                type = "Article/Chapter";
            } else if (rawTypes.contains(EresourceConstants.ARTICLE)) {
                type = EresourceConstants.ARTICLE;
            } else if (rawTypes.contains(EresourceConstants.CHAPTER)) {
                type = EresourceConstants.CHAPTER;
            } else {
                type = EresourceConstants.OTHER;
            }
        } else if (EresourceConstants.VISUAL_MATERIAL.equals(primaryType)) {
            if (rawTypes.contains(EresourceConstants.VIDEO)) {
                type = EresourceConstants.VIDEO;
            } else {
                type = EresourceConstants.IMAGE;
            }
        } else {
            type = primaryType;
        }
        return type;
    }

    public Collection<String> getTypes(final Record record) {
        String pType = getPrimaryType(record);
        Collection<String> rawTypes = getRawTypes(record);
        if (!EresourceConstants.OTHER.equals(pType) && !"Article/Chapter".equals(pType)) {
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

    private String getPrintOrDigital(final Record record) {
        boolean isDigital = getSubfieldData(record, "245", "h")
                .anyMatch(s -> s.contains("digital"));
        if (isDigital) {
            return "Digital";
        } else {
            return "Print";
        }
    }

    private Collection<String> getRawTypes(final Record record) {
        Set<String> rawTypes = new HashSet<>();
        List<Field> fields655 = getFields(record, "655")
                .collect(Collectors.toList());
        boolean installedSoftware = getSubfieldData(fields655.stream(), "a")
                .anyMatch("Software, Installed"::equalsIgnoreCase);
        if (installedSoftware) {
            if (getSubfieldData(fields655.stream(), "a")
                    .anyMatch("Subset, Biotools"::equalsIgnoreCase)) {
                rawTypes.add("Biotools Software, Installed");
            }
            if (getSubfieldData(fields655.stream(), "a")
                    .anyMatch("Statistics"::equalsIgnoreCase)) {
                rawTypes.add("Statistics Software, Installed");
            }
        }
        rawTypes.addAll(getSubfieldData(fields655.stream(), "a")
                .collect(Collectors.toSet()));
        if (getSubfieldData(record, "245", "h")
                .anyMatch(s -> s.contains("videorecording"))) {
            rawTypes.add("Video");
        }
        if (getSubfieldData(record, "035", "a")
                .anyMatch(s -> s.startsWith("(Bassett)"))) {
            rawTypes.add("Bassett");
        }
        if (getSubfieldData(record, "830", "a")
                .map(String::toLowerCase)
                .anyMatch(s -> s.contains("stanford") && s.contains("grand rounds"))) {
            rawTypes.add("Grand Rounds");
        }
        return rawTypes.stream()
                .map(this::getCompositeType)
                .filter(ALLOWED_TYPES::contains)
                .collect(Collectors.toSet());
    }
}
