package datastructures;

import java.util.HashSet;
import java.util.Set;

/**
 * An object representing a set of words, all of which have the same
 * probability of correctly matching the same subsequence of {@code FeatureSet}s
 * from a sequence of detected {@code FeatureSet}s.
 * 
 * Note: The words and the probability is stored.  The details of the subsequence
 * that these words match, however is not stored.  The creator of any ProbabilitySet
 * must separately store that information, if it is needed.
 * 
 * @author Jason Paller-Rzepka
 *
 */
public class ProbabilitySet {
    private final Set<String> words;
    private final float probability;
    
    public ProbabilitySet(Set<String> words, float probability) {
        this.words = new HashSet<String>(words);
        this.probability = probability;
    }
    
    public void addWord(String word){
        words.add(word);
    }

    public Set<String> getWords() {
        return new HashSet<String>(words);
    }

    public float getProbability() {
        return probability;
    }
}
