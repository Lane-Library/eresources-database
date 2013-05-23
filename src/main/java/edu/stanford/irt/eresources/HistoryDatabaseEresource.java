package edu.stanford.irt.eresources;

import java.util.HashSet;
import java.util.Set;

public class HistoryDatabaseEresource extends DatabaseEresource {

    private static final Set<String> ALLOWED_TYPES = new HashSet<String>();

    private static final String[] ALLOWED_TYPES_INITIALIZER = { "book", "movie", "serial", "graphic", "thesis",
            "people", "organization", "article", "chapter", "event", "finding aid" };
    static {
        for (String type : ALLOWED_TYPES_INITIALIZER) {
            ALLOWED_TYPES.add(type);
        }
    }

    @Override
    protected String getCompositeType(final String type) {
        return type;
    }

    @Override
    protected boolean isAllowable(final String type) {
        return ALLOWED_TYPES.contains(type);
    }
}
