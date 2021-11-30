package edu.stanford.irt.eresources.marc;

import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

/**
 * Parse SUL record type from MARC. This class was written to mimic <a href=
 * "https://github.com/sul-dlss/searchworks_traject_indexer/blob/master/lib/traject/config/sirsi_config.rb">SUL's
 * indexing code</a> and intentionally follows their structure in order to minimize maintenance pain over time.
 * Optimizing this class is not recommended.
 */
public final class SulTypeFactoryHelper extends MARCRecordSupport {

    private static final String FORTY_ZEROES = StringUtils.repeat("0", 40);

    public static Set<String> getTypes(final Record marcRecord) {
        Set<String> types = pass1(marcRecord);
        types.addAll(pass1(marcRecord));
        types = pass2(marcRecord, new HashSet<>(types));
        types = pass3(marcRecord, new HashSet<>(types));
        types.addAll(pass4(marcRecord));
        types.addAll(pass5(marcRecord));
        types.addAll(pass6(marcRecord));
        types.addAll(pass7(marcRecord));
        types = pass8(marcRecord, new HashSet<>(types));
        types.addAll(pass9(marcRecord));
        types = passs10(marcRecord, new HashSet<>(types));
        types = passs11(marcRecord, new HashSet<>(types));
        types = passs12(marcRecord, new HashSet<>(types));
        return types;
    }

    /**
     * https://github.com/sul-dlss/searchworks_traject_indexer/blob/master/lib/traject/config/sirsi_config.rb#L1401
     *
     * @param marcRecord
     * @return set of types
     */
    private static Set<String> pass1(final Record marcRecord) {
        Set<String> mytypes = new HashSet<>();
        byte leaderByte6 = marcRecord.getLeaderByte(6);
        byte leaderByte7 = marcRecord.getLeaderByte(7);
        String f008 = getFields(marcRecord, "008").map(Field::getData).findFirst().orElse(FORTY_ZEROES);
        if ('a' == leaderByte6 || 't' == leaderByte6) {
            if ('a' == leaderByte7 || 'm' == leaderByte7) {
                mytypes.add("Book");
            } else if ('c' == leaderByte7) {
                mytypes.add("Archive/Manuscript");
            }
        } else if ('b' == leaderByte6 || 'p' == leaderByte6) {
            mytypes.add("Archive/Manuscript");
        } else if ('c' == leaderByte6) {
            mytypes.add("Music score");
        } else if ('d' == leaderByte6) {
            mytypes.add("Music score");
            mytypes.add("Archive/Manuscript");
        } else if ('e' == leaderByte6) {
            mytypes.add("Map");
        } else if ('f' == leaderByte6) {
            mytypes.add("Map");
            mytypes.add("Archive/Manuscript");
        } else if ('g' == leaderByte6) {
            if (f008.substring(33, 34).matches("[ |[0-9]fmv]")) {
                mytypes.add("Video");
            } else if (f008.substring(33, 34).matches("[aciklnopst]")) {
                mytypes.add("Image");
            }
        } else if ('i' == leaderByte6) {
            mytypes.add("Sound recording");
        } else if ('j' == leaderByte6) {
            mytypes.add("Music recording");
        } else if ('k' == leaderByte6 && f008.substring(33, 34).matches("[ |[0-9]aciklnopst]")) {
            mytypes.add("Image");
        } else if ('m' == leaderByte6) {
            if ("a".equals(f008.substring(26, 27))) {
                mytypes.add("Dataset");
            } else {
                mytypes.add("Software/Multimedia");
            }
        } else if ('o' == leaderByte6) {
            mytypes.add("Other");
        } else if ('r' == leaderByte6) {
            mytypes.add("Object");
        }
        return mytypes;
    }

