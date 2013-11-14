package datastructures;

/**
 * An object for storing information about a single partial match in a {@code MaxHeap} (i.e. priority queue).
 * 
 * Specifically, a {@HeapNode} includes a reference to the {@code LexTreeNode}
 * at the tail end of the partial match, the current length of the partial match,
 * and the probability that the partial match matches the list of {@code FeatureSet}s
 * it has been compared to.
 * 
 * @author Jason Paller-Rzepka
 *
 */
public class HeapNode {
    private final LexTreeNode lexTreeNode;
    private final float probability;
    private final int matchLength;
    
    public HeapNode(LexTreeNode lexTreeNode, float probability, int matchLength) {
        this.lexTreeNode = lexTreeNode;
        this.probability = probability;
        this.matchLength = matchLength;
    }
    
    public LexTreeNode getLexTreeNode(){
        return lexTreeNode;
    }
    
    public float getProbability(){
        return probability;
    }
    public int getMatchLength(){
        return matchLength;
    }
}
