package datacollection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import laff.LaffLexicon;
import laff.LaffSentences;
import matcher.CMULexicon;
import resultevaluation.CohortSizeEvaluator;
import datastructures.FeatureSet;
import datastructures.FeatureSet.Feature;
import datastructures.FeatureWeights;
import datastructures.Lexicon;
import datastructures.Sentences;

/**
 * Evaluates the performance of the matcher on the LAFF sentences with the LAFF Lexicon, using a 100% (perfect) matching
 * threshold, in multiple trials.  In each trial, a different feature is excluding from consideration when performing the matching.
 * (This file is easily modifiable to *include only one* feature per trial, instead of *excluding* one per trial.  Writes results to file.
 * @author Jason Paller-Rzepka
 *
 */
public class SingleFeatureStats {
    public static void main(String[] args) throws IOException{
        String outFile = args[0];
        Lexicon lexicon = new LaffLexicon(new CMULexicon("src/english/cmudict.0.7a"), "src/english/laffwords.txt");
        Sentences sentences = new LaffSentences();
        //Lexicon lexicon = new TimitLexicon("src/english/timitdict.txt");
        //Sentences sentences = new TimitSentences();
        
        FileWriter fileWriter = new FileWriter(new File(outFile), true);
        BufferedWriter writer = new BufferedWriter(fileWriter);
    
        Set<FeatureWeights> weightsSet = new HashSet<FeatureWeights>();
        
        Map<Feature, Float> weightMap;
        
        //Use defaultWeight = 0.0f to do matchings where only one feature contributes to the matching score.
        //Use defaultWeight = 1.0f to do matchings where only one feature is excluded from affecting the matching score.
        float defaultWeight = 0.0f;
        
        for (Feature f : FeatureSet.allFeatures){
            weightMap = new HashMap<Feature, Float>();
            weightMap.put(f, 1.0f - defaultWeight);
            weightsSet.add(new FeatureWeights(defaultWeight, weightMap));
        }

        
        int processed = 0;
        for (FeatureWeights weights : weightsSet){
            System.out.println(String.format("Processing #%d.", ++processed));
            CohortSizeEvaluator evaluator = new CohortSizeEvaluator();
            evaluator.matchAndEvaluate(lexicon, sentences, weights, 1.0f);
            evaluator.printResults();
            writer.write("{");
            writer.newLine();
            writer.write(String.format("\t\"weights\": \"%s\",", weights.toString()));
            writer.newLine();
            writer.write(String.format("\t\"wordsPerRanking\": %f,", evaluator.getAverageCohortSize()));
            writer.newLine();
            writer.write(String.format("\t\"partitioningsPerSentence\": %f,", evaluator.getAveragePartitioningCount()));
            writer.newLine();
            writer.write("},");
            writer.newLine();
        }
        writer.close();

    }


}
