package datastructures;
import java.util.List;


/**
 * An object that represents a phone, which is essentially a string and a start time.
 * 
 * @author Jessica Kenney
 */
public class Phone {

	public String value;
	public Float time;
	public Phone(String value) {
		this.value = value;
		this.time = 0.0f;
	}
	public List<FeatureSet> featureSet(){
	    try {
	    	return FeatureSetLookup.lookup(this.value);
	      } catch (NullPointerException e) {
//	    	  System.out.println("Phone does not exist: "+ this.value);
	    	  throw e;
	      }
	}
}
