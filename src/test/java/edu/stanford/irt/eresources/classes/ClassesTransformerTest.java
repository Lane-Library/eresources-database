package edu.stanford.irt.eresources.classes;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Loader;

public class ClassesTransformerTest {

    private Loader loader;

    private ClassesTransformer processor;

    @Before
    public void setUp() {
        this.processor = new ClassesTransformer(getClass().getResourceAsStream(
                "/edu/stanford/irt/eresources/classes2er.xsl"), "lane-host");
        this.loader = createMock(Loader.class);
    }

    @Test
    public void testProcess() {
        replay(this.loader);
        this.processor.transform(getClass().getResourceAsStream("classes.xml"));
        verify(this.loader);
    }
}
