import java.util.*;
import java.io.*;

/********************************************************************************
 *   CBR
 *
 *   This class implements a case-based reasoning algorithm.  A CBR
 *   implements a similarity method, which returns a score of similarity
 *   between two examples.
 *
 *   When estimating the appropriate center word, it uses the K nearest
 *   neighbors, and chooses the majority choice of these neighbors.
 *
 ********************************************************************************/

abstract class CBR {

    /********************************************************************************
     *  main - Driver class for CBR parsing and testing
     ********************************************************************************/
    public static void main (String [] args) {

	// Verify that the correct number of command-line arguments were passed in
	if(args.length != 5) {
	    System.err.println("usage: java CBR wordX wordY fractionXoverY fileOfTrainingCases fileOfTestPhrases");
	    System.exit(1);
	}

	// Initialize the input variables
	String wordX = args[0];
	String wordY = args[1];
	double ratioXOverY = Double.parseDouble(args[2]);
	String trainFilename = args[3];
	String testFilename = args[4];

	// Parse train examples
	List <Example> trainSet = null;
	try {
	    trainSet = Parser.readFile (trainFilename);
	} catch (IOException e) {
	    System.err.println ("Unable to open train set " + trainFilename);
	    System.exit (1);
	}

	// Parse test examples
	List <Example> testSet = null;
	try {
	    testSet = Parser.readFile (testFilename);
	} catch (IOException e) {
	    System.err.println ("Unable to open train set " + trainFilename);
	    System.exit (1);
	}

	// Train CBRs
	CBRDist1 CBR1 = new CBRDist1 (trainSet, wordX, wordY);
	CBRDist2 CBR2 = new CBRDist2 (trainSet, wordX, wordY);

	// Test CBRs
	CBR1.runTests (testSet);
	CBR2.runTests (testSet);
    }
    

    
    /********************************************************************************
     *  Context based reasoning
     ********************************************************************************/
    // Number of neighbors to use
    static int K;
    static final int KMAX = 21;
    static final int KRATIO = 5;

    // Size of example input
    static final int exampleSize = 15;

    // Train examples
    protected List <Example> trainSet;

    // Word assignments
    protected String word1, word2;
    

    /********************************************************************************
     *  runTests - tests against test set and prints results
     ********************************************************************************/
    public void runTests (List <Example> testSet) {
	
	int noCorrect = 0;
	int noWrong = 0;
	int noTested = 0;
	List <String> failedExamples = new ArrayList <String> ();

	System.out.println ("========================================");
	System.out.printf ("    Results for %s vs. %s\n", word1, word2);
	System.out.println ("========================================");

	// Test CBR
	for (Example ex: testSet) {
	    // Skip incorrect usages
	    if (!ex.isCorrect ())
		continue;

	    // Otherwise, test
	    noTested++;
	    String correct = ex.centerWord ();
	    String estimated = guess (ex);

	    // And update stats
	    if (!correct.equals (estimated)) {
		noWrong++;
		failedExamples.add (ex.toString ());
	    } else {
		noCorrect++;
	    }
	}
	// Print results	
	System.out.println ("  Number of train examples: " + trainSet.size ());
	System.out.println ("  Number of test examples: " + noTested);
	System.out.println ("  Number answered correctly: " + noCorrect);
	System.out.println ("  Number answered incorrectly: " + noWrong);
	System.out.printf ("  Accuracy: %f", (float) noCorrect / noTested);
	
	if (failedExamples.size () > 0) {
	    System.out.println ("\n  Failed examples:");
	    for (int i = 0; i < failedExamples.size (); i++)
		System.out.println (failedExamples.get (i));
	}
	System.out.println ("\n\n");

    }

