package edu.stanford.irt.eresources.redivis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

final class RedivisDatasetTest {

    @Test
    void testMap() throws Exception {
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
        assertEquals("3tgx", dataset.getReferenceId());
        assertEquals("Born in Bradford", dataset.getName());
        assertTrue(dataset.getUpdatedAt().before(new Date()));
        assertEquals("https://redivis.com/datasets/3tgx-1cx7awrs3?v=0.1", dataset.getUrl());
    }
}
