package setoflines;

import java.util.HashSet;
import java.util.LinkedList;

public class Bucket {

	private int value;
	private HashSet<PotentialLine> potential_lines;
	private Bucket previous_bucket;
	private Bucket next_bucket;

	public Bucket(int value) {
		this.value = value;
		this.potential_lines = new HashSet<PotentialLine>();
		this.previous_bucket = null;
		this.next_bucket = null;
	}

	public void setPreviousBucket(Bucket b) {
		this.previous_bucket = b;
	}

	public void setNextBucket(Bucket b) {
		this.next_bucket = b;
	}

	public Bucket getPreviousBucket() {
		return this.previous_bucket;
	}

	public Bucket getNextBucket() {
		return this.next_bucket;
	}

	public int getValue() {
		return this.value;
	}

	public void addLine(PotentialLine pl) {
		this.potential_lines.add(pl);
	}

	public void removeLine(PotentialLine pl) {
		this.potential_lines.remove(pl);
	}

	public PotentialLine getPotentialLine() {
		return this.potential_lines.iterator().next();
	}

	public boolean isEmpty() {
		return this.potential_lines.isEmpty();
	}
}
