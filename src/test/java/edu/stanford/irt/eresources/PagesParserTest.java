package edu.stanford.irt.eresources;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PagesParserTest {

    @Test
    public final void testParser() {
        assertEquals("2623", PagesParser.parseEndPages("2620-3"));
        assertEquals("55", PagesParser.parseEndPages("50-5"));
        assertEquals("19", PagesParser.parseEndPages("12-9"));
        assertEquals("310", PagesParser.parseEndPages("304- 10"));
        assertEquals("336", PagesParser.parseEndPages("335-6"));
        assertEquals("1201", PagesParser.parseEndPages("1199-201"));
        assertEquals("", PagesParser.parseEndPages("24-32, 64"));
        assertEquals("37", PagesParser.parseEndPages("31-7 cntd"));
        assertEquals("178", PagesParser.parseEndPages("176-8 concl"));
        assertEquals("", PagesParser.parseEndPages("iii-viii"));
        assertEquals("", PagesParser.parseEndPages("XC-CIII"));
        assertEquals("P34", PagesParser.parseEndPages("P32- 4"));
        assertEquals("", PagesParser.parseEndPages("32P-35P"));
        assertEquals("112", PagesParser.parseEndPages("suppl 111-2"));
        assertEquals("E106", PagesParser.parseEndPages("E101-6"));
        assertEquals("48", PagesParser.parseEndPages("44; discussion 44-8"));
        assertEquals("926", PagesParser.parseEndPages("925; author reply 925- 6"));
        assertEquals("", PagesParser.parseEndPages("e66"));
        assertEquals("129e4", PagesParser.parseEndPages("129e1- 4"));
        assertEquals("18", PagesParser.parseEndPages("10.10-8"));
        assertEquals("", PagesParser.parseEndPages(""));
        assertEquals("", PagesParser.parseEndPages("2620-2623"));
    }
}
