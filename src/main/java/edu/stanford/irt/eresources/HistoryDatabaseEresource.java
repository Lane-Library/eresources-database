package edu.stanford.irt.eresources;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HistoryDatabaseEresource extends Eresource {

    private static final Set<String> ALLOWED_TYPES = new HashSet<String>();

    private static final String[] ALLOWED_TYPES_INITIALIZER = { "book", "movie", "serial", "graphic", "thesis",
            "people", "organization", "article", "chapter", "event", "finding aid", "object" };

    private static final Map<String, String> COMPOSITE_TYPES = new HashMap<String, String>();

    private static final String[][] COMPOSITE_TYPES_INITIALIZER = { { "article", "articles" }, { "book", "books" },
            { "chapter", "chapters" }, { "event", "events" }, { "finding aid", "finding aids" },
            { "graphic", "graphics" }, { "organization", "organizations" }, { "serial", "serials" },
            { "thesis", "dissertations, academic" }, { "object", "objects" } };
    static {
        for (String type : ALLOWED_TYPES_INITIALIZER) {
            ALLOWED_TYPES.add(type);
        }
        for (String[] element : COMPOSITE_TYPES_INITIALIZER) {
            for (int j = 1; j < element.length; j++) {
                COMPOSITE_TYPES.put(element[j], element[0]);
            }
        }
    }

    @Override
    protected String getCompositeType(final String type) {
        if (COMPOSITE_TYPES.containsKey(type)) {
            return COMPOSITE_TYPES.get(type);
        }
        return type;
    }

    @Override
    protected boolean isAllowable(final String type) {
        return ALLOWED_TYPES.contains(type);
    }
}
