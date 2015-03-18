package edu.stanford.irt.eresources.classes;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceException;
import edu.stanford.irt.eresources.Transformer;
import edu.stanford.irt.eresources.sax.DefaultEresourceBuilder;
import edu.stanford.irt.eresources.sax.EresourceHandler;

public class ClassesTransformer implements Transformer<InputStream, Eresource>, EresourceHandler {

    private DefaultEresourceBuilder eresourceBuilder;

    private List<Eresource> eresources;

    private ErrorHandler errorHandler = new DefaultHandler();

    /** the DocumentBuilderFactory. */
    private DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private String laneHost;

    private javax.xml.transform.Transformer transformer;

    public ClassesTransformer(final InputStream input, final String laneHost) {
        this.laneHost = laneHost;
        try {
            this.transformer = TransformerFactory.newInstance().newTransformer(new StreamSource(input));
        } catch (TransformerConfigurationException | TransformerFactoryConfigurationError e) {
            throw new EresourceException(e);
        }
        this.transformer.setParameter("lane-host", this.laneHost);
        this.eresourceBuilder = new DefaultEresourceBuilder(this);
    }

    @Override
    public void handleEresource(final Eresource eresource) {
        this.eresources.add(eresource);
    }

    @Override
    public List<Eresource> transform(final InputStream input) {
        this.eresources = new ArrayList<Eresource>();
        try {
            InputSource source;
            source = new InputSource(input);
            DocumentBuilder parser = this.factory.newDocumentBuilder();
            parser.setErrorHandler(this.errorHandler);
            Document doc = parser.parse(source);
            this.transformer.transform(new DOMSource(doc), new SAXResult(this.eresourceBuilder));
        } catch (SAXException | IOException | ParserConfigurationException | TransformerException e) {
            throw new EresourceException(e);
        }
        return this.eresources;
    }
}
