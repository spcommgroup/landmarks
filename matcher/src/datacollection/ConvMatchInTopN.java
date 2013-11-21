package datacollection;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import matcher.Matcher;
import matcher.CMULexicon;

import datastructures.FeatureSet;
import datastructures.FeatureSetLookup;
import datastructures.Lexicon;
import datastructures.Matching;
import datastructures.Pair;
import datastructures.Phone;
import datastructures.PhonePhrase;
import datastructures.Ranking;
import datastructures.ReducedFeatureSetLookup;
import datastructures.WordPhrase;

/**
 * Reads phones from a file (.lm file format) and attempts to match.
 * @author Jessica Kenney
 *
 */
public class ConvMatchInTopN {
	private static double averageLength(List<PhonePhrase> list){
		int sum = 0;
		for (PhonePhrase sublist : list){
			sum += sublist.size();
		}
		return (sum + 0.0)/list.size();
	}
    public static void main(String[] args) throws IOException{
      final int N_TOPS = 5;
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
//    		continue;
    	}
    	
    	Pair<List<PhonePhrase>, List<WordPhrase>> PhnWrdPair = PhonesFromFile.read(convSrc+"_phones.lm", convSrc+"_words.lm");
    	List<PhonePhrase> phonePhrases = PhnWrdPair.first;
    	List<WordPhrase> wordPhrases = PhnWrdPair.second;
//    	System.out.println("Matching "+convName+"...");
        //create the lexicon (from the file timitdict.txt)
        Lexicon lexicon = new CMULexicon(convSrc+"_lexicon.txt");
//        Lexicon lexicon = new CMULexicon("src/matcher_data/conv_all_lexicon.txt");
        //creating the matcher, based off of that lexicon.
        Matcher matcher = new Matcher(lexicon);

        List<Double> phraseRanks = new ArrayList<Double>();
        double[] matchInTopN = new double[N_TOPS];
        int currentPhonePhraseIndex = 0;
        for (PhonePhrase phonePhrase : phonePhrases){
        	if(phonePhrase.size() > 0){

		        List<Matching> matchings = matcher.match(phonePhrase.featureSetSequence(), 0.1f);
		        

		        Matching bestMatching = null;
		        double bestRank = 0.0;
		        int bestRankMatchRank = 0;
		        int matchingsChecked = 0;
		        WordPhrase correctWordPhrase = wordPhrases.get(currentPhonePhraseIndex);
		        while (correctWordPhrase.size()==0){
		        	currentPhonePhraseIndex++;
		        	correctWordPhrase = wordPhrases.get(currentPhonePhraseIndex);
		        }
//	        	System.out.print("phonePhrase: ");
//	        	phonePhrase.print();
//	        	System.out.print("wordPhrase: ");
//	        	correctWordPhrase.print();
		        
		        MeasureCohortSize.cohortSize(matchings, phonePhrase, lexicon, true, "none");

		        
	        	MatchLoop:
		        for (Matching m : matchings){
//		        	System.out.println("New match for "+correctWords);
		        	double thisRank = 0.0;
		        	if (matchingsChecked >= 5 || 1.0 - bestRank < 0.0001 || 1.0 - matchingsChecked * 0.2 < bestRank) {
		        		//If you checked the top 5 or if the next best match can't do better than
		        		// what you already have

		        		thisRank = 0.0;
		        		break;
		        	}
		        	int cwi = 0; //currentWordIndex
		        	boolean perfect = true;

		        	WordLoop:
		        	for (Ranking r : m.getRankings()){//each word in a phrase

		        		if(correctWordPhrase.size() > cwi &&
	        			   r.getBestProbabilitySet().getWords().contains(correctWordPhrase.phrase.get(cwi).value)){
		        			thisRank += 1.0/correctWordPhrase.size();
	        			} else {
	        				perfect=false;
	        			}

			        	cwi++;
		        	}
		        	if(perfect && matchInTopN.length > matchingsChecked){
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
        tab_sep_vals += convName + "\t" + result + "\t" + averageLength(phonePhrases) + "\t";
//        System.out.println(matchInTopN);
        double sumOfRanks = 0;
        for (int i=0; i < N_TOPS; i++){
            sumOfRanks += matchInTopN[i];

            NumberFormat percentRank = NumberFormat.getPercentInstance();
            percentRank.setMaximumFractionDigits(1);

            String accumPercent = percentRank.format(sumOfRanks / phraseRanks.size());
            String inNPercent = percentRank.format(matchInTopN[i] / phraseRanks.size());

//        	System.out.println("\tPerfect match in top "+ (i+1) + ": " + accumPercent);
        	tab_sep_vals += accumPercent + "\t";
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

//    System.out.println(headers);
//      System.out.print(tab_sep_vals);
    }

}
