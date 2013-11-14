package datastructures;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * An object representing a collection of {@code ProbabilitySet}s, each of which may
 * correctly match the same subsequence of {@code FeatureSet}s
 * from a sequence of detected {@code FeatureSet}s.
 * 
 * A {@code Ranking} sorts the {@code ProbabilitySet}s, so they may be accessed
 * in order of the likelihood that they correctly match the data.
 * 
 * @author Jason Paller-Rzepka
 *
 */
public class Ranking {

    private final Set<ProbabilitySet> probabilitySets;
    
    public Ranking(List<ProbabilitySet> probabilitySets) {
        this.probabilitySets = new HashSet<ProbabilitySet>(probabilitySets);
    }
    
    public boolean isEmpty(){
        return probabilitySets.isEmpty();
    }
    
    
    
    public float bestProbability(){
        ProbabilitySet bestProbSet = getBestProbabilitySet();
        if (bestProbSet == null){
            return 0.0f;
        } else {
            return bestProbSet.getProbability();
        }
    }
    
    public ProbabilitySet getBestProbabilitySet(){
        float bestProbability = 0.0f;
        float currentProbability;
        ProbabilitySet bestProbabilitySet = null;
        for (ProbabilitySet ps : probabilitySets){
            currentProbability = ps.getProbability();
            if (currentProbability > bestProbability){
                bestProbability = currentProbability;
                bestProbabilitySet = ps;
            }
        }
        return bestProbabilitySet;
    }

    public void add(ProbabilitySet ps){
        if (this.isEmpty()){
            probabilitySets.add(ps);
            return;
        }
        
        float psProb = ps.getProbability();
        
        for (ProbabilitySet existingPs : probabilitySets){
            if (existingPs.getProbability() == psProb){
                for (String word : ps.getWords()){
                    existingPs.addWord(word);
                }
                return;
            }
        }
        //else:
        probabilitySets.add(ps);
    }
    
    public Set<ProbabilitySet> getProbabilitySets(){
        return new HashSet<ProbabilitySet>(probabilitySets);
    }

}
