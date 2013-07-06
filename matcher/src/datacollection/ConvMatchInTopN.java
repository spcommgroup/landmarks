package datacollection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import matcher.Matcher;
import matcher.CMULexicon;

import datastructures.FeatureSet;
import datastructures.FeatureSetLookup;
import datastructures.Lexicon;
import datastructures.Matching;
import datastructures.Ranking;

/**
 * Reads phones from a file (.lm file format) and attempts to match.
 * @author Jessica Kenney
 *
 */
public class ConvMatchInTopN {
	private static double averageLength(List<ArrayList<String>> list){
		int sum = 0;
		for (ArrayList<String> sublist : list){
			sum += sublist.size();
		}
		return (sum + 0.0)/list.size();
	}
    public static void main(String[] args) throws IOException{
      final int N_TOPS = 3;
      String tab_sep_vals = "";
      for (int conv = 1; conv < 17; conv++){
    	String convName;
    	String convSrc;
    	if(conv==7){
    		convName = "Conv07";
    		convSrc = "src/english/"+convName;
    	} else {
    		convName = "conv" + String.format("%02d", conv) + "g";
    		convSrc = "src/matcher_data/"+convName;
    	}
//    	System.out.println("Matching "+convName+"...");
        //create the lexicon (from the file timitdict.txt)
        Lexicon lexicon = new CMULexicon(convSrc+"_lexicon.txt");
        //Lexicon lexicon = new CMULexicon("src/matcher_data/conv_all_lexicon.txt");
        //creating the matcher, based off of that lexicon.
        Matcher matcher = new Matcher(lexicon);

        //Read the phone sequence from file
        BufferedReader br = new BufferedReader(new FileReader(convSrc+"_phones.lm"));
        List<String> phones = new ArrayList<String>();
        List<Float> phonesTimes = new ArrayList<Float>(); //Start time of each phone in phones
        String line;
        while ((line = br.readLine()) != null){
            if (line.startsWith("#")){
                continue;
            }
            line = line.replace("\n", "").replace("\r", "");
            String[] parts = line.split(" ");
            if (parts.length == 2 && !parts[1].startsWith("#")){
                phones.add(parts[1].replaceAll("[0-9]","").toLowerCase());
            }
            else {
            	phones.add("");
            }
            phonesTimes.add(Float.parseFloat(parts[0]));
        }
        br.close();

        //Read the words sequence from file (same as above)
        br = new BufferedReader(new FileReader(convSrc+"_words.lm"));
        List<String> words = new ArrayList<String>();
        List<Float> wordsTimes = new ArrayList<Float>();
        while ((line = br.readLine()) != null){
            if (line.startsWith("#")){
                continue;
            }
            line = line.replace("\n", "").replace("\r", "");
            String[] parts = line.split(" ");
            if (parts.length == 2 && !parts[1].startsWith("<") && !parts[1].startsWith("#") &&!parts[1].startsWith("?")){
                words.add(parts[1].toLowerCase());
                wordsTimes.add(Float.parseFloat(parts[0]));
            }
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
        for (String phone : phones){
        	if (phone.trim()=="" && phoneGroup.size()>0){
        		phoneGroups.add(phoneGroup);
        		phoneGroupsStartTimes.add(phonesTimes.get(currentPhoneIndex));
                ArrayList<String> newPhoneGroup = new ArrayList<String>();
                phoneGroup = newPhoneGroup;
        	}
        	else if (phone.trim()!="") {
        		phoneGroup.add(phone);
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
    		if(phoneGroupsStartTimes.size() > nextPhoneTimesIndex &&
        	   wordsTimes.get(currentWordIndex)>= phoneGroupsStartTimes.get(nextPhoneTimesIndex)){
    			wordGroups.add(wordGroup);
        		ArrayList<String> newWordGroup = new ArrayList<String>();
        		wordGroup = newWordGroup;
        		nextPhoneTimesIndex++;
        	}
        	if(word.trim()==""){
        		continue;
        	}
        	wordGroup.add(word);
        	if(currentWordIndex+1==words.size()){
        		wordGroups.add(wordGroup);
        	}
        	currentWordIndex++;
        }
//        System.out.println(phoneGroups);
//        System.out.println(wordGroups);
        List<Double> phraseRanks = new ArrayList<Double>();
        double[] matchInTopN = new double[N_TOPS];
        int currentPhonePhraseIndex = 0;
        for (ArrayList<String> phonePhrase : phoneGroups){
        	if(phonePhrase.size() > 0){
		        //Get FeatureSet sequence from phones list
		        List<FeatureSet> featureSetSequence = new ArrayList<FeatureSet>();
		        for (String phone : phonePhrase){
		          try {
		            for (FeatureSet fs : FeatureSetLookup.lookup(phone)){
		                featureSetSequence.add(fs);
		            }
		          } catch (NullPointerException e) {
		        	  System.out.println("Phone not found! "+ phone);
		        	  throw e;
		          }
		        }

		        List<Matching> matchings = matcher.match(featureSetSequence, 0.1f);

		        Matching bestMatching = null;
		        double bestRank = 0.0;
		        int bestRankMatchRank = 0;
		        int matchingsChecked = 0;
	        	List<String> correctWords = wordGroups.get(currentPhonePhraseIndex);
//	        	System.out.println("correctWords = "+correctWords);
	        	MatchLoop:
		        for (Matching m : matchings){
//		        	System.out.println("New match for "+correctWords);
		        	double thisRank = 0.0;
		        	if (matchingsChecked >= 5 || 1.0 - bestRank < 0.0001 || 1.0 - matchingsChecked * 0.2 < bestRank) {
		        		//If you checked the top 5 or if the next best match can't do better than
		        		// what you already have
//		        		System.out.println("Stopping checking. mc="+matchingsChecked+"; br="+bestRank);
		        		thisRank = 0.0;
		        		break;
		        	}
		        	int cwi = 0; //currentWordIndex
		        	boolean perfect = true;

		        	WordLoop:
		        	for (Ranking r : m.getRankings()){//each word in a phrase
//		        		if(correctWords.size() > cwi) {
//		        			System.out.println(correctWords.get(cwi)+
//		        					" " + r.getBestProbabilitySet().getWords());
//		        		}
//		        		System.out.println("thisRank = "+thisRank + "; bestRank = "+bestRank);
//		        		System.out.println("Ranking: "+ r.getBestProbabilitySet().getWords());
//	        			boolean goodLength = (correctWords.size() > cwi);
//		        		if (goodLength) {System.out.println(r.getBestProbabilitySet().getWords().contains(correctWords.get(cwi)));}

		        		if(correctWords.size() > cwi &&
	        			   r.getBestProbabilitySet().getWords().contains(correctWords.get(cwi))){
		        			thisRank += 1.0/correctWords.size();
//		        			System.out.println("thisRank increases by "+1.0/correctWords.size());
	        			} else {
	        				perfect=false;
	        			}
//		        		for (String word : r.getBestProbabilitySet().getWords()){
//		        			//each word in a group of equal match (e.g. [to, two, too]
//		        			try {
//		        			System.out.print("["+word + "] should be ["+correctWords.get(cwi)+"]");
//		        			} catch (IndexOutOfBoundsException e) {}
//		        			if (correctWords.size() > cwi && !word.equals(correctWords.get(cwi))){
//		        				bestRank -= 1/correctWords.size();
//		        				matchingsChecked++;
//		        				System.out.print(" but it's not \n");
//		        				continue WordLoop;
//		        			} else{ System.out.print("\n"); }
//		        			cwi++;
//		        		}System.out.print("\n");
			        	cwi++;
		        	}
		        	if(perfect){
		        		matchInTopN[matchingsChecked]+=1;
		        	}
		        	thisRank = thisRank * (1.0 - 0.2 * matchingsChecked);
		        	if(thisRank > bestRank){
		        		bestMatching = m;
		        		bestRank = thisRank;
		        		bestRankMatchRank = matchingsChecked;
		        	}
//		        	System.out.println("thisRank = "+thisRank);
//		        	System.out.println("bestRank = "+bestRank);
		        	matchingsChecked++;
		        }
		        if(bestMatching==null){
//		        	System.out.println("No best matching found for "+correctWords);
//		        	bestRank = 0.0;
		        } else {
//		        	System.out.println(correctWords);
//			        System.out.println("Best matching: "+bestRank+" at rank "+bestRankMatchRank);
//			        for (Ranking r : bestMatching.getRankings()){
//			        	System.out.print(r.getBestProbabilitySet().getWords());
//			        }
//			        System.out.println("\n");
		        }
	        	phraseRanks.add(bestRank);

        	}
        	currentPhonePhraseIndex++;
        }
        Double rankSum = 0.0;
        for (Double rank : phraseRanks) {
        	rankSum += rank;
        }
        Double totalRank = rankSum / phraseRanks.size();

        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        percentFormat.setMaximumFractionDigits(3);
        String result = percentFormat.format(totalRank);
        System.out.println(convName + ": " + result);
//        System.out.printf("Avg phone group size: %.2f%n",averageLength(phoneGroups));
//        System.out.printf("Avg word group size: %.2f%n",averageLength(wordGroups));
        tab_sep_vals += convName + "\t" + result + "\t" + averageLength(phoneGroups) + "\t";
//        System.out.println(matchInTopN);
        double sumOfRanks = 0;
        for (int i=0; i < N_TOPS; i++){
        	sumOfRanks+=matchInTopN[i];
            NumberFormat percentRank = NumberFormat.getPercentInstance();
            percentRank.setMaximumFractionDigits(1);
            String percent = percentRank.format(sumOfRanks/phraseRanks.size());
        	System.out.println("\tPerfect match in top "+ (i+1) + ": "+percent);
        	tab_sep_vals += percent + "\t";
        }
        tab_sep_vals += "\n";
      }
      //Tab-separated data for pasting into a spreadsheet
      String headers = "";
      headers += "Name\t";
      headers += "Rank\t";
      headers += "Avg Phone Group Length\t";

      if (N_TOPS > 0) {
          headers += "% Perfect Match in top 1\t";

          for (int i = 1; i < N_TOPS; i++) {
            headers += String.format("In top %d\t", i+1);
          }
      }

    System.out.println(headers);
      System.out.print(tab_sep_vals);
    }

}
