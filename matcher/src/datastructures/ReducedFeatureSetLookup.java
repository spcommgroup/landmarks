package datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import datastructures.FeatureSet.Feature;


//TODO: Combine code into FeatureSet, and get rid of this class?
/**
 * A class for getting the FeatureSet that corresponds to any of the following phones (TODO: phones or phonemes?):
 * iy, ih, ey, eh, ae, aa, ao, ow, ah (and synonyms ix and ax), uw, uh, er (and synonym axr), aw, ay, oy, hh, w, y, r, l, m, n, ng, v, dh, z,
 * zh, f, th, s, sh, b, d, g, p, t, k, jh, ch
 * 
 * @author Jessica Kenney
 *
 */
public class ReducedFeatureSetLookup {

    private final static HashMap<String, List<FeatureSet>> featureSetMap;
    static {
        featureSetMap = new HashMap<String, List<FeatureSet>>();
        HashMap<Feature, Float> map;
        ArrayList<FeatureSet> list; //To be reused for each sound.
        
        //Using only landmarks for now
        
        //S
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, -1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("S", list);
        
        //F
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, +1.0f);
//        map.put(Feature.STRIDENT, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("F", list);
        
        //N
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, +1.0f);
        map.put(Feature.CONTINUANT, -1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("N", list);
        
        //G
        map = new HashMap<Feature, Float>();
        map.put(Feature.GLIDE, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("G", list);
        
        //V
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("V", list);
        
        
        //Vowels
        featureSetMap.put("iy", featureSetMap.get("V"));
        featureSetMap.put("ih", featureSetMap.get("V"));
        featureSetMap.put("ey", featureSetMap.get("V"));
        featureSetMap.put("eh", featureSetMap.get("V"));
        featureSetMap.put("ae", featureSetMap.get("V"));
        featureSetMap.put("aa", featureSetMap.get("V"));
        featureSetMap.put("ao", featureSetMap.get("V"));
        featureSetMap.put("ow", featureSetMap.get("V"));
        featureSetMap.put("ah", featureSetMap.get("V"));
        featureSetMap.put("uw", featureSetMap.get("V"));
        featureSetMap.put("uh", featureSetMap.get("V"));
        featureSetMap.put("er", featureSetMap.get("V"));
        featureSetMap.put("aw", featureSetMap.get("V"));
        featureSetMap.put("ix", featureSetMap.get("V"));
        featureSetMap.put("ax", featureSetMap.get("V"));
        featureSetMap.put("axr", featureSetMap.get("V"));

        //aw --- vowel/glide dipthong (2 parts)
	        map = new HashMap<Feature, Float>();
	        map.put(Feature.VOWEL, +1.0f);
	        map.put(Feature.GLIDE, +1.0f);
	        list = new ArrayList<FeatureSet>();
	        list.add(new FeatureSet(map));
	        
	        map = new HashMap<Feature, Float>();
	        map.put(Feature.VOWEL, +1.0f);
	        map.put(Feature.GLIDE, +1.0f);
	        list.add(new FeatureSet(map));
	        
	        featureSetMap.put("aw", list);
        
        //other V/G dipthongs
        featureSetMap.put("ay", featureSetMap.get("aw"));
        featureSetMap.put("oy", featureSetMap.get("aw"));

        //Glides
        featureSetMap.put("hh", featureSetMap.get("G"));
        featureSetMap.put("h", featureSetMap.get("G"));
        featureSetMap.put("w", featureSetMap.get("G"));
        featureSetMap.put("y", featureSetMap.get("G"));
        featureSetMap.put("r", featureSetMap.get("G"));
        featureSetMap.put("l", featureSetMap.get("G"));

        //Nasals
        featureSetMap.put("m", featureSetMap.get("N"));
        featureSetMap.put("n", featureSetMap.get("N"));
        featureSetMap.put("ng", featureSetMap.get("N"));

        //Fricatives
        featureSetMap.put("v", featureSetMap.get("F"));
        featureSetMap.put("dh", featureSetMap.get("F"));
        featureSetMap.put("z", featureSetMap.get("F"));
        featureSetMap.put("zh", featureSetMap.get("F"));
        featureSetMap.put("f", featureSetMap.get("F"));
        featureSetMap.put("th", featureSetMap.get("F"));
        featureSetMap.put("s", featureSetMap.get("F"));
        featureSetMap.put("sh", featureSetMap.get("F"));

        //Stops
        featureSetMap.put("b", featureSetMap.get("S"));
        featureSetMap.put("d", featureSetMap.get("S"));
        featureSetMap.put("g", featureSetMap.get("S"));
        featureSetMap.put("p", featureSetMap.get("S"));
        featureSetMap.put("t", featureSetMap.get("S"));
        featureSetMap.put("k", featureSetMap.get("S"));

        //jh --- Affricate (S/F) - 2 parts
	        map = new HashMap<Feature, Float>();
	        map.put(Feature.CONSONANT, +1.0f);
	        map.put(Feature.SONORANT, -1.0f);
	        map.put(Feature.CONTINUANT, -1.0f);
//	        map.put(Feature.STRIDENT, +1.0f);
	        list = new ArrayList<FeatureSet>();
	        list.add(new FeatureSet(map));
	        
	        map = new HashMap<Feature, Float>();
	        map.put(Feature.CONSONANT, +1.0f);
	        map.put(Feature.SONORANT, -1.0f);
	        map.put(Feature.CONTINUANT, +1.0f);
//	        map.put(Feature.STRIDENT, +1.0f);
	        list.add(new FeatureSet(map));
	        
	        featureSetMap.put("jh", list);
        
        //ch
        featureSetMap.put("ch", featureSetMap.get("jh"));

    }

    public static List<FeatureSet> lookup(String phone){
        return lookup(phone, "reduced");
    }
    public static List<FeatureSet> lookup(String phone, String type){
//    	System.out.println(featureSetMap);
    	List<FeatureSet> fs = featureSetMap.get(phone);
		HashMap<Feature, Float> map;
        ArrayList<FeatureSet> list = new ArrayList<FeatureSet>();
        
        boolean specialFS = false;
        Feature[] useFS;
        Feature[] VGC = {Feature.VOWEL, Feature.GLIDE, Feature.CONSONANT};
        Feature[] V = {Feature.VOWEL};
        if(type=="VGC"){
        	specialFS=true;
        	useFS=VGC;
        } else if(type=="V"){
        	specialFS=true;
        	useFS=V;
        } else {
        	useFS = new Feature[] {};
        }
        if(specialFS){
//        	System.out.println("VGC");
	        for(FeatureSet fs1 : fs){
                map = new HashMap<Feature, Float>();
	        	for (Feature f : useFS){
	                map.put(f, fs1.getValue(f));
	        	}
                list.add(new FeatureSet(map));
	        }
	        fs = list;
        }
//        System.out.println(fs);
//        System.out.println("-----------");
        return fs;

    }

}
