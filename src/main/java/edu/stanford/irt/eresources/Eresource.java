package edu.stanford.irt.eresources;

import java.util.Collection;
import java.util.Date;

public interface Eresource {
    
    Eresource EMPTY = new Eresource() {

        @Override
        public String getDescription() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int[] getItemCount() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getKeywords() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<String> getMeshTerms() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getPrimaryType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getRecordId() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getRecordType() {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getTitle() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<String> getTypes() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Date getUpdated() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<Version> getVersions() {
            throw new UnsupportedOperationException();
        }

        @Override
        public int getYear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isClone() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isCore() {
            throw new UnsupportedOperationException();
        }
        
    };

    String getDescription();

    int[] getItemCount();

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