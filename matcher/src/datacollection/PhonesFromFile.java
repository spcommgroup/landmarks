package datacollection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import datastructures.Pair;
import datastructures.Phone;
import datastructures.PhonePhrase;
import datastructures.Word;
import datastructures.WordPhrase;

/**
 * Reads phones from a file (.lm file format) and attempts to match.
 * @author Jessica Kenney
 *
 */
public class PhonesFromFile {
	
	public static Pair<List<PhonePhrase>, List<WordPhrase>> read(String phonesPath, String wordsPath) throws IOException{
		return read(phonesPath, wordsPath, "phones");
	}

    public static Pair<List<PhonePhrase>, List<WordPhrase>> read(String phonesPath, String wordsPath, String type) throws IOException{
        
        BufferedReader br = new BufferedReader(new FileReader(phonesPath));
        List<Phone> phones = new ArrayList<Phone>();
        String line;
        String open = ""; //Open landmark. e.g. we read "Sc" and mark open="S" and close it on the next line.
        while ((line = br.readLine()) != null){
            if (line.startsWith("#") || line.startsWith("_")){
                continue;
            }
            line = line.replace("\n", "").replace("\r", "");
            String[] parts = line.split(" ");
            Phone phone;
            if (parts.length == 2 && !parts[1].startsWith("#") && !parts[1].startsWith("_")){
            	phone = new Phone(parts[1].replaceAll("[0-9]","").trim());
            	if(type=="landmarks"){
	            	if(phone.value.endsWith("c")){
	            		phone.value = phone.value.substring(0,1); // remove the "c" in "Sc"
	            		open = phone.value;
	            	} else {
	            		if (phone.value.endsWith("r")){
	            			if(open != "" && phone.value.startsWith(open)){
	            				open = "";
	            				continue; // this LM was open and now is closed. don't add release as a phone.
	            			} else {
	            				//this is a release but there was no closure. add it anyway (removing the 'r').
	            				phone.value = phone.value.substring(0,1);
	            			}
	            		}
	            		open = "";
	            	}
            	}
            }
            else {
            	phone = new Phone("");
            }
            phone.time = Float.parseFloat(parts[0]);
            phones.add(phone);
        }
        br.close();
        
        
        br = new BufferedReader(new FileReader(wordsPath));
        List<Word> words = new ArrayList<Word>();
        while ((line = br.readLine()) != null){
            if (line.startsWith("#") || line.startsWith("_")){
                continue;
            }
            line = line.replace("\n", "").replace("\r", "");
            String[] parts = line.split(" ");
            Word word;
            if (parts.length == 2 && !parts[1].startsWith("<") && !parts[1].startsWith("#") && !parts[1].startsWith("_") && !parts[1].startsWith("?")){
                word = new Word(parts[1].toLowerCase().trim());
            }
            else {
            	word = new Word("");
            }
            word.time = Float.parseFloat(parts[0]);
            words.add(word);
        }
        br.close();
        
       
        List<PhonePhrase> phonePhrases = new ArrayList<PhonePhrase>();
        PhonePhrase phonePhrase = new PhonePhrase();
        List<WordPhrase> wordPhrases = new ArrayList<WordPhrase>();
        WordPhrase wordPhrase = new WordPhrase();
        
        for (Phone phone : phones){
        	if (phone.value == "" && phonePhrase.size()>0){
        		phonePhrases.add(phonePhrase);
                PhonePhrase newPhonePhrase = new PhonePhrase();
                phonePhrase = newPhonePhrase;
        	}
        	else if (phone.value != "") {
        		phonePhrase.add(phone);
        	}
        }
        phonePhrases.add(phonePhrase);
        
        int nextPhonePhrase = 0;
        for (Word word : words)  {
    		if(phonePhrases.size() > nextPhonePhrase && 
        	   word.time >= phonePhrases.get(nextPhonePhrase).time){
    			wordPhrases.add(wordPhrase);
        		WordPhrase newWordPhrase = new WordPhrase();
        		wordPhrase = newWordPhrase;
        		nextPhonePhrase++;
        	}
        	if(word.value == ""){
        		continue;
        	}
        	wordPhrase.add(word);
        }
        wordPhrases.add(wordPhrase);
        
        return new Pair<List<PhonePhrase>, List<WordPhrase>>(phonePhrases, wordPhrases);
    }

}
