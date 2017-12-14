package edu.stanford.irt.eresources.pubmed;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class EutilsIsReachable {

    public static boolean eutilsIsReachable() {
        boolean reachable = false;
        try {
            if (InetAddress.getByName("eutils.ncbi.nlm.nih.gov") != null) {
                reachable = true;
            }
        } catch (UnknownHostException e) {
            //
        }
        return reachable;
    }
}
