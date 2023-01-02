package lla.privat.atarixl.packer.huffman;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import lla.privat.atarixl.memory.Buffer;

public class TestHuffmanDecoder {

  private HuffmanDecoder huffmanDecoderSUT;

  @Before
  public void setUp() {
    String encode = "11011011011110110011100011110110001110110001110110001110110001110110001110110001";

    int[] tree = new int[] { 7, 3, 80, 5, 9, 1, -1, 2, -2, -3, -4, -5, -6, 8, 4, 6, 38, -7, -8, -9, 0, 0 };

//    EncodedString encoded = new EncodedString(encode, tree);
    huffmanDecoderSUT = new HuffmanDecoder(encode, tree);
  }
  
  @Test
  public void testEncoder() {
    Buffer buffer = huffmanDecoderSUT.decompress();
    int n = buffer.size();

    StringBuilder sampleBuffer = new StringBuilder(); 
    for (int i=0;i<n;i++) {
      int value = buffer.get(0 + i);
      sampleBuffer.append(String.format("%1$02X ", value));
    }
    Assert.assertEquals("09 01 26 02 03 08 06 26 04 08 06 26 04 08 06 26 04 08 06 26 04 08 06 26 04 08 06 26 04 ",
      sampleBuffer.toString());
  }
}
