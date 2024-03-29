package lla.privat.atarixl.packer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import lla.privat.atarixl.memory.Memory;
import lla.privat.atarixl.packer.huffman.EncodedString;
import lla.privat.atarixl.packer.huffman.HuffmanEncoder;
import lla.privat.atarixl.packer.pair.Block;
import lla.privat.atarixl.packer.pair.BlockEncoder;
import lla.privat.atarixl.packer.pair.Pair;
import lla.privat.atarixl.packer.pair.PairEncoder;
import lla.privat.atarixl.packer.rle.RunLengthEncoder;

/**
 * Create a compressed data list out of a lot files
 *
 */

public class Datalist {

  public enum Type {
    PAIRS, BLOCKS;
  }

  private final List<String> filenames;

  private final List<EncodedString> encodes;

  private final String outputfilename;

  private int fullDataLength;
  private int fullCompressedLength;

  boolean usePair = false;
  boolean useBlocks = false;

  private List<Pair> pairs = new ArrayList<>();
  private List<Block> blocks = new ArrayList<>();
  private int[] huffmanFrequences = new int[256];
  
  public Datalist(String outputfilename, List<String> filenames, Type type) {
    this.outputfilename = outputfilename;
    this.filenames = filenames;

    this.encodes = new ArrayList<>();
    if (type == Type.PAIRS) {
      this.usePair = true;
    }
    else if (type == Type.BLOCKS) {
      this.useBlocks = true;
    }
    fullDataLength = 0;
    fullCompressedLength = 0;
  }

