package edu.stanford.irt.eresources;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.Test;

public class TextParserHelperTest {

    public static final String THIS_YEAR = Integer.toString(LocalDate.now(ZoneId.of("America/Los_Angeles")).getYear());

    @Test
    public final void testAppendMaybeAddComma() {
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
    public final void testCleanIsxn() {
        assertEquals("981102488X", TextParserHelper.cleanIsxn("981102488X"));
        assertEquals("0721619673", TextParserHelper.cleanIsxn("0721619673 (v. 1)"));
        assertEquals("0393064239", TextParserHelper.cleanIsxn("0393064239 (pbk.) :"));
        assertEquals("0393064190", TextParserHelper.cleanIsxn("0393064190 :"));
    }

    @Test
    public final void testMaybeStripTrailingPeriod() {
        assertEquals("string ", TextParserHelper.maybeStripTrailingPeriod("string "));
        assertEquals("string ", TextParserHelper.maybeStripTrailingPeriod("string ."));
    }

    @Test
    public final void testMonthAlternates() {
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
    public final void testMonthAlternatesFreeText() {
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
    public final void testPagesParser() {
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
    public final void testParseYear() {
        assertEquals(null, TextParserHelper.parseYear("string"));
        assertEquals(THIS_YEAR, TextParserHelper.parseYear("9999"));
        assertEquals("1995", TextParserHelper.parseYear("199u"));
        assertEquals("1955", TextParserHelper.parseYear("19uu"));
        assertEquals("1555", TextParserHelper.parseYear("1uuu"));
        assertEquals(THIS_YEAR, TextParserHelper.parseYear("2uuu"));
        assertEquals(null, TextParserHelper.parseYear("uuuu"));
        assertEquals("1985", TextParserHelper.parseYear("198|"));
    }

    @Test
    public final void testRemoveTrailingSlashAndSpace() {
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
    public final void testToTitleCase() {
        assertEquals("Sound Recording", TextParserHelper.toTitleCase("sound recording"));
        assertEquals("Foo 123 Bar", TextParserHelper.toTitleCase("foo 123 bar"));
    }

    @Test
    public final void testZeroPadding() {
        assertEquals("2620 3", TextParserHelper.unpadZeroPadded("02620-03"));
        assertEquals("1", TextParserHelper.unpadZeroPadded("2017 Jan 01"));
        assertEquals("200", TextParserHelper.unpadZeroPadded("2017 0000200"));
        assertEquals("", TextParserHelper.unpadZeroPadded("no zero padding here"));
    }
}
