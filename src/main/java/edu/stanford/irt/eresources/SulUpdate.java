package edu.stanford.irt.eresources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SulUpdate extends SolrLoader {

    private static final Pattern DIGIT = Pattern.compile("\\\\d+");

    private static final Logger log = LoggerFactory.getLogger(SulUpdate.class);

    private String basePath;

    public SulUpdate(final String basePath) {
        this.basePath = basePath;
    }

    @Override
    public void load() {
        this.setUpdatedDateQuery("recordType:sul");
        super.load();
        processDeletes(getDeletes());
    }

    private List<String> getDeletes() {
        long time = this.getUpdatedDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        List<File> files = IOUtils.getUpdatedFiles(new File(this.basePath), ".del", time);
        List<String> dels = new ArrayList<>();
        while (!files.isEmpty()) {
            File file = files.remove(0);
            try (Stream<String> stream = Files.lines(Paths.get(file.getAbsolutePath()))) {
                dels = stream.filter(line -> DIGIT.matcher(line).matches()).collect(Collectors.toList());
            } catch (IOException e) {
                log.error("problem with file {}", file);
                throw new EresourceDatabaseException(e);
            }
        }
        return dels;
    }

    private void processDeletes(final List<String> deletes) {
        if (!deletes.isEmpty()) {
            try {
                for (String id : deletes) {
                    this.solrClient.deleteByQuery("id:sul-" + id);
                }
                this.solrClient.commit();
            } catch (SolrServerException | IOException e) {
                throw new EresourceDatabaseException(e);
            }
            log.info("handled {} deletes: {}", deletes.size(), deletes);
        }
    }
}
