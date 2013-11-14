package matcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import datastructures.FeatureSet;
import datastructures.FeatureSetLookup;
import datastructures.LexTreeNode;
import datastructures.Lexicon;

/**
 * An object for building a full lexical tree from an {@code AbstractLexicon}.
 * 
 * @author Jason Paller-Rzepka
 *
 */
public class LexTreeFactory {

    private Lexicon lexicon;
    
    public LexTreeFactory(Lexicon lexicon){
        this.lexicon = lexicon;
    }
    
    public LexTreeNode lexTree(){
        LexTreeNode root = new LexTreeNode(null, new HashSet<String>(), new HashSet<LexTreeNode>());
        for (String word : lexicon.getAllWords()){
            addToLexTree(word, root);
        }
        return root;
    }
    
    /**
     * Modifies the lexical tree rooted at root!
     */
    private void addToLexTree(String word, LexTreeNode root){

        List<FeatureSet> featureSets = new ArrayList<FeatureSet>();
        for (String phone : lexicon.getPhoneList(word)){
            for (FeatureSet fs : FeatureSetLookup.lookup(phone)){
                featureSets.add(fs);
            }
        }

        LexTreeNode currentNode = root;
        boolean makeNewChild;
        for (FeatureSet featureSet : featureSets){
            //Assume we'll need to create a new child at this step...
            makeNewChild = true;
            for (LexTreeNode child : currentNode.getChildren()){

                if (child.featureSetEquals(featureSet)){
                    currentNode = child;
                    //...unless we find a suitable match.
                    makeNewChild = false;
                    break;
                }
            }
            if (makeNewChild){
                LexTreeNode newChild = new LexTreeNode(featureSet, new HashSet<String>(), new HashSet<LexTreeNode>());
                currentNode.addChild(newChild);
                currentNode = newChild;
            }
        }
        currentNode.addWord(word);
        
    }

}
