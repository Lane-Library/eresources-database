package edu.stanford.irt.eresources;

import static org.easymock.EasyMock.anyInt;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class FolioDeletesTest {

    FolioDeletes deletes;

    HTTPCatalogRecordDeleteService deleteService;

    SolrClient solrClient;

    @Before
    public void setUp() throws Exception {
        this.deleteService = mock(HTTPCatalogRecordDeleteService.class);
        this.deletes = new FolioDeletes("", this.deleteService);
        this.solrClient = mock(SolrClient.class);
        this.deletes.setSolrClient(this.solrClient);
    }

    @Test
    public final void testLoad() throws Exception {
        List<String> records = new ArrayList<>();
        records.add("deletable-record-id-123");
        records.add("a-record-that-should-not-be-deleted-bc-not-num");
        expect(this.deleteService.getDeletes(-1)).andReturn(records);
        expect(this.solrClient.deleteByQuery("(recordType:sul OR recordType:bib) AND recordId:123"))
                .andReturn(new UpdateResponse());
        expect(this.solrClient.commit()).andReturn(new UpdateResponse());
        replay(this.deleteService, this.solrClient);
        this.deletes.load();
        verify(this.deleteService, this.solrClient);
    }

    @Test
    public final void testLoadDaily() throws Exception {
        this.deletes = new FolioDeletes("daily", this.deleteService);
        expect(this.deleteService.getDeletes(anyInt())).andReturn(Collections.emptyList());
        replay(this.deleteService);
        this.deletes.load();
        verify(this.deleteService);
    }

    @Test
    public final void testLoadException() throws Exception {
        List<String> records = new ArrayList<>();
        records.add("deletable-record-id-123");
        records.add("a-record-that-should-not-be-deleted-bc-not-num");
        expect(this.deleteService.getDeletes(-1)).andReturn(Collections.singletonList("L-1234"));
        expect(this.solrClient.deleteByQuery("recordType:bib AND recordId:1234"))
                .andThrow(new SolrServerException("oops"));
        replay(this.deleteService, this.solrClient);
        Assert.assertThrows(EresourceDatabaseException.class, () -> this.deletes.load());
        verify(this.deleteService, this.solrClient);
    }

    @Test
    public final void testLoadHourly() throws Exception {
        this.deletes = new FolioDeletes("hourly", this.deleteService);
        expect(this.deleteService.getDeletes(anyInt())).andReturn(Collections.emptyList());
        replay(this.deleteService);
        this.deletes.load();
        verify(this.deleteService);
    }
}
