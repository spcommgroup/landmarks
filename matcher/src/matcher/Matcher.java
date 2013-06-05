package matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import datastructures.FeatureSet;
import datastructures.FeatureSet.Feature;
import datastructures.FeatureWeights;
import datastructures.HeapNode;
import datastructures.LexTreeNode;
import datastructures.Lexicon;
import datastructures.Matching;
import datastructures.MaxHeap;
import datastructures.ProbabilitySet;
import datastructures.Ranking;

/**
 * An object that calculates the best matches (to words from a specified lexicon) for a sequence of {@code FeatureSet}s.
 * 
 * @author Jason Paller-Rzepka
 *
 */
public class Matcher {
    private final LexTreeNode lexTree;
    private final int maxWordLength;

    public Matcher(Lexicon lexicon) {
        LexTreeFactory lexTreeFactory = new LexTreeFactory(lexicon);
        this.lexTree = lexTreeFactory.lexTree();
        this.maxWordLength = lexTree.getHeight();
    }
    
    public List<Matching> match(List<FeatureSet> featureSetSequence, FeatureWeights weights, float probabilityThreshold){
        final int MAX_INSERTIONS = 10;
        final Ranking[][] rankings = new Ranking[featureSetSequence.size()][maxWordLength + MAX_INSERTIONS + 1];
        
        for (int i = 0; i < featureSetSequence.size(); i++){
            for (int j = 0; j < maxWordLength + MAX_INSERTIONS + 1; j++){
                rankings[i][j] = new Ranking(new ArrayList<ProbabilitySet>());
            }
        }
        
        for (int t = 0; t < featureSetSequence.size(); t++){
            MaxHeap heap = new MaxHeap(new ArrayList<HeapNode>());
            heap.insert(new HeapNode(lexTree, 1.0f, 0));
            boolean foundOne = false;
            HeapNode node;
            while (!heap.isEmpty()){
                node = heap.extractMax();
                if (node.getProbability() < probabilityThreshold && foundOne){
                    break;
                }
                if (node.getMatchLength() > maxWordLength + MAX_INSERTIONS){
                    continue;
                }
                if (node.getLexTreeNode().hasWords()){
                    //Add the word to the appropriate ranking list.
                    rankings[t][node.getMatchLength()].add(new ProbabilitySet(node.getLexTreeNode().getWords(),node.getProbability()));
                    foundOne = true;
                }
                
                //Add all children
                if (t + node.getMatchLength() >= featureSetSequence.size()){
                    continue;
                }
                
                for (LexTreeNode lexTreeChild : node.getLexTreeNode().getChildren()){
                    heap.insert(new HeapNode(lexTreeChild,
                                node.getProbability() * FeatureSet.matchProb(featureSetSequence.get(t + node.getMatchLength()), lexTreeChild.getFeatureSet(), weights),
                                node.getMatchLength() + 1));
                    /* Commenting because makes search tree blow up.  Not yet sure how to fix.
                    //Or, the detector may have missed a child:
                    heap.insert(new HeapNode(lexTreeChild, node.getProbability() * lexTreeChild.getFeatureSet().getDeletionProbability(), node.getMatchLength()));
                    */
                }
                
                /* Commenting because makes search tree blow up.  Not yet sure how to fix.
                //Or, the detector may have inserted featureSetSequence.get(t + node.getMatchLength()) by mistake:
                heap.insert(new HeapNode(node.getLexTreeNode(), node.getProbability() * featureSetSequence.get(t + node.getMatchLength()).getInsertionProbability(), node.getMatchLength()+1));
                */
                
                
                
            }  
        }

        List<Matching> bestMatchMemo = new ArrayList<Matching>();
        bestMatchMemo.add(new Matching(1.0f, new ArrayList<Ranking>(), new ArrayList<Integer>()));
        
        List<Matching> bestMatchFromMemo = new ArrayList<Matching>();
        bestMatchFromMemo.add(new Matching(1.0f, new ArrayList<Ranking>(), new ArrayList<Integer>()));
        
        /* We could, in time O(len(featureset_sequence)^3), determine best-matchings between every possible set of boundaries.                                                             
         * This would allow us to select an arbitrary number of word boundaries, and see the best matching for that selection.                                                             
         * Instead, for now, we simply calculate the best matchings bewtween 0 and any boundary, as well as for any boundary to the end.                                                   
         * This allows us to select any endpoints, require that a single word fit in it, and find the best matching for that selection.   
         */
        
        
        List<List<Integer>> allPartitionings = new ArrayList<List<Integer>>();
        List<Matching> matchings = new ArrayList<Matching>();
        
        Matching frontMatch;
        Matching endMatch;
        float score;
        
        List<Integer> partitioning;
        
        for (int start = 0; start < featureSetSequence.size(); start++){
            for (int end = start + 1; end <= featureSetSequence.size(); end++){
                if (end - start > maxWordLength){
                    break;
                }
                if (rankings[start][end-start].isEmpty()){
                    continue;
                }
                frontMatch = bestMatch(featureSetSequence, start, rankings, bestMatchMemo);
                endMatch = bestMatchFrom(featureSetSequence, end, rankings, bestMatchFromMemo);
                score = frontMatch.getBestProbability() * rankings[start][end - start].bestProbability() * endMatch.getBestProbability();
                if (score != 0.0f){
                    partitioning = new ArrayList<Integer>(frontMatch.getPartitioning());
                    partitioning.add(end-start);
                    partitioning.addAll(endMatch.getPartitioning());
                    
                    if (!allPartitionings.contains(partitioning)){
                        allPartitionings.add(partitioning);
                        List<Ranking> newRankings = new ArrayList<Ranking>(frontMatch.getRankings());
                        newRankings.add(rankings[start][end-start]);
                        newRankings.addAll(endMatch.getRankings());
                        matchings.add(new Matching(score, newRankings, partitioning));
                    }
                }
            }
        }
        
        Comparator<Matching> matchingComparator = new Comparator<Matching>(){

            @Override
            public int compare(Matching m1, Matching m2) {
                if (m1.getBestProbability() > m2.getBestProbability()){
                    //Then m1 should be sorted BEFORE m2.
                    return -1;
                } else if (m1.getBestProbability() == m2.getBestProbability()){
                    return 0;
                } else {
                    return +1;
                }
            }
            
        };
        
        Collections.sort(matchings, matchingComparator);
        return matchings;  
    }
    
