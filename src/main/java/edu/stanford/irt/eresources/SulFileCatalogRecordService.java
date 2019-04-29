package edu.stanford.irt.eresources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SulFileCatalogRecordService extends PipedInputStream implements Runnable, CatalogRecordService {

    private static final Logger log = LoggerFactory.getLogger(SulFileCatalogRecordService.class);

    private String basePath;

    private Executor executor;

    private PipedOutputStream output;

    private long time;

    public SulFileCatalogRecordService(final String basePath, final Executor executor) {
        this.basePath = basePath;
        this.executor = executor;
    }

    @Override
    public InputStream getRecordStream(final long time) {
        this.time = time;
        if (null == this.basePath) {
            throw new IllegalStateException("null basePath");
        }
        return this;
    }

    @Override
    public synchronized int read() throws IOException {
        if (null == this.output) {
            this.output = new PipedOutputStream(this);
            this.executor.execute(this);
        }
        return super.read();
    }

    @Override
    public void run() {
        List<File> files = edu.stanford.irt.eresources.IOUtils.getUpdatedFiles(new File(this.basePath), ".marc", this.time);
        Collections.sort(files);
        while (!files.isEmpty()) {
            File file = files.remove(0);
            try (InputStream stream = new FileInputStream(file)) {
                IOUtils.copy(stream, this.output);
            } catch (IOException e) {
                log.error("problem with file {}", file);
                throw new EresourceDatabaseException(e);
            }
            log.info("processed: {}", file);
        }
        try {
            this.output.close();
        } catch (IOException e) {
            log.error("can't close output stream", e);
        }
    }

    // for unit testing
    public void setPipedOutputStream(final PipedOutputStream outputStream) {
        this.output = outputStream;
    }
}
