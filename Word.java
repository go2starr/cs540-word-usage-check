/**
 *  Word - this is a container class for the three strings which constitute
 *         a word: the word, the stem, and part of speech
 *  
 **/
public class Word {

    String pos;
    String stem;
    String word;

    public Word (String word, String pos, String stem) {
	this.word = word;
	this.pos = pos;
	this.stem = stem;
    }

    public String toString () {
	String ret = "(" + word + ", " + pos + ")";
	return ret;
    }

}
