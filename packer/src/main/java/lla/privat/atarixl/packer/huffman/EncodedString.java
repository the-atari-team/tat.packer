package lla.privat.atarixl.packer.huffman;

public class EncodedString {
  
  private String filename;
  // private String encode; // hält einen Huffman codierten Datensatz als String, ein Zeichen ist ein Bit. 
  // private int[] tree;    // Wird zum entcodieren gebraucht

  private int x; // kleinster Wert für RLE
  private int y; // zweitkleinster Wert für RLE
  private int rleSize;
  private int realSize;

  private String rleSample;
  private HuffmanEncoder huffman;
  
  public EncodedString() {
//    this.encode = encode;
//    this.tree = tree;
    this.x = 0;
    this.y = 0;
    this.rleSize = 0;
    this.realSize = 0;
  }

  public void setHuffmanEncoder(HuffmanEncoder huffman) {
    this.huffman = huffman;
  }

  public HuffmanEncoder getHuffmanEncoder() {
    return huffman;
  }
  
  public String getEncode() {
    return huffman.compress();
  }

//  public int size() {
//    int n = encode.length();
//    return Double.valueOf(Math.ceil(n / 8)).intValue();
//  }
//  
//  public int[] getTree() {
//    return tree;
//  }

  public void setX(int x) {this.x = x;}
  public void setY(int y) {this.y = y;}
  
  public int getX() {return x;}
  public int getY() {return y;}

  public void setRleSize(int size) {
    rleSize = size;
  }
  public int getRleSize() {return rleSize;}

  public void setRealSize(int size) {
    realSize = size;    
  }
  public int getRealSize() {return realSize;}

  public void setFilename(String filename) {
    this.filename = filename;    
  }
  public String getFilename() {return filename;}

  public void setRLESample(String sample) {
    this.rleSample = sample;    
  }
  public String getRleSample() {return rleSample;}
}