    /**
     * https://github.com/sul-dlss/searchworks_traject_indexer/blob/master/lib/traject/config/sirsi_config.rb#L1452
     *
     * @param marcRecord
     * @param currentTypes
     *            set of assigned types
     * @return set of types
     */
    private static Set<String> pass2(final Record marcRecord, final Set<String> currentTypes) {
        if (!currentTypes.isEmpty()) {
            return currentTypes;
        }
        byte leaderByte7 = marcRecord.getLeaderByte(7);
        String f006 = getFields(marcRecord, "006").map(Field::getData).findFirst().orElse(FORTY_ZEROES);
        String f008 = getFields(marcRecord, "008").map(Field::getData).findFirst().orElse(FORTY_ZEROES);
        char f008Byte21 = f008.charAt(21);
        Set<String> mytypes = new HashSet<>();
        if ('s' == leaderByte7) {
            if ('m' == f008Byte21) {
                mytypes.add("Book");
            } else if ('n' == f008Byte21) {
                mytypes.add("Newspaper");
            } else if ("p |#".indexOf(f008Byte21) > -1) {
                mytypes.add("Journal/Periodical");
            } else if ('d' == f008Byte21) {
                mytypes.add("Database");
            } else if ('w' == f008Byte21) {
                mytypes.add("Journal/Periodical");
            } else {
                mytypes.add("Book");
            }
        } else if ('s' == f006.charAt(0)) {
            if ("lm".indexOf(f006.charAt(4)) > -1) {
                mytypes.add("Book");
            } else if ('n' == f006.charAt(4)) {
                mytypes.add("Newspaper");
            } else if ("p |#".indexOf(f006.charAt(4)) > -1) {
                mytypes.add("Journal/Periodical");
            } else if ('d' == f006.charAt(4)) {
                mytypes.add("Database");
            } else if ('w' == f006.charAt(4)) {
                mytypes.add("Journal/Periodical");
            } else {
                mytypes.add("Book");
            }
        }
        return mytypes;
    }

    /**
     * https://github.com/sul-dlss/searchworks_traject_indexer/blob/master/lib/traject/config/sirsi_config.rb#L1488
     *
     * @param marcRecord
     * @param currentTypes
     *            set of assigned types
     * @return set of types
     */
    private static Set<String> pass3(final Record marcRecord, final Set<String> currentTypes) {
        if (!currentTypes.isEmpty()) {
            return currentTypes;
        }
        byte leaderByte7 = marcRecord.getLeaderByte(7);
        String f008 = getFields(marcRecord, "008").map(Field::getData).findFirst().orElse(FORTY_ZEROES);
        char f008Byte21 = f008.charAt(21);
        Set<String> mytypes = new HashSet<>();
        if ('i' == leaderByte7) {
            if ('d' == f008Byte21) {
                mytypes.add("Database");
            } else if ('l' == f008Byte21) {
                mytypes.add("Book");
            } else if ('w' == f008Byte21) {
                mytypes.add("Journal/Periodical");
            } else {
                mytypes.add("Book");
            }
        }
        return mytypes;
    }

    /**
     * https://github.com/sul-dlss/searchworks_traject_indexer/blob/master/lib/traject/config/sirsi_config.rb#L1508
     *
     * @param marcRecord
     * @return set of types
     */
    private static Set<String> pass4(final Record marcRecord) {
        Set<String> mytypes = new HashSet<>();
        if (getSubfieldData(marcRecord, "999", "t").collect(Collectors.toSet()).contains("DATABASE")) {
            mytypes.add("Database");
        }
        return mytypes;
    }

    /**
     * https://github.com/sul-dlss/searchworks_traject_indexer/blob/master/lib/traject/config/sirsi_config.rb#L1514
     * skipping this method as it depends on access_facet and does not seem worth replicating here
     *
     * @param marcRecord
     * @return set of types
     */
    private static Set<String> pass5(final Record marcRecord) {
        return Collections.emptySet();
    }

    /**
     * https://github.com/sul-dlss/searchworks_traject_indexer/blob/master/lib/traject/config/sirsi_config.rb#L1522
     *
     * @param marcRecord
     * @return set of types
     */
    private static Set<String> pass6(final Record marcRecord) {
        Pattern subAPattern = Pattern.compile(
                "^(A\\d|F\\d|M\\d|MISC \\d|(MSS (CODEX|MEDIA|PHOTO|PRINTS))|PC\\d|SC[\\d|D|M]|V\\d)",
                Pattern.CASE_INSENSITIVE);
        for (Field f : getFields(marcRecord, "999").collect(Collectors.toList())) {
            boolean mSpecColl = f.getSubfields().stream()
                    .anyMatch((final Subfield s) -> s.getCode() == 'm' && "SPEC-COLL".equals(s.getData()));
            boolean wAlphanum = f.getSubfields().stream()
                    .anyMatch((final Subfield s) -> s.getCode() == 'w' && "ALPHANUM".equals(s.getData()));
            boolean aMatch = f.getSubfields().stream()
                    .anyMatch((final Subfield s) -> s.getCode() == 'a' && subAPattern.matcher(s.getData()).matches());
            if (mSpecColl && wAlphanum && aMatch) {
                return Collections.singleton("Archive/Manuscript");
            }
        }
        return Collections.emptySet();
    }

    /**
     * https://github.com/sul-dlss/searchworks_traject_indexer/blob/master/lib/traject/config/sirsi_config.rb#L1537
     *
     * @param marcRecord
     * @return set of types
     */
    private static Set<String> pass7(final Record marcRecord) {
        String f245h = getSubfieldData(marcRecord, "245", "h").collect(Collectors.joining(" "));
        if (f245h.contains("manuscript")
                && getSubfieldData(marcRecord, "999", "m").collect(Collectors.toSet()).contains("LANE-MED")) {
            return Collections.singleton("Book");
        }
        return Collections.emptySet();
    }

