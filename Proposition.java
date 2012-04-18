/**
 *  Generic class for probability propositions.
 **/
import java.util.*;
import java.io.*;

abstract class Proposition {
    // Identifier for variables which can take on any value
    static final String FREE = "__FREE__";

    // allows - returns true if the example holds in this proposition
    abstract boolean allows (Example ex);
}


/**
 *  PosProposition - a proposition which specifies values
 *  for parts of speech at each given position, and a
 *  word for the center.
 **/
class PosProposition extends Proposition {
    // Parts of speech for this example
    List <String> myPos;
    // Center word
    String myCenter;
    
    // c'tor - create a non-restrictive Proposition (true)
    PosProposition (String center) {
	myCenter = center;
	myPos = new ArrayList <String> ();
	for (int i = 0; i < 15; i++)
	    myPos.add (FREE);
    }   

    // Get pos
    public String get (int i) {
	return myPos.get (i);
    }

    // Set pos
    public void set (int i, String s) {
	myPos.set (i, s);
    }
    
    // Check whether an example holds under this proposition
    boolean allows (Example ex) {

	// Center must match
	if (!myCenter.equals (ex.centerWord ()))
	    return false;

	// Check if the query is valid
	for (int i = 0; i < 15; i++) {
	    String pPos = myPos.get (i);
	    String ePos = ex.get (i).pos;

	    // If free, ignore
	    if (pPos.equals (FREE))
		continue;

	    // Otherwise, make sure they match
	    if (!pPos.equals (ePos))
		return false;
	}
	return true;
    }
}

