package edu.stanford.irt.eresources.classes;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Loader;
import edu.stanford.irt.eresources.StartDate;

public class ClassesTransformerTest {

    private ClassesTransformer processor;
    
    private Loader loader;

    @Before
    public void setUp() {
        this.processor = new ClassesTransformer(getClass().getResourceAsStream("/edu/stanford/irt/eresources/classes2er.xsl"));
        this.loader = createMock(Loader.class);
    }

    @Test
    public void testProcess() {
        replay(this.loader);
        this.processor.transform(getClass().getResourceAsStream("classes.xml"));
        verify(this.loader);
    }
}
