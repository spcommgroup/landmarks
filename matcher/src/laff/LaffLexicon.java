package laff;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import datastructures.Lexicon;
/**
 * A Lexicon containing only the words in the LAFF sentences.
 * This lexicon takes a superset lexicon, and selects only the words present in the LAFF
 * text file to be part of the LaffLexicon.
 * 
 * @author Jason Paller-Rzepka
 *
 */
public class LaffLexicon extends Lexicon{

    public LaffLexicon(Lexicon superLexicon, String laffWordsListFileName) throws IOException {
        super();
        BufferedReader br = new BufferedReader(new FileReader(laffWordsListFileName));
        String line;
        while ((line = br.readLine()) != null){
            line = line.replace("\n", "").replace("\r", "");
            if (superLexicon.hasWord(line)){
                lexicon.put(line, superLexicon.getPhoneList(line));
            } else {
                //LOG:
                System.out.println("Cannot find entry for " + line);
            }
        }
        br.close();        
    }
}
