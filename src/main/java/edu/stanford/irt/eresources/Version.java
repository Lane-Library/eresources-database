package edu.stanford.irt.eresources;

import java.util.List;

public interface Version {

    String getAdditionalText();

    String getDates();

    String getHoldingsAndDates();

    List<Link> getLinks();

    String getPublisher();

    String getSummaryHoldings();

    boolean hasGetPasswordLink();

    boolean isProxy();
}
