package edu.stanford.irt.eresources.marc.sfx;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.zip.GZIPInputStream;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamWriter;
import org.marc4j.MarcWriter;
import org.marc4j.MarcXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.stanford.irt.eresources.CatalogRecordService;
import edu.stanford.irt.eresources.EresourceDatabaseException;

public class SfxFileCatalogRecordService extends PipedInputStream implements Runnable, CatalogRecordService {

    private static final Logger log = LoggerFactory.getLogger(SfxFileCatalogRecordService.class);

    private String basePath;

    private Executor executor;

    private PipedOutputStream output;

    private long time;

    public SfxFileCatalogRecordService(final String basePath, final Executor executor) {
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
        List<File> files = edu.stanford.irt.eresources.IOUtils.getUpdatedFiles(new File(this.basePath), ".xml-marc.gz",
                this.time);
        Collections.sort(files);
        while (!files.isEmpty()) {
            File file = files.remove(0);
            try (InputStream stream = new FileInputStream(file); InputStream gzipStream = new GZIPInputStream(stream)) {
                MarcReader reader = new MarcXmlReader(gzipStream);
                MarcWriter marcStreamWriter = new MarcStreamWriter(this.output, StandardCharsets.UTF_8.displayName());
                while (reader.hasNext()) {
                    org.marc4j.marc.Record marcRecord = reader.next();
                    marcStreamWriter.write(marcRecord);
                }
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
