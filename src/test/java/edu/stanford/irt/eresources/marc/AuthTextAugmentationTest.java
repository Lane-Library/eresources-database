package edu.stanford.irt.eresources.marc;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;

import java.io.InputStream;
import java.util.Collections;
import java.util.concurrent.Executor;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.marc4j.MarcReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;

public class AuthTextAugmentationTest {

    private AuthTextAugmentation augmentation;

    private DataField field;

    private MarcReaderFactory marcReaderFactory;

    private Record record;

    private Subfield subfield;

    private Executor executor;

    private DataSource dataSource;
    
    private MarcReader marcReader;

    @Before
    public void setUp() {
        this.marcReaderFactory = createMock(MarcReaderFactory.class);
        this.dataSource = createMock(DataSource.class);
        this.executor = createMock(Executor.class);
        this.augmentation = new AuthTextAugmentation(this.marcReaderFactory, this.dataSource, this.executor);
        this.marcReader = createMock(MarcReader.class);
        this.record = createMock(Record.class);
        this.field = createMock(DataField.class);
        this.subfield = createMock(Subfield.class);
    }

    @Test
    public void testGetAuthAugmentations() {
        expect(this.marcReaderFactory.newMarcReader(isA(InputStream.class))).andReturn(this.marcReader);
        expect(this.marcReader.hasNext()).andReturn(true);
        expect(this.marcReader.next()).andReturn(this.record);
        expect(this.record.getDataFields()).andReturn(Collections.singletonList(this.field));
        expect(this.field.getTag()).andReturn("400");
        expect(this.field.getSubfields('a')).andReturn(Collections.singletonList(this.subfield));
        expect(this.subfield.getData()).andReturn("augmentation");
        expect(this.marcReader.hasNext()).andReturn(false);
        replay(this.marcReaderFactory, this.marcReader, this.record, this.field, this.subfield);
        assertEquals("augmentation", this.augmentation.getAuthAugmentations("12"));
        verify(this.marcReaderFactory, this.marcReader, this.record, this.field, this.subfield);
    }
}
