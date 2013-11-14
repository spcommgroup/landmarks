package matcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import datastructures.Lexicon;

/**
 * A {@code Lexicon} containing entries for all the data in a file
 * formatted like the CMU lexicon, cmudict.0.7a.
 * 
 * @author Jason Paller-Rzepka
 *
 */
public class CMULexicon extends Lexicon{

    public CMULexicon(String filename) throws IOException {
        super();
        BufferedReader br = new BufferedReader(new FileReader(filename));
        String line;
        while ((line = br.readLine()) != null){
            if (line.startsWith(";;;")){
                continue;
            }
            line = line.replace("\n", "").replace("\r", "");
            String[] parts = line.split("  ");
            if (parts.length != 2){
                br.close();
                System.out.println(line);
                throw new IOException("Malformed CMU dictionary.");
            }
            String[] phones = parts[1].split(" ");
            for (int i = 0; i < phones.length; i++){
                phones[i] = phones[i].replaceAll("[0-9]", "").toLowerCase();
            }
            lexicon.put(parts[0].toLowerCase(), Arrays.asList(phones));
        }
        br.close();
    }

}
