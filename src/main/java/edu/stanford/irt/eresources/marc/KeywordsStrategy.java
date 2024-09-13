package edu.stanford.irt.eresources.marc;

import java.util.List;
import java.util.Map;

import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class KeywordsStrategy {

    private static final String AGUMENTABLE_TAGS = "100|600|650|700";

    private static final String BIB_KEYWORD_TAG_ADDITIONS = "020|022|024|030|035|901|902|903|909|915|941|942|943";

    private static final String HLDG_KEYWORD_TAGS = "020|022|655|844|852|856|866|907|931";

    private static final int TAG_100 = 100;

    private static final int TAG_863 = 863;

    private static final int TAG_900 = 900;

    private AuthTextAugmentation authTextAugmentation;

    private ReservesTextAugmentation reservesAugmentation;

    public KeywordsStrategy(final AuthTextAugmentation authTextAugmentation,
            final ReservesTextAugmentation reservesAugmentation) {
        this.authTextAugmentation = authTextAugmentation;
        this.reservesAugmentation = reservesAugmentation;
    }

    public String getKeywords(final Record marcRecord) {
        StringBuilder sb = new StringBuilder();
        List<Field> fields = marcRecord.getFields();
        byte leaderByte6 = marcRecord.getLeaderByte(AbstractMarcEresource.LEADER_BYTE_06);
        if ("uvxy".indexOf(leaderByte6) > -1) {
            getKeywordsFromHoldingsRec(fields, sb);
        } else {
            getKeywordsFromBibRec(fields, sb);
        }
        return sb.toString();
    }

    public String getKeywordsFromFolioHoldings(final List<Map<String, Object>> folioHoldings) {
        // folioHoldings fields to extract as keywords:
        // notes.note
        // location.effectiveLocation.code
        // location.effectiveLocation.name
        // callNumber.callNumber
        // statisticalCodes.code
        // electronicAccess.uri
        // electronicAccess.linkText
        // electronicAccess.materialsSpecification
        // electronicAccess.publicNote
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> holding : folioHoldings) {
            List<Map<String, String>> statisticalCodes = (List<Map<String, String>>) holding.get("statisticalCodes");
            for (Map<String, String> code : statisticalCodes) {
                sb.append(code.get("code")).append(' ');
            }
            List<Map<String, String>> notes = (List<Map<String, String>>) holding.get("notes");
            for (Map<String, String> note : notes) {
                if (null != note && null != note.get("staffOnly") && note.get("staffOnly").equals("false")) {
                    sb.append(note.get("note")).append(' ');
                }
            }
            Map<String, Object> location = (Map<String, Object>) holding.get("location");
            Map<String, String> effectiveLocation = (Map<String, String>) location.get("effectiveLocation");
            sb.append(effectiveLocation.get("code")).append(' ').append(effectiveLocation.get("name")).append(' ');
            Map<String, String> callNumber = (Map<String, String>) holding.get("callNumber");
            sb.append(callNumber.get("callNumber")).append(' ');
            List<Map<String, Object>> statCodes = (List<Map<String, Object>>) holding.get("statisticalCodes");
            statCodes.stream().forEach((final Map<String, Object> statCode) -> sb.append(statCode.get("code"))
                    .append(' ').append(statCode.get("name")).append(' '));
            List<Map<String, Object>> electronicAccesses = (List<Map<String, Object>>) holding.get("electronicAccess");
            electronicAccesses.stream()
                    .forEach((final Map<String, Object> electronicAccess) -> sb.append(electronicAccess.get("uri"))
                            .append(' ').append(electronicAccess.get("linkText")).append(' ')
                            .append(electronicAccess.get("materialsSpecification")).append(' ')
                            .append(electronicAccess.get("publicNote")).append(' '));
        }
        return sb.toString().trim();
    }

    private void getKeywordsFromBibRec(final List<Field> fields, final StringBuilder sb) {
        for (Field field : fields) {
            String tag = field.getTag();
            if (isBibKeywordTag(tag)) {
                getKeywordsFromField(tag, field.getSubfields(), sb);
            }
        }
        if (null != this.reservesAugmentation) {
            String reservesText = this.reservesAugmentation.getReservesAugmentations(fields.stream()
                    .filter((final Field f) -> "001".equals(f.getTag())).map(Field::getData).findFirst().orElse("0"));
            if (reservesText != null) {
                sb.append(' ').append(reservesText);
            }
        }
    }

    private void getKeywordsFromField(final String tag, final List<Subfield> subfields, final StringBuilder sb) {
        for (Subfield subfield : subfields) {
            char code = subfield.getCode();
            if (isKeywordSubfield(tag, code)) {
                getKeywordsFromSubfield(subfield.getData(), sb);
            }
            if (isAugmentable(tag, code) && null != this.authTextAugmentation) {
                String authText = this.authTextAugmentation.getAuthAugmentations(subfield.getData());
                if (authText != null) {
                    sb.append(' ').append(authText);
                }
            }
        }
    }

    private void getKeywordsFromHoldingsRec(final List<Field> fields, final StringBuilder sb) {
        fields.forEach((final Field f) -> {
            String tag = f.getTag();
            if (isHldgKeywordTag(tag)) {
                f.getSubfields().stream().filter((final Subfield sf) -> isKeywordSubfield(tag, sf.getCode()))
                        .forEach((final Subfield sf) -> getKeywordsFromSubfield(sf.getData(), sb));
            }
        });
    }

    private void getKeywordsFromSubfield(final String data, final StringBuilder sb) {
        if (sb.length() != 0) {
            sb.append(' ');
        }
        sb.append(data).append(' ');
    }

    private boolean isAugmentable(final String tag, final char code) {
        return code == '0' && AGUMENTABLE_TAGS.indexOf(tag) != -1;
    }

    private boolean isBibKeywordTag(final String tag) {
        int tagNumber = Integer.parseInt(tag);
        // 863 is SUL holdings tag and indexing it creates relevance ranking
        // noise with number searching
        // create separate SulKeywordStrategy if this conflicts with Lane
        // practice
        return (tagNumber >= TAG_100 && tagNumber < TAG_900 && tagNumber != TAG_863)
                || BIB_KEYWORD_TAG_ADDITIONS.indexOf(tag) != -1;
    }

    private boolean isHldgKeywordTag(final String tag) {
        return HLDG_KEYWORD_TAGS.indexOf(tag) != -1;
    }

    private boolean isKeywordSubfield(final String tag, final char code) {
        // 907 ^xy = indexable but other 907 subfields should not be included
        // 907s won't be found in bibs but we can use this for bibs and holdings
        // all other tag/subfield combinations are indexable
        return !"907".equals(tag) || "xy".indexOf(code) > -1;
    }
}
