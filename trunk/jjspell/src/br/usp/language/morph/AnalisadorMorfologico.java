package br.usp.language.morph;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*para compilar:
 * 
 * 
 javac br/usp/language/morph/AnalisadorMorfologico.java
 sudo javah -d jamori/ br.usp.language.morph.AnalisadorMorfologico

 gcc -fPIC -g -c -Wall jamori.c -ljspell
 gcc -shared -Wl,-soname,jamori.so -o ../jamori.so jamori.o -lc -ljspell

 */

//NOTE: o código nativ ainda pode dar pau caso sejam nulos s1 e s2

//TODO: seria interessante fazer buffToken ao invés de buffString 
public class AnalisadorMorfologico implements MorphologicAnalyser {
    private static final String NullReturn = "nao achou nada";

    private native String consultWord(String consultanda);

    private native String[] consultWordPossibilities(String consultanda);

    private native void loadDic(String path);

    //private final String strmatcher="\\[wü-ú]+=lex\\(";//usada na separação de tokens
    private final String diacriticos = "àáâãéêóôõçíüú";
    private final String letras = "\\w" + diacriticos + diacriticos.toUpperCase();

    private final String strmatcher = "\\w+=\\w+?[,\\]]";//usada na separação de tokens
    private final String strmatcherapprox = "=lex\\(";//usada na separação de tokens
    private final String strmatcherdicword = "\\(([" + letras + "\\-]+),";
    //private final String strmatcherdicword="\\(([[:alpha:]ã]+),";

    private Reader entrada;
    private BufferedReader br;

    private String buffString = null;
    private List<String> bufList = new ArrayList<String>();

    static {
        String fileseparator = System.getProperty("file.separator");
        // Jamori stands for Jspell Analisador Morfológico Interface
        System.load(System.getProperty("user.dir") + fileseparator + "lib" + fileseparator + "jamori.so");
        //System.load(ClassLoader.getSystemResource("jspell/jamori.so").getPath());
    }

    /**
     * This constructor loads the dictionary intended to be used during analysis
     * 
     * @param dic
     */
    public AnalisadorMorfologico(String dic) {
        loadDic(dic);
    }

    public void setInput(Reader reader) {
        this.entrada = reader;
        this.br = new BufferedReader(entrada);
    }

    public boolean hasMoreTokens() {
        if (!bufList.isEmpty())
            return true;
        String x = getLexemeFromBuf();
        if (x != null)
            bufList.add(x);
        return !bufList.isEmpty();
        /*
         * if (this.buffString != null) return true; buffString = getLexemeFromBuf(); return buffString != null ? true :
         * false;
         */
    }

    public TokenMorph lookNextToken() {
        //para povoar o buffer. ou não, caso no qual null é retornado
        hasMoreTokens();
        return tkFromJspellAnswer(bufList.get(0), consultWord(bufList.get(0)));

        //return tkFromJspellAnswer(buffString, consultWord(buffString));
    }

    /**
     * Returns the the next TokenMorph. If there are more than one option for the next word, returns the best guess.
     */
    public TokenMorph getNextToken() {
        TokenMorph[] nextTokens = this.getNextTokens();
        if (nextTokens != null && nextTokens.length > 0) {
            return nextTokens[0];
        }
        return null;
    }

    //retorna as varias possibilidades do proximo token..
    //TODO: mudar o nome!
    public TokenMorph[] getNextTokens() {
        //nesse caso, não devemos tentar pegar o proximo token do buffer de entrada,
        //mas sim da buffString. Seguidamente, essa variável deve ser nulificada
        String lexema;

        if (!hasMoreTokens())
            return null;

        lexema = bufList.remove(0);

        /*
         * if (buffString != null) { lexema = new String(buffString); buffString = null; } else { lexema =
         * getLexemeFromBuf(); } if (lexema == null) return null;
         */

        String[] possibdef = consultWordPossibilities(lexema);
        List<TokenMorph> mp = new ArrayList<TokenMorph>();

        for (String s : possibdef) {
            mp.add(tkFromJspellAnswer(lexema, s));
        }
        return mp.toArray(new TokenMorph[0]);//coisa estranha para tipagem 
    }

    //Lê do Buffer de entrada (a input string)
    private String getLexemeFromBuf() {
        String lexema = "";
        char[] chlexema = new char[1];
        try {
            int acabou;
            do {
                acabou = br.read(chlexema, 0, 1);
                if (chlexema[0] != ' ' && acabou != -1)
                    lexema += chlexema[0];
            } while (chlexema[0] != ' ' && acabou != -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lexema.length() > 0 ? lexema : null;
    }

    private TokenMorph tkFromJspellAnswer(String lexeme, String originalDef) {
        Map<String, String> mapa = new HashMap<String, String>();
        boolean approx = false;
        String dicWord = null;

        //for 'processing' Jpell's answer:
        Pattern pattern = Pattern.compile(strmatcher);
        Matcher matcher = pattern.matcher(originalDef);

        //boolean found = false;
        while (matcher.find()) {
            String t = matcher.group(); //obtem strings do tipo "DN=s]" , "CAT=v," etc.            
            t = t.replaceAll("[,\\]]", ""); //obtem strings do tipo "DN=s"  , "CAT=v"  etc.
            t = t.toLowerCase(); //obtem strings do tipo "dn=s"  , "cat=v"  etc.

            int indice = t.indexOf('='); //used in line below            
            mapa.put(t.substring(0, indice), t.substring(indice + 1));//breaks strs in pairs like ("dn","s"),("cat,"v") etc.

            //found = true;
        }

        //checks if its an approx solution
        pattern = Pattern.compile(strmatcherapprox);
        matcher = pattern.matcher(originalDef);

        if (matcher.find())
            approx = true;

        pattern = Pattern.compile(strmatcherdicword);
        matcher = pattern.matcher(originalDef);

        if (matcher.find())
            dicWord = matcher.group(1);

        return new TokenMorph(lexeme, originalDef, dicWord, mapa, approx);
    }
}