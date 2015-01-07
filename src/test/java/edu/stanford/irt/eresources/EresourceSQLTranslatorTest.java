package edu.stanford.irt.eresources;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class EresourceSQLTranslatorTest {
    
    private EresourceSQLTranslator translator;
    private Eresource eresource;
    private Version version;
    private VersionSQLTranslator versionTranslator;

    @Before
    public void setUp() {
        this.versionTranslator = createMock(VersionSQLTranslator.class);
        this.translator = new EresourceSQLTranslator(this.versionTranslator);
        this.eresource = createMock(Eresource.class);
        this.version = createMock(Version.class);
    }

    @Test
    public void testGetEresourceIdSQL() {
        expect(this.eresource.getRecordId()).andReturn(1);
        expect(this.eresource.getRecordType()).andReturn("type");
        replay(this.eresource);
        assertEquals("SELECT ERESOURCE_ID FROM ERESOURCE WHERE RECORD_ID = 1 AND RECORD_TYPE = 'type'", this.translator.getEresourceIdSQL(this.eresource));
        verify(this.eresource);
    }

    @Test
    public void testGetInsertSQL() {
        expect(this.eresource.getKeywords()).andReturn("keywords");
        expect(this.eresource.getItemCount()).andReturn(new int[] {1,1});
        expect(this.eresource.getDescription()).andReturn("description");
        expect(this.eresource.getRecordId()).andReturn(1);
        expect(this.eresource.getRecordType()).andReturn("type");
        expect(this.eresource.getUpdated()).andReturn(new Date(0));
        expect(this.eresource.getTitle()).andReturn("title");
        expect(this.eresource.getPrimaryType()).andReturn("primaryType");
        expect(this.eresource.isCore()).andReturn(false);
        expect(this.eresource.getYear()).andReturn(2010).times(2);
        expect(this.eresource.getTypes()).andReturn(Collections.singleton("type"));
        expect(this.eresource.getMeshTerms()).andReturn(Collections.singleton("mesh"));
        expect(this.eresource.getVersions()).andReturn(Collections.singleton(this.version));
        expect(this.versionTranslator.getInsertSQL(this.version, 0)).andReturn(Collections.<String>emptyList());
        replay(this.eresource, this.version, this.versionTranslator);
        List<String> sql = this.translator.getInsertSQL(this.eresource);
        String dateString = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date(0));
        assertEquals("INSERT INTO ERESOURCE (ERESOURCE_ID , RECORD_ID, RECORD_TYPE, UPDATED, TITLE, PRIMARY_TYPE, CORE, YEAR, TOTAL, AVAILABLE, DESCRIPTION, TEXT) VALUES (ERESOURCE_ID_SEQ.NEXTVAL,'1','type',TO_DATE('" + dateString + "','YYYYMMDDHH24MISS'),'title','primaryType',NULL,2010,1,1,empty_clob(), empty_clob())", sql.get(0));
        assertEquals("TEXT:keywords", sql.get(1));
        assertEquals("DESCRIPTION:description", sql.get(2));
        assertEquals("INSERT INTO TYPE VALUES (ERESOURCE_ID_SEQ.CURRVAL,'type')", sql.get(3));
        assertEquals("INSERT INTO MESH VALUES (ERESOURCE_ID_SEQ.CURRVAL,'mesh')", sql.get(4));
        verify(this.eresource, this.version, this.versionTranslator);
    }
}
