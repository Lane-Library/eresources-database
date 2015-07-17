package edu.stanford.irt.eresources;

import java.util.Collection;
import java.util.Date;

public interface Eresource {

    String getAuthor();

    String getDescription();

    ItemCount getItemCount();

    String getKeywords();

    Collection<String> getMeshTerms();

    String getPrimaryType();

    int getRecordId();

    String getRecordType();

    String getTitle();

    Collection<String> getTypes();

    Date getUpdated();

    Collection<Version> getVersions();

    int getYear();

    boolean isClone();

    boolean isCore();
}