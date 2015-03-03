package edu.stanford.irt.eresources.jdbc;

import java.util.regex.Pattern;

public class AbstractSQLTranslator {

    protected static final char APOS = '\'';

    protected static final char COMMA = ',';

    protected static final char END_PAREN = ')';

    protected static final String NULL = "NULL";

    private static final Pattern APOS_PATTERN = Pattern.compile("'");

    protected String apostrophize(final String string) {
        if (string == null) {
            return NULL;
        }
        StringBuilder sb = new StringBuilder().append(APOS).append(APOS_PATTERN.matcher(string).replaceAll("''"))
                .append(APOS);
        return sb.toString();
    }
}