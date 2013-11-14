package resultevaluation;

import datastructures.Matching;
import datastructures.ProbabilitySet;
import datastructures.Ranking;

/**
 * An Evaluator that determines and reports the Average Words Per Ranking and Average Partitionings Per Sentence of
 * a series of matchings.
 * @author Jason Paller-Rzepka
 *
 */
public class CohortSizeEvaluator extends Evaluator{

    private int wordCountSum;
    private int rankingCount;
    private int resultCount;
    private int matchingCount;
    public CohortSizeEvaluator() {
        wordCountSum = 0;
        rankingCount = 0;
        resultCount = 0;
        matchingCount += 1;
        
    }

    @Override
    public void addResult(MatcherTestResult result) {
        resultCount += 1;
        for (Matching m : result.getMatchings()){
            matchingCount += 1;
            for (Ranking r : m.getRankings()){
                rankingCount++;
                for (ProbabilitySet ps : r.getProbabilitySets()){
                    wordCountSum += ps.getWords().size();
                }
            }
        }
    }

    @Override
    public void printResults() {
        System.out.println(String.format("Average Words Per Ranking: %f", getAverageCohortSize()));
        System.out.println(String.format("Average Partitionings per Sentence: %f", getAveragePartitioningCount()));
    }
    
    public float getAverageCohortSize(){
        return (float) wordCountSum / rankingCount;
    }
    
    public float getAveragePartitioningCount(){
        return (float) matchingCount / resultCount;
    }
    
}
