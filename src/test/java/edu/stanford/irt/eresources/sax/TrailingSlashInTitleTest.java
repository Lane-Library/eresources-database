package edu.stanford.irt.eresources.sax;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceHandler;
import edu.stanford.irt.eresources.ItemCount;
import edu.stanford.irt.eresources.ItemService;
import edu.stanford.lane.catalog.impl.xml.UTF8ComposingMarcReader;

public class TrailingSlashInTitleTest implements EresourceHandler {

    private Eresource eresource;

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public void handleEresource(final Eresource eresource) {
        this.eresource = eresource;
    }

    @Override
    public void run() {
    }

    @Before
    public void setUp() {
    }

    @Override
    public void stop() {
    }

    @Test
    public void testTitleNoSlash() throws IOException, SAXException {
        XMLReader reader = new UTF8ComposingMarcReader();
        MARCEresourceBuilder builder = new MARCEresourceBuilder();
        AugmentationsService augmentationsService = new AugmentationsService() {

            @Override
            public Map<String, String> buildAugmentations() {
                return Collections.emptyMap();
            }
        };
        builder.setAuthTextAugmentation(new AuthTextAugmentation(augmentationsService));
        builder.setReservesTextAugmentation(new ReservesTextAugmentation(augmentationsService));
        builder.setItemCount(new ItemCount(new ItemService() {

            @Override
            public Map<Integer, Integer> getAvailables() {
                return Collections.emptyMap();
            }

            @Override
            public Map<Integer, Integer> getTotals() {
                return Collections.emptyMap();
            }
        }));
        reader.setContentHandler(builder);
        builder.setEresourceHandler(this);
        reader.parse(new InputSource(getClass().getResourceAsStream("3317.mrc")));
        assertEquals("Detection and measurement of free Ca 2+ in cells", this.eresource.getTitle());
    }
}