    /**
     * Returns the best Matching of featureSetSequence[0 includsive ... t exclusive].
     * Updates memo to include this value at memo.get(t).
     * @param featureSetSequence
     * @param t
     * @param rankings
     * @param memo
     * @return
     */
    private Matching bestMatch(List<FeatureSet> featureSetSequence, int t, Ranking[][] rankings, List<Matching> memo){
        if (t < memo.size()){
            return memo.get(t);
        } else {
            float bestProbability = 0.0f;
            List<Ranking> bestRankings = new ArrayList<Ranking>();
            List<Integer> bestPartitioning = new ArrayList<Integer>();
            for (int length = 1; length <= maxWordLength && length <= t; length++){
                float bestProbSoFar = bestMatch(featureSetSequence, t-length, rankings, memo).getBestProbability();
                if (!rankings[t-length][length].isEmpty()){
                    float currentProbability = rankings[t-length][length].bestProbability() * bestProbSoFar;
                    if (currentProbability >= bestProbability && currentProbability != 0){
                        bestProbability = currentProbability;
                        
                        bestRankings = new ArrayList<Ranking>(bestMatch(featureSetSequence, t-length, rankings, memo).getRankings());
                        bestRankings.add(rankings[t-length][length]);
                        
                        bestPartitioning = new ArrayList<Integer>(bestMatch(featureSetSequence, t-length, rankings, memo).getPartitioning());
                        bestPartitioning.add(length);
                    }
                }
            }
            if (memo.size() == t){
                memo.add(new Matching(bestProbability, bestRankings, bestPartitioning));
                return memo.get(t);
            } else {
                throw new RuntimeException("bestMatch memo is corrupt!  it must have size " + t + " but it has size " + memo.size());   
            }
        }
    }
    
    /**
     * Returns the best Matching starting at (and including) featureSetSequence.get(featureSetSequence.size() - t), and going to the end of featureSetSequence.
     */
    private Matching bestMatchFrom(List<FeatureSet> featureSetSequence, int t,  Ranking[][] rankings, List<Matching> memo){
        /* Note!  As expected, bestMatchFrom(fss, t, mwl, r, m) returns the best matching starting at (and including) the t-to-last element element, and going
         * to the end.  However, the memo stores the best matching starting at (and including) the *t'th* element.  Be careful about that!
         */ 
        
        if (featureSetSequence.size() - t < memo.size()){
            
            return memo.get(featureSetSequence.size() - t);
        } else {
            float bestProbability = 0.0f;
            List<Ranking> bestRankings = new ArrayList<Ranking>();
            List<Integer> bestPartitioning = new ArrayList<Integer>();
            for (int length = 1; length <= maxWordLength && length <= featureSetSequence.size() - t; length++){
                
                Matching bestMatchFromHere = bestMatchFrom(featureSetSequence, t + length, rankings, memo);
                float bestProbFromHere;
                if (bestMatchFromHere == null ){
                    continue;
                } else {
                    bestProbFromHere = bestMatchFromHere.getBestProbability();
                }
                
                                
                if (!rankings[t][length].isEmpty()){
                    float currentProbability = rankings[t][length].bestProbability() * bestProbFromHere;
                    if (currentProbability >= bestProbability && currentProbability != 0){
                        bestProbability = currentProbability;
                        
                        bestRankings = new ArrayList<Ranking>();
                        bestRankings.add(rankings[t][length]);
                        bestRankings.addAll(bestMatchFrom(featureSetSequence, t + length, rankings, memo).getRankings());
                        
                        
                        bestPartitioning = new ArrayList<Integer>();
                        bestPartitioning.add(length);
                        bestPartitioning.addAll(bestMatchFrom(featureSetSequence, t + length,  rankings, memo).getPartitioning());
                    }
                }
            }
            
            if (memo.size() == featureSetSequence.size() - t){
                memo.add(new Matching(bestProbability, bestRankings, bestPartitioning));
                return memo.get(featureSetSequence.size() - t);
            } else {
                throw new RuntimeException("bestMatch memo is corrupt!  it must have size " + t + " but it has size " + memo.size());   
            } 
        }
    }

    public List<Matching> match(List<FeatureSet> featureSetSequence, float threshold) {
        FeatureWeights weights = new FeatureWeights(1.0f, new HashMap<Feature, Float>());
        return match(featureSetSequence, weights, threshold);
        
    }

}
