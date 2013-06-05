package laff;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import datastructures.Sentences;

/**
 * A subclass of Sentences that parses the sentences from the source text file listing
 * all the LAFF sentences.
 * @author Jason Paller-Rzepka
 */
public class LaffSentences extends Sentences{

    public LaffSentences() throws IOException {
        sentences = new ArrayList<String>();
        BufferedReader br = new BufferedReader(new FileReader("src/english/laffsent.txt"));
        String line;
        while ((line = br.readLine()) != null){
            line = line.replace("\n", "").replace("\r", "");
            sentences.add(line.split(" ", 2)[1]);
        }
        br.close();
    }
}
