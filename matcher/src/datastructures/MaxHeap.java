package datastructures;

import java.util.ArrayList;
import java.util.List;

//TODO: There *must* be a way to make java's PriorityQueue work here.  How?
/**
 * An max heap (a.k.a. priority queue) for maintaining a collection of {@code HeapNode}s.
 * 
 * @author Jason Paller-Rzepka
 */
public class MaxHeap {
    private List<HeapNode> elements;
    public MaxHeap(List<HeapNode> elements) {
        this.elements = new ArrayList<HeapNode>(elements);
        buildHeap();
    }
    
    private void buildHeap(){
        for (int i = parentIndex(elements.size() - 1); i >= 0; i++){
            maxHeapify(i);
        }
    }
    
    /**
     * Requires heap is not empty.
     */
    public HeapNode extractMax(){
        HeapNode max =  elements.get(0);
        if (elements.size() > 1){
            elements.set(0, elements.remove(elements.size() - 1));
            maxHeapify(0);
        } else {
            elements.remove(0);
        }
        return max;
    }
    
    private void maxHeapify(int index){
        int leftIndex = leftChildIndex(index);
        int rightIndex = rightChildIndex(index);
        float leftProb = (leftIndex < elements.size()) ? elements.get(leftIndex).getProbability() : -1.0f;
        float rightProb = (rightIndex < elements.size()) ? elements.get(rightIndex).getProbability() : -1.0f;
        if (elements.get(index).getProbability() >= leftProb && elements.get(index).getProbability() >= rightProb){
            //Already a valid max heap.
            return;
        } else {
            int switchIndex = (leftProb > rightProb) ? leftIndex : rightIndex;
            HeapNode switchNode = elements.get(switchIndex);
            elements.set(switchIndex, elements.get(index));
            elements.set(index, switchNode);
            maxHeapify(switchIndex);
        }
    }
    
    public void insert(HeapNode node){
        elements.add(node);
        int index = elements.size() - 1;
        while (index != 0 && elements.get(index).getProbability() > elements.get(parentIndex(index)).getProbability()){
            index = parentIndex(index);
            maxHeapify(index);
        }
    }
    
    private int parentIndex(int index){
        return (index + index % 2) / 2 - 1;
    }
    
    private int leftChildIndex(int index){
        return 2 * index + 1;
    }
    
    private int rightChildIndex(int index){
        return 2 * index + 2;
    }
    
    public boolean isEmpty(){
        return elements.isEmpty();
    }

}
