package edu.stanford.irt.eresources.marc;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import edu.stanford.irt.eresources.Eresource;
import edu.stanford.irt.eresources.EresourceDatabaseException;
import edu.stanford.irt.eresources.LanguageMap;
import edu.stanford.irt.eresources.Version;
import edu.stanford.irt.eresources.VersionComparator;
import edu.stanford.irt.eresources.sax.DateParser;
import edu.stanford.lane.catalog.Record;
import edu.stanford.lane.catalog.Record.Field;
import edu.stanford.lane.catalog.Record.Subfield;

/**
 * An Eresource that encapsulates the marc Records from which it is derived.
 */
public class BibMarcEresource extends MARCRecordSupport implements Eresource {

    public static final int THIS_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    private static final Pattern ACCEPTED_YEAR_PATTERN = Pattern.compile("^\\d[\\d|u]{3}$");

    private static final Comparator<Version> COMPARATOR = new VersionComparator();

    private static final LanguageMap LANGUAGE_MAP = new LanguageMap();

    private static final String SEMICOLON_SPACE = "; ";

    private static final Pattern SPACE_SLASH = Pattern.compile(" /");

    private DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    private List<Record> holdings;

    private int[] itemCount;

    private String keywords;

    private Record record;

    private TypeFactory typeFactory;

    private List<Version> versions;

    private Collection<String> types;

    private String primaryType;

    public BibMarcEresource(final List<Record> recordList, final String keywords, final int[] itemCount,
            final TypeFactory typeFactory) {
        this.keywords = keywords;
        this.record = recordList.get(0);
        this.holdings = recordList.subList(1, recordList.size());
        this.itemCount = itemCount;
        this.typeFactory = typeFactory;
    }

