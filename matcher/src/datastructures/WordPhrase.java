package datastructures;

import java.util.ArrayList;

/**
 * An object that represents a word phrase, which is essentially a list of strings and a start time.
 * 
 * @author Jessica Kenney
 */
public class WordPhrase extends PhonePhrase {

	public WordPhrase() {
		this.phrase = new ArrayList<Phone>();
		this.time = 0.0f;
	}
	
	public String toString(){
		return this.phrase.toString();
	}
	
}