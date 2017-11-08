package edu.stanford.irt.eresources;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TextParserHelperTest {

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
    }

    @Test
    public final void testZeroPadding() {
        assertEquals("2620 3", TextParserHelper.unpadZeroPadded("02620-03"));
        assertEquals("1", TextParserHelper.unpadZeroPadded("2017 Jan 01"));
        assertEquals("200", TextParserHelper.unpadZeroPadded("2017 0000200"));
        assertEquals("", TextParserHelper.unpadZeroPadded("no zero padding here"));
    }
}