    @Override
    public Collection<String> getAbbreviatedTitles() {
        return getSubfieldDataStream(getFieldStream(this.record, "246")
                .filter(f -> {
                    Subfield i = f.getSubfields()
                            .stream()
                            .filter(s -> s.getCode() == 'i')
                            .findFirst()
                            .orElse(null);
                    Subfield a = f.getSubfields()
                            .stream()
                            .filter(s -> s.getCode() == 'a')
                            .findFirst()
                            .orElse(null);
                    return i != null && a != null && "Acronym/initialism:".equalsIgnoreCase(i.getData());
                    }), "a")
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<String> getAlternativeTitles() {
        return getSubfieldDataStream(this.record, "130|210|246|247", "a")
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<String> getBroadMeshTerms() {
        return getSubfieldDataStream(getFieldStream(this.record, "650")
                .filter(f -> f.getIndicator1() == '4' && f.getIndicator2() == '2'), "a")
                .map(this::maybeStripTrailingPeriod)
                .collect(Collectors.toSet());
    }

    @Override
    public String getDate() {
        String date = null;
        List<Field> fields773 = getFieldStream(this.record, "773")
                .collect(Collectors.toList());
        int subfieldWCount = 0;
        for (int i = 0; i < fields773.size(); i++) {
            List<Subfield> subfields = fields773.get(i).getSubfields();
            if (subfieldWCount == 0) {
                date = subfields.stream()
                        .filter(s -> s.getCode() == 'd')
                        .map(Subfield::getData)
                        .reduce((a,b) -> b)
                        .orElse(date);
            }
            if (subfields.stream().anyMatch(s -> s.getCode() == 'w')) {
                subfieldWCount += 1;
            }
        }
        if (date != null) {
            date = DateParser.parseDate(date.replaceAll("(:|;)", " "));
        }
        if (null == date || "0".equals(date) || date.isEmpty()) {
            String field008 = getFieldStream(this.record, "008")
                    .map(Field::getData)
                    .findFirst().orElse("");
            String endDate = parseYear(field008.substring(11, 15));
            String beginDate = parseYear(field008.substring(7, 11));
            int year = 0;
            if (null != endDate) {
                year = Integer.parseInt(endDate);
            } else if (null != beginDate) {
                year = Integer.parseInt(beginDate);
            } else {
                year = getYear();
            }
            date = DateParser.parseDate(Integer.toString(year));
        }
        return date;
    }

    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        getSubfieldDataStream(this.record, "520")
            .forEach(s -> {
                if (sb.length() > 0) {
                    sb.append(' ');
                }
                sb.append(s);
            });
        if (sb.length() == 0) {
            getSubfieldDataStream(this.record, "505")
                .forEach(s -> {
                    if (sb.length() > 0) {
                        sb.append(' ');
                    }
                    sb.append(s);
                });
        }
        return sb.length() > 0 ? sb.toString() : null;
    }

    @Override
    public int[] getItemCount() {
        return this.itemCount;
    }

    @Override
    public String getKeywords() {
        return this.keywords;
    }

    @Override
    public Collection<String> getMeshTerms() {
        return getSubfieldDataStream(getFieldStream(this.record, "650|651")
                .filter(f -> ("650".equals(f.getTag()) && "2356".indexOf(f.getIndicator2()) > -1)
                        || ("651".equals(f.getTag()) && f.getIndicator2() == '7')), "a")
                .map(this::maybeStripTrailingPeriod)
                .collect(Collectors.toSet());
    }

    @Override
    public String getPrimaryType() {
        if (this.primaryType == null) {
            this.primaryType = this.typeFactory.getPrimaryType(this.record);
        }
        return this.primaryType;
    }

    @Override
    public Collection<String> getPublicationAuthors() {
        return Collections.unmodifiableCollection(getSubfieldDataStream(getFieldStream(this.record, "100|700")
                .filter(f -> "100".equals(f.getTag())
                        || ("700".equals(f.getTag()) && !(getPrimaryType().startsWith("Journal")))), "a")
                .map(s -> s.replaceFirst(",$", ""))
                .map(auth -> auth.endsWith(".") && !auth.matches(".* \\w\\.") ? auth.substring(0, auth.length() - 1)
                        : auth)
                .collect(Collectors.toList()));
    }

    @Override
    public String getPublicationAuthorsText() {
        String authorsText =  getSubfieldDataStream(this.record, "245", "c")
                // get the last c
                .reduce((a, b) -> b)
                .orElse(null);
        if (authorsText == null) {
            StringBuilder sb = new StringBuilder();
            for (String auth : getPublicationAuthors()) {
                sb.append(auth).append(SEMICOLON_SPACE);
            }
            if (sb.toString().endsWith(SEMICOLON_SPACE)) {
                sb.delete(sb.length() - 2, sb.length());
            }
            if (sb.length() > 0 && !sb.toString().endsWith(".")) {
                sb.append('.');
            }
            authorsText = sb.toString();
        }
        return authorsText;
    }

    @Override
    public Collection<String> getPublicationLanguages() {
        Set<String> languages = new HashSet<>();
        String field008 = getFieldStream(this.record, "008")
                .map(Field::getData)
                .findFirst()
                .orElse("");
        String lang = field008.substring(35, 38);
        languages.add(LANGUAGE_MAP.getLanguage(lang.toLowerCase(Locale.US)));
        languages.addAll(getSubfieldDataStream(this.record, "041")
                .map(String::toLowerCase)
                .map(LANGUAGE_MAP::getLanguage)
                .collect(Collectors.toSet()));
        return languages;
    }

    @Override
    public String getPublicationText() {
        StringBuilder sb = new StringBuilder();
        List<Field> fields773 = getFieldStream(this.record, "773")
                .collect(Collectors.toList());
        int subfieldWCount = 0;
        for (int i = 0; i < fields773.size(); i++) {
            Field field733 = fields773.get(i);
            if(subfieldWCount == 0 && sb.length() == 0) {
                sb.append(field733.getSubfields()
                    .stream()
                    .filter(s -> "tp".indexOf(s.getCode()) > -1)
                    .map(Subfield::getData)
                    .reduce((a, b) -> b)
                    .orElse(""));
                if (sb.length() > 0) {
                    sb.append(". ");
                }
            }
            for (Subfield subfield : field733.getSubfields()) {
                if ("dg".indexOf(subfield.getCode()) > -1) {
                    sb.append(' ').append(subfield.getData());
                } else if (subfieldWCount > 0 && subfield.getCode() == 't') {
                    sb.append("; ").append(subfield.getData());
                }
            }
            if (field733.getSubfields().stream().anyMatch(s -> s.getCode() == 'w')) {
                subfieldWCount++;
            }
        }
        return sb.toString();
    }

    @Override
    public String getPublicationTitle() {
      List<Field> fields773 = getFieldStream(this.record, "773")
              .collect(Collectors.toList());
      int countOf733W = 0;
      String data = null;
      for (int i = 0; i < fields773.size() && countOf733W == 0; i++) {
          for (Subfield subfield: fields773.get(i).getSubfields()) {
              char code = subfield.getCode();
              if (code == 't' || code == 'p') {
                  data = subfield.getData();
              } else if (code == 'w') {
                  countOf733W++;
              }
          }
      }
      return data;
    }

    @Override
    public int getRecordId() {
        return Integer.parseInt(getFieldStream(this.record, "001")
                .map(f -> f.getData())
                .findFirst()
                .orElse("0"));
    }

    @Override
    public String getRecordType() {
        return "bib";
    }

    @Override
    public String getShortTitle() {
        return getSubfieldDataStream(this.record, "149", "a")
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getSortTitle() {
        StringBuilder sb = getStringBuilderWith245();
        int offset = getFieldStream(this.record, "245")
                .map(f -> Integer.valueOf(f.getIndicator2()) - 48)
                .findFirst()
                .orElse(0);
        return sb.substring(offset);
    }

    @Override
    public String getTitle() {
        StringBuilder sb = getStringBuilderWith245();
        removeTrailingSlashAndSpace(sb);
        String edition = getSubfieldDataStream(this.record, "250", "a")
                .collect(Collectors.joining(". "));
        if (!edition.isEmpty()) {
            sb.append(". ").append(edition);
            removeTrailingSlashAndSpace(sb);
        }
        return sb.toString();
    }

    private void removeTrailingSlashAndSpace(StringBuilder sb) {
        while (sb.lastIndexOf("/") == sb.length() - 1 || sb.lastIndexOf(" ") == sb.length() - 1) {
            sb.setLength(sb.length() - 1);
        }
    }

    @Override
    public Collection<String> getTypes() {
        if (this.types == null) {
            this.types = this.typeFactory.getTypes(this.record);
        }
        return this.types;
    }

    @Override
    public Date getUpdated() {
        try {
            Date updated = this.dateFormat.parse(getFieldStream(this.record, "005")
                    .map(Field::getData)
                    .findFirst()
                    .orElse(null));
            for (Record holding : this.holdings) {
                Date holdingUpdated = this.dateFormat.parse(getFieldStream(holding, "005")
                        .map(Field::getData)
                        .findFirst()
                        .orElse(null));
                if (holdingUpdated.compareTo(updated) > 0) {
                    updated = holdingUpdated;
                }
            }
            return updated;
        } catch (ParseException e) {
            throw new EresourceDatabaseException(e);
        }
    }

    @Override
    public List<Version> getVersions() {
        if (this.versions == null) {
            Collection<Version> versionSet = new TreeSet<>(COMPARATOR);
            for (Record holding : this.holdings) {
                Version version = createVersion(holding);
                if (!version.getLinks().isEmpty()) {
                    versionSet.add(version);
                }
            }
            this.versions = Collections.unmodifiableList(new ArrayList<>(versionSet));
        }
        return this.versions;
    }

    @Override
    public int getYear() {
        int year = 0;
        String dateField = getFieldStream(this.record, "008")
                .map(Field::getData)
                .findFirst()
                .orElse("0000000000000000");
        String endDate = parseYear(dateField.substring(11, 15));
        if (endDate != null) {
            year = Integer.parseInt(endDate);
        } else {
            String beginDate = parseYear(dateField.substring(7, 11));
            if (beginDate != null) {
                year = Integer.parseInt(beginDate);
            }
        }
        return year;
    }

    @Override
    public boolean isCore() {
        return getSubfieldDataStream(this.record, "655", "a")
                .anyMatch("core material"::equalsIgnoreCase);
    }

    @Override
    public boolean isEnglish() {
        return getPublicationLanguages().contains("English");
    }

    @Override
    public boolean isLaneConnex() {
        return getSubfieldDataStream(this.record, "655", "a")
                .anyMatch("laneconnex"::equalsIgnoreCase);
    }

    protected Version createVersion(final Record record) {
        return new MarcVersion(record, this.record, this);
    }

    private StringBuilder getStringBuilderWith245() {
        StringBuilder sb = new StringBuilder();
        getFieldStream(this.record, "245")
            .findFirst()
            .ifPresent(f -> f.getSubfields().stream().filter(s -> "abnpq".indexOf(s.getCode()) > -1).forEach(s -> {
                    String data = s.getData();
                    if ('b' == s.getCode()) {
                        if (sb.indexOf(":") != sb.length() - 1) {
                            sb.append(" :");
                        }
                        data = SPACE_SLASH.matcher(data).replaceFirst("");
                    }
                    if (sb.length() > 0) {
                        sb.append(' ');
                    }
                    sb.append(data);
                    removeTrailingSlashAndSpace(sb);
                }));
        return sb;
    }

    // remove trailing periods, some probably should have them but
    // voyager puts them on everything :-(
    private String maybeStripTrailingPeriod(final String string) {
        int lastPeriod = string.lastIndexOf('.');
        if (lastPeriod >= 0) {
            int lastPosition = string.length() - 1;
            if (lastPeriod == lastPosition) {
                return string.substring(0, lastPosition);
            }
        }
        return string;
    }

    private String parseYear(final String year) {
        String parsedYear = null;
        Matcher yearMatcher = ACCEPTED_YEAR_PATTERN.matcher(year);
        if (yearMatcher.matches()) {
            parsedYear = year;
            if ("9999".equals(year)) {
                parsedYear = Integer.toString(THIS_YEAR);
            } else if (year.contains("u")) {
                int estimate = Integer.parseInt(year.replace('u', '5'));
                if (estimate > THIS_YEAR) {
                    estimate = THIS_YEAR;
                }
                parsedYear = Integer.toString(estimate);
            }
        }
        return parsedYear;
    }

    @Override
    public String getId() {
        return getRecordType() + "-" + getRecordId();
    }

    @Override
    public Collection<String> getPublicationTypes() {
        return Collections.emptySet();
    }

    @Override
    public boolean isClone() {
        return false;
    }

    @Override
    public String getPublicationDate() {
        return null;
    }

    @Override
    public String getPublicationPages() {
        return null;
    }

    @Override
    public String getPublicationVolume() {
        return null;
    }

    @Override
    public String getPublicationIssue() {
        return null;
    }
}
