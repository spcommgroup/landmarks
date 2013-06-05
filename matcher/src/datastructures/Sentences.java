package datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for storing a sequence of sentences (intended to be dealt with by a {@code Matcher}).
 * 
 * @author Jason Paller-Rzepka
 *
 */
public abstract class Sentences {
    protected List<String> sentences;
    
    public List<String> getSentences(){
        return new ArrayList<String>(sentences);
    }
}
