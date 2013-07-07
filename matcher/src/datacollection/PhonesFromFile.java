package datacollection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import matcher.Matcher;
import matcher.CMULexicon;

import datastructures.FeatureSet;
import datastructures.FeatureSetLookup;
import datastructures.Lexicon;
import datastructures.Matching;
import datastructures.Ranking;
import datastructures.ReducedFeatureSetLookup;

/**
 * Reads phones from a file (.lm file format) and attempts to match.
 * @author Jessica Kenney
 *
 */
public class PhonesFromFile {

    public static void main(String[] args) throws IOException{
        //create the lexicon (from the file timitdict.txt)
        Lexicon lexicon = new CMULexicon("src/english/Conv07_lexicon.txt");
        //creating the matcher, based off of that lexicon.
        Matcher matcher = new Matcher(lexicon);
        
        //Read the phone sequence from file
        BufferedReader br = new BufferedReader(new FileReader("src/english/Conv07_phones.lm"));
        List<String> phones = new ArrayList<String>();
        List<Float> phonesTimes = new ArrayList<Float>(); //Start time of each phone in phones
        String line;
        while ((line = br.readLine()) != null){
            if (line.startsWith("#")){
                continue;
            }
            //System.out.println(line);
            line = line.replace("\n", "").replace("\r", "");
            String[] parts = line.split(" ");
            //System.out.println(parts[0]);
            //System.out.println(parts[1]);
            if (parts.length == 2){
                phones.add(parts[1].replaceAll("[0-9]","").toLowerCase());
            }
            else {
            	phones.add("");
            }
            phonesTimes.add(Float.parseFloat(parts[0]));
        }
        br.close();
        
        //Read the words sequence from file (same as above)
        br = new BufferedReader(new FileReader("src/english/Conv07_words.lm"));
        List<String> words = new ArrayList<String>();
        List<Float> wordsTimes = new ArrayList<Float>();
        while ((line = br.readLine()) != null){
            if (line.startsWith("#")){
                continue;
            }
            //System.out.println(line);
            line = line.replace("\n", "").replace("\r", "");
            String[] parts = line.split(" ");
            //System.out.println(parts[0]);
            //System.out.println(parts[1]);
            if (parts.length == 2 && !parts[1].startsWith("<")){
                words.add(parts[1].toLowerCase());
                wordsTimes.add(Float.parseFloat(parts[0]));
            }
//            else {
//            	words.add("");
//            }
//            wordsTimes.add(Float.parseFloat(parts[0]));
//            System.out.println(parts[1]+ " " + parts[0]);
        }
        br.close();
        
        //Split into "phone groups", separating by spaces in the phone list
        //So instead of running the matcher on the entire file, it splits up
        //into smaller, more manageable chunks
        List<ArrayList<String>> phoneGroups = new ArrayList<ArrayList<String>>();
        ArrayList<String> phoneGroup = new ArrayList<String>();
        List<Float> phoneGroupsStartTimes = new ArrayList<Float>();
        List<ArrayList<String>> wordGroups = new ArrayList<ArrayList<String>>();
        ArrayList<String> wordGroup = new ArrayList<String>();
        int currentPhoneIndex = 0;
//        System.out.println("phones = "+phones);
        for (String phone : phones){
//        	System.out.println("phone = "+phone);
        	if (phone.trim()=="" && phoneGroup.size()>0){
        		//System.out.println("Adding wordgroup to groups");
        		//System.out.println(wordGroup);
        		phoneGroups.add(phoneGroup);
        		phoneGroupsStartTimes.add(phonesTimes.get(currentPhoneIndex));
        		//wordGroups.add(wordGroup);
                ArrayList<String> newPhoneGroup = new ArrayList<String>();
                phoneGroup = newPhoneGroup;
                //ArrayList<String> newWordGroup = new ArrayList<String>();
                //wordGroup = newWordGroup;
                //currentWordIndex++;
        	}
        	else if (phone.trim()!="") {
        		//System.out.println("Adding phone to group");
        		phoneGroup.add(phone);
        		//System.out.println("cwi = "+currentWordIndex + "; cpi = "+currentPhoneIndex);
        		//System.out.println("wt = "+wordsTimes.get(currentWordIndex)+"; pt = "+phonesTimes.get(currentPhoneIndex));
	        	/*if (wordsTimes.size() >= currentWordIndex+2 &&
	        		phonesTimes.size() >= currentPhoneIndex+2 && 
	        		wordsTimes.get(currentWordIndex+1) <= phonesTimes.get(currentPhoneIndex+1)){
	        		//If the next words starts at or before the next phoneme, the next phoneme
	        		//starts a new word.
	        		wordGroup.add(words.get(currentWordIndex));
	        		currentWordIndex++;
	        	}*/
        	}
        	if(currentPhoneIndex+1==phones.size()){
        		//End of loop. add last phonegroup
        		phoneGroups.add(phoneGroup);
        	}
        	currentPhoneIndex++;
        }
        int currentWordIndex = 0;
        int nextPhoneTimesIndex = 0;
        for (String word : words)  {
//        	System.out.println("word: "+word);
//        	System.out.println("word time: "+wordsTimes.get(currentWordIndex));
//    		if(phoneGroupsStartTimes.size() > nextPhoneTimesIndex) {
//    			System.out.println("Next phrase start time: "+phoneGroupsStartTimes.get(nextPhoneTimesIndex));
//    		}
    		if(phoneGroupsStartTimes.size() > nextPhoneTimesIndex && 
        	   wordsTimes.get(currentWordIndex)>= phoneGroupsStartTimes.get(nextPhoneTimesIndex)){
//        		System.out.println("Starting new wordgroup");
    			wordGroups.add(wordGroup);
        		ArrayList<String> newWordGroup = new ArrayList<String>();
        		wordGroup = newWordGroup;
        		nextPhoneTimesIndex++;
        	}
        	if(word.trim()==""){
//        		System.out.println("Skipping blank word");
        		continue;
        	}
        	wordGroup.add(word);
//        	System.out.println("Word group: "+wordGroup);
        	if(currentWordIndex+1==words.size()){
        		wordGroups.add(wordGroup);
        	}
        	currentWordIndex++;
        }
//        System.out.println(wordsTimes);
//        System.out.println(phoneGroups);
//        System.out.println(wordGroups);
//        System.out.println(phoneGroupsStartTimes);
//        System.out.println(phoneGroups);
        List<Double> phraseRanks = new ArrayList<Double>();
        int currentPhonePhraseIndex = 0;
//        int[] matchingRanks = {0,0,0,0,0};
        for (ArrayList<String> phonePhrase : phoneGroups){
        	//System.out.println(phonePhrase);
        	if(phonePhrase.size() > 0){
		        //Get FeatureSet sequence from phones list
		        List<FeatureSet> featureSetSequence = new ArrayList<FeatureSet>();
		        for (String phone : phonePhrase){
		            for (FeatureSet fs : FeatureSetLookup.lookup(phone)){
		                featureSetSequence.add(fs);
		            }
		        }
		        
		        //Get some predicted matchings.
		        //System.out.print("Running matcher on ");
		        //System.out.print(phoneGroup);
		        //System.out.print("....\n");
		        List<Matching> matchings = matcher.match(featureSetSequence, 0.1f);
		        //System.out.println("Finished matcher");
		        //System.out.println(matchings);
//		        System.out.print("Matched "+wordGroups.get(currentPhonePhraseIndex)+" as ");
//		        
//		        
//		        //Print best matching
//		        for (Ranking r : matchings.get(0).getRankings()){
//		        	System.out.print(r.getBestProbabilitySet().getWords());
//		        }
//		        System.out.println("\n");
//		        System.out.print("Word group: ");
//		        for (String word : wordGroups.get(currentPhonePhraseIndex)) {
//		        	System.out.print(word + " ");
//		        }
		        
		        Matching bestMatching = null;
		        Double bestRank = 0.0;
		        int matchingsChecked = 0;
	        	List<String> correctWords = wordGroups.get(currentPhonePhraseIndex);
//	        	System.out.println("Matches for "+correctWords+":");
	        	MatchLoop:
		        for (Matching m : matchings){
		        	if (matchingsChecked >= 5) {
		        		//If you checked the top 5 or if the next best match can't do better than
		        		// what you already have
		        		break;
		        	}
//		        	float thisRank = 0.0f;
		        	int cwi = 0; //currentWordIndex
//		        	for (Ranking r : m.getRankings()){
//		        		System.out.println("\t"+r.getBestProbabilitySet().getWords());
//		        	}
		        	for (Ranking r : m.getRankings()){//each word in a phrase
		        		for (String word : r.getBestProbabilitySet().getWords()){
//		        			if (correctWords.size() > cwi && correctWords.get(cwi)==word){
//		        				thisRank += 1.0f/matchWords.size(); //Perfect match for this word
//		        				break;
//		        			}
//		        			else if(correctWords.contains(word)){
//		        				for (int index = correctWords.indexOf(word);
//		        					     index >= 0;
//		        					     index = correctWords.subList(index+1, correctWords.size()).indexOf(word))
//		        					{ //Finds the first instance of "word" after cwi
//		        					    if (index > cwi){
//		        					    	cwi = index;
//		        					    	thisRank += 1.0f/matchWords.size();
//		        					    	break;
//		        					    }
//		        					}
//		        			}
//		        			System.out.println("Checking "+ word + ".equals("+correctWords.get(cwi)+")");
		        			if (correctWords.size() > cwi && !word.equals(correctWords.get(cwi))){
//		        				System.out.println("match fail. "/*+correctWords.get(cwi)+" != "+word*/);
		        				matchingsChecked++;
		        				continue MatchLoop;
		        			}
		        			cwi++;
		        		}
		        	}
		        	bestRank = 1.0 - 0.2 * matchingsChecked;
//		        	matchingRanks[matchingsChecked]++;
//		        	System.out.println("MatchingsChecked = "+matchingsChecked);
//		        	System.out.println("Rank = "+bestRank);
		        	bestMatching = m;
		        	
//		        	thisRank *= m.getBestProbability();
//		        	if (thisRank > bestRank){
//		        		bestRank = thisRank;
//		        		bestMatching = m;
//		        	}
		        	break MatchLoop;
		        }
		        if(bestMatching==null){
		        	System.out.println("No best matching found for "+correctWords);
		        	phraseRanks.add(0.0);
		        } else {
		        	System.out.println(correctWords);
			        System.out.println("Best matching: "+bestRank);
			        for (Ranking r : bestMatching.getRankings()){
			        	System.out.print(r.getBestProbabilitySet().getWords());
			        }
			        System.out.println("\n");
		        	phraseRanks.add(bestRank);
		        }
        	}
        	currentPhonePhraseIndex++;
        }
        Double rankSum = 0.0;
        for (Double rank : phraseRanks) {
        	rankSum += rank;
        }
        Double totalRank = rankSum / phraseRanks.size();
        System.out.println(totalRank);
        
//        for (int i=0; i<5; i++) {
//        	System.out.println("Correct matchings ranked "+(i+1)+": "+((matchingRanks[i]+0.0f)/phoneGroups.size()));
//        }
        //Present all the matchings to the user.
//        for (Matching m : matchings){
//            System.out.println(String.format("Matching (p = %f):",m.getBestProbability()));
//            for (Ranking r : m.getRankings()){
//                //The most probable words from each ranking in the matching are the ones we want to
//                //display when we represent the matching for the user.
//                System.out.print(r.getBestProbabilitySet().getWords());
//            }
//            System.out.println();
//        }
    }

}
