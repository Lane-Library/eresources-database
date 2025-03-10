package edu.stanford.irt.eresources.marc.sfx;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.mock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.SolrEresourceHandler;
import edu.stanford.irt.eresources.marc.KeywordsStrategy;
import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import edu.stanford.irt.eresources.marc.RecordCollectionFactory;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.RecordCollection;

public class SfxRecordEresourceProcessorTest {

        private EresourceHandler eresourceHandler;

        private KeywordsStrategy keywordsStrategy;

        private RecordCollectionFactory recordCollectionFactory;

        private LaneDedupAugmentation laneDedupAugmentation;

        private SfxRecordEresourceProcessor sfxRecordEresourceProcessor;

        @BeforeEach
        public void setup() {
                this.eresourceHandler = mock(SolrEresourceHandler.class);
                this.keywordsStrategy = mock(KeywordsStrategy.class);
                this.recordCollectionFactory = mock(RecordCollectionFactory.class);
                this.laneDedupAugmentation = mock(LaneDedupAugmentation.class);
                this.sfxRecordEresourceProcessor = new SfxRecordEresourceProcessor(this.eresourceHandler,
                                this.keywordsStrategy,
                                this.recordCollectionFactory, this.laneDedupAugmentation);
        }

        @Test
        public void testProcess() throws Exception {
                LocalDateTime startDate = LocalDateTime.now();
                Long start = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                this.sfxRecordEresourceProcessor.setStartDate(startDate);
                RecordCollection rc = mock(RecordCollection.class);
                expect(this.recordCollectionFactory.newRecordCollection(start)).andReturn(rc);
                expect(rc.hasNext()).andReturn(true);
                Record mr1 = new Record(
                                Files.readAllBytes(Paths.get(
                                                "src/test/resources/edu/stanford/irt/eresources/marc/sfx/fake.marc")));
                Record mr2 = new Record(Files
                                .readAllBytes(Paths.get(
                                                "src/test/resources/edu/stanford/irt/eresources/marc/sfx/sfx-export.marc")));
                expect(rc.next()).andReturn(mr1);
                expect(rc.hasNext()).andReturn(true);
                expect(rc.next()).andReturn(mr2);
                expect(rc.hasNext()).andReturn(false);
                this.eresourceHandler.handleEresource(isA(SfxMarcEresource.class));
                expectLastCall();
                expect(this.laneDedupAugmentation.isDuplicate("isbn->0123456789123")).andReturn(false);
                expect(this.laneDedupAugmentation.isDuplicate("isbn->9781558600928")).andReturn(false);
                expect(this.laneDedupAugmentation.isDuplicate("isbn->1558600922")).andReturn(false);
                replay(rc, this.eresourceHandler, this.keywordsStrategy, this.recordCollectionFactory,
                                this.laneDedupAugmentation);
                this.sfxRecordEresourceProcessor.process();
                verify(rc, this.eresourceHandler, this.keywordsStrategy, this.recordCollectionFactory,
                                this.laneDedupAugmentation);
        }
}
