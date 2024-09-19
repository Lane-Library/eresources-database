package edu.stanford.irt.eresources.marc.sul;

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
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.SolrEresourceHandler;
import edu.stanford.irt.eresources.marc.KeywordsStrategy;
import edu.stanford.irt.eresources.marc.LaneDedupAugmentation;
import edu.stanford.irt.eresources.marc.RecordCollectionFactory;
import edu.stanford.irt.eresources.pmc.PmcDedupAugmentation;
import edu.stanford.lane.catalog.FolioRecord;
import edu.stanford.lane.catalog.FolioRecordCollection;
import edu.stanford.lane.catalog.Record;

public class SulMARCRecordEresourceProcessorTest {

        private EresourceHandler eresourceHandler;

        private KeywordsStrategy keywordsStrategy;

        private RecordCollectionFactory recordCollectionFactory;

        private LaneDedupAugmentation laneDedupAugmentation;

        private PmcDedupAugmentation pmcDedupAugmentation;

        private SulMARCRecordEresourceProcessor sulRecordEresourceProcessor;

        private List<InclusionStrategy> inclusionStrategies;

        @Before
        public void setup() {
                this.eresourceHandler = mock(SolrEresourceHandler.class);
                this.keywordsStrategy = mock(KeywordsStrategy.class);
                this.recordCollectionFactory = mock(RecordCollectionFactory.class);
                this.laneDedupAugmentation = mock(LaneDedupAugmentation.class);
                this.pmcDedupAugmentation = mock(PmcDedupAugmentation.class);
                InclusionStrategy alwaysIncludeStrategy = new InclusionStrategy() {

                        @Override
                        public boolean isAcceptable(Record marcRecord) {
                                return true;
                        }
                };
                this.inclusionStrategies = Collections.singletonList(alwaysIncludeStrategy);
                this.sulRecordEresourceProcessor = new SulMARCRecordEresourceProcessor(this.eresourceHandler,
                                this.keywordsStrategy, this.recordCollectionFactory, this.laneDedupAugmentation,
                                this.pmcDedupAugmentation, this.inclusionStrategies);
        }

        @Test
        public void testProcess() throws Exception {
                LocalDateTime startDate = LocalDateTime.now();
                Long start = startDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
                this.sulRecordEresourceProcessor.setStartDate(startDate);
                FolioRecordCollection rc = mock(FolioRecordCollection.class);
                expect(this.recordCollectionFactory.newFolioRecordCollection(start)).andReturn(rc);
                expect(rc.hasNext()).andReturn(true);
                FolioRecord fr1 = new FolioRecord(Files.readAllBytes(Paths
                                .get("src/test/resources/edu/stanford/irt/eresources/marc/sul/a10009616-folio.json")));
                FolioRecord fr2 = new FolioRecord(Files.readAllBytes(Paths.get(
                                "src/test/resources/edu/stanford/irt/eresources/marc/sul/in00000172243-folio.json")));
                expect(rc.next()).andReturn(fr1);
                expect(rc.hasNext()).andReturn(true);
                expect(rc.next()).andReturn(fr2);
                expect(rc.hasNext()).andReturn(false);
                this.eresourceHandler.handleEresource(isA(SulMarcEresource.class));
                expectLastCall().times(2);
                expect(this.laneDedupAugmentation.isDuplicate(isA(String.class))).andReturn(false).anyTimes();
                expect(this.pmcDedupAugmentation.isDuplicate(isA(String.class))).andReturn(false).anyTimes();
                replay(rc, this.eresourceHandler, this.keywordsStrategy, this.recordCollectionFactory,
                                this.laneDedupAugmentation, this.pmcDedupAugmentation);
                this.sulRecordEresourceProcessor.process();
                verify(rc, this.eresourceHandler, this.keywordsStrategy, this.recordCollectionFactory,
                                this.laneDedupAugmentation, this.pmcDedupAugmentation);
        }
}
