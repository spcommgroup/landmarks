package timit;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import datastructures.Lexicon;
/**
 * A Lexicon containing only the words in the TIMIT sentences.
 * This lexicon takes a superset lexicon, and selects only the words present in the TIMIT
 * text file to be part of the TimitLexicon.
 * 
 * @author Jason Paller-Rzepka
 *
 */
public class TimitLexicon extends Lexicon {

    public TimitLexicon(String dictFileName) throws IOException {
        super();
        BufferedReader br = new BufferedReader(new FileReader(dictFileName));
        String line;
        String pronunciation;
        List<String> phoneList;
        while ((line = br.readLine()) != null){
            if (line.startsWith(";")){
                //Ignore ;comments
                continue;
            }
            
            String[] entry = line.replace("\n","").replace("\r","").split("  ");
            if (entry.length != 2){
                br.close();
                throw new IOException("Malformatted Timit Lexicon! Line " + line + " is invalid");
            }
            pronunciation = entry[1].replaceAll("^\\s*/(.*)/\\s*$", "$1");
            
            
            phoneList = new ArrayList<String>();
            for (String phone : pronunciation.split(" ")){
                phone = phone.replaceAll("(.*)[1-2]$","$1");
                if (phone.equals("el")){
                    phoneList.add("ah");
                    phoneList.add("l");
                } else if (phone.equals("em")){
                    phoneList.add("ah");
                    phoneList.add("m");
                } else if (phone.equals("en")){
                    phoneList.add("ah");
                    phoneList.add("l");
                } else {
                    phoneList.add(phone);
                }
            }
            lexicon.put(entry[0], phoneList);   
        }
        br.close();        
    }
}
