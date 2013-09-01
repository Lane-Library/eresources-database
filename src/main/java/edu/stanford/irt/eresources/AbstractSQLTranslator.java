package edu.stanford.irt.eresources;

import java.util.regex.Pattern;

public class AbstractSQLTranslator {

    protected static final char COMMA = ',';

    protected static final char END_PAREN = ')';

    private static final String INSERT_INTO = "INSERT INTO ";

    protected static final String NULL = "NULL";

    protected static final char APOS = '\'';

    private static final Pattern APOS_PATTERN = Pattern.compile("'");

    private String insertInto;

    private String tablePrefix;

    public AbstractSQLTranslator(final String tablePrefix) {
        this.tablePrefix = tablePrefix;
        this.insertInto = INSERT_INTO + tablePrefix;
    }

    protected String apostrophize(final String string) {
        if (string == null) {
            return NULL;
        }
        StringBuilder sb = new StringBuilder().append(APOS).append(APOS_PATTERN.matcher(string).replaceAll("''"))
                .append(APOS);
        return sb.toString();
    }

    protected String getInsertInto() {
        return this.insertInto;
    }

    protected String getTablePrefix() {
        return this.tablePrefix;
    }
}