package edu.stanford.irt.eresources;

import java.util.Collection;
import java.util.List;

public interface Version {

    String getDates();

    String getAdditionalText();

    String getHoldingsAndDates();

    List<Link> getLinks();

    String getPublisher();

    Collection<String> getSubsets();

    String getSummaryHoldings();

    boolean hasGetPasswordLink();

    boolean isProxy();
}