    /********************************************************************************
     *  Guess - returns a best guess for the appropriate word choice
     *          based on the K nearest neighbors.
     ********************************************************************************/
    public String guess (Example query) {
	PriorityQueue <Neighbor> pq = new PriorityQueue <Neighbor> ();
	for (Example ex : trainSet) {

	    // Score this example
	    int s = score (ex, query);
	    Neighbor n = new Neighbor (s, ex);
	    
	    // Add to queue
	    pq.add (n);
	}

	// Return the majority of K nearest
	int word1Score = 0;
	int word2Score = 0;

	// Pull off the K best
	for (int i = 0; i < K; i++) {
	    Neighbor n = pq.poll ();
	    String center = n.ex.centerWord ();
	    if (center.equals (word1)) {
		word1Score ++;
	    } else if (center.equals (word2)) {
		word2Score ++;
	    } else {
		System.err.println ("Bad center word: " + center);
		System.exit (1);
	    }
	}
	// Return most frequently seen word 
	return word1Score > word2Score ? word1 : word2;
    }

    /********************************************************************************
     *  score - Abstract method implemented by any CBR.
     *          returns a similarity score.
     ********************************************************************************/
    abstract int score (Example ex1, Example ex2);
}


/********************************************************************************
 *    CBRDist1 - Case-based reasoning metric one.
 *
 *    This CBR metric is based on the following:
 *       1. Position specific POS matching
 *
 *    "Points" are awarded for similarities, and therefore
 *    the query word returning the higher score will be
 *    returned.
 *
********************************************************************************/
class CBRDist1 extends CBR {

    // Weight assignments for neighboring parts of speech
    static final int [] weight = {0,0,0,1,1,3,7,0,5,2,1,1,0,0,0}; 

    // c'tor
    public CBRDist1 (List <Example> trainSet, String word1, String word2) {
	this.trainSet = trainSet;
	this.word1 = word1;
	this.word2 = word2;
	K = trainSet.size () > KMAX * KRATIO ? KMAX : trainSet.size () / KRATIO;
    }


    /********************************************************************************
     *  Score - returns a similarity score based on matching POS at a given
     *          position.
     ********************************************************************************/
    protected int score (Example ex1, Example ex2) {
	int score = 0;
	for (int i = 0; i < exampleSize; i++) {
	    String pos1 = ex1.get (i).pos;
	    String pos2 = ex2.get (i).pos;
	    if (pos1.equals (pos2))
		score += weight [i];
	}
	return score;
    }
}


/********************************************************************************
 *    CBRDist2 - Case-based reasoning metric two.
 *
 *    This CBR metric is based on the following:
 *        Algorithmic "Edit-Distance"
 *
 *    The number of edits needed to rearrange one
 *    examples part of speech into another is returned.
 *
 *
********************************************************************************/
class CBRDist2 extends CBR {

    // Weight assignments for neighboring parts of speech
    static final int [] weight = {0,0,0,0,1,3,6,0,6,2,1,0,0,0,0}; 

    // c'tor
    public CBRDist2 (List <Example> trainSet, String word1, String word2) {
	this.trainSet = trainSet;
	this.word1 = word1;
	this.word2 = word2;
	K = trainSet.size () > KMAX * KRATIO ? KMAX : trainSet.size () / KRATIO;
    }


    /********************************************************************************
     *  Score - returns a similarity score based on matching POS at a given
     *          position.
     ********************************************************************************/
    protected int score (Example ex1, Example ex2) {
	int [][] d = new int [exampleSize + 1][exampleSize + 1];
	for (int i = 0; i <= exampleSize; i++) {
	    d [i][0] = 0;
	    d [0][i] = 0;
	}
	for (int j = 1; j <= exampleSize; j++) {
	    for (int i = 1; i <= exampleSize; i++) {
		String w1 = ex1.get (i-1).pos;
		String w2 = ex2.get (j-1).pos;
		if (w1.equals (w2)) {
		    d [i][j] = d [i-1][j-1];
		} else {
		    int w = weight [i-1];
		    d [i][j] = min (min (d [i-1][j] + w, // delete
					 d[i][j-1] + w),  // insert
				    d [i-1][j-1] + w);    // substitution
		}
	    }
	}		
	return - d[exampleSize][exampleSize];
    }

/********************************************************************************		
 *  Min -
 ********************************************************************************/
    int min (int l, int r) {
	return l > r ? l : r;
    }
}

    


