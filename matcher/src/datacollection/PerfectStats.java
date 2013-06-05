package datacollection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import resultevaluation.CohortSizeEvaluator;

import laff.LaffLexicon;
import laff.LaffSentences;
import matcher.CMULexicon;

import datastructures.FeatureWeights;
import datastructures.Lexicon;
import datastructures.Sentences;

/**
 * Evaluates the performance of the matcher on the LAFF sentences with the LAFF Lexicon, using
 * thresholds varying among {.95, .85, ..., .15, .05}.  Writes results to file.
 * @author Jason Paller-Rzepka
 *
 */
public class PerfectStats {
    public static void main(String[] args) throws IOException{
        String outFile = args[0];
        Lexicon lexicon = new LaffLexicon(new CMULexicon("src/english/cmudict.0.7a"), "src/english/laffwords.txt");
        Sentences sentences = new LaffSentences();
        //Lexicon lexicon = new TimitLexicon("src/english/timitdict.txt");
        //Sentences sentences = new TimitSentences();
        
        FileWriter fileWriter = new FileWriter(new File(outFile), true);
        BufferedWriter writer = new BufferedWriter(fileWriter);
        
        for (int iThreshold = 95; iThreshold >= 0; iThreshold -= 10){
            float threshold = ((float) iThreshold) / 100;
            CohortSizeEvaluator evaluator = new CohortSizeEvaluator();
            evaluator.matchAndEvaluate(lexicon, sentences, new FeatureWeights(1.0f), threshold);
            evaluator.printResults();
            writer.write("{");
            writer.newLine();
            writer.write(String.format("\t\"threshold\": %f,", threshold));
            writer.newLine();
            writer.write(String.format("\t\"wordsPerRanking\": %f,", evaluator.getAverageCohortSize()));
            writer.newLine();
            writer.write(String.format("\t\"partitioningsPerSentence\": %f,", evaluator.getAveragePartitioningCount()));
            writer.newLine();
            writer.write("},");
            writer.newLine();
        }
        writer.close();
        
        
        /*
        Set<FeatureWeights> weightsSet = new HashSet<FeatureWeights>();
        
        Map<Feature, Float> weightMap = new HashMap<Feature, Float>();
        weightMap.put(Feature.VOWEL, 1.0f);
        weightMap.put(Feature.GLIDE, 1.0f);
        weightMap.put(Feature.CONSONANT, 1.0f);
        weightMap.put(Feature.SONORANT, 1.0f);
        weightsSet.add(new FeatureWeights(0.0f, weightMap));
        
        for (float defaultWeight : new float[]{0.0f, 1.0f})
        {
            for (Feature f : FeatureSet.allFeatures){
                weightMap = new HashMap<Feature, Float>();
                weightMap.put(f, 1.0f - defaultWeight);
                weightsSet.add(new FeatureWeights(defaultWeight, weightMap));
            }
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
        */
    }


}
