package edu.stanford.irt.eresources;

import java.util.List;

public class ETLProcessor<T> {

    private Extractor<T> extractor;

    private Transformer<T> transformer;

    private Loader loader;

    public ETLProcessor(final Extractor<T> extractor, final Transformer<T> transformer, final Loader loader) {
        this.extractor = extractor;
        this.transformer = transformer;
        this.loader = loader;
    }
    
    public void process() {
        while(this.extractor.hasNext()) {
//            T t = this.extractor.next();
//            List<Eresource> ers = this.transformer.transform(t);
//            this.loader.load(ers);
            this.loader.load(this.transformer.transform(this.extractor.next()));
        }
    }
}
