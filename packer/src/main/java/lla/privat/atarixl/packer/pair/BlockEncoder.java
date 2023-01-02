package lla.privat.atarixl.packer.pair;

import java.util.List;

import lla.privat.atarixl.memory.Memory;

public class BlockEncoder {

	private final Memory memory;
	private final int minaddr;
	private final int maxaddr;

	Memory newMemory;

	List<Block> blocks;

	public BlockEncoder(Memory memory) {
		this.memory = memory;
		this.minaddr = memory.getMinAddr();
		this.maxaddr = memory.getMaxAddr();
	}

	// liefert -1 falls der aktuelle block nicht in der blocks Liste vorhanden ist.
	private int findBlock(Block block) {
		if (blocks.contains(block)) { // enthält unser Array das gesuchte Pair?
			for (int i = 0; i < blocks.size(); i++) { // Position heraussuchen
				if (blocks.get(i).equals(block)) {
					return i;
				}
			}
		}
		return -1;
	}
	// Die Idee ist, immer 2 hintereinander liegende Bytes zu einem Wert neuen
	// zusammen zu fassen, um es effektiver packen zu können

	public void compress(List<Block> blocks) {
		this.blocks = blocks;
		newMemory = new Memory();
		int newstart = memory.getMinAddr();
		newMemory.setMinAddr(newstart);

		int packed_index = 0;

		// Wir laufen Blockweise durch den Speicher
		// 0  1  2  3       4  5  6  7
		//40 41 42 43      44 45 46 47
		//80 81 82 83      84 85 86 87 ...

		// 120 121 122 123
		// 160 161 162 163
		// 200 201 202 203

		int x=0;
		int y=0;
		for (int i = minaddr; i <= maxaddr;) {

//			Block currentBlock = new Block(4,3);
//			currentBlock.set(memory.get(i), memory.get(i + 1), memory.get(i + 2), memory.get(i + 3),
//			     memory.get(i + 40), memory.get(i + 41), memory.get(i + 42), memory.get(i + 43),
//			     memory.get(i + 80), memory.get(i + 81), memory.get(i + 82), memory.get(i + 83));

    Block currentBlock = new Block(2,3);
    currentBlock.set(memory.get(i), memory.get(i + 1), 
         memory.get(i + 40), memory.get(i + 41), 
         memory.get(i + 80), memory.get(i + 81));

			x++;
			i+=2;
			if (x == 20) {
			  x = 0;
			  i += 80;
			  y++;
			}
			  
			int n = findBlock(currentBlock); // ein Block versuchen zu finden
			if (n == -1) {
				// das Pair ist nicht vorhanden ==> einen neuen erstellen
				blocks.add(currentBlock);

				n = findBlock(currentBlock); // erneut suchen und jetzt finden
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
