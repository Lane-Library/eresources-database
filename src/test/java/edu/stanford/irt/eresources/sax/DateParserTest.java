package edu.stanford.irt.eresources.sax;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DateParserTest {

    @Test
    public final void testParseDate() throws Exception {
        assertEquals("12000101", DateParser.parseDate("1200"));
        assertEquals("19770404", DateParser.parseDate("1977 Apr 4"));
        assertEquals("20120216", DateParser.parseDate("2012 Feb 16"));
        assertEquals("20140324", DateParser.parseDate("2014 Mar 24"));
        assertEquals("20140301", DateParser.parseDate("2014 Mar"));
        assertEquals("20140324", DateParser.parseDate("2014 Mar 24 - 26"));
        assertEquals("20140324", DateParser.parseDate("2014 Mar 24-26"));
        assertEquals("19981201", DateParser.parseDate("1998 Dec-1999 Jan"));
        assertEquals("20001101", DateParser.parseDate("2000 Nov-Dec"));
        assertEquals("20001223", DateParser.parseDate("2000 Dec 23- 30"));
        assertEquals("20160101", DateParser.parseDate("20160101"));
        assertEquals("20160101", DateParser.parseDate("2016 Winter"));
        assertEquals("20160701", DateParser.parseDate("2016 Summer"));
        assertEquals("20161001", DateParser.parseDate("2016 fall"));
        assertEquals("20160401", DateParser.parseDate("2016 spring-summer"));
        assertEquals("20160101", DateParser.parseDate("2016 foo"));
        assertEquals("20160101", DateParser.parseDate("foo 2016"));
        assertEquals("20160101", DateParser.parseDate("bar 2016 foo"));
        assertEquals("20160101", DateParser.parseDate("2016 2018"));
        assertEquals("20160218", DateParser.parseDate("2016 2 18"));
        assertEquals("20160201", DateParser.parseDate("2016 2"));
        assertEquals("20160201", DateParser.parseDate("2016 02"));
    }
}
