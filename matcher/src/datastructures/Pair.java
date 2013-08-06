package datastructures;
/**
 * A generic holder for two variables.
 * 
 * @author Jessica Kenney
 */
public class Pair<U, V> {
	public U first;
	public V second;
	public Pair(U first, V second){
		this.first = first;
		this.second = second;
	}
}