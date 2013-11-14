package datastructures;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * An object that represents a single landmark (e.g. Sc)
 * {@code Landmark} is an alternative to (and based off of) {@code FeatureSet}. 
 * Meant to be used with {@code Matcher}.
 * 
 * @author Jessica Kenney
 */
public class Landmark {
    /*
     * Rep. invariants:
     *      Immutable.
     *      featureValues has a key for each String in allFeatures.
     */
    
    public static final String[] Landmarks= {
            "Sc", "Sr", "Fc", "Fr", "Nc", "Nr", "G", "V"
            };
    
    private String landmark;
    
    public Landmark(String landmark) {
        if(Arrays.asList(Landmarks).contains(landmark)){
        	this.landmark = landmark;
        }
    }
    
    @Override
    public boolean equals(Object o){
        if (o == null){
            return false;
        }
        else if (o instanceof Landmark) {
        	Landmark that = (Landmark) o;
        	return (that.getValue() == this.landmark);
        }
        else if (o instanceof String) {
        	return (o == this.landmark);
        }
        else {
        	return false;
        }
    }
    
    public String getValue(){
        return this.landmark;
    }
    
    private static final float VERY_BAD_SCORE = 0.0f;
    private static final float BAD_SCORE = 0.35f;
    private static final float GOOD_SCORE = 1.0f;

    public static float matchProb(Landmark observed, Landmark predicted){
        if(observed.getValue() == predicted.getValue()){
        	return GOOD_SCORE;
        }
        else {
        	return VERY_BAD_SCORE;
        }
    }
    
//    public FeatureSet detected(){
//        Map<Feature, Float> detectedValues = new HashMap<Feature, Float>();
//        Random rGen = new Random();
//        for (Feature f : allFeatures){
//            float detectedValue;
//            float rVal = rGen.nextFloat();
//            if (featureValues.get(f) == 1.0f){
//                //For +, detect 85%, delete 15%.
//                if (rVal < 0.85){
//                    detectedValue = 1.0f;
//                } else {
//                    detectedValue = 0.0f;
//                }
//            } else if (featureValues.get(f) == 0.0f){
//                //For 0, insert 20% as +, 20% as -.
//                if (rVal < 0.20){
//                    detectedValue = 1.0f;
//                } else if (rVal < 0.80) {
//                    detectedValue = 0.0f;
//                } else {
//                    detectedValue = -1.0f;
//                }
//            } else {
//                //For -, detect 85%, delete 15%.
//                if (rVal < 0.85){
//                    detectedValue = -1.0f;
//                } else {
//                    detectedValue = 0.0f;
//                }
//            }
//            detectedValues.put(f, detectedValue);
//        }
//        return new FeatureSet(detectedValues);
//    }
    
    public float getInsertionProbability(){
        final float INSERTION_PROBABILITY = 0.0f;
        return INSERTION_PROBABILITY;
    }
    
    public float getDeletionProbability(){
        final float DELETION_PROBABILITY = 0.0f;
        return DELETION_PROBABILITY;
    }
    
}
