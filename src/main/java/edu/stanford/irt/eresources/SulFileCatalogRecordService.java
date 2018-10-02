package edu.stanford.irt.eresources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SulFileCatalogRecordService extends PipedInputStream implements Runnable, CatalogRecordService {

    private static final Logger log = LoggerFactory.getLogger(SulFileCatalogRecordService.class);

    private String basePath;

    private Executor executor;

    private PipedOutputStream output;

    public SulFileCatalogRecordService(final String basePath, final Executor executor) {
        this.basePath = basePath;
        this.executor = executor;
    }

    @Override
    public InputStream getRecordStream(final long time) {
        if (null == this.basePath) {
            throw new IllegalStateException("null basePath");
        }
        return this;
    }

    @Override
    public int read() throws IOException {
        if (null == this.output) {
            this.output = new PipedOutputStream(this);
            this.executor.execute(this);
        }
        return super.read();
    }

    @Override
    public void run() {
        List<File> files = getFiles(new File(this.basePath));
        Collections.sort(files);
        while (!files.isEmpty()) {
            File file = files.remove(0);
            try (InputStream stream = new FileInputStream(file)) {
                if (file.getName().endsWith(".zip")) {
                    IOUtils.copy(new ZipInputStream(stream), this.output);
                } else {
                    IOUtils.copy(stream, this.output);
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

    private List<File> getFiles(final File directory) {
        File[] files = directory.listFiles((final File file) -> file.isDirectory() || file.getName().endsWith(".marc")
                || file.getName().endsWith(".zip"));
        List<File> result = new LinkedList<>();
        if (null != files) {
            for (File file : files) {
                if (file.isDirectory()) {
                    result.addAll(getFiles(file));
                } else {
                    result.add(file);
                }
            }
        }
        return result;
    }
}
