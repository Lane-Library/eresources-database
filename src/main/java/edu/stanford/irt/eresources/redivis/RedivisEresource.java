package edu.stanford.irt.eresources.redivis;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.sax.DateParser;

public class RedivisEresource implements Eresource {

    private static final int TWO = 2;

    private static final List<String> TYPES = Arrays.asList("Dataset");

    private Dataset dataset;

    public RedivisEresource(final Dataset dataset) {
        this.dataset = dataset;
    }

    @Override
    public Collection<String> getAbbreviatedTitles() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getAlternativeTitles() {
        return Collections.emptyList();
    }

    @Override
    public Collection<String> getBroadMeshTerms() {
        return Collections.emptyList();
    }

    @Override
    public String getDate() {
        return DateParser.parseDate(Integer.toString(this.getYear()));
    }

    @Override
    public String getDescription() {
        return buildDescription();
    }

    @Override
    public String getId() {
        return this.dataset.getId();
    }

    @Override
    public int[] getItemCount() {
        return new int[TWO];
    }

    @Override
    public String getKeywords() {
        return buildKeywords();
    }

    @Override
    public Collection<String> getMeshTerms() {
        return Collections.emptyList();
    }

    @Override
    public String getPrimaryType() {
        return TYPES.get(0);
    }

    @Override
    public Collection<String> getPublicationAuthors() {
        return Collections.emptyList();
    }

    @Override
    public String getPublicationAuthorsText() {
        return null;
    }

    @Override
    public String getPublicationDate() {
        return null;
    }

    @Override
    public String getPublicationIssue() {
        return null;
    }

    @Override
    public Collection<String> getPublicationLanguages() {
        return Collections.emptyList();
    }

    @Override
    public String getPublicationPages() {
        return null;
    }

    @Override
    public String getPublicationText() {
        return null;
    }

    @Override
    public String getPublicationTitle() {
        return null;
    }

    @Override
    public Collection<String> getPublicationTypes() {
        return Collections.emptyList();
    }

    @Override
    public String getPublicationVolume() {
        return null;
    }

    @Override
    public int getRecordId() {
        return Integer.parseInt(this.dataset.getId());
    }

    @Override
    public String getRecordType() {
        return "redivis";
    }

    @Override
    public String getShortTitle() {
        return null;
    }

    @Override
    public String getSortTitle() {
        return null;
    }

    @Override
    public String getTitle() {
        return this.dataset.getName();
    }

    @Override
    public Collection<String> getTypes() {
        return new ArrayList<>(TYPES);
    }

    @Override
    public LocalDateTime getUpdated() {
        return this.dataset.getUpdatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    public Collection<Version> getVersions() {
        List<Version> versions = new ArrayList<>();
        versions.add(new DatasetVersion(this.dataset));
        return versions;
    }

    @Override
    public int getYear() {
        return this.getUpdated().getYear();
    }

    @Override
    public boolean isCore() {
        return false;
    }

    @Override
    public boolean isEnglish() {
        // assume all datasets are English?
        return true;
    }

    @Override
    public boolean isLaneConnex() {
        return false;
    }

    private String buildDescription() {
        StringBuilder sb = new StringBuilder();
        if (null != this.dataset.getDescription()) {
            sb.append(this.dataset.getDescription());
            sb.append("<br/>");
        }
        for (DatasetCollection col : this.dataset.getCollections()) {
            sb.append("Collection: ").append('[').append(col.getName()).append("](").append(col.getUrl()).append(')');
            sb.append("<br/>");
        }
        if (null != this.dataset.getUpdatedAt()) {
            sb.append("Updated: ").append(this.dataset.getUpdatedAt());
        }
        return sb.toString();
    }

    private String buildKeywords() {
        StringBuilder sb = new StringBuilder("Redivis - Stanford Center for Population Health Sciences SPHS PHS ");
        sb.append(this.dataset.getName()).append(" ");
        sb.append(this.dataset.getOrganization().getName()).append(" ");
        sb.append(this.dataset.getOrganization().getShortName()).append(" ");
        for (DatasetCollection col : this.dataset.getCollections()) {
            sb.append(col.getName()).append(" ");
        }
        for (Tag obj : this.dataset.getTags()) {
            sb.append(obj.getName()).append(" ");
        }
        for (Documentation obj : this.dataset.getDocumentations()) {
            sb.append(obj.getText()).append(" ");
        }
        if (null != this.dataset.getEntity()) {
            sb.append(this.dataset.getEntity().getName()).append(" ");
        }
        for (Variable obj : this.dataset.getVariables()) {
            sb.append(obj.getName()).append(" ");
            sb.append(obj.getDescription()).append(" ");
            sb.append(obj.getLabel()).append(" ");
            sb.append(obj.getValueLabels()).append(" ");
        }
        return sb.toString();
    }
}
