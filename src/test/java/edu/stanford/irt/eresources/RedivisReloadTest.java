package edu.stanford.irt.eresources;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executor;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;

public class RedivisReloadTest {

    Executor executor;

    EresourceHandler handler;

    RedivisReload loader;

    AbstractEresourceProcessor processor;

    List<AbstractEresourceProcessor> processors = new ArrayList<>();

    Queue<Eresource> queue;

    SolrClient solrClient;

    @Before
    public void setUp() throws Exception {
        this.solrClient = mock(SolrClient.class);
        this.executor = mock(Executor.class);
        this.handler = mock(EresourceHandler.class);
        this.queue = mock(Queue.class);
        this.processor = mock(AbstractEresourceProcessor.class);
        this.processors.add(this.processor);
        this.loader = new RedivisReload();
        this.loader.setSolrClient(this.solrClient);
        this.loader.setProcessors(this.processors);
        this.loader.setExecutor(this.executor);
        this.loader.setHandler(this.handler);
        this.loader.setQueue(this.queue);
        this.loader.setVersion("version");
    }

    @Test
    public final void testLoad() throws Exception {
        QueryResponse rsp = mock(QueryResponse.class);
        SolrDocumentList docs = mock(SolrDocumentList.class);
        expect(this.solrClient.query(isA(SolrQuery.class))).andReturn(rsp);
        expect(rsp.getResults()).andReturn(docs);
        expect(docs.isEmpty()).andReturn(true);
        this.executor.execute(this.handler);
        this.processor.setStartDate(isA(LocalDateTime.class));
        this.processor.process();
        this.handler.stop();
        expect(this.queue.isEmpty()).andReturn(true);
        expect(this.handler.getCount()).andReturn(1000).times(2);
        expect(this.solrClient.deleteByQuery(isA(String.class))).andReturn(null);
        expect(this.solrClient.commit()).andReturn(null);
        replay(this.solrClient, rsp, docs, this.processor, this.executor, this.handler, this.queue);
        this.loader.load();
        verify(this.solrClient, rsp, docs, this.processor, this.executor, this.handler, this.queue);
    }

    @Test
    public final void testLoadInsufficientRecords() throws Exception {
        QueryResponse rsp = mock(QueryResponse.class);
        SolrDocumentList docs = mock(SolrDocumentList.class);
        SolrDocument doc = mock(SolrDocument.class);
        expect(this.solrClient.query(isA(SolrQuery.class))).andReturn(rsp);
        expect(rsp.getResults()).andReturn(docs);
        expect(docs.isEmpty()).andReturn(false);
        expect(docs.get(0)).andReturn(doc);
        expect(doc.getFieldValue("updated")).andReturn(new Date());
        this.executor.execute(this.handler);
        this.handler.stop();
        expect(this.queue.isEmpty()).andReturn(true);
        expect(this.handler.getCount()).andReturn(10).times(2);
        replay(this.solrClient, rsp, docs, doc, this.executor, this.handler, this.queue);
        this.loader.load();
        verify(this.solrClient, rsp, docs, doc, this.executor, this.handler, this.queue);
    }

    @Test(expected = IllegalArgumentException.class)
    public final void testSolrLoaderSetProcessors() throws Exception {
        RedivisReload loader = new RedivisReload();
        loader.setProcessors(null);
    }
}
