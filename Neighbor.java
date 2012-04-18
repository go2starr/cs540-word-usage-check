/**
 *  Neighbor - this class is necessary for placing examples
 *  into a priority queue (used in CBR).
 **/
class Neighbor implements Comparable {
    int similarity;
    Example ex;

    public Neighbor (int s, Example ex) {
	this.similarity = s;
	this.ex = ex;
    }

    public int compareTo (Object other) {
	return ((Neighbor) other).similarity - this.similarity;
    }
}
