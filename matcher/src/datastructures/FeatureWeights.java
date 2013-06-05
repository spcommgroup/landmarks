package datastructures;

import java.util.HashMap;
import java.util.Map;

import datastructures.FeatureSet.Feature;


/**
 * An object which tells {@code FeatureSet} the weight, or importance, of different features when calculating the probability of a match between two {@code FeatureSet}s.
 * 
 * @author Jason Paller-Rzepka
 */
public class FeatureWeights {
    private final float defaultWeight;
    private Map<Feature, Float> weights;

    public FeatureWeights(float defaultWeight, Map<Feature, Float> weights) {
        this.defaultWeight = defaultWeight;
        this.weights = new HashMap<Feature, Float>();
        for (Feature feature : FeatureSet.allFeatures){
            if (weights.containsKey(feature)){
                this.weights.put(feature, weights.get(feature));
            } else {
                this.weights.put(feature, defaultWeight);
            }
        }
    }
    
    public FeatureWeights(float defaultWeight) {
        this(defaultWeight, new HashMap<Feature, Float>());
    }
    
    public float getWeight(Feature feature){
        return weights.get(feature);
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(String.format("Default: %f.", defaultWeight));
        for (Feature f : weights.keySet()){
            float weight = weights.get(f);
            if (weight != defaultWeight){
                sb.append(" ");
                sb.append(f.toString());
                sb.append(": ");
                sb.append(weight);
                sb.append(".");
            }
        }
        return sb.toString();
    }
    

}
