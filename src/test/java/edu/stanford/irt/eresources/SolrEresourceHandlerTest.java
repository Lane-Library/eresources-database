package edu.stanford.irt.eresources;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.solr.client.solrj.SolrClient;
import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.marc.AbstractMarcEresource;

public class SolrEresourceHandlerTest {

    AbstractMarcEresource eresource;

    SolrEresourceHandler handler;

    BlockingQueue<Eresource> queue;

    SolrClient solrClient;

    @Before
    public void setUp() throws Exception {
        this.queue = mock(BlockingQueue.class);
        this.solrClient = mock(SolrClient.class);
        this.handler = new SolrEresourceHandler(this.queue, this.solrClient, 1);
        this.eresource = mock(AbstractMarcEresource.class);
    }

    @Test
    public final void testGetCount() {
        assertEquals(0, this.handler.getCount());
    }

    @Test
    public final void testHandleEresource() throws Exception {
        this.queue.put(this.eresource);
        replay(this.eresource, this.queue);
        this.handler.handleEresource(this.eresource);
        assertEquals(1, this.handler.getCount());
        verify(this.eresource, this.queue);
    }

    @Test
    public final void testRun() throws Exception {
        Version v = mock(Version.class);
        Link l = mock(Link.class);
        expect(this.queue.isEmpty()).andReturn(false);
        expect(this.queue.poll(1, TimeUnit.SECONDS)).andReturn(this.eresource);
        expect(this.eresource.getSortTitle()).andReturn(null);
        expect(this.eresource.getShortTitle()).andReturn("short title");
        expect(this.eresource.getTitle()).andReturn("title").anyTimes();
        expect(this.eresource.getItemCount()).andReturn(new int[2]);
        expect(this.eresource.getId()).andReturn("id");
        expect(this.eresource.getRecordId()).andReturn("123");
        expect(this.eresource.getRecordType()).andReturn("recordType");
        expect(this.eresource.getDescription()).andReturn("description").anyTimes();
        expect(this.eresource.getKeywords()).andReturn("keywords");
        expect(this.eresource.getPublicationText()).andReturn("PublicationText").anyTimes();
        expect(this.eresource.getPublicationTitle()).andReturn("PublicationTitle").anyTimes();
        expect(this.eresource.getTypes()).andReturn(Collections.singletonList("type")).anyTimes();
        expect(this.eresource.getAbbreviatedTitles()).andReturn(Collections.singletonList("AbbreviatedTitle"));
        expect(this.eresource.getAlternativeTitles()).andReturn(Collections.singletonList("AlternativeTitle"));
        expect(this.eresource.getPrimaryType()).andReturn("primaryType");
        expect(this.eresource.getYear()).andReturn(1990).anyTimes();
        expect(this.eresource.getDate()).andReturn("date");
        expect(this.eresource.isEnglish()).andReturn(true);
        expect(this.eresource.getPublicationAuthorsText()).andReturn("PublicationAuthorsText");
        expect(this.eresource.getPublicationAuthors()).andReturn(Collections.singletonList("PublicationAuthor"))
                .anyTimes();
        expect(this.eresource.getPublicationTypes()).andReturn(Collections.singletonList("PublicationType"));
        expect(this.eresource.getPublicationLanguages()).andReturn(Collections.singletonList("PublicationLang"));
        expect(this.eresource.getPublicationDate()).andReturn("PublicationDate");
        expect(this.eresource.getPublicationVolume()).andReturn("PublicationVolume");
        expect(this.eresource.getPublicationIssue()).andReturn("PublicationIssue");
        expect(this.eresource.getPublicationPages()).andReturn("PublicationPages");
        expect(this.eresource.getVersions()).andReturn(Collections.singletonList(v)).times(2);
        expect(this.eresource.getMeshTerms()).andReturn(Collections.singletonList("mesh")).anyTimes();
        expect(this.eresource.getBroadMeshTerms()).andReturn(Collections.singletonList("broad mesh"));
        int[] itemCount = { 10, 5 };
        expect(v.getItemCount()).andReturn(itemCount);
        expect(v.getCallnumber()).andReturn("cn");
        expect(v.getLocationCode()).andReturn("loc code");
        expect(v.getLocationName()).andReturn("loc name");
        expect(v.getLocationUrl()).andReturn("loc url");
        expect(v.getLinks()).andReturn(Collections.singletonList(l)).times(2);
        expect(v.isProxy()).andReturn(true).times(2);
        expect(v.getHoldingsAndDates()).andReturn("");
        expect(v.getPublisher()).andReturn("");
        expect(v.getAdditionalText()).andReturn("");
        expect(v.getDates()).andReturn("");
        expect(v.getSummaryHoldings()).andReturn("");
        expect(l.getAdditionalText()).andReturn("");
        expect(l.getLabel()).andReturn("linkLabel");
        expect(l.getLinkText()).andReturn("linkText");
        expect(l.isRelatedResourceLink()).andReturn(false);
        expect(l.isResourceLink()).andReturn(false);
        expect(l.getUrl()).andReturn("linkUrl").times(3);
        expect(this.eresource.getIsbns()).andReturn(Collections.singletonList("isbn"));
        expect(this.eresource.getIssns()).andReturn(Collections.singletonList("issn"));
        expect(this.queue.isEmpty()).andReturn(true);
        expect(this.solrClient.add(isA(Collection.class))).andReturn(null);
        replay(this.eresource, this.queue, v, l, this.solrClient);
        // clearly poor design ... have to stop before run so that keepGoing doesn't run forever
        this.handler.stop();
        this.handler.run();
        verify(this.eresource, this.queue, v, l, this.solrClient);
    }
}
