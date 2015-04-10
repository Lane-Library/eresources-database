package edu.stanford.irt.eresources.laneblog;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.Transformer;
import edu.stanford.irt.eresources.sax.DefaultEresourceBuilder;
import edu.stanford.irt.eresources.sax.EresourceHandler;

public class LaneblogTransformer implements Transformer<Document>, EresourceHandler {

    private DefaultEresourceBuilder eresourceBuilder;

    private List<Eresource> eresources;

    private javax.xml.transform.Transformer transformer;

    public LaneblogTransformer(final InputStream input) {
        try {
            this.transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(input));
        } catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
            throw new EresourceException(e);
        }
        this.eresourceBuilder = new DefaultEresourceBuilder(this);
    }

    @Override
    public void handleEresource(final Eresource eresource) {
        this.eresources.add(eresource);
    }

    @Override
    public List<Eresource> transform(final Document doc) {
        this.eresources = new ArrayList<Eresource>();
        try {
            this.transformer.transform(new DOMSource(doc), new SAXResult(this.eresourceBuilder));
        } catch (TransformerException e) {
            throw new EresourceException(e);
        }
        return this.eresources;
    }
}
