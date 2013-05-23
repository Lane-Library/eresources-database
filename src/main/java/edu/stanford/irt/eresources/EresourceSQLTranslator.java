/**
 * 
 */
package edu.stanford.irt.eresources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author ceyates
 */
public class EresourceSQLTranslator {

    private static final String INSERT_ERESOURCE = "INSERT INTO ERESOURCE (ERESOURCE_ID , RECORD_ID, RECORD_TYPE, UPDATED, TITLE, CORE, YEAR, DESCRIPTION, TEXT) VALUES (";

    private static final String INSERT_LINK = "INSERT INTO LINK (LINK_ID, VERSION_ID, ERESOURCE_ID, LABEL, URL, INSTRUCTION, LINK_TEXT) VALUES (";

    private static final String INSERT_VERSION = "INSERT INTO VERSION (VERSION_ID, ERESOURCE_ID, PUBLISHER, HOLDINGS, DATES, DESCRIPTION, PROXY, GETPASSWORD, SEQNUM) VALUES (";

    private DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

    public String getEresourceIdSQL(final Eresource er) {
        return "SELECT ERESOURCE_ID FROM ERESOURCE WHERE RECORD_ID = '" + er.getRecordId() + "' AND RECORD_TYPE = '"
                + er.getRecordType() + "'";
    }

    public List<String> getInsertSQL(final DatabaseEresource er) {
        List<String> sql = new LinkedList<String>();
        String apostrophizedTitle = apostrophize(er.getTitle());
        String keywords = er.getKeywords();
        StringBuilder sb = new StringBuilder(INSERT_ERESOURCE).append("ERESOURCE_ID_SEQ.NEXTVAL,").append('\'')
                .append(er.getRecordId()).append("',").append('\'').append(er.getRecordType()).append("',")
                .append("TO_DATE('").append(this.formatter.format(er.getUpdated())).append("','YYYYMMDDHH24MISS'),")
                .append('\'').append(apostrophizedTitle).append("',").append(er.isCore() ? "'Y'," : "NULL,")
                .append(er.getYear() > 0 ? Integer.toString(er.getYear()) : "NULL")
                .append(er.getDescription() != null ? ",empty_clob()" : ",NULL")
                .append(", empty_clob())");
        sql.add(sb.toString());
        sql.add("TEXT:" + keywords);
        if (er.getDescription() != null) {
            sql.add("DESCRIPTION:" + er.getDescription());
        }
        sql.addAll(getInsertTypeSQL(er));
        sql.addAll(getInsertMeshSQL(er));
        int order = 0;
        for (Version version : er.getVersions()) {
            sql.addAll(getInsertVersionSQL((DatabaseVersion) version, order++));
        }
        return sql;
    }

    private String apostrophize(final String string) {
        return string.replaceAll("'", "''");
    }

    private String getInsertLinkSQL(final DatabaseLink link) {
        StringBuilder sb = new StringBuilder(INSERT_LINK)
                .append("LINK_ID_SEQ.NEXTVAL, VERSION_ID_SEQ.CURRVAL, ERESOURCE_ID_SEQ.CURRVAL, ")
                .append(null == link.getLabel() ? "NULL," : "'" + apostrophize(link.getLabel()) + "',")
                .append(null == link.getUrl() ? "NULL," : "'" + apostrophize(link.getUrl()) + "',")
                .append(null == link.getInstruction() ? "NULL," : "'" + apostrophize(link.getInstruction()) + "',")
                .append(link.getLinkText() == null ? "NULL)" : "'" + apostrophize(link.getLinkText()) + ")");
        return sb.toString();
    }

    private Collection<String> getInsertMeshSQL(final DatabaseEresource er) {
        Collection<String> sql = new LinkedList<String>();
        for (String meshTerm : er.getMeshTerms()) {
            sql.add("INSERT INTO MESH VALUES (ERESOURCE_ID_SEQ.CURRVAL,'" + apostrophize(meshTerm) + "')");
        }
        return sql;
    }

    private Collection<String> getInsertSubsetSQL(final DatabaseVersion vr) {
        Collection<String> sql = new LinkedList<String>();
        for (String subset : vr.getSubsets()) {
            sql.add("INSERT INTO SUBSET VALUES (VERSION_ID_SEQ.CURRVAL,ERESOURCE_ID_SEQ.CURRVAL,'"
                    + apostrophize(subset) + "')");
        }
        return sql;
    }

    private Collection<String> getInsertTypeSQL(final DatabaseEresource er) {
        Collection<String> sql = new LinkedList<String>();
        for (String type : er.getTypes()) {
            sql.add("INSERT INTO TYPE VALUES (ERESOURCE_ID_SEQ.CURRVAL,'" + apostrophize(type) + "')");
        }
        return sql;
    }

    private Collection<String> getInsertVersionSQL(final DatabaseVersion vr, final int order) {
        Collection<String> sql = new LinkedList<String>();
        StringBuilder sb = new StringBuilder(INSERT_VERSION)
                .append("VERSION_ID_SEQ.NEXTVAL, ERESOURCE_ID_SEQ.CURRVAL,")
                .append(null == vr.getPublisher() ? "NULL," : "'" + apostrophize(vr.getPublisher()) + "',")
                .append(null == vr.getSummaryHoldings() ? "NULL," : "'" + apostrophize(vr.getSummaryHoldings()) + "',")
                .append(null == vr.getDates() ? "NULL," : "'" + apostrophize(vr.getDates()) + "',")
                .append(null == vr.getDescription() ? "NULL," : "'" + apostrophize(vr.getDescription()) + "',")
                .append(vr.isProxy() ? "'T'," : "'F',").append(vr.hasGetPasswordLink() ? "'T'," : "'F',").append(order)
                .append(')');
        sql.add(sb.toString());
        sql.addAll(getInsertSubsetSQL(vr));
        for (Link link : vr.getLinks()) {
            sql.add(getInsertLinkSQL((DatabaseLink)link));
        }
        return sql;
    }
}
