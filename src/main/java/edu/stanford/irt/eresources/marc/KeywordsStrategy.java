package edu.stanford.irt.eresources.marc;

import java.util.List;

import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

public class KeywordsStrategy {

    private static final String AGUMENTABLE_TAGS = "100|600|650|700";

    private static final String KEYWORD_TAGS = "020|022|030|035|901|902|903|907|941|942|943";

    private AuthTextAugmentation authTextAugmentation;

    private ReservesTextAugmentation reservesAugmentation;

    public KeywordsStrategy(final AuthTextAugmentation authTextAugmentation,
            final ReservesTextAugmentation reservesAugmentation) {
        this.authTextAugmentation = authTextAugmentation;
        this.reservesAugmentation = reservesAugmentation;
    }

    public String getKeywords(final Record record) {
        StringBuilder sb = new StringBuilder();
        List<Field> fields = record.getFields();
        byte leaderByte6 = record.getLeaderByte(6);
        if ("uvxy".indexOf(leaderByte6) > -1) {
            fields.stream().filter((final Field f) -> "852".equals(f.getTag()) || "866".equals(f.getTag()))
                    .flatMap((final Field f) -> f.getSubfields().stream()).map(Subfield::getData)
                    .forEach((final String s) -> sb.append(s).append(' '));
        } else if (leaderByte6 == 'q') {
            fields.stream().filter((final Field f) -> {
                int tagNumber = Integer.parseInt(f.getTag());
                return tagNumber >= 100 && tagNumber <= 943;// && tagNumber != 245;
            }).forEach((final Field f) -> {
                String tag = f.getTag();
                f.getSubfields().stream().forEach((final Subfield s) -> {
                    String data = s.getData();
                    getKeywordsFromSubfield(data, sb);
                    if (isAugmentable(tag, s.getCode())) {
                        String authText = this.authTextAugmentation.getAuthAugmentations(data);
                        if (authText != null) {
                            sb.append(' ').append(authText);
                        }
                    }
                });
            });
        } else {
            for (Field field : fields) {
                String tag = field.getTag();
                if (isKeywordTag(tag)) {
                    getKeywordsFromField(tag, field.getSubfields(), sb);
                }
            }
            String reservesText = this.reservesAugmentation.getReservesAugmentations(fields.stream()
                    .filter((final Field f) -> "001".equals(f.getTag())).map(Field::getData).findFirst().orElse("0"));
            if (reservesText != null) {
                sb.append(' ').append(reservesText);
            }
        }
        return sb.toString();
    }

    private void getKeywordsFromField(final String tag, final List<Subfield> subfields, final StringBuilder sb) {
        for (Subfield subfield : subfields) {
            char code = subfield.getCode();
            if (isKeywordSubfield(tag, code)) {
                getKeywordsFromSubfield(subfield.getData(), sb);
            }
            if (isAugmentable(tag, code)) {
                String authText = this.authTextAugmentation.getAuthAugmentations(subfield.getData());
                if (authText != null) {
                    sb.append(' ').append(authText);
                }
            }
        }
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

    private boolean isKeywordSubfield(final String tag, final char code) {
        return !"907".equals(tag) || "xy".indexOf(code) > -1;
    }

    private boolean isKeywordTag(final String tag) {
        int tagNumber = Integer.parseInt(tag);
        return (tagNumber >= 100 && tagNumber < 900) || KEYWORD_TAGS.indexOf(tag) != -1;
    }
}
