package datacollection;

import java.io.IOException;
import java.util.List;

import matcher.Matcher;

import timit.TimitLexicon;

import datastructures.FeatureSet;
import datastructures.Lexicon;
import datastructures.Matching;
import datastructures.Ranking;

/**
 * A demonstration showing the Matcher doing a matching on a sequence of FeatureSets
 * for which one of the expected FeatureSets was "not detected."
 * @author J
 *
 */
public class DeletionInsertionDemo {

    public static void main(String[] args) throws IOException{
        Lexicon lexicon = new TimitLexicon("src/english/timitdict.txt");
        Matcher matcher = new Matcher(lexicon);
        
        List<FeatureSet> featureSetSequence = lexicon.featureSetSequence("This is a mark.");
        featureSetSequence.remove(4);
        List<Matching> matchings = matcher.match(featureSetSequence, 0.1f);
        for (Matching m : matchings){
            System.out.println(String.format("Matching (p = %f):",m.getBestProbability()));
            for (Ranking r : m.getRankings()){
                System.out.print(r.getBestProbabilitySet().getWords());
            }
            System.out.println();
        }
    }

}
