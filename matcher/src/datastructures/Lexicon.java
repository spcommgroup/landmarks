package datastructures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * An abstract class for storing the representations of words as lists
 * of phones (TODO: phones or phonemes?).
 * 
 * In the usual case, classes inheriting from {@code AbstractLexicon}
 * will load lexicon data from a file into the {@code lexicon} {@code Hashmap},
 * in their constructors.
 * 
 * @author Jason Paller-Rzepka
 */
public abstract class Lexicon {
    protected final HashMap<String, List<String>> lexicon;
    
    public Lexicon(){
        lexicon = new HashMap<String, List<String>>();
    }
    
    public boolean hasWord(String word){
        return lexicon.containsKey(word);
    }
    
    public List<String> getPhoneList(String word){
        if (!lexicon.containsKey(word)){
            throw new RuntimeException("Invalid word: " + word);
        }
        return lexicon.get(word);
    }

    public Set<String> getAllWords(){
        return lexicon.keySet();
    }
    
    private String[] getWords(String sentence){
        return sentence.replaceAll("[!,\n\"?.();:]","").toLowerCase().split(" ");
    }
    
    public List<FeatureSet> featureSetSequence(String sentence){
        List<FeatureSet> featureSetSequence = new ArrayList<FeatureSet>();
        for (String word : getWords(sentence)){
            if (!hasWord(word)){
                //System.out.println("Cannot find word: " + word);
                return null;
            }
            for (String phone : getPhoneList(word)){
                for (FeatureSet fs : FeatureSetLookup.lookup(phone)){
                    featureSetSequence.add(fs);
                }
            }
        }
        return featureSetSequence;
    }
    public List<FeatureSet> reducedFeatureSetSequence(String sentence){
        List<FeatureSet> reducedFeatureSetSequence = new ArrayList<FeatureSet>();
        for (String word : getWords(sentence)){
            if (!hasWord(word)){
                //System.out.println("Cannot find word: " + word);
                return null;
            }
            for (String phone : getPhoneList(word)){
                for (FeatureSet fs : ReducedFeatureSetLookup.lookup(phone)){
                    reducedFeatureSetSequence.add(fs);
                }
            }
        }
        return reducedFeatureSetSequence;
    }
    
    public List<Integer> correctPartitioning(String sentence){
        List<Integer> correctPartitioning = new ArrayList<Integer>();
        for (String word : getWords(sentence)){
            correctPartitioning.add(getPhoneList(word).size());
        }
        return correctPartitioning;   
    }
        
}
