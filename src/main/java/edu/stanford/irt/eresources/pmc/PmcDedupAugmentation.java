package edu.stanford.irt.eresources.pmc;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.EresourceDatabaseException;

public class PmcDedupAugmentation {

    public static final String KEY_DNLM_CONTROL_NUMBER = "dnlm";

    public static final String KEY_ISSN = "issn";

    public static final String SEPARATOR = "->";

    private static final Logger log = LoggerFactory.getLogger(PmcDedupAugmentation.class);

    private Set<String> augmentations;

    public PmcDedupAugmentation(final SolrClient solrClient) {
        log.info("starting augmentation fetch");
        this.augmentations = new HashSet<>();
        SolrQuery query = new SolrQuery();
        query.setQuery("id:dnlm*");
        query.setRows(Integer.MAX_VALUE);
        QueryResponse rsp = null;
        try {
            rsp = solrClient.query(query);
        } catch (SolrServerException | IOException e) {
            throw new EresourceDatabaseException(e);
        }
        SolrDocumentList rdocs = rsp.getResults();
        rdocs.stream().forEach((final SolrDocument doc) -> {
            String dnlm = (String) doc.getFieldValue("recordId");
            this.augmentations.add(KEY_DNLM_CONTROL_NUMBER + SEPARATOR + dnlm);
            Collection<Object> issns = (null != doc.getFieldValues("issns")) ? doc.getFieldValues("issns")
                    : Collections.emptyList();
            for (Object issn : issns) {
                this.augmentations.add(KEY_ISSN + SEPARATOR + issn);
            }
        });
        log.info("found {} augmentations ", this.augmentations.size());
    }

    public boolean isDuplicate(final String keySeparatorValue) {
        return this.augmentations.contains(keySeparatorValue);
    }

    public boolean isDuplicate(final String key, final String value) {
        return this.augmentations.contains(key + SEPARATOR + value);
    }
}
