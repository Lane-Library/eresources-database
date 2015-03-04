package edu.stanford.irt.eresources;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class VersionSQLTranslator extends AbstractSQLTranslator {

    private static final String INSERT_VERSION = "VERSION (VERSION_ID, ERESOURCE_ID, PROXY, GETPASSWORD, SEQNUM, ADDITIONAL_TEXT, PUBLISHER, HOLDINGS_DATES) VALUES (";

    private LinkSQLTranslator linkTranslator;

    public VersionSQLTranslator(final String tablePrefix) {
        super(tablePrefix);
        this.linkTranslator = new LinkSQLTranslator(tablePrefix);
    }

    public List<String> getInsertSQL(final Version vr, final int order) {
        List<String> sql = new LinkedList<String>();
        String tablePrefix = getTablePrefix();
        StringBuilder sb = new StringBuilder(this.getInsertInto()).append(INSERT_VERSION).append(tablePrefix)
                .append("VERSION_ID_SEQ.NEXTVAL, ").append(tablePrefix).append("ERESOURCE_ID_SEQ.CURRVAL,")
                .append(vr.isProxy() ? "'T'" : "'F'").append(COMMA).append(vr.hasGetPasswordLink() ? "'T'" : "'F'")
                .append(COMMA).append(order).append(COMMA).append(apostrophize(vr.getAdditionalText())).append(COMMA)
                .append(apostrophize(vr.getPublisher())).append(COMMA)
                .append(apostrophize(vr.getHoldingsAndDates())).append(END_PAREN);
        sql.add(sb.toString());
        sql.addAll(getInsertSubsetSQL(vr));
        for (Link link : vr.getLinks()) {
            sql.add(this.linkTranslator.getInsertSQL(link));
        }
        return sql;
    }

    private Collection<String> getInsertSubsetSQL(final Version vr) {
        Collection<String> sql = new LinkedList<String>();
        String tablePrefix = getTablePrefix();
        StringBuilder sb = new StringBuilder();
        for (String subset : vr.getSubsets()) {
            sb.setLength(0);
            sb.append(this.getInsertInto()).append("SUBSET VALUES (").append(tablePrefix)
                    .append("VERSION_ID_SEQ.CURRVAL,").append(tablePrefix).append("ERESOURCE_ID_SEQ.CURRVAL,")
                    .append(apostrophize(subset)).append(END_PAREN);
            sql.add(sb.toString());
        }
        return sql;
    }
}
