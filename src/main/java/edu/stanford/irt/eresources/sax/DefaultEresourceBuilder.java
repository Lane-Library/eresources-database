package edu.stanford.irt.eresources.sax;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import edu.stanford.irt.eresources.EresourceException;

public class DefaultEresourceBuilder extends DefaultHandler {

    @FunctionalInterface
    private static interface EndElementHandler {

        void handleEndElement();
    }

    private SAXEresource currentEresource;

    private SAXLink currentLink;

    private StringBuilder currentText = new StringBuilder();

    private SAXVersion currentVersion;

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private Map<String, EndElementHandler> endElementHandlers;

    private EresourceHandler eresourceHandler;

    public DefaultEresourceBuilder(final EresourceHandler eresourceHandler) {
        this.eresourceHandler = eresourceHandler;
        this.endElementHandlers = new HashMap<String, EndElementHandler>();
        this.endElementHandlers.put("eresource", () -> {
            this.eresourceHandler.handleEresource(this.currentEresource);
            this.currentEresource = null;
        });
        this.endElementHandlers.put("version", () -> {
            this.currentEresource.addVersion(this.currentVersion);
            this.currentVersion = null;
        });
        this.endElementHandlers.put("link", () -> {
            this.currentVersion.addLink(this.currentLink);
            this.currentLink.setVersion(this.currentVersion);
            this.currentLink = null;
        });
        this.endElementHandlers.put("url", () -> this.currentLink.setUrl(this.currentText.toString()));
        this.endElementHandlers.put("label", () -> this.currentLink.setLabel(this.currentText.toString()));
        this.endElementHandlers.put("date", () -> this.currentVersion.setDates(this.currentText.toString()));
        this.endElementHandlers.put("summary-holdings",
                () -> this.currentVersion.setSummaryHoldings(this.currentText.toString()));
        this.endElementHandlers.put("subset", () -> {
            String subset = this.currentText.toString();
            if ("proxy".equals(subset)) {
                this.currentVersion.setProxy(true);
            } else if ("noproxy".equals(subset)) {
                this.currentVersion.setProxy(false);
            } else {
                this.currentVersion.addSubset(subset);
            }
        });
        this.endElementHandlers.put("publisher", () -> this.currentVersion.setPublisher(this.currentText.toString()));
        this.endElementHandlers.put("type", () -> this.currentEresource.addType(this.currentText.toString()));
        this.endElementHandlers.put("primaryType", () -> {
            String type = this.currentText.toString();
            this.currentEresource.setPrimaryType(type);
            this.currentEresource.addType(type);
        });
        this.endElementHandlers.put("keywords", () -> this.currentEresource.setKeywords(this.currentText.toString()));
        this.endElementHandlers.put("mesh", () -> this.currentEresource.addMeshTerm(this.currentText.toString()));
        this.endElementHandlers.put("title", () -> this.currentEresource.setTitle(this.currentText.toString()));
        this.endElementHandlers.put("instruction", () -> this.currentLink.setInstruction(this.currentText.toString()));
        this.endElementHandlers.put("description",
                () -> this.currentVersion.setDescription(this.currentText.toString()));
        this.endElementHandlers.put("eresources", () -> {
            // do nothing
            });
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) {
        this.currentText.append(ch, start, length);
    }

    @Override
    public void endElement(final String uri, final String localName, final String name) {
        if (this.endElementHandlers.containsKey(name)) {
            this.endElementHandlers.get(name).handleEndElement();
        } else {
            throw new EresourceException("cant handle " + name);
        }
    }

    @Override
    public void startElement(final String uri, final String localName, final String name, final Attributes atts) {
        this.currentText.setLength(0);
        if ("eresource".equals(name)) {
            this.currentEresource = new SAXEresource();
            this.currentEresource.setRecordId(Integer.parseInt(atts.getValue("id")));
            this.currentEresource.setRecordType(atts.getValue("type"));
            try {
                this.currentEresource.setUpdated(this.dateFormat.parse(atts.getValue("update")));
            } catch (ParseException e) {
                throw new EresourceException(e);
            }
        } else if ("version".equals(name)) {
            this.currentVersion = new SAXVersion();
        } else if ("link".equals(name)) {
            this.currentLink = new SAXLink();
        }
    }
}
