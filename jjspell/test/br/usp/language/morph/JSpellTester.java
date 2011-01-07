package br.usp.language.morph;

import java.io.StringReader;

public class JSpellTester {

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        String texto = new String("vi");
        AnalisadorMorfologico amor = new AnalisadorMorfologico("./resources/port");

        amor.setInput( new StringReader(texto));
     
        TokenMorph[] atm; 
        
        for(int i=0;i<9;i++){
            
            if(amor.hasMoreTokens()){
                atm = amor.getNextTokens();
                for(TokenMorph tm: atm){
                    System.out.println(tm.toString());
                }
            }
        }
    }
}
