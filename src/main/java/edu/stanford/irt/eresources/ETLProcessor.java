package edu.stanford.irt.eresources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ETLProcessor<T> {
    
    private static final Logger LOG = LoggerFactory.getLogger(ETLProcessor.class);

    private Extractor<T> extractor;

    private Transformer<T> transformer;

    private Loader loader;

    private String name;

    public ETLProcessor(final String name, final Extractor<T> extractor, final Transformer<T> transformer, final Loader loader) {
        this.name = name;
        this.extractor = extractor;
        this.transformer = transformer;
        this.loader = loader;
    }
    
    public void process() {
        LOG.info("start " + this.name);
        while(this.extractor.hasNext()) {
//            T t = this.extractor.next();
//            List<Eresource> ers = this.transformer.transform(t);
//            this.loader.load(ers);
            this.loader.load(this.transformer.transform(this.extractor.next()));
        }
        LOG.info("end " + this.name);
    }
}
