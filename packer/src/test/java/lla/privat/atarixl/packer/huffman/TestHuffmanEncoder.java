package lla.privat.atarixl.packer.huffman;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import lla.privat.atarixl.memory.Buffer;
import lla.privat.atarixl.memory.Memory;

public class TestHuffmanEncoder {

  private HuffmanEncoder huffmanSUT;
  
  private Memory fillMemory(int startaddr, String text) {

    Memory memory = new Memory();
    int i=startaddr;
    memory.setMinAddr(startaddr);

    for (int j=0;j<text.length();j++) {
      int value = text.charAt(j);
      memory.set(i++, value);
    }
    memory.setMaxAddr(i - 1);
    return memory;
  }

  @Test
  public void justAMemoryTest() {
    Memory memory = fillMemory(0x2000, "Hallo World");
    
    Assert.assertEquals(0x2000, memory.getMinAddr());
    Assert.assertEquals(0x200a, memory.getMaxAddr());
    
    Assert.assertEquals(memory.get(0x2000), (byte)'H');
    Assert.assertEquals(memory.get(0x2006), (byte)'W');
  }
  
  @Test
  public void test10bytes() {

    Memory memory = new Memory();
    int i=0x2000;
    memory.setMinAddr(i);
    memory.set(i++, '1');
    memory.set(i++, '1');
    memory.set(i++, '1');
    memory.set(i++, '1');
    memory.set(i++, '1');

    memory.set(i++, '2');
    memory.set(i++, '2');
    memory.set(i++, '2');
    memory.set(i++, '2');

    memory.set(i++, '3');
    memory.set(i++, '3');
    memory.set(i++, '3');
    memory.set(i++, '3');
    memory.set(i++, '3');
    memory.set(i++, '3');
    memory.set(i++, '3');
    memory.set(i++, '3');

    memory.set(i++, '5');
    memory.set(i++, '5');
    memory.set(i++, '5');
    
    memory.setMaxAddr(i-1);
    
    int[] freq = HuffmanEncoder.initFrequencies();
    huffmanSUT = new HuffmanEncoder(memory);
    huffmanSUT.prepareFrequences(freq);
    String encoded = huffmanSUT.compress();

//  String decoded = huffmanSUT.decompress(encoded);

    HuffmanDecoder decoder = new HuffmanDecoder(encoded, HuffmanEncoder.getTreeout());
    Buffer buffer = decoder.decompress();

    String actual = buffer.toString().substring(0, 20);
    Assert.assertEquals("11111222233333333555", actual);
  }
  

  @Test
  public void testCompress() {

    int startaddr = 0x2000;
    String aString = "Dies ist ein bloeder Text, der keinerlei Sonderzeichen enthalten soll, alles ausser Satzzeichen ist verpoennt.";
    Memory memory = fillMemory(startaddr, aString);

    int endaddr = startaddr + aString.length() - 1;
    Assert.assertEquals(memory.get(endaddr), '.');
    
    int[] freq = HuffmanEncoder.initFrequencies();
    huffmanSUT = new HuffmanEncoder(memory);
    huffmanSUT.prepareFrequences(freq);
    String encoded = huffmanSUT.compress();


    HuffmanDecoder decoder = new HuffmanDecoder(encoded, HuffmanEncoder.getTreeout());
    Buffer buffer = decoder.decompress();

    String actual = buffer.toString().substring(0, aString.length());
    Assert.assertEquals(aString, actual);
  }


  @Test
  public void testCompressFirst8bit() {

    int startaddr = 0x00;
    Memory memory = new Memory();

    int i= startaddr;
    int n=1;
    memory.setMinAddr(startaddr);
    for (int x=0;x<256;x++) {
      for (int y=0;y<n;y++) {
        memory.set(i++, x);
      }
      n=n + 10;
      if (n>255) {
        n=3;
      }
    }
    int endaddr = i - 1;
    memory.setMaxAddr(endaddr);
    
    int[] freq = HuffmanEncoder.initFrequencies();
    huffmanSUT = new HuffmanEncoder(memory);
    huffmanSUT.prepareFrequences(freq);
    String encoded = huffmanSUT.compress();

    HuffmanDecoder decoder = new HuffmanDecoder(encoded, HuffmanEncoder.getTreeout());
    Buffer buffer = decoder.decompress();

    i = 0;
    byte[] buf = buffer.getBuffer();
    
    System.out.println("Teste bytes: " + (endaddr - startaddr + 1) +" bytes");
    for (int j=startaddr;j<=endaddr;j++) {
      byte a =(byte)memory.get(j);
      byte b =buf[i];
      if (a != b) {
        Assert.fail("ab position i:"+i +" ist das Ergebnis nicht gleich");
      }
      i++;
    }
  }

  @Test
  public void testCompressUpper8bit() {

    int startaddr = 0x0;
    Memory memory = new Memory();

    int i= startaddr;
    memory.setMinAddr(i);
    
    memory.set(i++, 129);
    memory.set(i++, 129);
    memory.set(i++, 129);
    memory.set(i++, 129);
    memory.set(i++, 129);
    memory.set(i++, 129);
    memory.set(i++, 127);

    int endaddr = i - 1;
    memory.setMaxAddr(endaddr);
    
    int[] freq = HuffmanEncoder.initFrequencies();
    huffmanSUT = new HuffmanEncoder(memory);
    huffmanSUT.prepareFrequences(freq);
    String encoded = huffmanSUT.compress();

//    Memory newMemory = huffmanSUT.convertEncodedToMemory(encoded);
//    Assert.assertEquals(1, newMemory.size());
    
    HuffmanDecoder decoder = new HuffmanDecoder(encoded, HuffmanEncoder.getTreeout());
    Buffer buffer = decoder.decompress();

    i = 0;
    byte[] buf = buffer.getBuffer();
    
    System.out.println("Teste bytes: " + (endaddr - startaddr + 1) +" bytes");
    for (int j=startaddr;j<=endaddr;j++) {
      byte a =(byte)memory.get(j);
      byte b =buf[i]; 
      if (a != b) {
        Assert.fail("ab position i:"+i +" ist das Ergebnis nicht gleich");
      }
      i++;
    }
  }

  //  @Ignore("Macht was es soll")
  @Test
  public void testBinToDec() {
    Assert.assertEquals(0, HuffmanEncoder.binToDec("0"));
    Assert.assertEquals(1, HuffmanEncoder.binToDec("1"));
    Assert.assertEquals(2, HuffmanEncoder.binToDec("10"));
    Assert.assertEquals(3, HuffmanEncoder.binToDec("11"));    
  }
  
  @Ignore("Macht was es soll")
  @Test
  public void testDecimalToBinString() {
    Assert.assertEquals("0", Integer.toString(0,2));  
    Assert.assertEquals("1", Integer.toString(1,2));
    Assert.assertEquals("10", Integer.toString(2,2));
    Assert.assertEquals("10000000", Integer.toString(128,2));
  }
  
  @Ignore
  @Test
  public void testStringvalueof() {
    System.out.println("Zahl: '"+ Integer.toHexString(255)+"'");
  }
}
