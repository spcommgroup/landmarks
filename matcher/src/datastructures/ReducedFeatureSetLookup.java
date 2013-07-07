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
        
        //iy
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("iy", list);
        
        //ih
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("ih", list);

        //ey
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("ey", list);
        
        //eh
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("eh", list);
        
        //ae
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("ae", list);
        
        
        //aa
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("aa", list);
        
        //ao
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("ao", list);
        
        //ow
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("ow", list);
        
        //ah
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("ah", list);
        
        //uw
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("uw", list);
        
        //uh
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("uh", list);
        
        //er
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("er", list);
        
        //aw
        //first part of aw
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        map.put(Feature.GLIDE, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        
        //second part of aw
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        map.put(Feature.GLIDE, +1.0f);
        list.add(new FeatureSet(map));
        
        featureSetMap.put("aw", list);
        
        //ay
        //first part of ay
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        map.put(Feature.GLIDE, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        
        //second part of ay
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        map.put(Feature.GLIDE, +1.0f);
        list.add(new FeatureSet(map));
        featureSetMap.put("ay", list);
        
        //oy
        //first part of oy
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        map.put(Feature.GLIDE, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        
        //second part of oy
        map = new HashMap<Feature, Float>();
        map.put(Feature.VOWEL, +1.0f);
        map.put(Feature.GLIDE, +1.0f);
        list.add(new FeatureSet(map));

        featureSetMap.put("oy", list);
        
        //hh
        map = new HashMap<Feature, Float>();
        map.put(Feature.GLIDE, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("hh", list);
        //Alias "h"
        featureSetMap.put("h",list);
        
        //w
        map = new HashMap<Feature, Float>();
        map.put(Feature.GLIDE, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("w", list);
        
        
        //y
        map = new HashMap<Feature, Float>();
        map.put(Feature.GLIDE, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("y", list);
        
        
        //r
        map = new HashMap<Feature, Float>();
        map.put(Feature.GLIDE, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("r", list);
        
        
        //l
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, +1.0f);
        map.put(Feature.CONTINUANT, -1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("l", list);
        
        //m
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, +1.0f);
        map.put(Feature.CONTINUANT, -1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("m", list);
        
        //n
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, +1.0f);
        map.put(Feature.CONTINUANT, -1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("n", list);
        
        //ng
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, +1.0f);
        map.put(Feature.CONTINUANT, -1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("ng", list);
        
        //v
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("v", list);
        
        //dh
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, +1.0f);
        map.put(Feature.CONTINUANT, -1.0f);
        map.put(Feature.STRIDENT, -1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("dh", list);
        
        //z
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, +1.0f);
        map.put(Feature.STRIDENT, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("z", list);
        
        //zh
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, +1.0f);
        map.put(Feature.STRIDENT, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("zh", list);

        //f
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("f", list);
        
        //th
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, +1.0f);
        map.put(Feature.STRIDENT, -1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("th", list);
        
        //s
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, +1.0f);
        map.put(Feature.STRIDENT, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("s", list);
        
        //sh
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, +1.0f);
        map.put(Feature.STRIDENT, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("sh", list);
        
        
        //b
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, -1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("b", list);
        
        //d
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, -1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("d", list);
        
        //g
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, -1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("g", list);
        
        //p
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, -1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("p", list);
        
        //t
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, -1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("t", list);
        
        //k
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, -1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        featureSetMap.put("k", list);

        //jh
        //first part of jh
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, -1.0f);
        map.put(Feature.STRIDENT, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        
        //second part of jh
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, +1.0f);
        map.put(Feature.STRIDENT, +1.0f);
        list.add(new FeatureSet(map));
        
        featureSetMap.put("jh", list);
        
        //ch
        //first part of ch
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, -1.0f);
        map.put(Feature.STRIDENT, +1.0f);
        list = new ArrayList<FeatureSet>();
        list.add(new FeatureSet(map));
        
        //second part of ch
        map = new HashMap<Feature, Float>();
        map.put(Feature.CONSONANT, +1.0f);
        map.put(Feature.SONORANT, -1.0f);
        map.put(Feature.CONTINUANT, +1.0f);
        map.put(Feature.STRIDENT, +1.0f);
        list.add(new FeatureSet(map));
        
        featureSetMap.put("ch", list);
        
        //ix
        featureSetMap.put("ix", featureSetMap.get("ah"));
        
        //ax
        featureSetMap.put("ax", featureSetMap.get("ah"));
        
        //axr
        featureSetMap.put("axr", featureSetMap.get("er"));
        
    }
    
    public static List<FeatureSet> lookup(String phone){
        return featureSetMap.get(phone);
    }

}
