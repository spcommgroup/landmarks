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
 * Takes a {@Matching} and evaluates cohort size of a matched phrase based on variable parameters.
 * @author Jessica Kenney
 *
 */
public class MeasureCohortSize {
	
    public static int cohortSize(List<Matching> matchings, PhonePhrase seq, Lexicon lexicon, boolean reduced, String type) throws IOException{
      	
//    	System.out.println("\nPhone Phrase: "+seq);
    	List<FeatureSet> featureSeq;
    	if(reduced){
    		featureSeq = seq.reducedFeatureSetSequence(type);
    	}else {
    		featureSeq = seq.featureSetSequence();
    	}
//    	int max_matchings = 5;
    	int cohort_size = 0;
    	int current_matchings = 0;
        for (Matching m : matchings){
        	//evaluate size of matching
        	String sentence = "";
        	int size = 1;
        	for (Ranking r : m.getRankings()){
        		sentence += r.getBestProbabilitySet().getWords().iterator().next() + " "; //construct sentence for lexicon lookup
//        		System.out.print(r.getBestProbabilitySet().getWords());
        		size *= r.getBestProbabilitySet().getWords().size();
        	}

            
        	List<FeatureSet> correctSeq;
        	if(reduced){
        		correctSeq = lexicon.reducedFeatureSetSequence(sentence, type);
        	} else {
        		correctSeq = lexicon.featureSetSequence(sentence);
        	}
        	System.out.println("featureSeq = "+featureSeq);
        	System.out.println("correctSeq = "+correctSeq);
        	if(correctSeq != null && correctSeq.equals(featureSeq)){
//            	System.out.println("\nSize: "+size);
//                System.out.println(String.format("Matching (p = %f):",m.getBestProbability()));
        		cohort_size+= size;
        	}

        	current_matchings++;
        }
//		System.out.println("Cohort size: "+cohort_size);
		return cohort_size;
    }

}
