package edu.stanford.irt.eresources;

import java.util.Collection;
import java.util.Date;

public interface Eresource {

    String getAuthor();

    String getDate();

    String getDescription();

    int[] getItemCount();

    String getKeywords();

    Collection<String> getMeshTerms();

    String getPrimaryType();

    Collection<String> getPublicationAuthors();

    String getPublicationAuthorsText();

    Collection<String> getPublicationLanguages();

    String getPublicationText();

    String getPublicationTitle();

    Collection<String> getPublicationTypes();

    int getRecordId();
    
    String getId();

    String getRecordType();

    String getTitle();

    Collection<String> getTypes();

    Date getUpdated();

    Collection<Version> getVersions();

    int getYear();

    boolean isClone();

    boolean isCore();

    boolean isEnglish();

    boolean isLaneConnex();
}
