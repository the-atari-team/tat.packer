package lla.privat.atarixl.packer.rle;

import lla.privat.atarixl.memory.Buffer;
import lla.privat.atarixl.memory.Memory;

public class RunLengthEncoder {

  private final Memory memory;
  private final int minaddr;
  private final int maxaddr;

  private int x;
  private int y;

  private int[] findMinXY;

  Buffer compressed;
  int compressed_startaddr;
  int compressed_endaddr;

  public RunLengthEncoder(Memory memory) {
    this.memory = memory;
    this.minaddr = memory.getMinAddr();
    this.maxaddr = memory.getMaxAddr();
    findMinXY = new int[256];

    generateMinXMinY();
  }

  private void generateMinXMinY() {
    int value;
    for (int i = minaddr; i <= maxaddr; i++) {
      value = memory.get(i);
      findMinXY[value] = findMinXY[value] + 1;
    }

    int[] kleinst = new int[2];

    for (int i = 0; i < 2; i++) {
      int wert = 65535;
      int min = 0;
      for (int a = 0; a < 256; a++) {
        if (findMinXY[a] < wert) {
          wert = findMinXY[a];
          min = a;
        }
      }
      kleinst[i] = min;
      findMinXY[min] = 65535; // damit wir den kleinsten nicht nochmal finden fett machen
    }

    x = kleinst[0];
    y = kleinst[1];
  }

  public void compress() {
    compressed = new Buffer();

    int anz = 0;
    int l = 0;
    int oldwert;
    int memory_ptr = minaddr;
    int wert = memory.get(memory_ptr);
    int x_byte = getX();
    int y_byte = getY();

    do {
      // hole byteanzahl
      do {
        anz = anz + 1;
        oldwert = memory.get(++memory_ptr);
        if (memory_ptr > maxaddr)
          break;
      }
      while (oldwert == wert && anz < 255);
//        while (!(oldwert!=wert || anz==255 || memory_ptr == maxaddr));

      if (anz > 2 && wert == 0) {
        compressed.add(y_byte); // pack 0 Byte
        compressed.add(anz);
        l += 2;
      }

      else {
        if (anz > 3 || wert == x_byte || wert == y_byte) {
          compressed.add(x_byte);
          compressed.add(anz);
          compressed.add(wert);
          l += 3;
        }
        else {
          for (int a = 1; a <= anz; a++) {
            compressed.add(wert);
            l++;
          }
        }
      }
      anz = 0;
      wert = oldwert;
    }
    while (memory_ptr <= maxaddr);

    compressed_startaddr = maxaddr - l + 1;
    compressed_endaddr = maxaddr;
  }

  public Memory getCompressed() {
    Memory newMemory = new Memory();
    
    int j=0;
    int newstart = getCompressedStartAddr();
    newMemory.setMinAddr(newstart);
    int i;
    for (i = newstart ; i <= getCompressedEndAddr() ; i++) {
      Integer anInt = compressed.get(j++);
      newMemory.set(i, anInt);
    }
    newMemory.setMaxAddr(i-1);

    return newMemory;
  }

  public int getCompressedStartAddr() {
    return compressed_startaddr;
  }
  
  public int getCompressedEndAddr() {
    return compressed_endaddr;
  }
  
  /**
   * 
   * @return kleinsten Wert von der Anzahl her
   */
  public int getX() {
    return x;
  }

  /**
   * 
   * @return zweit kleinsten Wert von der Anzahl her (>= getX())
   */
  public int getY() {
    return y;
  }

}
