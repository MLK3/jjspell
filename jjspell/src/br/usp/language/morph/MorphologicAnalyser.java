package br.usp.language.morph;

import java.io.Reader;

/**
 * Morphologic Analyser Interface
 * @author mlk
 *
 */
public interface MorphologicAnalyser {

    /**
     * Defines de reader of the input
     * @param reader
     */
    public void setInput (Reader reader);
    
    /**
     * Checks whether there are more tokens or not
     * @return true, if there are.
     */
    public boolean hasMoreTokens();
    
    /**
     * Returns next token but Analyser does not "consume" it. 
     * @return next token
     */
    public TokenMorph lookNextToken();
    
    
    /**
     * Returns next Token in the sequence of tokens the analysed input. 
     * @return next token
     */
    public TokenMorph getNextToken();
    
    /**
     * Returns a list of possible Tokens. They can be aproximado or not.  
     * @return list of tokens
     */
    public TokenMorph[] getNextTokens();
}
