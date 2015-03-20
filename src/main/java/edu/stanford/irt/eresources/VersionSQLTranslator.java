package edu.stanford.irt.eresources;

import java.util.LinkedList;
import java.util.List;

public class VersionSQLTranslator extends AbstractSQLTranslator {

    private static final String INSERT_VERSION = "INSERT INTO VERSION (VERSION_ID, ERESOURCE_ID, PROXY, GETPASSWORD, SEQNUM, ADDITIONAL_TEXT, PUBLISHER, HOLDINGS_DATES) VALUES (";

    private LinkSQLTranslator linkTranslator;

    public VersionSQLTranslator() {
        this.linkTranslator = new LinkSQLTranslator();
    }

    public List<String> getInsertSQL(final Version vr, final int order) {
        List<String> sql = new LinkedList<String>();
        StringBuilder sb = new StringBuilder(INSERT_VERSION)
                .append("VERSION_ID_SEQ.NEXTVAL, ").append("ERESOURCE_ID_SEQ.CURRVAL,")
                .append(vr.isProxy() ? "'T'" : "'F'").append(COMMA).append(vr.hasGetPasswordLink() ? "'T'" : "'F'")
                .append(COMMA).append(order).append(COMMA).append(apostrophize(vr.getAdditionalText())).append(COMMA)
                .append(apostrophize(vr.getPublisher())).append(COMMA)
                .append(apostrophize(vr.getHoldingsAndDates())).append(END_PAREN);
        sql.add(sb.toString());
        for (Link link : vr.getLinks()) {
            sql.add(this.linkTranslator.getInsertSQL(link));
        }
        return sql;
    }
}
