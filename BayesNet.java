import java.util.*;
import java.io.*;

/********************************************************************************
 *  BayesNet 
 *
 *  This class implements a Naive-Bayes learning algorithm. A BayesNet, @bnet,
 *  is a list of BayesNode's.  Each BayesNode has parents which represent
 *  dependencies.
 *
 *  By assigning various network topologies, different dependencies are
 *  embedded in the Baye's net.
 *
 *  Training examples are used for calculating conditional probabilities,
 *  and testing examples are used to check the accuracy of the algorithm.
 *  
 ********************************************************************************/

abstract class BayesNet {
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

	// Train
	BayesNet1 bn1 = new BayesNet1 (wordX, wordY, ratioXOverY, trainSet, testSet);
	BayesNet2 bn2 = new BayesNet2 (wordX, wordY, ratioXOverY, trainSet, testSet);
	bn1.runTests (testSet);
	bn2.runTests (testSet);

    }

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
     *  Bayes Net Class
     ********************************************************************************/
    // The Bayes Net
    List <BayesNode> bnet;

    // train and test set
    List <Example> trainSet;
    List <Example> testSet;

    // words being tested
    String word1, word2;

    // ratioXoverY (unused)
    double ratio;


    /********************************************************************************
     *  guess - Returns the best guess for the word to be placed in @query.
     *
     *  The probability of each possible configuration of the example is calculated
     *  using the Bayes net.  The higher probability string is returned.
     *********************************************************************************/
    public String guess (Example query) {

	// Calculate probability using wordX and wordY
	query.get (7).word = word1;
	double pX = probability (query);
	query.get (7).word = word2;
	double pY = probability (query);

	// Return the more likely candidate
	if (pX > pY) {
	    return word1;
	} else {
	    return word2;
	}
    }


    /********************************************************************************
     *  Returns the full-joint probability of an example
     *
     *  P (query) = product (P (node[i] | parents (node[i])))
     ********************************************************************************/
    private double probability (Example query) {
	double p = 1;
	for (BayesNode n : bnet) {
	    try {
		p *= n.conditionalProbability (query);
	    } catch (Exception e) {
		System.exit (1);
	    }
	}
	return p;
    }
}    


/********************************************************************************
 *   BayesNet1 - First Bayes Net topology;
 *
 *   This Bayes net configuration utilizes the following concept:
 *   Given a sentence, a word is (likely) conditioned on the word
 *   immediately preceding it.
 *
 *   The network is layed out as follows:
 *
 *   [0] [1] ... [5] <- [6] <- (7) <- [8] <- [9] ... [15]
 *
 ********************************************************************************/

class BayesNet1 extends BayesNet {

    // C'tor
    BayesNet1 (String word1, String word2, double ratio, List <Example> trainSet, List <Example> testSet) {
	this.word1 = word1;
	this.word2 = word2;
	this.ratio = ratio;
	this.trainSet = trainSet;
	this.testSet = testSet;

	// Pass trainSet to BayesNode class
	BayesNode.setTrainset (trainSet);

	// Create a node for each word position
	bnet = new ArrayList <BayesNode> ();
	for (int i = 0; i < 15; i++) {
	    BayesNode bn = new BayesNode (i);
	    bnet.add (bn);
	}

	// Connect edges
	bnet.get (6).addParent (bnet.get (5));
	bnet.get (7).addParent (bnet.get (6));
	bnet.get (8).addParent (bnet.get (7));
	bnet.get (9).addParent (bnet.get (8));
    }
}


/********************************************************************************
 *   BayesNet2 - First Bayes Net topology;
 *
 *   This Bayes net configuration utilizes the following concept:
 *   Given a sentence, a word is (likely) conditioned on the words
 *   directly to the left and right.
 *
 *   The network is layed out as follows:
 *
 *   [0] [1] ... [5] <- [6]  <- (7) -> [8]    [9] ... [15]
 *                ^------------/   \-----------^
 *
 ********************************************************************************/

class BayesNet2 extends BayesNet {

    // C'tor
    BayesNet2 (String word1, String word2, double ratio, List <Example> trainSet, List <Example> testSet) {
	this.word1 = word1;
	this.word2 = word2;
	this.ratio = ratio;
	this.trainSet = trainSet;
	this.testSet = testSet;

	// Pass trainSet to BayesNode class
	BayesNode.setTrainset (trainSet);

	// Create a node for each word position
	bnet = new ArrayList <BayesNode> ();
	for (int i = 0; i < 15; i++) {
	    BayesNode bn = new BayesNode (i);
	    bnet.add (bn);
	}

	// Connect edges
	bnet.get (7).addParent (bnet.get (6));
	bnet.get (7).addParent (bnet.get (5));
	bnet.get (7).addParent (bnet.get (8));
	bnet.get (8).addParent (bnet.get (9));
    }
}


    
