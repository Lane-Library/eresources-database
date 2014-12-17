package edu.stanford.irt.eresources;

public interface EresourceHandler extends Runnable {

    int getCount();

    void handleEresource(Eresource eresource);

    void stop();
}
