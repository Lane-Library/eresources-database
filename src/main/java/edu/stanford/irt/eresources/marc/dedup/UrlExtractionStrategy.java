package edu.stanford.irt.eresources.marc.dedup;

import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import edu.stanford.irt.eresources.marc.MARCRecordSupport;
import edu.stanford.lane.catalog.Record;

public class UrlExtractionStrategy implements KeyExtractionStrategy {

    private static final Pattern BEGINS_HTTPS_OR_ENDS_SLASH = Pattern.compile("(^https?://)|(/$)");

    @Override
    public Set<String> extractKeys(Record marcRecord) {
        return MARCRecordSupport.getSubfieldData(marcRecord, "856", "u")
                .map((final String s) -> s.replace("https://stanford.idm.oclc.org/login?url=", "")).map(String::trim)
                .map((final String s) -> BEGINS_HTTPS_OR_ENDS_SLASH.matcher(s).replaceAll(""))
                .map(url -> LaneDedupAugmentation.KEY_URL + LaneDedupAugmentation.SEPARATOR + url)
                .collect(Collectors.toSet());
    }
}
