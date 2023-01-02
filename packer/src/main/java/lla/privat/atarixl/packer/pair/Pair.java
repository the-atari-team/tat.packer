package lla.privat.atarixl.packer.pair;

import java.util.Objects;

/*
 * Hilfklasse für 2 Bytes die nebeneinander liegen 
 */
public class Pair {
	int n0, n1;

	public Pair(int n0, int n1) {
		this.n0 = n0;
		this.n1 = n1;
	}

	public int getLeft() {return n0;}
	public int getRight() {return n1;}
	
	@Override
	public int hashCode() {
		return Objects.hash(n0, n1);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Pair other = (Pair) obj;
		return n0 == other.n0 && n1 == other.n1;
	}

}
