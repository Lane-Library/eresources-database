package edu.stanford.irt.eresources;

import java.util.Collection;
import java.util.Date;

public interface Eresource {

    String getDescription();

    String getKeywords();

    Collection<String> getMeshTerms();

    int getRecordId();

    String getRecordType();

    String getTitle();

    Collection<String> getTypes();

    Date getUpdated();

    Collection<Version> getVersions();

    int getYear();

    boolean isCore();

    boolean isClone();
}