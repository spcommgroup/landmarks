package resultevaluation;

import java.util.List;

import matcher.Matcher;

import datastructures.FeatureSet;
import datastructures.FeatureWeights;
import datastructures.Lexicon;
import datastructures.Matching;
import datastructures.Sentences;

/**
 * An abstract class for evaluating the results of performing a matching on
 * a some Sentences with a given Lexicon and FeatureWeights.
 * 
 * Subclasses must contain code for defining the relevant metrics for evaluation,
 * for defining how a given result should affect the computed values for those metrics,
 * and for printing those metrics to the console (or elsewhere?) for the user to examine.
 * 
 * @author Jason Paller-Rzepka
 *
 */
public abstract class  Evaluator {

    public abstract void addResult(MatcherTestResult result);
    public abstract void printResults();
    
    public void matchAndEvaluate(Lexicon lexicon, Sentences sentences, FeatureWeights weights, float threshold){
        Matcher matcher = new Matcher(lexicon);
        int i = 0;
        for (String sentence : sentences.getSentences()){
            System.out.println(i++);
            List<FeatureSet> featureSetSequence = lexicon.featureSetSequence(sentence);
            if (featureSetSequence == null){
                //System.out.println("Skipping...");
                continue;
            }
            List<Matching> matchings = matcher.match(featureSetSequence, weights, threshold);
            List<Integer> correctPartitioning = lexicon.correctPartitioning(sentence);
            addResult(new MatcherTestResult(sentence, featureSetSequence, matchings, correctPartitioning));
        }
    }

}
