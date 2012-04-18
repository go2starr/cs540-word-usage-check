import java.util.*;

/**
 *  Example
 *
 *  This class implements an example for the semantic spell checker.  It contains
 *  a list of words, constituting a portion of a sentence.
 *
 **/
public class Example {
    private List <Word> words;  
    private boolean isCorrect;  // True iff this was a positive example
    
    // C'tor
    public Example () {
	words = new ArrayList <Word> ();
    }

    // Add word to list
    public void addWord (Word w) {
	words.add (w);
    }

    // Return words
    public List <Word> getWords () {
	return words;
    }

    // Return a specific word
    public Word get (int i) {
	return words.get (i);
    }

    // Return the "center" word
    public String centerWord () {
	return words.get (7).word;
    }

    // Convert to string
    public String toString () {
	String ret = centerWord () + "::";
	for (Word w: words) {
	    if (w != null)
		ret += w.toString ();
	}
	return ret;
    }
    
    // some examples are positive, some negative
    public void setCorrect (boolean isCorrect) {
	this.isCorrect = isCorrect;
    }
    public boolean isCorrect () {
	return isCorrect;
    }
}
