package lla.privat.atarixl.packer.pair;

import java.util.List;

import lla.privat.atarixl.memory.Memory;

public class PairEncoder {

	private final Memory memory;
	private final int minaddr;
	private final int maxaddr;

	Memory newMemory;

	List<Pair> pairs;

	public PairEncoder(Memory memory) {
		this.memory = memory;
		this.minaddr = memory.getMinAddr();
		this.maxaddr = memory.getMaxAddr();
	}

	// liefert -1 falls der aktuelle pair nicht in der pairs liste vorhanden ist.
	private int findPair(Pair pair) {
		if (pairs.contains(pair)) { // enthält unser Array das gesuchte Pair?
			for (int i = 0; i < pairs.size(); i++) { // Position heraussuchen
				if (pairs.get(i).equals(pair)) {
					return i;
				}
			}
		}
		return -1;
	}
	// Die Idee ist, immer 2 hintereinander liegende Bytes zu einem Wert neuen
	// zusammen zu fassen, um es effektiver packen zu können

	public void compress(List<Pair> pairs) {
		this.pairs = pairs;
		newMemory = new Memory();
		int newstart = memory.getMinAddr();
		newMemory.setMinAddr(newstart);

		int packed_index = 0;

		for (int i = minaddr; i <= maxaddr; i += 2) {

			Pair currentPair = new Pair(memory.get(i), memory.get(i + 1));

			int n = findPair(currentPair); // ein Pair versuchen zu finden
			if (n == -1) {
				// das Pair ist nicht vorhanden ==> einen neuen erstellen
				pairs.add(currentPair);

				n = findPair(currentPair); // erneut suchen und jetzt finden
			}
			newMemory.set(newstart + packed_index, n);
			packed_index++;
		}
		newMemory.setMaxAddr(newstart + packed_index - 1);

//		System.out.println("length " + packed_index);
//		System.out.println(" pairs " + pairs.size());
//		
//		for (int i=0;i<packed_index;i++) {
//			if (i % 20 == 0) {
//				System.out.println();
//			}
//			char ch = (char) ('A' + packedScreen[i]);
//			System.out.print(ch);
//		}
	}

	public Memory getCompressed() {
		return newMemory;
	}

}
