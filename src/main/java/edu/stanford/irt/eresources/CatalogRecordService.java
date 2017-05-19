package edu.stanford.irt.eresources;

import java.io.InputStream;

public interface CatalogRecordService {
    InputStream getRecordStream(long time);
}
