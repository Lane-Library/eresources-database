/**
 * 
 */
package edu.stanford.irt.eresources;

import org.xml.sax.ContentHandler;

/**
 * @author ceyates
 */
public interface EresourceBuilder extends ContentHandler {

    void setEresourceHandler(EresourceHandler handler);
}
