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
	
    public static void cohortSize(List<Matching> matchings, PhonePhrase seq, Lexicon lexicon, boolean reduced) throws IOException{
      	
    	System.out.println("\nPhone Phrase: "+seq);
    	List<FeatureSet> featureSeq;
    	if(reduced){
    		featureSeq = seq.reducedFeatureSetSequence();
    	}else {
    		featureSeq = seq.featureSetSequence();
    	}
    	String sentence = "";
//    	int max_matchings = 5;
    	int cohort_size = 0;
    	int current_matchings = 0;
        for (Matching m : matchings){
        	//evaluate size of matching
        	int size = 1;
        	for (Ranking r : m.getRankings()){
        		sentence += r.getBestProbabilitySet().getWords().iterator().next() + " "; //construct sentence for lexicon lookup
        		System.out.print(r.getBestProbabilitySet().getWords());
        		size *= r.getBestProbabilitySet().getWords().size();
        	}

        	System.out.println("\nSize: "+size);
            System.out.println(String.format("Matching (p = %f):",m.getBestProbability()));
            
        	List<FeatureSet> correctSeq;
        	if(reduced){
        		correctSeq = lexicon.reducedFeatureSetSequence(sentence);
        	} else {
        		correctSeq = lexicon.featureSetSequence(sentence);
        	}
        	if(correctSeq == featureSeq){
        		cohort_size++;
        	} else {
        		System.out.println("Cohort size: "+cohort_size);
        		return;
        	}

        	current_matchings++;
        }
    }

}
