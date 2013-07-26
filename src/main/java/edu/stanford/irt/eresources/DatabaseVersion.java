package edu.stanford.irt.eresources;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import edu.stanford.irt.eresources.impl.VersionImpl;

public class DatabaseVersion extends VersionImpl {

    private static final Set<String> ALLOWED_SUBSETS = new HashSet<String>();

    private static final String[] ALLOWED_SUBSETS_INITIALIZER = {"mobile applications","pda tools","mobile resources", "biotools"};
    
    static {
        for (String subset : ALLOWED_SUBSETS_INITIALIZER) {
            ALLOWED_SUBSETS.add(subset);
        }
    }

    private boolean hasGetPasswordLink = false;

    private Collection<String> subsets;

    @Override
    public void addSubset(final String subset) {
        if (ALLOWED_SUBSETS.contains(subset)) {
            if (null == this.subsets) {
                this.subsets = new HashSet<String>();
            }
            this.subsets.add(subset);
        }
    }

    @Override
    public Collection<String> getSubsets() {
        if (null == this.subsets) {
            return Collections.emptySet();
        }
        return Collections.unmodifiableCollection(this.subsets);
    }

    public boolean hasGetPasswordLink() {
        return this.hasGetPasswordLink;
    }

    public void setHasGetPasswordLink(final boolean hasGetPasswordLink) {
        this.hasGetPasswordLink = hasGetPasswordLink;
    }
}
