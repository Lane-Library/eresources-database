package edu.stanford.irt.eresources.pmc;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collections;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.stanford.irt.eresources.EresourceDatabaseException;

public class PmcDedupAugmentationTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    PmcDedupAugmentation pmcDedupAugmentation;

    SolrClient solrClient;

    @Before
    public void setUp() throws Exception {
        this.solrClient = mock(SolrClient.class);
        QueryResponse solrResponse = mock(QueryResponse.class);
        SolrDocumentList solrDocumentList = new SolrDocumentList();
        SolrDocument solrDocument1 = mock(SolrDocument.class);
        solrDocumentList.add(solrDocument1);
        SolrDocument solrDocument2 = mock(SolrDocument.class);
        solrDocumentList.add(solrDocument2);
        expect(this.solrClient.query(isA(SolrQuery.class))).andReturn(solrResponse);
        expect(solrResponse.getResults()).andReturn(solrDocumentList);
        expect(solrDocument1.getFieldValue("recordId")).andReturn("dnlmId-1");
        expect(solrDocument1.getFieldValues("issns")).andReturn(Collections.singletonList("one-issn")).times(2);
        expect(solrDocument2.getFieldValue("recordId")).andReturn("dnlmId-2");
        expect(solrDocument2.getFieldValues("issns")).andReturn(null);
        replay(this.solrClient, solrResponse, solrDocument1, solrDocument2);
        this.pmcDedupAugmentation = new PmcDedupAugmentation(this.solrClient);
        verify(this.solrClient, solrResponse, solrDocument1, solrDocument2);
    }

    @Test
    public final void testIsDuplicateString() {
        assertFalse(this.pmcDedupAugmentation.isDuplicate("not-dup"));
    }

    @Test
    public final void testIsDuplicateStringString() {
        assertTrue(this.pmcDedupAugmentation.isDuplicate("issn", "one-issn"));
    }

    @Test
    public void testSolrException() throws Exception {
        this.thrown.expect(EresourceDatabaseException.class);
        this.thrown.expectMessage("something failed");
        this.solrClient = mock(SolrClient.class);
        expect(this.solrClient.query(isA(SolrQuery.class))).andThrow(new SolrServerException("something failed"));
        replay(this.solrClient);
        this.pmcDedupAugmentation = new PmcDedupAugmentation(this.solrClient);
        verify(this.solrClient);
    }
}
