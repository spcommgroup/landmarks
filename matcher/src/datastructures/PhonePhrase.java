package datastructures;
import java.util.ArrayList;
import java.util.List;


/**
 * An object that represents a phone phrase, which is essentially a list of 
 * {@code Phone}s with a start time.
 * 
 * @author Jessica Kenney
 */
public class PhonePhrase {
	public List<Phone> phrase;
	public Float time;
	public PhonePhrase() {
		this.phrase = new ArrayList<Phone>();
		this.time = 0.0f;
	}
	public void add(Phone phone){
		if(this.phrase.size() == 0 || this.time > phone.time) {
			this.time = phone.time;
		}
		this.phrase.add(phone);
	}
    public List<FeatureSet> featureSetSequence() {
	    List<FeatureSet> featureSetSequence = new ArrayList<FeatureSet>();
	    for (Phone phone : this.phrase){
	      try {
	        featureSetSequence.addAll(phone.featureSet());
	      } catch (NullPointerException e) {
	    	  System.out.println("Phone not found! "+ phone);
	    	  throw e;
	      }
	    }
	    return featureSetSequence;
    }
    public List<FeatureSet> reducedFeatureSetSequence() {
	    return reducedFeatureSetSequence("reduced");
    }
    public List<FeatureSet> reducedFeatureSetSequence(String type) {
	    List<FeatureSet> reducedFeatureSetSequence = new ArrayList<FeatureSet>();
	    for (Phone phone : this.phrase){
	      try { 
	        reducedFeatureSetSequence.addAll(phone.reducedFeatureSet(type));
	      } catch (NullPointerException e) {
	    	  System.out.println("Phone not found! "+ phone);
	    	  throw e;
	      }
	    }
	    return reducedFeatureSetSequence;
    }
    
	public int size() {
		return this.phrase.size();
	}
	public void print() {
		for(Phone phone : this.phrase){
			System.out.print(phone.value+" ");
		}
		System.out.print("\n");
	}
	public String toString() {
		return "PhonePhrase(" + this.phrase + ")";
	}
}
