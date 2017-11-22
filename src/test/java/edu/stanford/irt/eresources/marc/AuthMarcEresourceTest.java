package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Collections;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.lane.catalog.Record;

public class AuthMarcEresourceTest {

    private AuthMarcEresource eresource;

    private Record record;

    private TypeFactory typeFactory;

    @Before
    public void setUp() {
        this.record = mock(Record.class);
        this.typeFactory = mock(TypeFactory.class);
        this.eresource = new AuthMarcEresource(this.record, "", this.typeFactory);
    }

    @Test
    public void testGetMeshTerms() {
        expect(this.record.getFields()).andReturn(Collections.emptyList());
        replay(this.record);
        assertEquals(Collections.emptySet(), this.eresource.getMeshTerms());
        verify(this.record);
    }

    @Test
    public void testGetRecordType() {
        assertEquals("auth", this.eresource.getRecordType());
    }

    @Test
    public void testGetVersions() {
        expect(this.record.getFields()).andReturn(Collections.emptyList());
        replay(this.record);
        assertEquals(Collections.emptyList(), this.eresource.getVersions());
        verify(this.record);
    }

    @Test
    public void testGetYear() {
        expect(this.record.getFields()).andReturn(Collections.emptyList());
        replay(this.record);
        assertEquals(0, this.eresource.getYear());
        verify(this.record);
    }
}
