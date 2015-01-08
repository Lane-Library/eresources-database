package edu.stanford.irt.eresources.marc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.marc4j.MarcReader;
import org.marc4j.marc.DataField;
import org.marc4j.marc.Record;
import org.marc4j.marc.Subfield;
import org.slf4j.LoggerFactory;

import com.ibm.icu.text.Normalizer;

import edu.stanford.irt.eresources.EresourceException;

public class AuthTextAugmentation {

    private Map<String, String> augmentations = new HashMap<String, String>();

    private StringBuilder augmentationText = new StringBuilder();

    private MarcReader marcReader;

    @SuppressWarnings("unchecked")
    public AuthTextAugmentation(final MarcReader marcReader) {
        this.marcReader = marcReader;
        // create a new augmentation map each Sunday:
        if (Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            this.augmentations = new HashMap<String, String>();
            // otherwise use the existing one:
        } else {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("augmentations.obj"))) {
                this.augmentations = (Map<String, String>) ois.readObject();
            } catch (IOException e) {
                LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
                this.augmentations = new HashMap<String, String>();
            } catch (ClassNotFoundException e) {
                throw new EresourceException(e);
            }
        }
    }

    public String getAuthAugmentations(final String term, final String lookupTag) {
        String result = this.augmentations.get(term);
        if (null == result) {
            this.augmentationText.setLength(0);
            while (this.marcReader.hasNext()) {
                getAugmentations(this.marcReader.next());
            }
            result = this.augmentationText.toString().trim();
            this.augmentations.put(term, result);
        }
        return result;
    }

    public void save() throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("augmentations.obj"))) {
            oos.writeObject(this.augmentations);
        }
    }

    private void getAugmentations(final Record record) {
        for (DataField field : record.getDataFields()) {
            String tag = field.getTag();
            if ("400".equals(tag) || "450".equals(tag)) {
                for (Subfield subfield : field.getSubfields('a')) {
                    this.augmentationText.append(' ').append(Normalizer.compose(subfield.getData(), false));
                }
            }
        }
    }
}
