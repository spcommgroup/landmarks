package datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * An object representing a single Matching of some sequence of {@code FeatureSet}s.
 * 
 * A {@code Matching} contains a sequence of {@code Ranking}s, as well as a description
 * of the partitions of the {@code FeatureSet} sequence to which these {@code Ranking}s
 * correspond.
 * 
 * @author Jason Paller-Rzepka
 *
 */
public class Matching {

    private final List<Ranking> rankings;
    private final List<Integer> partitioning;
    private float bestProbability;
    public Matching(float bestProbability, List<Ranking> rankings, List<Integer> partitionings) {
        this.rankings = new ArrayList<Ranking>(rankings);
        this.partitioning = new ArrayList<Integer>(partitionings);
        this.bestProbability = bestProbability;
    }
    public List<Ranking> getRankings() {
        return rankings;
    }
    public List<Integer> getPartitioning() {
        return partitioning;
    }
    public float getBestProbability() {
        return bestProbability;
    }

    

}
