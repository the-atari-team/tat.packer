package lla.privat.atarixl.packer;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import lla.privat.atarixl.memory.Memory;
import lla.privat.atarixl.packer.huffman.HuffmanEncoder;
import lla.privat.atarixl.packer.rle.RunLengthEncoder;

public class ITMain {

  private Main mainSUT;
    
  @Test
  public void testReadLEVELA_and_RLE() throws Exception {
    File file = new File("src/test/resources/LEVELA.DAT");
    mainSUT = new Main(file,0,"","");
    Assert.assertEquals(35751, mainSUT.getMemory().getMinAddr());
    Assert.assertEquals(44031, mainSUT.getMemory().getMaxAddr());

    int originalLength = mainSUT.getMemory().getMaxAddr() - mainSUT.getMemory().getMinAddr() + 1;
    RunLengthEncoder rle = new RunLengthEncoder(mainSUT.getMemory());
    Assert.assertEquals(26, rle.getX());
    Assert.assertEquals(38, rle.getY());

    rle.compress();
    // TODO: Die Encoder sollen nur die komprimierten Daten liefern und nichts anderes, keine Start/Endemarke etc.
    Memory compressed = rle.getCompressed();

    int rleCompressedLength = 3301;
    Assert.assertEquals(rleCompressedLength, compressed.size());

    double percent = 100 - (100.0 * rleCompressedLength / originalLength);
    Assert.assertEquals(60.1, percent, 0.1); // ~60% Ersparnis, nett
  }

  @Test
  public void testReadLEVELA_RLE_and_Huffman() throws Exception {
    File file = new File("src/test/resources/LEVELA.DAT");
    mainSUT = new Main(file,0,"","");

    Assert.assertEquals(8281, mainSUT.getMemory().size());
    
    RunLengthEncoder rle = new RunLengthEncoder(mainSUT.getMemory());

    rle.compress();
    Memory rleCompressed = rle.getCompressed();
    Assert.assertEquals(3301, rleCompressed.size());
    
    int[] frequencies = HuffmanEncoder.initFrequencies();
    HuffmanEncoder huffman = new HuffmanEncoder(rleCompressed);
    huffman.prepareFrequences(frequencies);
    String encoded = huffman.compress();

    int size = HuffmanEncoder.calcSize(encoded);
//    Memory newMemory = huffman.convertEncodedToMemory(encoded);
    Assert.assertEquals(2054, size);
    
//    Assert.assertEquals(178, huffman.treesize(encoded.getTree()));
  }

  
  @Test
  public void testReadPacman_RLE_and_Huffman() throws Exception {
    File file = new File("src/test/resources/PACMAN.COM");
    mainSUT = new Main(file,0,"","");
    
    RunLengthEncoder rle = new RunLengthEncoder(mainSUT.getMemory());

    rle.compress();
    Memory rleCompressed = rle.getCompressed();
    Assert.assertEquals(18605, rleCompressed.size());
    
    int[] frequencies = HuffmanEncoder.initFrequencies();
    HuffmanEncoder huffman = new HuffmanEncoder(rleCompressed);
    huffman.prepareFrequences(frequencies);
    String encoded = huffman.compress();
    int size = HuffmanEncoder.calcSize(encoded);
    
    // Memory newMemory = huffman.convertEncodedToMemory(encoded);
    Assert.assertEquals(16075, size);
    
    Assert.assertEquals(510, huffman.treesize(HuffmanEncoder.getTreeout()));
    // Assert.assertEquals(510, huffman.treesize(encoded.getTree()));
  }

  @Test
  public void testRead5bytes() throws Exception {
    File file = new File("../test-sources/test-5bytes.COM");
    mainSUT = new Main(file,0,"","");

    Assert.assertEquals(0x4000, mainSUT.getMemory().getMinAddr());
    Assert.assertEquals(0x4004, mainSUT.getMemory().getMaxAddr());
  }

}
