package br.usp.language.morph;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.Set;

/**
 * This class provides more information about Portuguese, which not present in JSpell.
 * 
 * @author mlk
 * 
 */
public class PortugueseAnalyser extends AnalisadorMorfologico {

    private static final String ADVDEGPOSTAG = "ADV-INT-MAIS";
    private static final String ADVDEGNEGTAG = "ADV-INT-MENOS";
    private static final String ADVMANFASTTAG = "ADV-MODO-RAPIDO";
    private static final String ADVMANSLOWTAG = "ADV-MODO-DEVAGAR";
    private static final String VLIG = "VERBOS-LIGACAO";

    private Set<String> advDegPos;
    private Set<String> advDegNeg;
    private Set<String> advManFast;
    private Set<String> advManSlow;
    private Set<String> linkingVerbs;

    /**
     * @param dic dictionary file of Portuguese
     * @param list filename with list of categorized words, which should be added to JSpell's analysis
     * @throws IOException
     */
    public PortugueseAnalyser(String dic, String list) throws IOException {
        super(dic);
        this.advDegPos = new HashSet<String>();
        this.advDegNeg = new HashSet<String>();
        this.advManFast = new HashSet<String>();
        this.advManSlow = new HashSet<String>();
        this.linkingVerbs = new HashSet<String>();
        BufferedReader reader = new BufferedReader(new FileReader(list));
        int readingGroup = 0;
        while (reader.ready()) {
            String line = reader.readLine().trim();
            if (line.equals(ADVDEGPOSTAG)) {
                readingGroup = 1;
            } else if (line.equals(ADVDEGNEGTAG)) {
                readingGroup = 2;
            } else if (line.equals(ADVMANFASTTAG)) {
                readingGroup = 3;
            } else if (line.equals(ADVMANSLOWTAG)) {
                readingGroup = 4;
            } else if (line.equals(VLIG)) {
                readingGroup = 5;
            } else if (!line.equals("") && !line.startsWith("#")) {
                switch (readingGroup) {
                    case 1:
                        advDegPos.add(line);
                        break;
                    case 2:
                        advDegNeg.add(line);
                        break;
                    case 3:
                        advManFast.add(line);
                        break;
                    case 4:
                        advManSlow.add(line);
                        break;
                    case 5:
                        linkingVerbs.add(line);
                        break;
                    default:
                }
            }
        }
    }

    @Override
    public TokenMorph getNextToken() {
        return getNextTokens()[0];
    }

    @Override
    public TokenMorph[] getNextTokens() {

        TokenMorph[] nextTokens = super.getNextTokens();

        for (TokenMorph nextToken : nextTokens) {
            String cat = nextToken.getType();
            if (cat.equals(Tags.ADVERB)) {
                String lexeme = nextToken.getLexeme();
                if (advDegPos.contains(lexeme)) {
                    nextToken.setProperty(Tags.ADV_TYPE_KEY, Tags.ADV_DEG_POS);
                } else if (advDegNeg.contains(lexeme)) {
                    nextToken.setProperty(Tags.ADV_TYPE_KEY, Tags.ADV_DEG_NEG);
                } else if (advManFast.contains(lexeme)) {
                    nextToken.setProperty(Tags.ADV_TYPE_KEY, Tags.ADV_MAN_FAST);
                } else if (advManSlow.contains(lexeme)) {
                    nextToken.setProperty(Tags.ADV_TYPE_KEY, Tags.ADV_MAN_SLOW);
                }
            } else if (cat.equals(Tags.VERB)) {
                String infinitive = nextToken.getDicWord();
                if (linkingVerbs.contains(infinitive)) {
                    // Changes category of the token
                    nextToken.setProperty(Tags.CATEGORY, Tags.LINKINGVERB);
                    
                    //TODO Criar novo token e acrescentar ao inves de modificar
                }
            }
        }

        return nextTokens;
    }
}
