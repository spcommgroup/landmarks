package datastructures;

import java.util.HashSet;
import java.util.Set;


/**
 * An object representing a single node in a lexical tree.
 * 
 * A {@code LexTreeNode} stores:   
 *  The {@code FeatureSet} associated with the node.
 *  A set of words which are represented exactly by the sequence of
 *  {@codeFeatureSet}s encountered when traveling from the root of the
 *  lexical tree to this node.
 *  References to this node's children.
 * 
 * @author Jason Paller-Rzepka
 *
 */
public class LexTreeNode {
    //Invariant: This node always refers to the same node on the tree, even if it is modified (e.g. if words or children are added).
    private FeatureSet featureSet;
    private Set<String> words;
    private Set<LexTreeNode> children;
    
    public LexTreeNode(FeatureSet featureSet, Set<String> words, Set<LexTreeNode> children) {
        this.featureSet = featureSet;
        this.words = new HashSet<String>(words);
        this.children = new HashSet<LexTreeNode>(children);
    }
    
    public int getHeight(){
        int highestChildHeight = 0;
        int currentChildHeight;
        for (LexTreeNode child : children){
            currentChildHeight = child.getHeight();
            if (currentChildHeight > highestChildHeight){
                highestChildHeight = currentChildHeight;
            }
        }
        return highestChildHeight + 1;
    }
    
    public boolean hasWords(){
        return !words.isEmpty();
    }
    
    public void addWord(String word){
        words.add(word);
    }
    
    public Set<String> getWords(){
        return new HashSet<String>(words);
    }
    
    
    public void addChild(LexTreeNode child){
        children.add(child);
    }
    
    public boolean featureSetEquals(FeatureSet otherFeatureSet){
        return featureSet.equals(otherFeatureSet);
    }
    
    public Set<LexTreeNode> getChildren(){
        return new HashSet<LexTreeNode>(children);
    }

    public FeatureSet getFeatureSet() {
       return featureSet;
    }
    
    
    
}
