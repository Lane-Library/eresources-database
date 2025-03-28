package edu.stanford.irt.eresources;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

public class TextParserHelperTest {

    public static final String THIS_YEAR = Integer.toString(LocalDate.now(ZoneId.of("America/Los_Angeles")).getYear());

    @Test
    final void testAppendMaybeAddComma() {
        StringBuilder sb = new StringBuilder("append to me");
        TextParserHelper.appendMaybeAddComma(sb, "string");
        sb.delete(0, sb.length());
        TextParserHelper.appendMaybeAddComma(sb, "string");
        assertEquals("string", sb.toString());
        sb.delete(0, sb.length());
        TextParserHelper.appendMaybeAddComma(sb, null);
        assertEquals("", sb.toString());
    }

    @Test
    final void testCleanId() {
        assertEquals("981102488", TextParserHelper.cleanId(981102488));
        assertEquals("981102488", TextParserHelper.cleanId(-981102488));
    }

    @Test
    final void testCleanOrcid() {
        assertEquals("0000-0001-5769-0004", TextParserHelper.cleanOrcid("0000-0001-5769-0004"));
        assertEquals("0000-0001-5321-983X", TextParserHelper.cleanOrcid("0000-0001-5321-983x"));
        assertEquals("0000-0000-0001-5769-0004", TextParserHelper.cleanOrcid("0000-0000-0001-5769-0004"));
        assertEquals("0000-0003-2819-2553", TextParserHelper.cleanOrcid("'http://orcid.org/0000-0003-2819-2553"));
        assertEquals("http://orcid.org/http://orcid.org/0000-0002-5430-3205-0002-5430-3205",
                TextParserHelper.cleanOrcid("http://orcid.org/http://orcid.org/0000-0002-5430-3205-0002-5430-3205"));
        assertEquals("0000-0002-5430-3205", TextParserHelper.cleanOrcid("http://orcid.org/0000-0002-5430-3205"));
        assertEquals("0000-0001-9070-3962", TextParserHelper.cleanOrcid("0000 0001 9070 3962"));
    }

    @Test
    final void testExtractDois() {
        String input = "this is a doi:10.1016/j.lfs.2015.10.025 and this is another doi:10.109/zas and "
                + "this is not a doi:100.foo/ard but this one is 10.5694/j.1326-5377.1916.tb117256.x  doi "
                + "and this one should not match 10.5694/j.1326-5377.1916.tb117256.x because missing label";
        assertEquals(3, TextParserHelper.extractDois(input).size());
        assertEquals("10.1016/j.lfs.2015.10.025", TextParserHelper.extractDois(input).get(0));
        assertEquals("10.109/zas", TextParserHelper.extractDois(input).get(1));
        assertEquals("10.5694/j.1326-5377.1916.tb117256.x", TextParserHelper.extractDois(input).get(2));
    }

    @Test
    final void testMaybeStripTrailingPeriod() {
        assertEquals("string ", TextParserHelper.maybeStripTrailingPeriod("string "));
        assertEquals("string ", TextParserHelper.maybeStripTrailingPeriod("string ."));
    }

    @Test
    final void testMaybeStripUnbalancedBracket() {
        assertEquals("string", TextParserHelper.maybeStripTrailingUnbalancedBracket("string]"));
        assertEquals("[string]", TextParserHelper.maybeStripTrailingUnbalancedBracket("[string]"));
        assertEquals("string] ", TextParserHelper.maybeStripTrailingUnbalancedBracket("string] "));
    }

    @Test
    final void testMonthAlternates() {
        assertEquals("", TextParserHelper.explodeMonth(null));
        assertEquals("", TextParserHelper.explodeMonth(""));
        assertEquals("1 01 Jan January", TextParserHelper.explodeMonth("1"));
        assertEquals("1 01 Jan January", TextParserHelper.explodeMonth("01"));
        assertEquals("1 01 Jan January", TextParserHelper.explodeMonth("Jan"));
        assertEquals("1 01 Jan January", TextParserHelper.explodeMonth("January"));
        assertEquals("5 05 May", TextParserHelper.explodeMonth("5"));
        assertEquals("6 06 Jun June", TextParserHelper.explodeMonth("Jun"));
        assertEquals("7 07 Jul July", TextParserHelper.explodeMonth("7"));
        assertEquals("7 07 Jul July", TextParserHelper.explodeMonth("07"));
        assertEquals("7 07 Jul July", TextParserHelper.explodeMonth("Jul"));
        assertEquals("7 07 Jul July", TextParserHelper.explodeMonth("July"));
        assertEquals("12 Dec December", TextParserHelper.explodeMonth("Dec"));
        assertEquals("12 Dec December", TextParserHelper.explodeMonth("12"));
        assertEquals("12 Dec December", TextParserHelper.explodeMonth("December"));
    }

