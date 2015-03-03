package edu.stanford.irt.eresources.jdbc;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Link;
import edu.stanford.irt.eresources.Version;


public class VersionSQLTranslatorTest {
    
    private VersionSQLTranslator translator;
    private LinkSQLTranslator linkTranslator;
    private Version version;
    private Link link;

    @Before
    public void setUp() {
        this.linkTranslator = createMock(LinkSQLTranslator.class);
        this.translator = new VersionSQLTranslator(this.linkTranslator);
        this.version = createMock(Version.class);
        this.link = createMock(Link.class);
    }

    @Test
    public void testGetInsertSQL() {
        expect(this.version.isProxy()).andReturn(true);
        expect(this.version.hasGetPasswordLink()).andReturn(false);
        expect(this.version.getAdditionalText()).andReturn("additionalText");
        expect(this.version.getPublisher()).andReturn("publisher");
        expect(this.version.getSubsets()).andReturn(Collections.singleton("subset"));
        expect(this.version.getLinks()).andReturn(Collections.singletonList(this.link));
        expect(this.linkTranslator.getInsertSQL(this.link)).andReturn("link");
        replay(this.linkTranslator, this.version, this.link);
        List<String> sql = this.translator.getInsertSQL(this.version, 0);
        assertEquals("INSERT INTO VERSION (VERSION_ID, ERESOURCE_ID, PROXY, GETPASSWORD, SEQNUM, ADDITIONAL_TEXT, PUBLISHER) VALUES (VERSION_ID_SEQ.NEXTVAL, ERESOURCE_ID_SEQ.CURRVAL,'T','F',0,'additionalText','publisher')", sql.get(0));
        assertEquals("INSERT INTO SUBSET VALUES (VERSION_ID_SEQ.CURRVAL,ERESOURCE_ID_SEQ.CURRVAL,'subset')", sql.get(1));
        assertEquals("link", sql.get(2));
        verify(this.linkTranslator, this.version, this.link);
    }
}
