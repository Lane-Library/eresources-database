package edu.stanford.irt.eresources;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;

public interface Eresource {

    static final int EMPTY_ITEM_COUNT_SIZE = 2;

    Collection<String> getAbbreviatedTitles();

    Collection<String> getAlternativeTitles();

    Collection<String> getBroadMeshTerms();

    String getDate();

    String getDescription();

    String getId();

    default Collection<String> getIsbns() {
        return Collections.emptyList();
    }

    default Collection<String> getIssns() {
        return Collections.emptyList();
    }

    default int[] getItemCount() {
        return new int[EMPTY_ITEM_COUNT_SIZE];
    }

    String getKeywords();

    Collection<String> getMeshTerms();

    String getPrimaryType();

    Collection<String> getPublicationAuthors();

    String getPublicationAuthorsText();

    String getPublicationDate();

    String getPublicationIssue();

    Collection<String> getPublicationLanguages();

    String getPublicationPages();

    String getPublicationText();

    String getPublicationTitle();

    Collection<String> getPublicationTypes();

    String getPublicationVolume();

    int getRecordId();

    String getRecordType();

    String getShortTitle();

    String getSortTitle();

    String getTitle();

    Collection<String> getTypes();

    LocalDateTime getUpdated();

    Collection<Version> getVersions();

    int getYear();

    boolean isEnglish();
}
