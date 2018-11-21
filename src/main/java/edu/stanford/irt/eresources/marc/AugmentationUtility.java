package edu.stanford.irt.eresources.marc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.serialization.ValidatingObjectInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.EresourceDatabaseException;

public final class AugmentationUtility {

    public static final int ONE_DAY = 1000 * 60 * 60 * 24;

    private static final Logger log = LoggerFactory.getLogger(AugmentationUtility.class);

    private AugmentationUtility() {
        // empty private constructor
    }

    public static Map<String, String> fetchAugmentations(final String augmentationsFileName,
            final AugmentationsService augmentationsService, final int timePeriod) {
        Map<String, String> augmentations;
        File objFile = new File(augmentationsFileName);
        if (!objFile.exists() || objFile.lastModified() < System.currentTimeMillis() - timePeriod) {
            augmentations = augmentationsService.buildAugmentations();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(augmentationsFileName))) {
                oos.writeObject(new HashMap<>(augmentations));
            } catch (IOException e) {
                throw new EresourceDatabaseException(e);
            }
        } else {
            try (ValidatingObjectInputStream ois = new ValidatingObjectInputStream(new FileInputStream(objFile))) {
                ois.accept(HashMap.class);
                augmentations = (HashMap<String, String>) ois.readObject();
            } catch (IOException e) {
                log.error("can't open augmentations file", e);
                augmentations = Collections.emptyMap();
            } catch (ClassNotFoundException e) {
                throw new EresourceDatabaseException(e);
            }
        }
        return augmentations;
    }
}
