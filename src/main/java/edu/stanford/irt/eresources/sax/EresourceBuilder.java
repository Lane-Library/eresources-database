package edu.stanford.irt.eresources.sax;

import org.xml.sax.ContentHandler;

import edu.stanford.irt.eresources.EresourceHandler;

public interface EresourceBuilder extends ContentHandler {

    void setEresourceHandler(EresourceHandler handler);
}
