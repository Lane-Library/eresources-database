package edu.stanford.irt.eresources.redivis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;

import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class RedivisDatasetTest {

    @Test
    public void testMap() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DatasetList datasets = mapper.readValue(RedivisDatasetTest.class.getResourceAsStream("datasets.json"),
                DatasetList.class);
        assertFalse(datasets.getResults().isEmpty());
        assertNull(datasets.getNextPageToken());
        Result dataset = datasets.getResults().get(0);
        assertEquals("overview", dataset.getAccessLevel());
        assertEquals(
                "The Born in Bradford study is tracking the health and wellbeing of over 13,500 children, and their parents born at Bradford Royal Infirmary between March 2007 and December 2010. ",
                dataset.getDescription());
        assertEquals("1", dataset.getReferenceId());
        assertEquals("Born in Bradford", dataset.getName());
        assertTrue(dataset.getUpdatedAt().before(new Date()));
        assertEquals("https://redivis.com/stanfordphs/datasets/2", dataset.getUrl());
    }
}
