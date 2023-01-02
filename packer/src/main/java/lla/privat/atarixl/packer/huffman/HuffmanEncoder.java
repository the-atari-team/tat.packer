package lla.privat.atarixl.packer.huffman;

import java.util.PriorityQueue;
import java.util.TreeMap;

import lla.privat.atarixl.memory.Memory;

public class HuffmanEncoder {
  private final Memory memory;
  private final int minaddr;
  private final int maxaddr;

  private String encode;
  
  public HuffmanEncoder(Memory memory) {
    this.memory = memory;
    this.minaddr = memory.getMinAddr();
    this.maxaddr = memory.getMaxAddr();
  }

//  int zeiger;
  // Hier legen wir ab, welches Byte wie häufig vorkommt,
  // daraus errechnet sich später der Huffman-Code
  static int[] frequences;

  private TreeMap<String, String> codes;

  PriorityQueue<Node> nodeQueue;
  
  static int[] treeout;

  public void prepareFrequences(int[] frequences) {
    this.frequences = frequences;
    fillFrequences();
  }

  public static int[] initFrequencies() {
    frequences = new int[256];
    return frequences;
  }
  
  public String compress() {

    int l = maxaddr - minaddr + 1;

//    frequences = new int[256];
//    fillFrequences(frequences);
    
    nodeQueue = new PriorityQueue<>((node1, node2) -> (node1.freq < node2.freq) ? -1 : 1);

    // In die PriorityQueue nur die Werte ablegen, die wirklich vorhanden sind
    fillPriorityQueue(nodeQueue);

    System.out.println("; Anzahl einfache Nodes:" + nodeQueue.size());
    buildTree(nodeQueue);

    codes = new TreeMap<>();

    treeout = new int[517];
    generateCodes(nodeQueue.peek(), "");
    
    cleanupTreeout(treeout);
    
//    printCodes(codes);

    System.out.println("; -- Encoding/Decoding --");
    final String encoded = encodeText();
    // System.out.println("Encoded Text: " + encoded);
    int l2 = calcSize(encoded);
    // int l2 = calculateCompressedSizeInBytes();
//    int ts = treesize(treeout);

    System.out.println(";      Original length:   " + l + " bytes");
    System.out.println(";    compressed length:   " + l2 + " bytes");
//    System.out.println(";          tree length:   " + (ts * 2) + " bytes");
    
    System.out.println("; Ratio (without tree):   1:" + (float) l / l2);
//    System.out.print("; Real ratio (with tree): 1:" + (float) l / (l2 + (ts * 2)));
//    if (l / (l2 + (ts * 2)) < 1)
//      System.out.println(" => INFLATION!");
//    else
//  System.out.println();
    System.out.println();
    encode = encoded;
    
    return encoded;
  }
  
  public static int calcSize(String huffmanEncode) {
    int n = huffmanEncode.length();
    return Double.valueOf(Math.ceil(n / 8)).intValue();
  }

  public static int[] getTreeout() {
    return treeout;
  }
  
  public Memory convertEncodedToMemory(EncodedString encoded) {
    if (encode == null) {
      return null;
    }
    
    Memory newMemory = new Memory();
    newMemory.setMaxAddr(memory.getMaxAddr());
    int startAddr = memory.getMaxAddr() - calcSize(encode) + 1;
    newMemory.setMinAddr(startAddr);
    int index = startAddr;
    final String binaerString = encoded.getEncode();
    for (int i=0; i<binaerString.length();i+=8) {
      String binaer = binaerString.substring(i, i+8);
      int value = binToDec(binaer);
      newMemory.set(index, value);
    }
    
    return newMemory;
  }
  
  private void cleanupTreeout(int[] treeout) {
    // das treeout muss nochmal um 2 verschoben werden
    for (int i=0; i<515;i++) {
      treeout[i] = treeout[i+2];
    }
    treeout[treeout.length-2]=0;
    treeout[treeout.length-1]=0;
  }


  private void fillPriorityQueue(PriorityQueue<Node> nodeQueue) {
    for (int i = 0; i < 256; i++) {
      if (frequences[i] > 0) {
        String s = String.valueOf(i);
        Node node = new Node(frequences[i], s);
        nodeQueue.add(node);
      }
    }    
  }

  private void fillFrequences() { 
      // Ein Array mit allen Werten und dessen Häufigkeit erstellen
      
      for (int i = minaddr; i <= maxaddr; i++) {
        int value = memory.get(i);
        frequences[value] = frequences[value] + 1;
      }
  }
      
