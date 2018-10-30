package edu.stanford.irt.eresources.redivis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
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
        assertNull(datasets.getNextPageToken());
        Dataset dataset = mapper.readValue(RedivisDataset.class.getResourceAsStream("dataset.json"), Dataset.class);
        assertEquals("overview", dataset.getAccessLevel());
        assertEquals("9", dataset.getCollections().get(0).getId());
        assertEquals("Truven", dataset.getCollections().get(0).getName());
        assertEquals("https://redivis.com/StanfordPHS?collection=9", dataset.getCollections().get(0).getUrl());
        assertEquals(
                "This file contains individual inpatient claim records that were used to create admissions records.",
                dataset.getDescription());
        assertEquals("overview", dataset.getDocumentations().get(0).getRequiredAccessLevel());
        assertTrue(dataset.getDocumentations().get(0).getText().contains("This file contains individual"));
        assertEquals("service", dataset.getEntity().getName());
        assertEquals("45", dataset.getId());
        assertEquals("MarketScan Inpatient Services", dataset.getName());
        assertEquals("Stanford Center for Population Health Sciences", dataset.getOrganization().getName());
        assertEquals("StanfordPHS", dataset.getOrganization().getShortName());
        assertEquals("1", dataset.getOrganization().getId());
        assertEquals("usa", dataset.getTags().get(0).getName());
        assertEquals("2007-2015", dataset.getTemporalRange().getDisplayRange());
        assertEquals("2007-01-01", dataset.getTemporalRange().getMin());
        assertEquals("2015-01-01", dataset.getTemporalRange().getMax());
        assertEquals("date", dataset.getTemporalRange().getPrecision());
        assertTrue(dataset.getUpdatedAt().before(new Date()));
        assertEquals("https://redivis.com/StanfordPHS/datasets/45", dataset.getUrl());
        assertEquals("STDPLAC", dataset.getVariables().get(0).getName());
        assertTrue(dataset.getVariables().get(52).getValueLabels().containsValue("Female"));
        assertEquals("https://redivis.com/StanfordPHS/datasets/45/data?variable=301135",
                dataset.getVariables().get(27).getUrl());
        assertEquals("Unique enrollee identifier", dataset.getVariables().get(27).getLabel());
        assertEquals("Unique enrollee identifier", dataset.getVariables().get(27).getDescription());
    }
}
