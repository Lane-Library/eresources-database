package edu.stanford.irt.eresources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ETLProcessor<E> {

    private static final Logger LOG = LoggerFactory.getLogger(ETLProcessor.class);

    private Extractor<E> extractor;

    private Loader loader;

    private String name;

    private Transformer<E> transformer;

    public ETLProcessor(final String name, final Extractor<E> extractor, final Transformer<E> transformer,
            final Loader loader) {
        this.name = name;
        this.extractor = extractor;
        this.transformer = transformer;
        this.loader = loader;
    }

    public void process() {
        LOG.info("start " + this.name);
        while (this.extractor.hasNext()) {
            this.loader.load(this.transformer.transform(this.extractor.next()));
        }
        LOG.info("end " + this.name);
    }
}
