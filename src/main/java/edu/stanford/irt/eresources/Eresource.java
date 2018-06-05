package edu.stanford.irt.eresources;

import java.time.LocalDateTime;
import java.util.Collection;

public interface Eresource {

    Collection<String> getAbbreviatedTitles();

    Collection<String> getAlternativeTitles();

    Collection<String> getBroadMeshTerms();

    String getDate();

    String getDescription();

    String getId();

    int[] getItemCount();

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

    boolean isClone();

    boolean isCore();

    boolean isEnglish();

    boolean isLaneConnex();
}
