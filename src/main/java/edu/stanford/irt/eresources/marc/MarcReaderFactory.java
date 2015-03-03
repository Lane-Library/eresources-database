package edu.stanford.irt.eresources.marc;

import java.io.InputStream;

import org.marc4j.MarcReader;
import org.marc4j.MarcStreamReader;

public class MarcReaderFactory {

    public MarcReader newMarcReader(final InputStream inputStream) {
        return new MarcStreamReader(inputStream);
    }
}
