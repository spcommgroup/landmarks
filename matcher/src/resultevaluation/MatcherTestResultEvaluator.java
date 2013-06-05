package resultevaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


import datastructures.Matching;
import datastructures.Ranking;

/**
 * An Evaluator that reports, for a series of matchings, the rankings of the correct matchings (i.e. whether
 * the correct matchings were accurately predicted as the best matching, or the second best matching, etc).
 * Also prints the rankings of the *most accurate* matchings (among the top 10 predicted matchings) for each sentence,
 * including cases where the correct matching was not present in the results.
 * 
 * @author Jason Paller-Rzepka
 *
 */

public class MatcherTestResultEvaluator extends Evaluator {

    final List<List<Float>> closestPartRankings;
    
    public MatcherTestResultEvaluator(){
        closestPartRankings = new ArrayList<List<Float>>();
    }
    
    @Override
    public void addResult(MatcherTestResult result){
        final int RANKS_TO_CONSIDER = 10;
        int currentRank = 0;
        float bestPartScore = 0.0f;
        int bestPartitionedRank = -1;
        
        for (Matching m : result.getMatchings()){
            if (currentRank >= RANKS_TO_CONSIDER){
                break;
            }
            
            float currentPartScore = result.partitionMatchScore(m.getPartitioning());
            if (currentPartScore > bestPartScore){    
                bestPartitionedRank = currentRank;
                bestPartScore = currentPartScore;
            }
            currentRank++;
        }
        
        
        if (bestPartitionedRank != -1){
            while (closestPartRankings.size() <= bestPartitionedRank){
                closestPartRankings.add(new ArrayList<Float>());
            }
            closestPartRankings.get(bestPartitionedRank).add(bestPartScore);
            if (bestPartScore != 1.0f){
                System.out.println(result.getSentence());
                Matching bestMatching = result.getMatchings().get(bestPartitionedRank);
                for (Ranking r : bestMatching.getRankings()){
                    System.out.print(r.getBestProbabilitySet().getWords().toString());
                }
                System.out.println();
            }
        } 
    }
    
    /**
     * Tabulates the results and prints them to the console.
     */
    @Override
    public void printResults(){
        System.out.println("Correct");
        for (int rank = 0; rank < closestPartRankings.size(); rank++){
            System.out.println(String.format("Rank %d: Size %d", rank + 1, Collections.frequency(closestPartRankings.get(rank), 1.0f)));
        }
        
        System.out.println("Total");
        for (int rank = 0; rank < closestPartRankings.size(); rank++){
            System.out.println(String.format("Rank %d: Size %d", rank + 1, closestPartRankings.get(rank).size()));
        }
        
        
    }

}
