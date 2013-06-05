package datastructures;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * An object that represents a single feature set (a.k.a. feature vector, a.k.a. feature bundle).
 * 
 * Each {@code FeatureSet} is intended to represent the features of a single
 * landmark, except possibly in the case of diphthongs.
 * 
 * @author Jason Paller-Rzepka
 */
public class FeatureSet {
    /*
     * Rep. invariants:
     *      Immutable.
     *      featureValues has a key for each String in allFeatures.
     */
    public static enum Feature 
    {
        VOWEL, GLIDE, CONSONANT, SONORANT, CONTINUANT,
        STRIDENT, STIFF, SLACK, SPREAD, CONSTRICTED,
        ATR, CTR, NASAL, BODY,
        BLADE, LIPS, HIGH, LOW, BACK,
        ANTERIOR, DISTRIBUTED, LATERAL, RHOTIC, ROUND        
    }
    
    public static final Feature[] allFeatures= {
            Feature.VOWEL, Feature.GLIDE, Feature.CONSONANT, Feature.SONORANT, Feature.CONTINUANT,
            Feature.STRIDENT, Feature.STIFF, Feature.SLACK, Feature.SPREAD, Feature.CONSTRICTED,
            Feature.ATR, Feature.CTR, Feature.NASAL, Feature.BODY,
            Feature.BLADE, Feature.LIPS, Feature.HIGH, Feature.LOW, Feature.BACK,
            Feature.ANTERIOR, Feature.DISTRIBUTED, Feature.LATERAL, Feature.RHOTIC, Feature.ROUND
            };
    
    private HashMap<Feature, Float> featureValues;
    
    public FeatureSet(Map<Feature, Float> featureValues) {
        this.featureValues = new HashMap<Feature, Float>();
        for (Feature feature : allFeatures){
            if (featureValues.containsKey(feature)){
                this.featureValues.put(feature,  featureValues.get(feature));
            } else {
                this.featureValues.put(feature, 0.0f);
            }
        }
    }
    
    
    @Override
    public boolean equals(Object o){
        if (o == null){
            return false;
        } else if (!(o instanceof FeatureSet)) {
            return false;
        } else {
            FeatureSet that = (FeatureSet) o;
            for (Feature feature : allFeatures){
                if (that.getValue(feature) != this.getValue(feature)){
                    return false;
                }
            }
            return true;
        }
    }
    
    /**
     * Requires that feature is in allFeatures
     */
    public float getValue(Feature feature){
        return featureValues.get(feature);
    }
    
    private static final float VERY_BAD_SCORE = 0.0f;
    private static final float BAD_SCORE = 0.35f;
    private static final float GOOD_SCORE = 1.0f;
    
    public static float matchProb(FeatureSet observed, FeatureSet predicted){
        return matchProb(observed, predicted, new FeatureWeights(1.0f, new HashMap<Feature, Float>()));
    }
    
    public static float matchProb(FeatureSet observed, FeatureSet predicted, FeatureWeights weights){
        float score = 0.0f;
        float total = 0.0f;

        for (Feature feature : allFeatures){
            if (predicted.getValue(feature) == 0.0f)
            {
                //Completely ignore it.  Don't add to the score, don't add to the total.
                continue;
            } else if (observed.getValue(feature) == 0.0f){
                score += BAD_SCORE * weights.getWeight(feature);
            } else if (observed.getValue(feature) == predicted.getValue(feature)){
                score += GOOD_SCORE * weights.getWeight(feature);
            } else {
                score += VERY_BAD_SCORE * weights.getWeight(feature);
            }
            total += 1.0f * weights.getWeight(feature);
        }

        if (total == 0.0f){
            //TODO: Is this the correct handling of total = 0.0f?
            return 1.0f;
        } else {
            return score / total;
        }
    }
    
    public FeatureSet detected(){
        Map<Feature, Float> detectedValues = new HashMap<Feature, Float>();
        Random rGen = new Random();
        for (Feature f : allFeatures){
            float detectedValue;
            float rVal = rGen.nextFloat();
            if (featureValues.get(f) == 1.0f){
                //For +, detect 85%, delete 15%.
                if (rVal < 0.85){
                    detectedValue = 1.0f;
                } else {
                    detectedValue = 0.0f;
                }
            } else if (featureValues.get(f) == 0.0f){
                //For 0, insert 20% as +, 20% as -.
                if (rVal < 0.20){
                    detectedValue = 1.0f;
                } else if (rVal < 0.80) {
                    detectedValue = 0.0f;
                } else {
                    detectedValue = -1.0f;
                }
            } else {
                //For -, detect 85%, delete 15%.
                if (rVal < 0.85){
                    detectedValue = -1.0f;
                } else {
                    detectedValue = 0.0f;
                }
            }
            detectedValues.put(f, detectedValue);
        }
        return new FeatureSet(detectedValues);
    }
    
    public float getInsertionProbability(){
        final float INSERTION_PROBABILITY = 0.0f;
        return INSERTION_PROBABILITY;
    }
    
    public float getDeletionProbability(){
        final float DELETION_PROBABILITY = 0.0f;
        return DELETION_PROBABILITY;
    }
    
}
