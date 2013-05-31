package edu.stanford.irt.eresources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class EresourceSQLTranslator {
    
    private static final String INSERT_INTO = "INSERT INTO ";

    private static final String INSERT_ERESOURCE = "ERESOURCE (ERESOURCE_ID , RECORD_ID, RECORD_TYPE, UPDATED, TITLE, CORE, YEAR, DESCRIPTION, TEXT) VALUES (";

    private static final String INSERT_LINK = "LINK (LINK_ID, VERSION_ID, ERESOURCE_ID, LABEL, URL, INSTRUCTION, LINK_TEXT) VALUES (";

    private static final String INSERT_VERSION = "VERSION (VERSION_ID, ERESOURCE_ID, PUBLISHER, HOLDINGS, DATES, DESCRIPTION, PROXY, GETPASSWORD, SEQNUM) VALUES (";

    private static final String NULL = "NULL";
    
    private static final char COMMA = ',';

    private static final char END_PAREN = ')';

    private static final char APOS = '\'';
    
    private static final Pattern APOS_PATTERN = Pattern.compile("'");

    private DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    
    private String insertInto;

    private String tablePrefix;
    
    public EresourceSQLTranslator() {
        this("");
    }
    
    public EresourceSQLTranslator(String tablePrefix) {
        this.tablePrefix = tablePrefix;
        this.insertInto = INSERT_INTO + tablePrefix;
    }

    public String getEresourceIdSQL(final Eresource er) {
        StringBuilder sb = new StringBuilder("SELECT ERESOURCE_ID FROM ").append(this.tablePrefix)
                .append("ERESOURCE WHERE RECORD_ID = '").append(er.getRecordId())
                .append("' AND RECORD_TYPE = '").append(er.getRecordType()).append(APOS);
        return sb.toString();
    }

    public List<String> getInsertSQL(final DatabaseEresource er) {
        List<String> sql = new LinkedList<String>();
        String keywords = er.getKeywords();
        StringBuilder sb = new StringBuilder(this.insertInto).append(INSERT_ERESOURCE)
                .append(this.tablePrefix).append("ERESOURCE_ID_SEQ.NEXTVAL,")
                .append(APOS).append(er.getRecordId()).append(APOS).append(COMMA)
                .append(APOS).append(er.getRecordType()).append(APOS).append(COMMA)
                .append("TO_DATE('").append(this.formatter.format(er.getUpdated())).append("','YYYYMMDDHH24MISS')").append(COMMA)
                .append(apostrophize(er.getTitle())).append(COMMA)
                .append(er.isCore() ? "'Y'" : NULL).append(COMMA)
                .append(er.getYear() > 0 ? Integer.toString(er.getYear()) : NULL).append(COMMA)
                .append(er.getDescription() != null ? "empty_clob()" : NULL)
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
        if (string == null) {
            return NULL;
        }
        StringBuilder sb = new StringBuilder().append(APOS).append(APOS_PATTERN.matcher(string).replaceAll("''")).append(APOS);
        return sb.toString();
    }

    private String getInsertLinkSQL(final DatabaseLink link) {
        StringBuilder sb = new StringBuilder(this.insertInto).append(INSERT_LINK)
                .append(this.tablePrefix).append("LINK_ID_SEQ.NEXTVAL, ")
                .append(this.tablePrefix).append("VERSION_ID_SEQ.CURRVAL, ")
                .append(this.tablePrefix).append("ERESOURCE_ID_SEQ.CURRVAL, ")
                .append(apostrophize(link.getLabel())).append(COMMA)
                .append(apostrophize(link.getUrl())).append(COMMA)
                .append(apostrophize(link.getInstruction())).append(COMMA)
                .append(apostrophize(link.getLinkText()))
                .append(END_PAREN);
        return sb.toString();
    }

    private Collection<String> getInsertMeshSQL(final DatabaseEresource er) {
        Collection<String> sql = new LinkedList<String>();
        StringBuilder sb = new StringBuilder();
        for (String meshTerm : er.getMeshTerms()) {
            sb.setLength(0);
            sb.append(this.insertInto)
            .append("MESH VALUES (").append(this.tablePrefix).append("ERESOURCE_ID_SEQ.CURRVAL,").append(apostrophize(meshTerm)).append(END_PAREN);
            sql.add(sb.toString());
        }
        return sql;
    }

    private Collection<String> getInsertSubsetSQL(final DatabaseVersion vr) {
        Collection<String> sql = new LinkedList<String>();
        StringBuilder sb = new StringBuilder();
        for (String subset : vr.getSubsets()) {
            sb.setLength(0);
            sb.append(this.insertInto).append("SUBSET VALUES (")
            .append(this.tablePrefix).append("VERSION_ID_SEQ.CURRVAL,")
            .append(this.tablePrefix).append("ERESOURCE_ID_SEQ.CURRVAL,")
            .append(apostrophize(subset)).append(END_PAREN);
            sql.add(sb.toString());
        }
        return sql;
    }

    private Collection<String> getInsertTypeSQL(final DatabaseEresource er) {
        Collection<String> sql = new LinkedList<String>();
        StringBuilder sb = new StringBuilder();
        for (String type : er.getTypes()) {
            sb.setLength(0);
            sb.append(this.insertInto).append("TYPE VALUES (")
            .append(this.tablePrefix).append("ERESOURCE_ID_SEQ.CURRVAL,")
            .append(apostrophize(type)).append(END_PAREN);
            sql.add(sb.toString());
        }
        return sql;
    }

    private Collection<String> getInsertVersionSQL(final DatabaseVersion vr, final int order) {
        Collection<String> sql = new LinkedList<String>();
        StringBuilder sb = new StringBuilder(this.insertInto).append(INSERT_VERSION)
                .append(this.tablePrefix).append("VERSION_ID_SEQ.NEXTVAL, ")
                .append(this.tablePrefix).append("ERESOURCE_ID_SEQ.CURRVAL,")
                .append(apostrophize(vr.getPublisher())).append(COMMA)
                .append(apostrophize(vr.getSummaryHoldings())).append(COMMA)
                .append(apostrophize(vr.getDates())).append(COMMA)
                .append(apostrophize(vr.getDescription())).append(COMMA)
                .append(vr.isProxy() ? "'T'" : "'F'").append(COMMA)
                .append(vr.hasGetPasswordLink() ? "'T'" : "'F'").append(COMMA)
                .append(order)
                .append(END_PAREN);
        sql.add(sb.toString());
        sql.addAll(getInsertSubsetSQL(vr));
        for (Link link : vr.getLinks()) {
            sql.add(getInsertLinkSQL((DatabaseLink)link));
        }
        return sql;
    }
}
