import java.util.*;
import java.io.*;

/**
 *  Parser - parses an input file and produces a list of examples
 **/
public class Parser {

    private static final int wordsPerLine = 15;

    /*
     *      Parses input file and returns a list of examples
     */
    public static List <Example> readFile (String filename) throws IOException {
	List <Example> examples = new ArrayList <Example> ();
	Scanner scan = new Scanner (new FileReader (filename));
	boolean isCorrect = true; // pos/neg examples alternate
	while (scan.hasNext ()) {
	    Example ex = new Example ();
	    String line = scan.nextLine ();
	    Scanner lscan = new Scanner (line);
	    try {
		for (int i = 0; i < wordsPerLine; i++) {
		    String word = lscan.next ();
		    lscan.next (); // consume left bracket
		    String pos = lscan.next ();
		    String stem = lscan.next ();
		    lscan.next (); // consume right bracket
		    Word w = new Word(word, pos, stem);
		    ex.addWord (w);
		    ex.setCorrect (isCorrect);
		}
		examples.add (ex);
		isCorrect = !isCorrect;
	    } catch (NoSuchElementException e) {
		// may be trailing whitespace
	    }
	}
	return examples;
    }

    
}
