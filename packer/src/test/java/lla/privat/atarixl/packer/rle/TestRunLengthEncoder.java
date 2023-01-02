package lla.privat.atarixl.packer.rle;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.memory.Memory;


public class TestRunLengthEncoder {

  private RunLengthEncoder rleSUT;
  
  @Test
  public void test0to255() {

    int i=0x2000;
    Memory memory = new Memory();
    memory.setMinAddr(i);
    
    for (int j=0;j<256;j++) {
      for (int k=0;k<=j;k++) {
        memory.set(i++, j);
      }
    }
    memory.setMaxAddr(i - 1);
    
    rleSUT = new RunLengthEncoder(memory);
    Assert.assertEquals(0, rleSUT.getX());
    Assert.assertEquals(1, rleSUT.getY());
  }

  @Test
  public void test10bytes() {

    Memory memory = new Memory();
    int i=0x2000;
    memory.setMinAddr(i);
    
    memory.set(i++, 1); // packen zu X-Wert, 5(Anzahl), 1
    memory.set(i++, 1);
    memory.set(i++, 1);
    memory.set(i++, 1);
    memory.set(i++, 1);
    memory.set(i++, 0); // packen zu Y-Wert, 4(Anzahl)
    memory.set(i++, 0);
    memory.set(i++, 0);
    memory.set(i++, 0);
    memory.set(i++, 3); // nicht packen

    int endaddr = i - 1;
    memory.setMaxAddr(endaddr);
    
    rleSUT = new RunLengthEncoder(memory);
    Assert.assertEquals(4, rleSUT.getY()); // 1,2,3 sind vergeben

    rleSUT.compress();
    Memory compressed = rleSUT.getCompressed();

    int n=compressed.getMinAddr();
    
    Assert.assertEquals(2, compressed.get(n++)); // X-Wert
    Assert.assertEquals(5, compressed.get(n++)); // Anzahl
    Assert.assertEquals(1, compressed.get(n++)); // Wert

    Assert.assertEquals(4, compressed.get(n++)); // Y-Wert (Wert==0)
    Assert.assertEquals(4, compressed.get(n++)); // Anzahl

    Assert.assertEquals(3, compressed.get(n++)); // Wert
  }

}
