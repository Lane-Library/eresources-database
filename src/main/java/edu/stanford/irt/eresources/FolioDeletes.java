package edu.stanford.irt.eresources;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * FOLIO record deletes for Lane and SUL
 */
public class FolioDeletes extends SolrLoader {

    private static final String LANE = "recordType:bib";

    private static final String LANE_OR_SUL = "(recordType:sul OR recordType:bib)";

    private static final Logger log = LoggerFactory.getLogger(FolioDeletes.class);

    private static final long ONE_HOUR = (60 * 60 * 1000);

    private static final String SUL = "recordType:sul";

    private HTTPCatalogRecordDeleteService catalogRecordDeleteService;

    private String interval;

    public FolioDeletes(final String interval, final HTTPCatalogRecordDeleteService catalogRecordDeleteService) {
        this.interval = interval;
        this.catalogRecordDeleteService = catalogRecordDeleteService;
    }

    @Override
    public void load() {
        processDeletes(getDeletes());
    }

    private Collection<String> getDeletes() {
        long time = -1;
        if ("hourly".equals(this.interval)) {
            time = LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - ONE_HOUR;
        }
        return this.catalogRecordDeleteService.getDeletes(time);
    }

    private void processDeletes(final Collection<String> deletes) {
        if (!deletes.isEmpty()) {
            log.info("found {} records to delete", deletes.size());
            try {
                for (String id : deletes) {
                    String recordId = id.replaceFirst("[^\\d]+", "");
                    String recordType = LANE_OR_SUL;
                    if (id.startsWith("a")) {
                        recordType = SUL;
                    } else if (id.startsWith("L")) {
                        recordType = LANE;
                    }
                    if (!recordId.isBlank()) {
                        this.solrClient.deleteByQuery(recordType + " AND recordId:" + recordId);
                    }
                }
                this.solrClient.commit();
                log.info("attempted to delete {} records (which may or may not have been indexed)", deletes.size());
            } catch (SolrServerException | IOException e) {
                throw new EresourceDatabaseException(e);
            }
        }
    }
}
