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

public class DecemberASAExperiments {
	private static double averageLength(List<PhonePhrase> list){
		int sum = 0;
		for (PhonePhrase sublist : list){
			sum += sublist.size();
		}
		return (sum + 0.0)/list.size();
	}
    public static void main(String[] args) throws IOException{
    	/*Config info starts here!*/
      String[] convs = {"src/english/Conv07","src/matcher_data/conv09g_jessk"};
//      String[] lexicons = {"src/matcher_data/conv_all_lexicon.txt"};
      String[] lexicons = {convs[0]+"_lexicon.txt", convs[1]+"_lexicon.txt"};
      for (int i=0; i<convs.length; i++){
    	String conv = convs[i];
    	String conv_words = conv + "_words.lm";
    	String conv_lms = conv + "_landmarks_spaces.lm";
    	String conv_phones = conv + "_phones.lm";
    	String conv_lexicon = conv + "_lexicon.txt";
    	String type = "V";
//    	boolean reduced = true;
    	boolean[] tf = {true, false};
    	/*Config info ends here!*/
    	for (boolean reduced : tf) {
    	for (String convSrc : new String[]{conv_lms, conv_phones}) {
    		Pair<List<PhonePhrase>, List<WordPhrase>> PhnWrdPair;
    		if(convSrc==conv_lms){
    			if(!reduced){
    				continue;
    			}
    			PhnWrdPair = PhonesFromFile.read(convSrc, conv_words, "landmarks");
    		} else {
    			PhnWrdPair = PhonesFromFile.read(convSrc, conv_words, "phones");
    		}
	    	List<PhonePhrase> phonePhrases = PhnWrdPair.first;
	    	List<WordPhrase> wordPhrases = PhnWrdPair.second;

	    	//TODO: check if lexicon should be timit
	        Lexicon lexicon = new CMULexicon(conv_lexicon);
	        Matcher matcher = new Matcher(lexicon);
	        

//    		ArrayList<List<FeatureSet>> featureSets = new ArrayList();
//    		featureSets.add(phonePhrase.VGCfeatureSet());
	
	        List<Double> phraseRanks = new ArrayList<Double>();
	        List<Integer> cohortSizes = new ArrayList<Integer>();
	        List<Float> matchConfidences = new ArrayList<Float>();

	        int currentPhonePhraseIndex = 0;
	        for (PhonePhrase phonePhrase : phonePhrases){
	        	if(phonePhrase.size() > 0){
	        		
	        		List<Matching> matchings;
	        		if(reduced){
	        			matchings = matcher.match(phonePhrase.reducedFeatureSetSequence(type), 0.1f);
	        		}else{
	        			matchings = matcher.match(phonePhrase.featureSetSequence(), 0.1f);
	        		}
			        
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
			        
			        cohortSizes.add(MeasureCohortSize.cohortSize(matchings, phonePhrase, lexicon, reduced, type));
			        boolean firstMatch = false;
		        	MatchLoop:
			        for (Matching m : matchings){
			        	if(!firstMatch){
					        matchConfidences.add(m.getBestProbability());
				        	firstMatch = true;
			        	}
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
		        			} 
			        		else {perfect=false;}
				        	cwi++;
			        	}
			        	thisRank = thisRank * (1.0 - 0.2 * matchingsChecked);
			        	if(thisRank > bestRank){
			        		bestMatching = m;
			        		bestRank = thisRank;
			        		bestRankMatchRank = matchingsChecked;
			        	}
			        	matchingsChecked++;
			        }
		        	phraseRanks.add(bestRank);
	        	}
	        	currentPhonePhraseIndex++;
	        }
	        
	        //Finding average values:
	        Double rankSum = 0.0;
	        for (Double rank : phraseRanks) {
	        	rankSum += rank;
	        }
	        Double totalRank = rankSum / phraseRanks.size();
	        Integer cohortSum = 0;
	        for (Integer cohort : cohortSizes){
	        	cohortSum += cohort;
	        }
	        Double avgCohort = (0.0 + cohortSum)/cohortSizes.size();
	        Float matchConfSum = 0.0f;
	        for (Float matchConf : matchConfidences){
	        	matchConfSum += matchConf;
	        }
	        Float avgMatchConf = matchConfSum/matchConfidences.size();
	
	        NumberFormat percentFormat = NumberFormat.getPercentInstance();
	        percentFormat.setMaximumFractionDigits(3);
	        String result = percentFormat.format(totalRank);
	        if(reduced){
	        System.out.println(convSrc + "(" + type + "): " + result);
	        } else {
		        System.out.println(convSrc +": " + result);
	        }
	        System.out.println("Match confidence: "+avgMatchConf);
	        System.out.println("Average cohort size: "+avgCohort);
//	        System.out.printf("Avg phone group size: %.2f%n",averageLength(phonePhrases));
	//        System.out.printf("Avg word group size: %.2f%n",averageLength(wordGroups));
	        System.out.println(" ");

	      }
    	}
      }
    }

}
