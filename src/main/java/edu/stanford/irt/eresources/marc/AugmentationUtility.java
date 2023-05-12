package edu.stanford.irt.eresources.marc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.serialization.ValidatingObjectInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.EresourceDatabaseException;

public final class AugmentationUtility {

    public static final int ONE_DAY = 1000 * 60 * 60 * 24;

    private static final Logger log = LoggerFactory.getLogger(AugmentationUtility.class);

    public static Map<String, String> fetchAugmentations(final String augmentationsFileName,
            final AugmentationsService augmentationsService, final int timePeriod) {
        log.info("starting {} fetch",
                StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass().getSimpleName());
        Map<String, String> augmentations = Collections.emptyMap();
        long now = System.currentTimeMillis();
        File objFile = new File(augmentationsFileName);
        if (!objFile.exists() || objFile.lastModified() < now - timePeriod) {
            augmentations = augmentationsService.buildAugmentations();
            log.info("found {} augmentations ", augmentations.size());
            if (objFile.exists() && objFile.setLastModified(now)) {
                log.info("won't check augmentation source again until {}", new Date(now + timePeriod));
            }
        }
        if (!augmentations.isEmpty()) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(augmentationsFileName))) {
                oos.writeObject(new HashMap<>(augmentations));
            } catch (IOException e) {
                throw new EresourceDatabaseException(e);
            }
            log.info("fetched and wrote {} augmentations to file {} ", augmentationsService.getClass(),
                    augmentationsFileName);
        } else if (objFile.exists()) {
            log.info("pulling augmentations from file {} ", augmentationsFileName);
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

    private AugmentationUtility() {
        // empty private constructor
    }
}