  // dies decompress benoetigt die nodeQueue. Wie man die im Speicher ablegen soll, k.A.
  public String decompress(String encoded) {
    final String decoded = decodeText(encoded, nodeQueue);
    return decoded;    
  }

// Das ist jetzt falsch, da die frequencies über alle Dateien gebildet wird   
//  private int calculateCompressedSizeInBytes() {
//    int length = 0;
//    for (Map.Entry<String, String> code : codes.entrySet()) {
//      int binarLength = code.getValue().length();
//      int databyte = Integer.parseInt(code.getKey());
//      length = length + frequences[databyte] * binarLength;
//      // bithighest = Math.max(binarLength, bithighest);
//    }
//
//    int lenInBytes = length / 8;
//    if (length % 8 != 0) {
//      lenInBytes++;
//    }
//
//    return lenInBytes;
//  }

  public int treesize(int[] treein) {
    int ts = 0;
    while (!((treein[ts] == 0) && (treein[ts + 1] == 0)))
      ts++;
    return ts;
  }

  private String encodeText() {
    final StringBuilder builder = new StringBuilder();
    for (int i = minaddr; i <= maxaddr; i++) {
      int keyValue = memory.get(i);
      final String key = String.valueOf(keyValue);
      final String binaerstring = codes.get(key);
      builder.append(binaerstring);
    }
    int len = builder.length() % 8;
    while (len != 0) {
      builder.append("0");
      len = builder.length() % 8;
    }
    return builder.toString();
  }

  // fasst so lange 2 Nodes zusammen, bis es nur noch genau eine gibt
  private void buildTree(PriorityQueue<Node> vector) {
    int nodeNumber = 1;
    while (vector.size() > 1) {
      vector.add(new Node(nodeNumber++, vector.poll(), vector.poll()));
    }
  }
  
  // Erstellt die Liste der Codes
  // generiert gleichzeitig das treeout Array, das gebraucht wird um die Daten
  // sehr einfach zu decodieren.
  // Die Funktion arbeitet rekursiv!
  private void generateCodes(Node node, String s) {
    if (node != null) {
      int treePosition = node.nodeNumber * 2;
      
      if (node.left != null) {
        if (node.left.nodeNumber == 0) {
          int value = Integer.parseInt(node.left.character);
          treeout[treePosition] = value;
          codes.put(node.left.character, s+"0");        
        }
        else {
          treeout[treePosition] = (-1) * node.left.nodeNumber;
        }
        // System.out.println("treeout["+treePosition+"] := " + treeout[treePosition]);
        generateCodes(node.left, s + "0");
      }
      
      if (node.right != null) {
        treePosition = treePosition + 1;
        if (node.right.nodeNumber == 0) {
          int value = Integer.parseInt(node.right.character);
          treeout[treePosition] = value;
          codes.put(node.right.character, s+"1");        
        }
        else {
          treeout[treePosition] = (-1) * node.right.nodeNumber;
        }
        // System.out.println("treeout["+(treePosition)+"] := " + treeout[treePosition]);
        generateCodes(node.right, s + "1");
      }
        
//      if (node.left == null && node.right == null) {
//        int value = Integer.parseInt(node.character);
//        treePosition = treePosition + leftRight;
//        treeout[treePosition] = value;
//        System.out.println("treeout["+treePosition+"] := " + treeout[treePosition] + " // " + s);
//        codes.put(node.character, s);        
//      }
    }
  }

  // Gibt sämtliche Codes aus
  private void printCodes(TreeMap<String, String> codes) {
    System.out.println("--- Printing Codes ---");
    codes.forEach((k, v) -> System.out.println("'" + k + "' : " + v));
  }

  public static int binToDec(String binary) {
    int decimal = 0;
    for (int i = 0; i < binary.length(); i++) {
      decimal = 2 * decimal + Integer.parseInt("" + binary.charAt(i));
    }
    return decimal;
  }

  class Node {
    final int nodeNumber;
    Node left, right;
    int freq;
    String character;

    public Node(int freq, String character) {
      this.nodeNumber = 0;
      this.freq = freq;
      this.character = character;
      left = null;
      right = null;
    }

    public Node(int nodeNumber, Node left, Node right) {
      this.nodeNumber = nodeNumber;
      this.freq = left.freq + right.freq;
      character = left.character + right.character;
      if (left.freq < right.freq) {
        this.right = right;
        this.left = left;
      }
      else {
        this.right = left;
        this.left = right;
      }
    }
  }

  private String decodeText(String encoded, PriorityQueue<Node> nodes) {
    StringBuilder decoded = new StringBuilder();

    Node node = nodes.peek();
    for (int i = 0; i < encoded.length();) {
      Node tmpNode = node;
      while (tmpNode.left != null && tmpNode.right != null && i < encoded.length()) {
        if (encoded.charAt(i) == '1') {
          tmpNode = tmpNode.right;
        }
        else {
          tmpNode = tmpNode.left;
        }
        i++;
      }
      if (tmpNode != null)
        if (tmpNode.character.length() == 1) {
          decoded.append(tmpNode.character);
        }
        else {
          System.out.println("Input not Valid");
        }
    }
    // System.out.println("Decoded Text: " + decoded);
    return decoded.toString();
  }
}
