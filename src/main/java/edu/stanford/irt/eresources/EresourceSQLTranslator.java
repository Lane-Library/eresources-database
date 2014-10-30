package edu.stanford.irt.eresources;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class EresourceSQLTranslator extends AbstractSQLTranslator {

    private static final String INSERT_ERESOURCE = "ERESOURCE (ERESOURCE_ID , RECORD_ID, RECORD_TYPE, UPDATED, TITLE, PRIMARY_TYPE, CORE, YEAR, TOTAL, AVAILABLE, DESCRIPTION, TEXT) VALUES (";

    private DateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

    private VersionSQLTranslator versionTranslator;
    
    public EresourceSQLTranslator() {
        this("");
    }

    public EresourceSQLTranslator(final String tablePrefix) {
        super(tablePrefix);
        this.versionTranslator = new VersionSQLTranslator(tablePrefix);
    }

    public String getEresourceIdSQL(final Eresource er) {
        StringBuilder sb = new StringBuilder("SELECT ERESOURCE_ID FROM ").append(this.getTablePrefix())
                .append("ERESOURCE WHERE RECORD_ID = ").append(er.getRecordId())
                .append(" AND RECORD_TYPE = '").append(er.getRecordType()).append(APOS);
        return sb.toString();
    }

    public List<String> getInsertSQL(final Eresource er) {
        List<String> sql = new LinkedList<String>();
        String keywords = er.getKeywords();
        int[] itemCount = er.getItemCount();
        StringBuilder sb = new StringBuilder(this.getInsertInto()).append(INSERT_ERESOURCE)
                .append(this.getTablePrefix()).append("ERESOURCE_ID_SEQ.NEXTVAL,")
                .append(APOS).append(er.getRecordId()).append(APOS).append(COMMA)
                .append(APOS).append(er.getRecordType()).append(APOS).append(COMMA)
                .append("TO_DATE('").append(this.formatter.format(er.getUpdated())).append("','YYYYMMDDHH24MISS')").append(COMMA)
                .append(apostrophize(er.getTitle())).append(COMMA)
                .append(apostrophize(er.getPrimaryType())).append(COMMA)
                .append(er.isCore() ? "'Y'" : NULL).append(COMMA)
                .append(er.getYear() > 0 ? Integer.toString(er.getYear()) : NULL).append(COMMA)
                .append(itemCount[0]).append(COMMA)
                .append(itemCount[1]).append(COMMA)
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
            sql.addAll(this.versionTranslator.getInsertSQL(version, order++));
        }
        return sql;
    }

    private Collection<String> getInsertMeshSQL(final Eresource er) {
        Collection<String> sql = new LinkedList<String>();
        StringBuilder sb = new StringBuilder();
        for (String meshTerm : er.getMeshTerms()) {
            sb.setLength(0);
            sb.append(this.getInsertInto())
            .append("MESH VALUES (").append(this.getTablePrefix()).append("ERESOURCE_ID_SEQ.CURRVAL,").append(apostrophize(meshTerm)).append(END_PAREN);
            sql.add(sb.toString());
        }
        return sql;
    }

    private Collection<String> getInsertTypeSQL(final Eresource er) {
        Collection<String> sql = new LinkedList<String>();
        StringBuilder sb = new StringBuilder();
        for (String type : er.getTypes()) {
            sb.setLength(0);
            sb.append(this.getInsertInto()).append("TYPE VALUES (")
            .append(this.getTablePrefix()).append("ERESOURCE_ID_SEQ.CURRVAL,")
            .append(apostrophize(type)).append(END_PAREN);
            sql.add(sb.toString());
        }
        return sql;
    }
}
