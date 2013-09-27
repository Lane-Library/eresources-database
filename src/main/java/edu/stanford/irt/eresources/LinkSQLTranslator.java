package edu.stanford.irt.eresources;

public class LinkSQLTranslator extends AbstractSQLTranslator {

    private static final String INSERT_LINK = "LINK (LINK_ID, VERSION_ID, ERESOURCE_ID, LABEL, URL, LINK_TEXT, ADDITIONAL_TEXT) VALUES (";

    public LinkSQLTranslator(final String tablePrefix) {
        super(tablePrefix);
    }

    public String getInsertSQL(final Link link) {
        String tablePrefix = this.getTablePrefix();
        StringBuilder sb = new StringBuilder(this.getInsertInto()).append(INSERT_LINK)
                .append(tablePrefix).append("LINK_ID_SEQ.NEXTVAL, ")
                .append(tablePrefix).append("VERSION_ID_SEQ.CURRVAL, ")
                .append(tablePrefix).append("ERESOURCE_ID_SEQ.CURRVAL, ")
                .append(apostrophize(link.getLabel())).append(COMMA)
                .append(apostrophize(link.getUrl())).append(COMMA)
                .append(apostrophize(link.getLinkText())).append(COMMA)
                .append(apostrophize(link.getAdditionalText()))
                .append(END_PAREN);
        return sb.toString();
    }
}
