package edu.stanford.irt.eresources.webpage;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.Loader;


public class WebpageFileTransformerTest {
    
    private WebpageFileTransformer processor;
    
    private File file;
    
    private Loader loader;

    @Before
    public void setUp() {
        String basePath = System.getProperty("user.dir") + "/src/test/resources/edu/stanford/irt/eresources/webpage";

        this.processor = new WebpageFileTransformer(basePath, getClass().getResourceAsStream("/edu/stanford/irt/eresources/web2er.xsl"), "lane-host");
        this.file = new File(basePath + "/file.html");
        this.loader = createMock(Loader.class);
    }

    @Test
    public void testProcess() {
//        expect(this.startDate.getStartDate()).andReturn(new Date(0));
//        expect(this.input.isEmpty()).andReturn(true);
//        expect(this.input.take()).andReturn(WebpageFileExtractor.EMPTY);
//        expect(file.getAbsolutePath()).andReturn("absolutePath");
//        expect(file.getPath()).andReturn("path");
        replay(this.loader);
        this.processor.transform(this.file);
        verify(this.loader);
    }
}
