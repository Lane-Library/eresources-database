/**
 * 
 */
package edu.stanford.irt.eresources;

/**
 * @author ceyates
 */
public interface EresourceHandler extends Runnable {

    int getCount();

    void handleEresource(Eresource eresource);

    void stop();
}
