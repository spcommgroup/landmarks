package timit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import datastructures.Sentences;

/**
 * A subclass of Sentences that parses the sentences from the source text file listing
 * all the TIMIT sentences.
 * @author Jason Paller-Rzepka
 */
public class TimitSentences extends Sentences {

    
    public TimitSentences() throws IOException {
        sentences = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader("src/english/timitsent.txt"));
        String line;
        while ((line = br.readLine()) != null){
            if (line.startsWith(";")){
                continue;
            }
            line = line.trim();
            sentences.add(line.replaceAll("^(.*)\\s+\\(.*\\)$", "$1"));
        }
        br.close();  
    }

}
