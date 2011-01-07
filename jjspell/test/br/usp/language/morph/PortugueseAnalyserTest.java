package br.usp.language.morph;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

public class PortugueseAnalyserTest {
    
    MorphologicAnalyser pa;

    @Before
    public void setUp() throws Exception {
        pa = new PortugueseAnalyser("./resources/port", "./resources/port_amor_config.list");
    }

    @Test
    public void testGetNextToken() {
        String texto = new String("lentamente");
        pa.setInput(new StringReader(texto));
        TokenMorph token = pa.getNextToken();
        assertNotNull(token.getProperty(Tags.ADV_TYPE_KEY));
        assertEquals(token.getProperty(Tags.ADV_TYPE_KEY), Tags.ADV_MAN_SLOW);
    }
    
    @Test
    public void testLinkingVerb() {
        String texto = new String("sou");
        pa.setInput(new StringReader(texto));
        TokenMorph token = pa.getNextToken();
        assertNotNull(token.getProperty(Tags.CATEGORY));
        assertEquals(token.getProperty(Tags.CATEGORY), Tags.LINKINGVERB);
    }
}