    @Test
    final void testMonthAlternatesFreeText() {
        assertEquals("", TextParserHelper.explodeMonthAbbreviations(null));
        assertEquals("", TextParserHelper.explodeMonthAbbreviations(""));
        assertEquals("4 04 Apr April", TextParserHelper.explodeMonthAbbreviations("1977 Apr 4"));
        assertEquals("2 02 Feb February", TextParserHelper.explodeMonthAbbreviations("2012 Feb 16"));
        assertEquals("3 03 Mar March", TextParserHelper.explodeMonthAbbreviations("2014 Mar 24"));
        assertEquals("3 03 Mar March", TextParserHelper.explodeMonthAbbreviations("2014 Mar"));
        assertEquals("3 03 Mar March", TextParserHelper.explodeMonthAbbreviations("2014 Mar 24 - 26"));
        assertEquals("12 Dec December 1 01 Jan January",
                TextParserHelper.explodeMonthAbbreviations("1998 Dec-1999 Jan"));
        assertEquals("11 Nov November 12 Dec December", TextParserHelper.explodeMonthAbbreviations("2000 Nov-Dec"));
        assertEquals("", TextParserHelper.explodeMonthAbbreviations("2016 Winter"));
        assertEquals("", TextParserHelper.explodeMonthAbbreviations("2016 Summer"));
        assertEquals("", TextParserHelper.explodeMonthAbbreviations("2016 fall"));
        assertEquals("", TextParserHelper.explodeMonthAbbreviations("2016 spring-summer"));
        assertEquals("", TextParserHelper.explodeMonthAbbreviations("2016 spring/summer"));
        assertEquals("", TextParserHelper.explodeMonthAbbreviations("2016 foo"));
    }

    @Test
    final void testPagesParser() {
        assertEquals("2620-2623", TextParserHelper.parseEndPages("2620-3"));
        assertEquals("50-55", TextParserHelper.parseEndPages("50-5"));
        assertEquals("12-19", TextParserHelper.parseEndPages("12-9"));
        assertEquals("304-310", TextParserHelper.parseEndPages("304- 10"));
        assertEquals("335-336", TextParserHelper.parseEndPages("335-6"));
        assertEquals("1199-1201", TextParserHelper.parseEndPages("1199-201"));
        assertEquals("", TextParserHelper.parseEndPages("24-32, 64"));
        assertEquals("31-37", TextParserHelper.parseEndPages("31-7 cntd"));
        assertEquals("176-178", TextParserHelper.parseEndPages("176-8 concl"));
        assertEquals("", TextParserHelper.parseEndPages("iii-viii"));
        assertEquals("", TextParserHelper.parseEndPages("XC-CIII"));
        assertEquals("P32-P34", TextParserHelper.parseEndPages("P32- 4"));
        assertEquals("", TextParserHelper.parseEndPages("32P-35P"));
        assertEquals("111-112", TextParserHelper.parseEndPages("suppl 111-2"));
        assertEquals("E101-E106", TextParserHelper.parseEndPages("E101-6"));
        assertEquals("44-48", TextParserHelper.parseEndPages("44; discussion 44-8"));
        assertEquals("925-926", TextParserHelper.parseEndPages("925; author reply 925- 6"));
        assertEquals("", TextParserHelper.parseEndPages("e66"));
        assertEquals("129e1-129e4", TextParserHelper.parseEndPages("129e1- 4"));
        assertEquals("10-18", TextParserHelper.parseEndPages("10.10-8"));
        assertEquals("", TextParserHelper.parseEndPages(""));
        assertEquals("", TextParserHelper.parseEndPages("2620-2623"));
        assertEquals("", TextParserHelper.parseEndPages(null));
    }