  public void create() throws IOException {
    File outputfile = new File(outputfilename);
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputfile));) {
      writeToFile(bw);
    }
  }

  private void writeToFile(Writer wr) throws IOException {
    int completeSize = 0;
    int count = 0;
    wr.write("; Autogenerated Huffman and RLE data blob\n");
    if (usePair) {
      wr.write("; use pair extension\n");
    }
    wr.write("; written for WiNiFe Compiler\n");
    wr.write("; cdw by 'The Atari Team' 2021-2022\n");
    wr.write("\n");
    wr.write("; Use always RLE at first, then Huffman encoding\n");
    wr.write("\n");
    wr.write(" .local\n");
    wr.write("\n");

    // TODO: Umbau, das wir am Ende nur noch einen Huffman Tree haben
    // TODO: Dazu muessen wir erst mal nur RLE machen, und dann Huffman fuer alle
    // nochmal

    huffmanFrequences = HuffmanEncoder.initFrequencies();

    for (String filename : filenames) {
      if (new File(filename).exists()) {
        // ich brauche ein Word für Zeiger auf die Daten
        // 2 Bytes für Länge der ausgepackten Daten
        // 2 Byte für Länge der RLE Daten
        // 2 Byte für RLE zum decodieren, Werte zu X und Y
        // 2 Bytes für Länge der gepackten Huffmandaten
        // der Huffman Tree endet immer mit 0,0 und wird durchgezählt
        // 2 Bytes für Eintrag in @compressed_data
        // Gesamt 10 Bytes pro Daten Blob extra
        count += 8;
        EncodedString str = compressSingleFile(filename);
        encodes.add(str);
//        completeSize += encodes.size();
        fullCompressedLength += 8;
      }
      else {
        EncodedString str = new EncodedString();
        str.setFilename(filename);
        encodes.add(str);
      }
    }

    // create tables to decompress
    for (int i = 0; i < encodes.size(); i++) {
      EncodedString encode = encodes.get(i);
//      int[] treein = encode.getTree();

      wr.write("; --------------------------------------------------\n");
      wr.write("; data based on: " + encode.getFilename() + "\n");
      wr.write("; --------------------------------------------------\n");
      wr.write("?compressed_data" + i + "\n");

      outputRLEInfo(wr, i, encode.getRealSize(), encode.getX(), encode.getY(), encode.getRleSize(),
          encode.getRleSample());

// TODO: Huffman soll immer die selben frequencies verwenden, damit muss der Tree nur einmal ausgegeben werden
      HuffmanEncoder huffman = encode.getHuffmanEncoder();
      if (huffman != null) {
        String huffmanStr = huffman.compress();
        int size = HuffmanEncoder.calcSize(huffmanStr);
        fullCompressedLength += size;
        outputHuffman(wr, i, size, huffmanStr);
      }
    }
    outputHuffmanTree(wr, HuffmanEncoder.getTreeout());

    // TODO: hier sich etwas besseres ueberlegen
    wr.write("@compressed_extension=");
    if (usePair) {
      wr.write("1 ; we use pairs\n");
    }
    else if (useBlocks) {
      wr.write("2 ; we use blocks\n");
    }
    else {
      wr.write("0\n");
    }

    boolean newLine = true;
    // TODO: due to a bug in huffman decoder, we must add the @compressed_pairs variable
    wr.write("@compressed_pairs\n");

    if (usePair) {
      wr.write("; --------------------------------------------------\n");
      wr.write("; pairs data\n");
      wr.write("; --------------------------------------------------\n");
      count = 0;
      for (int i = 0; i < pairs.size(); i++) {
        wr.write(" .byte ");
        int left = pairs.get(i).getLeft();
        wr.write(String.valueOf(left));
        wr.write(", ");
        int right = pairs.get(i).getRight();
        wr.write(String.valueOf(right));

        wr.write("\n");
      }
      fullCompressedLength += pairs.size() * 2;
      wr.write("0"); // end mark!
      wr.write("\n");
      wr.write("\n");
    }
    newLine = true;
    if (useBlocks) {
      wr.write("; --------------------------------------------------\n");
      wr.write("; blocks data\n");
      wr.write("; --------------------------------------------------\n");
      wr.write("@compressed_blocks\n");
      count = 0;
      for (int i = 0; i < blocks.size(); i++) {
        wr.write(" .byte ");
        wr.write(blocks.get(i).out());
        wr.write("\n");
      }
      fullCompressedLength += blocks.size() * 12;
      wr.write("\n");
      wr.write("\n");
    }

    // create pointer list to real compressed data
    wr.write("@compressed_data\n");
    newLine = true;
    count = 0;
    for (int i = 0; i < encodes.size(); i++) {
      if (newLine == true) {
        wr.write(" .word ");
        newLine = false;
      }
      String compressed_data_pointer = "?compressed_data" + i;
      wr.write(compressed_data_pointer);
      count++;
      if (count % 10 == 0) {
        wr.write("\n");
        newLine = true;
      }
      else {
        wr.write(", ");
      }
    }
    wr.write("0"); // end mark!
    wr.write("\n");

    wr.write("\n");
    wr.write("; --------------------------------------------------\n");
    wr.write("; Statistics:\n");
    wr.write("; Data uncompressed length: " + fullDataLength + "\n");
    wr.write("; Data   compressed length: " + fullCompressedLength + "\n");
    wr.write("; Ratio with all data:   1:" + ((float) fullDataLength / fullCompressedLength) + "\n");
    wr.write("; --------------------------------------------------\n");
  }

  private void outputRLEInfo(Writer wr, int n, int realSize, int x, int y, int rleSize, String rleSample)
      throws IOException {
    wr.write("?real_size" + n + "\n");
    wr.write(" .word " + realSize + " ; size in bytes in real" + "\n");
    if (realSize > 0) {
      wr.write("; rle encode-start: " + rleSample + "\n");
      wr.write("?rle_size" + n + "\n");
      wr.write(" .word " + rleSize + " ; size in bytes when rle encoded" + "\n");
      wr.write("?rle_x" + n + "\n");
      wr.write(" .byte " + x + "\n");
      wr.write("?rle_y" + n + "\n");
      wr.write(" .byte " + y + "\n");
      wr.write("\n");
    }
  }

  private void outputHuffman(Writer wr, int n, int countOfBytes, String encode) throws IOException {
    if (countOfBytes == 0) {
      return;
    }

    wr.write("?huffman_size" + n + "\n");
    wr.write(" .word " + countOfBytes + " ; size in bytes when rle and huffman encoded" + "\n");
    wr.write("\n");

    if (countOfBytes > 3) {
      wr.write("; huffman encode-start: " + encode.substring(0, 24) + "\n");
    }
    wr.write("?huffman" + n + "\n");
    boolean newLine = true;
    int count = 0;
    for (int i = 0; i < encode.length(); i += 8) {
      if (newLine == true) {
        wr.write(" .byte ");
        newLine = false;
      }
      String aByte = encode.substring(i, i + 8);
      wr.write("~" + aByte);
      count++;
      if (count % 10 == 0) {
        wr.write("\n");
        newLine = true;
      }
      else {
        if (count * 8 < encode.length()) {
          wr.write(", ");
        }
      }
    }
    // wr.write(0+"\n");
    wr.write("\n");
    wr.write("\n");
  }

  private void outputHuffmanTree(Writer wr, int[] treein) throws IOException {
    if (treein.length > 2) {
      int ts = 0;
      boolean newLine = true;

      wr.write("@huffman_tree\n");
      while (!((treein[ts] == 0) && (treein[ts + 1] == 0))) {
        if (newLine == true) {
          wr.write(" .word ");
          newLine = false;
        }
        wr.write(String.valueOf(treein[ts]));
        fullCompressedLength += 2;
        ts++;
        if (ts % 10 == 0) {
          wr.write("\n");
          newLine = true;
        }
        else {
          wr.write(", ");
        }
      }
      if (newLine == true) {
        wr.write(" .word ");
      }
      wr.write(String.valueOf(treein[ts]) + "," + String.valueOf(treein[ts + 1]) + "\n");
    }
    wr.write("\n");
  }

  private EncodedString compressSingleFile(final String filename) throws IOException {
    File file = new File(filename);
    Main mainSUT = new Main(file, 0, "", "");

    Memory uncompressed = mainSUT.getMemory();
    fullDataLength += uncompressed.size();

    RunLengthEncoder rle;
    if (usePair) {
      PairEncoder pairEnc = new PairEncoder(uncompressed);
      pairEnc.compress(pairs);
      Memory pairCompressed = pairEnc.getCompressed();

      rle = new RunLengthEncoder(pairCompressed);
    }
    else if (useBlocks) {
      BlockEncoder blockEnc = new BlockEncoder(uncompressed);
      blockEnc.compress(blocks);
      Memory blockCompressed = blockEnc.getCompressed();

      rle = new RunLengthEncoder(blockCompressed);
    }
    else {
      rle = new RunLengthEncoder(uncompressed);
    }

    rle.compress();
    Memory rleCompressed = rle.getCompressed();

    // Wir wollen den Anfang(~40 Bytes) des encodierten RLE mit ausgeben
    int address = rleCompressed.getMinAddr();
    StringBuilder sampleBuffer = new StringBuilder();
    for (int i = 0; i < 40; i++) {
      int value = rleCompressed.get(address + i);
      sampleBuffer.append(String.format("%1$02X ", value));
    }

    HuffmanEncoder huffman = new HuffmanEncoder(rleCompressed);
    huffman.prepareFrequences(huffmanFrequences);
    
    EncodedString encoded = new EncodedString();
    encoded.setHuffmanEncoder(huffman);
    encoded.setFilename(filename);
    encoded.setX(rle.getX());
    encoded.setY(rle.getY());
    encoded.setRleSize(rleCompressed.size());
    encoded.setRealSize(mainSUT.getMemory().size());

    encoded.setRLESample(sampleBuffer.toString());

//    Memory newMemory = huffman.convertEncodedToMemory(encoded);

//    wr.write(" RLE compressed: " + rleCompressed.size(+"\n"));
//    System.out.println("; HUFF compressed: " + newMemory.size());
//    System.out.println(";       TREE data: " + huffman.treesize(encoded.getTree()));
//    fullCompressedLength += newMemory.size();
//    fullCompressedLength += huffman.treesize(encoded.getTree());

//    int values = newMemory.size() + huffman.treesize(encoded.getTree());
//    return values;
    return encoded;
  }

  public int getFullCompressedLength() {
    return fullCompressedLength;
  }

  public boolean isUsePair() {
    return usePair;
  }

  public boolean isUseBlocks() {
    return useBlocks;
  }
}
