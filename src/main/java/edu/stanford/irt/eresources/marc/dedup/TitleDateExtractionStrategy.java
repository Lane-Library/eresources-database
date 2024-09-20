package edu.stanford.irt.eresources.marc.dedup;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;

public class TitleDateExtractionStrategy implements KeyExtractionStrategy {

    private static final int F008_DATES_BEGIN = 7;

    private static final int F008_DATES_END = 15;

    private static final Pattern NOT_ALPHANUM_OR_SPACE = Pattern.compile("[^a-zA-Z_0-9 ]");

    @Override
    public Set<String> extractKeys(Record marcRecord) {
        Set<String> keys = new HashSet<>();
        String title = NOT_ALPHANUM_OR_SPACE.matcher(
                MARCRecordSupport.getSubfieldData(marcRecord, "245", "a").findFirst().map(String::trim).orElse(""))
                .replaceAll("");
        String dates = MARCRecordSupport.getFields(marcRecord, "008").map(Field::getData).findFirst()
                .map((final String s) -> s.substring(F008_DATES_BEGIN, F008_DATES_END)).orElse("00000000");
        StringBuilder sb = new StringBuilder(title);
        sb.append(dates);
        keys.add(LaneDedupAugmentation.KEY_TITLE_DATE + LaneDedupAugmentation.SEPARATOR + sb.toString());
        return keys;
    }
}