    @Test
    final void testParseYear() {
        assertEquals(null, TextParserHelper.parseYear("string"));
        assertEquals(THIS_YEAR, TextParserHelper.parseYear("9999"));
        assertEquals("1990", TextParserHelper.parseYear("199u"));
        assertEquals("1900", TextParserHelper.parseYear("19uu"));
        assertEquals("1000", TextParserHelper.parseYear("1uuu"));
        assertEquals("2000", TextParserHelper.parseYear("2uuu"));
        assertEquals(null, TextParserHelper.parseYear("uuuu"));
        assertEquals("1980", TextParserHelper.parseYear("198|"));
    }

    @Test
    final void testRecordIdFromLaneControlNumber() {
        assertEquals(1234, TextParserHelper.recordIdFromLaneControlNumber("L1234").intValue());
        assertEquals(1234, TextParserHelper.recordIdFromLaneControlNumber("Q1234").intValue());
        assertEquals(1234, TextParserHelper.recordIdFromLaneControlNumber("Z1234").intValue());
        assertNull(TextParserHelper.recordIdFromLaneControlNumber("foo"));
        assertNull(TextParserHelper.recordIdFromLaneControlNumber(null));
        assertNull(TextParserHelper.recordIdFromLaneControlNumber("L1234q"));
    }

    @Test
    final void testRemoveTrailingSlashAndSpace() {
        StringBuilder sb = new StringBuilder("remove from me / ");
        TextParserHelper.removeTrailingSlashAndSpace(sb);
        assertEquals("remove from me", sb.toString());
        TextParserHelper.removeTrailingSlashAndSpace(sb);
        assertEquals("remove from me", sb.toString());
        sb = new StringBuilder();
        TextParserHelper.removeTrailingSlashAndSpace(sb);
        assertEquals(0, sb.length());
    }

    @Test
    final void testToTitleCase() {
        assertEquals("Sound Recording", TextParserHelper.toTitleCase("sound recording"));
        assertEquals("Foo 123 Bar", TextParserHelper.toTitleCase("foo 123 bar"));
        assertEquals("e-Anatomy", TextParserHelper.toTitleCase("e-Anatomy"));
        assertEquals("Foo 123 Bar", TextParserHelper.toTitleCase("foo 123 bar"));
        assertEquals("The Atlantic", TextParserHelper.toTitleCase("The Atlantic."));
        assertEquals("NEJM AI", TextParserHelper.toTitleCase("NEJM AI."));
        assertEquals("Red Book (American Academy of Pediatrics)",
                TextParserHelper.toTitleCase("Red book (American Academy of Pediatrics)"));
        assertEquals("New York Times (National Edition)",
                TextParserHelper.toTitleCase("New York times (National edition)"));
        assertEquals("The New York Times", TextParserHelper.toTitleCase("The New York times"));
        assertEquals("New England Journal of Medicine",
                TextParserHelper.toTitleCase("New England journal of medicine"));
        assertEquals("Stream Ecology. Third Edition", TextParserHelper.toTitleCase("Stream ecology. Third edition."));
        assertEquals("aBIOTECH", TextParserHelper.toTitleCase("aBIOTECH."));
        assertEquals("Apple iPhone Lightning USB Data Cable & Wall Charger",
                TextParserHelper.toTitleCase("Apple iPhone lightning USB Data Cable & Wall Charger."));
        assertEquals("Dell External USB Ultra Slim DVD +/- RW Slot Drive",
                TextParserHelper.toTitleCase("Dell External USB Ultra Slim DVD +/- RW Slot Drive"));
    }

    @Test
    final void testZeroPadding() {
        assertEquals("2620 3", TextParserHelper.unpadZeroPadded("02620-03"));
        assertEquals("1", TextParserHelper.unpadZeroPadded("2017 Jan 01"));
        assertEquals("200", TextParserHelper.unpadZeroPadded("2017 0000200"));
        assertEquals("", TextParserHelper.unpadZeroPadded("no zero padding here"));
    }
}
