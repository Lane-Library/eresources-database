/**
 * 
 */
package edu.stanford.irt.eresources;

/**
 * @author ceyates
 */
public interface EresourceHandler extends Runnable {

    void handleEresource(DatabaseEresource eresource);

    void stop();
    
    int getCount();
}
