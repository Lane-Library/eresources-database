package edu.stanford.irt.eresources;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;

public class SulSolrCatalogRecordServiceTest {

    SolrClient client;

    Executor executor;

    SulSolrCatalogRecordService recordService;

    @Before
    public void setUp() throws Exception {
        this.client = mock(SolrClient.class);
        this.executor = mock(Executor.class);
        this.recordService = new SulSolrCatalogRecordService(this.client, this.executor);
    }

    @Test
    public final void testGetRecordFormats() throws Exception {
        List<Object> formats = new ArrayList<>();
        formats.add("foo");
        formats.add("bar");
        QueryResponse response = mock(QueryResponse.class);
        SolrDocument doc = mock(SolrDocument.class);
        expect(this.client.query(isA(SolrQuery.class))).andReturn(response);
        SolrDocumentList list = mock(SolrDocumentList.class);
        expect(response.getResults()).andReturn(list);
        expect(list.get(0)).andReturn(doc);
        expect(doc.getFieldValues(isA(String.class))).andReturn(formats);
        replay(this.client, response, list, doc);
        this.recordService.getRecordFormats("123");
        verify(this.client, response, list, doc);
        assertTrue(this.recordService.getRecordFormats("123").contains("foo"));
    }

    @Test(expected = EresourceDatabaseException.class)
    public final void testGetRecordFormatsSolrException() throws Exception {
        expect(this.client.query(isA(SolrQuery.class))).andThrow(new SolrServerException("oops"));
        replay(this.client);
        this.recordService.getRecordFormats("123");
        verify(this.client);
    }

    @Test
    public final void testGetRecordStream() throws Exception {
        assertEquals(PipedInputStream.class, this.recordService.getRecordStream(0).getClass().getSuperclass());
    }

    @Test
    public final void testRun() throws Exception {
        QueryResponse response = mock(QueryResponse.class);
        SolrDocument doc = mock(SolrDocument.class);
        SolrDocumentList list = new SolrDocumentList();
        PipedOutputStream output = mock(PipedOutputStream.class);
        this.recordService.setPipedOutputStream(output);
        list.add(doc);
        expect(this.client.query(isA(SolrQuery.class))).andReturn(response).times(2);
        expect(response.getNextCursorMark()).andReturn("*");
        expect(response.getResults()).andReturn(list).times(2);
        expect(doc.getFieldValue(isA(String.class))).andReturn("id");
        expect(doc.getFieldValues(isA(String.class))).andReturn(Collections.emptyList());
        expect(doc.getFieldValue(isA(String.class))).andReturn("marc xml");
        expect(doc.getFieldValue(isA(String.class))).andReturn("bib marc xml");
        output.close();
        replay(this.client, response, doc, output);
        this.recordService.run();
        verify(this.client, response, doc, output);
    }
}