    /**
     * https://github.com/sul-dlss/searchworks_traject_indexer/blob/master/lib/traject/config/sirsi_config.rb#L1550
     *
     * @param marcRecord
     * @param currentTypes
     *            set of assigned types
     * @return set of types
     */
    private static Set<String> pass8(final Record marcRecord, final Set<String> currentTypes) {
        byte leaderByte6 = marcRecord.getLeaderByte(6);
        byte leaderByte7 = marcRecord.getLeaderByte(7);
        if ("at".indexOf(leaderByte6) > -1 && "cd".indexOf(leaderByte7) > -1
                && getSubfieldData(marcRecord, "999", "m").collect(Collectors.toSet()).contains("LANE-MED")) {
            currentTypes.remove("Archive/Manuscript");
            currentTypes.add("Book");
        }
        return currentTypes;
    }

    /**
     * https://github.com/sul-dlss/searchworks_traject_indexer/blob/master/lib/traject/config/sirsi_config.rb#L1561
     *
     * @param marcRecord
     * @return set of types
     */
    private static Set<String> pass9(final Record marcRecord) {
        if (getSubfieldData(marcRecord, "590", "a").anyMatch((final String s) -> s.contains("MARCit brief record"))) {
            return Collections.singleton("Journal/Periodical");
        }
        return Collections.emptySet();
    }

    /**
     * https://github.com/sul-dlss/searchworks_traject_indexer/blob/master/lib/traject/config/sirsi_config.rb#L1570
     *
     * @param marcRecord
     * @param currentTypes
     *            set of assigned types
     * @return set of types
     */
    private static Set<String> passs10(final Record marcRecord, final Set<String> currentTypes) {
        if (getSubfieldData(marcRecord, "914", "a").collect(Collectors.toSet()).contains("EQUIP")) {
            return Collections.singleton("Equipment");
        }
        return currentTypes;
    }

    /**
     * https://github.com/sul-dlss/searchworks_traject_indexer/blob/master/lib/traject/config/sirsi_config.rb#L1581
     *
     * @param marcRecord
     * @param currentTypes
     *            set of assigned types
     * @return set of types
     */
    private static Set<String> passs11(final Record marcRecord, final Set<String> currentTypes) {
        if (currentTypes.isEmpty() || currentTypes.contains("Other")) {
            String f245h = getSubfieldData(marcRecord, "245", "h").map((final String s) -> s.toLowerCase(Locale.US))
                    .collect(Collectors.joining(" "));
            String format = null;
            if (Pattern.compile("(video|motion picture|filmstrip|vcd-dvd)").matcher(f245h).find()) {
                format = "Video";
            } else if (Pattern.compile("manuscript").matcher(f245h).find()) {
                format = "Archive/Manuscript";
            } else if (Pattern.compile("sound recording").matcher(f245h).find()) {
                format = "Sound recording";
            } else if (Pattern.compile(
                    "(graphic|slide|chart|art reproduction|technical drawing|flash card|transparency|activity card|picture|diapositives)")
                    .matcher(f245h).find()) {
                format = "Image";
            } else if (Pattern.compile("kit").matcher(f245h).find()) {
                char f007Byte0 = getFields(marcRecord, "007").map(Field::getData).findFirst().orElse("0").charAt(0);
                if ("ad".indexOf(f007Byte0) > -1) {
                    format = "Map";
                } else if ('c' == f007Byte0) {
                    format = "Software/Multimedia";
                } else if ("gmv".indexOf(f007Byte0) > -1) {
                    format = "Video";
                } else if ("kr".indexOf(f007Byte0) > -1) {
                    format = "Image";
                } else if ('q' == f007Byte0) {
                    format = "Music score";
                } else if ('s' == f007Byte0) {
                    format = "Sound recording";
                }
            }
            if (null != format) {
                currentTypes.remove("Other");
                currentTypes.add(format);
                return currentTypes;
            }
        }
        return currentTypes;
    }

    /**
     * https://github.com/sul-dlss/searchworks_traject_indexer/blob/master/lib/traject/config/sirsi_config.rb#L1622
     *
     * @param marcRecord
     * @param currentTypes
     *            set of assigned types
     * @return set of types
     */
    private static Set<String> passs12(final Record marcRecord, final Set<String> currentTypes) {
        if (currentTypes.isEmpty()) {
            return Collections.emptySet();
        }
        currentTypes.remove("Other");
        return currentTypes;
    }
}
