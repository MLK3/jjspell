package br.usp.language.morph;

import java.util.HashMap;
import java.util.Map;

/**
 * Token
 * 
 */
public class TokenMorph {
    private String lexeme;
    private String originalDef;
    private String dicWord;
    private Map<String, String> properties;
    private boolean approx;

    public TokenMorph(String lexeme, String original, Map<String, String> atribs, boolean approx) {
        this(lexeme, original, null, atribs, approx);
    }

    public TokenMorph(String lexeme, String original, String dicWord, Map<String, String> atribs, boolean approx) {
        this.lexeme = lexeme;
        this.originalDef = original;
        this.dicWord = dicWord;
        this.properties = atribs != null ? atribs:new HashMap<String, String>() ;
        this.approx = approx;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public boolean isApprox() {
        return approx;
    }

    public boolean hasProperty(String name) {
        return properties.containsKey(name);
    }

    public String getProperty(String name) {
        return properties.get(name);
    }
    
    public Map<String,String> getProperties() {
        return properties;
    }

    public String setProperty(String key, String property) {
        return properties.put(key, property);
    }

    public String getType() {
        return properties.get(Tags.CATEGORY);
    }

    public String getOriginalDef() {
        return originalDef;
    }

    public String getDicWord() {
        return dicWord;
    }

    public String toString(){
        return this.getLexeme()+"\t"+ this.getDicWord()+"\t" + this.isApprox() + "\t"+properties.toString();
    }
}
