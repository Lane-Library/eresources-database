package edu.stanford.irt.eresources;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TextParserHelperTest {

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
    public final void testZeroPadding() {
        assertEquals("2620 3", TextParserHelper.unpadZeroPadded("02620-03"));
        assertEquals("1", TextParserHelper.unpadZeroPadded("2017 Jan 01"));
        assertEquals("200", TextParserHelper.unpadZeroPadded("2017 0000200"));
        assertEquals("", TextParserHelper.unpadZeroPadded("no zero padding here"));
    }
}
