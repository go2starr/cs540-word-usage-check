/********************************************************************************
 *   BayesNode - Implements a node for a BayesNet
 *
 *   This class implements the majority of probability calculations.
 *   Each node is capable of producing a conditional probability, given
 *   a certain probibalistic proposition.
 *
 *   This is done using a CPT and summing over worlds where the proposition
 *   holds.
 *
 ********************************************************************************/
import java.util.*;
import java.io.*;

public class BayesNode {

    // Examples for calculating all probabilities
    static List <Example> trainSet;
    
    // Parent nodes
    List <BayesNode> parents;
    
    // Position within example sentences
    int position;
    
    // Probability map - cache calculated values
    Map <Proposition, Double> map;
    
    
    /********************************************************************************
     *  BayesNode
     ********************************************************************************/
    BayesNode (int pos) {
	position = pos;
	parents = new ArrayList <BayesNode> ();
	map = new HashMap <Proposition, Double> ();
    }
    
    /********************************************************************************
     *  setTrainset
     ********************************************************************************/
    static void setTrainset (List <Example> t) {
	trainSet = t;
    }
    
    /********************************************************************************
     *  addParent
     ********************************************************************************/
    void addParent (BayesNode bn) {
	parents.add (bn);
    }

    
    /********************************************************************************
     *  conditionalProbability - returns the conditional probability of an
     *  example as given by this nodes parents in the Baye's net.
     *
     *  Calculate P(x[i] | x[i-1],..,x[1]) = P(x[i] | Parents (x[i])) = P(x[i] ^ Parents(x[i]) / Parents(x[i])
     ********************************************************************************/
    double conditionalProbability (Example query) {

	// Create the proposition: P(centerWord)
	PosProposition Prop = new PosProposition (query.centerWord ());

	// For each parent Prop &= Parent
	for (int i = 0; i < parents.size (); i++) {
	    int position = parents.get (i).position;
	    String ppos = query.get (position).pos;
	    Prop.set (position, ppos);
	}
	return probability (Prop);
    }	

    
    /********************************************************************************
     *  Probability - returns the probability that this node can take on the
     *                value in proposition e.
     *
     *  @query Constrains the possible world states, and we sum over examples
     *         where the constraint holds.
     ********************************************************************************/
	double probability (Proposition query) {
	    
	    // Start counts at 1
	    int count = 1;
	    
	    // Check previously calculated first
	    if (map.containsKey (query)) {
		return map.get (query);
	    }
	    
	    // For every example
	    for (Example e : trainSet) {
		// Check if the statement is valid
		if (query.allows (e)) {
		    count++;
		} 
	    }
	    
	    // Store it
	    double p = (double) count / trainSet.size ();
	    map.put (query, p);
	    
	    return p;
	}


    /********************************************************************************
     *  toString
     ********************************************************************************/
    public String toString () {
	return "" + position;
    }
}
