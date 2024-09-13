package edu.stanford.irt.eresources;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public interface Link {

    String getAdditionalText();

    String getLabel();

    String getLinkText();

    String getUrl();

    default boolean isRelatedResourceLink() {
        return false;
    }

    default boolean isResourceLink() {
        return false;
    }

}
