package edu.stanford.irt.eresources.pubmed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PubmedSpecialTypesManager {

    private Collection<PubmedSearcher> searchers;

    private Map<String, Collection<String[]>> specialPmids;

    public PubmedSpecialTypesManager(final Collection<PubmedSearcher> searchers) {
        this.searchers = new ArrayList<>(searchers);
    }

    public Collection<String[]> getTypes(final String pmid) {
        if (null == this.specialPmids || this.specialPmids.isEmpty()) {
            init();
        }
        if (this.specialPmids.containsKey(pmid)) {
            return this.specialPmids.get(pmid);
        }
        return Collections.emptyList();
    }

    private void init() {
        this.specialPmids = new HashMap<>();
        for (PubmedSearcher searcher : this.searchers) {
            String field = searcher.getField();
            String value = searcher.getValue();
            List<String> pmids = searcher.getPmids();
            for (String pmid : pmids) {
                Collection<String[]> values;
                if (this.specialPmids.containsKey(pmid)) {
                    values = this.specialPmids.get(pmid);
                } else {
                    values = new ArrayList<>();
                }
                String[] fieldAndValue = { field, value };
                values.add(fieldAndValue);
                this.specialPmids.put(pmid, values);
            }
        }
    }
}
