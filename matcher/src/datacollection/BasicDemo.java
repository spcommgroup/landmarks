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
 * A simple program to demonstrate the Matcher matching one sentence,
 * among a Lexicon of only words contained in the TIMIT database.
 * @author Jason Paller-Rzepka
 *
 */
public class BasicDemo {

    public static void main(String[] args) throws IOException{
        //create the lexicon (from the file timitdict.txt)
        Lexicon lexicon = new TimitLexicon("src/english/timitdict.txt");
        //creating the matcher, based off of that lexicon.
        Matcher matcher = new Matcher(lexicon);
        
        //Get the sequence of FeatureSets that represent the sentence "Look at that machine."
        //In the real world, this computation wouldn't be done... rather, signal processing
        //code would predict these FeatureSets from a speech signal.
        List<FeatureSet> featureSetSequence = lexicon.featureSetSequence("Look at that machine.");
        //Get some predicted matchings.
        List<Matching> matchings = matcher.match(featureSetSequence, 0.1f);
        //Present all the matchings to the user.
        for (Matching m : matchings){
            System.out.println(String.format("Matching (p = %f):",m.getBestProbability()));
            for (Ranking r : m.getRankings()){
                //The most probable words from each ranking in the matching are the ones we want to
                //display when we represent the matching for the user.
                System.out.print(r.getBestProbabilitySet().getWords());
            }
            System.out.println();
        }
    }

}
