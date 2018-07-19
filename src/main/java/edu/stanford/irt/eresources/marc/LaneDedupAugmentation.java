package edu.stanford.irt.eresources.marc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.EresourceDatabaseException;

public class LaneDedupAugmentation {

    private static final String AUGMENTATION_FILE = "dedup-augmentations.obj";

    private static final Logger log = LoggerFactory.getLogger(LaneDedupAugmentation.class);

    private static final int ONE_DAY = 1000 * 60 * 60 * 24;

    private Map<String, String> augmentations;

    public LaneDedupAugmentation(final AugmentationsService augmentationsService) {
        File objFile = new File(AUGMENTATION_FILE);
        if (!objFile.exists() || objFile.lastModified() < System.currentTimeMillis() - ONE_DAY) {
            this.augmentations = augmentationsService.buildAugmentations();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(AUGMENTATION_FILE))) {
                oos.writeObject(new HashMap<>(this.augmentations));
            } catch (IOException e) {
                throw new EresourceDatabaseException(e);
            }
        } else {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(objFile))) {
                this.augmentations = (HashMap<String, String>) ois.readObject();
            } catch (IOException e) {
                log.error("can't open augmentations file", e);
                this.augmentations = Collections.emptyMap();
            } catch (ClassNotFoundException e) {
                throw new EresourceDatabaseException(e);
            }
        }
    }

    public boolean isDuplicate(final String key, final String value) {
        return this.augmentations.containsKey(key + "->" + value);
    }
}
