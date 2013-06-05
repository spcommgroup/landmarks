package resultevaluation;

import java.util.ArrayList;
import java.util.List;


import datastructures.FeatureSet;
import datastructures.Matching;

/**
 * A simple datastructure for storing data about a sentence and some computed matchings.
 * Provides some simple tools for determining how good the matchings are.
 * @author Jason Paller-Rzepka
 *
 */
public class MatcherTestResult {

    private final String sentence;
    private final List<FeatureSet> featureSetSequence;
    private final List<Matching> matchings;
    private final List<Integer> correctPartitioning;
    public MatcherTestResult(String sentence, List<FeatureSet> featureSetSequence, List<Matching> matchings, List<Integer> correctPartitioning) {
        this.sentence = sentence;
        this.featureSetSequence = featureSetSequence;
        this.matchings = matchings;
        this.correctPartitioning = correctPartitioning;
    }
    public List<Matching> getMatchings() {
        return matchings;
    }
    
    public List<Integer> getCorrectPartitioning() {
        return correctPartitioning;
    }
    
    public List<FeatureSet> getFeatureSetSequence() {
        return featureSetSequence;
    }
    public String getSentence() {
        return sentence;
    }
    
    /**
     * Returns the fraction of partitions in correctPartitioning that show up in partitioning.
     * @param partitioning
     * @return
     */
    public float partitionMatchScore(List<Integer> partitioning){
        List<Integer> correctBoundaries = boundaries(correctPartitioning);
        List<Integer> boundaries = boundaries(partitioning);
        int matchPosition;
        int numMatches = 0;
        for (int i = 0; i < correctBoundaries.size() - 1; i++){
            matchPosition = boundaries.indexOf(correctBoundaries.get(i));
            if (matchPosition != -1 /*matches in front*/
                    && matchPosition != boundaries.size() - 1 /*Not final position*/ 
                    && correctBoundaries.get(i+1).equals(boundaries.get(matchPosition + 1)) /*matches at back*/){
                numMatches++;
            }
        }
        return ((float) numMatches)/correctPartitioning.size(); 
    }
    
    /**
     * Returns a list of locations of word boundaries, given a partitioning.
     * @param partitioning
     * @return
     */
    private List<Integer> boundaries(List<Integer> partitioning){
        List<Integer> boundaries = new ArrayList<Integer>();
        int currentBoundary = 0;
        boundaries.add(currentBoundary);
        for (int length : partitioning){
            currentBoundary += length;
            boundaries.add(currentBoundary);
        }
        return boundaries;
        
    }
    
    
    

}
