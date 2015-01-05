package edu.stanford.irt.eresources;

public class LinkSQLTranslator extends AbstractSQLTranslator {

    private static final String INSERT_LINK = "INSERT INTO LINK (LINK_ID, VERSION_ID, ERESOURCE_ID, LABEL, URL, LINK_TEXT, ADDITIONAL_TEXT) VALUES (";

    public String getInsertSQL(final Link link) {
        StringBuilder sb = new StringBuilder(INSERT_LINK)
                .append("LINK_ID_SEQ.NEXTVAL, ")
                .append("VERSION_ID_SEQ.CURRVAL, ")
                .append("ERESOURCE_ID_SEQ.CURRVAL, ")
                .append(apostrophize(link.getLabel())).append(COMMA)
                .append(apostrophize(link.getUrl())).append(COMMA)
                .append(apostrophize(link.getLinkText())).append(COMMA)
                .append(apostrophize(link.getAdditionalText()))
                .append(END_PAREN);
        return sb.toString();
    }
}
