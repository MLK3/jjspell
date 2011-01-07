package br.usp.language.morph;

import static org.junit.Assert.*;

import java.io.StringReader;

import org.junit.Before;
import org.junit.Test;

public class AnalisadorMorfologicoTest {
    
    AnalisadorMorfologico amor;

    @Before
    public void setUp() throws Exception {
        amor = new AnalisadorMorfologico("./resources/port");
    }

    @Test
    public void testGetNextToken() {
        String texto = new String("A casa");        
        amor.setInput( new StringReader(texto));
        TokenMorph nextToken = amor.getNextToken();
        assertEquals("A", nextToken.getLexeme());
        nextToken = amor.getNextToken();
        assertEquals("casa", nextToken.getLexeme());
    }

    @Test
    public void testHasMoreTokens() {
        String texto = new String("A casa");        
        amor.setInput( new StringReader(texto));
        assertTrue(amor.hasMoreTokens());
        amor.getNextToken();
        assertTrue(amor.hasMoreTokens());
        amor.getNextToken();
        assertFalse(amor.hasMoreTokens());
    }

    @Test
    public void testLookNextToken() {
        String texto = new String("casa");        
        amor.setInput( new StringReader(texto));
        TokenMorph nextToken = amor.lookNextToken();
        assertEquals("casa", nextToken.getLexeme());
        assertTrue(amor.hasMoreTokens());
    }
    
    @Test
    public void testContraction() {
        String texto = new String("do");        
        amor.setInput( new StringReader(texto));
        TokenMorph nextToken = amor.getNextToken();
        System.out.println(nextToken.toString());
        nextToken = amor.getNextToken();
        System.out.println(nextToken.toString());
    }
}
