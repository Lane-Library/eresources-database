package edu.stanford.irt.eresources.redivis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class RedivisDataset {

    @Test
    public void testMap() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DatasetList datasets = mapper.readValue(RedivisDataset.class.getResourceAsStream("datasets.json"),
                DatasetList.class);
        assertFalse(datasets.getDatasets().isEmpty());
        Dataset dataset = mapper.readValue(RedivisDataset.class.getResourceAsStream("dataset.json"), Dataset.class);
        assertEquals("overview", dataset.getAccessLevel());
        assertEquals("Truven", dataset.getCollections().get(0).getName());
        assertEquals(
                "This file contains individual inpatient claim records that were used to create admissions records.",
                dataset.getDescription());
        assertEquals("overview", dataset.getDocumentations().get(0).getRequiredAccessLevel());
        assertEquals("service", dataset.getEntity().getName());
        assertEquals("45", dataset.getId());
        assertEquals("MarketScan Inpatient Services", dataset.getName());
        assertEquals("Stanford Center for Population Health Sciences", dataset.getOrganization().getName());
        assertEquals("StanfordPHS", dataset.getOrganization().getShortName());
        assertEquals("usa", dataset.getTags().get(0).getName());
        assertEquals("2007-2015", dataset.getTemporalRange().getDisplayRange());
        assertTrue(dataset.getUpdatedAt().before(new Date()));
        assertEquals("STDPLAC", dataset.getVariables().get(0).getName());
    }
}
