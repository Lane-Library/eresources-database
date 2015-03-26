package edu.stanford.irt.eresources.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;

public class VersionSQLTranslator extends AbstractSQLTranslator {

    private static final String INSERT_VERSION = "INSERT INTO VERSION (VERSION_ID, ERESOURCE_ID, PROXY, GETPASSWORD, SEQNUM, ADDITIONAL_TEXT, PUBLISHER, HOLDINGS_DATES) VALUES (";

    private LinkSQLTranslator linkTranslator;

    public VersionSQLTranslator(final LinkSQLTranslator linkTranslator) {
        this.linkTranslator = linkTranslator;
    }

    public List<String> getInsertSQL(final Version vr, final int order) {
        List<String> sql = new ArrayList<String>();
        StringBuilder sb = new StringBuilder(INSERT_VERSION).append("VERSION_ID_SEQ.NEXTVAL, ")
                .append("ERESOURCE_ID_SEQ.CURRVAL,").append(vr.isProxy() ? "'T'" : "'F'").append(COMMA)
                .append(vr.hasGetPasswordLink() ? "'T'" : "'F'").append(COMMA).append(order).append(COMMA)
                .append(apostrophize(vr.getAdditionalText())).append(COMMA).append(apostrophize(vr.getPublisher()))
                .append(COMMA).append(apostrophize(vr.getHoldingsAndDates())).append(END_PAREN);
        sql.add(sb.toString());
        sql.addAll(getInsertSubsetSQL(vr));
        for (Link link : vr.getLinks()) {
            sql.add(this.linkTranslator.getInsertSQL(link));
        }
        return sql;
    }

    private Collection<String> getInsertSubsetSQL(final Version vr) {
        Collection<String> sql = new ArrayList<String>();
        StringBuilder sb = new StringBuilder();
        for (String subset : vr.getSubsets()) {
            sb.setLength(0);
            sb.append("INSERT INTO SUBSET VALUES (").append("VERSION_ID_SEQ.CURRVAL,")
            .append("ERESOURCE_ID_SEQ.CURRVAL,").append(apostrophize(subset)).append(END_PAREN);
            sql.add(sb.toString());
        }
        return sql;
    }
}
