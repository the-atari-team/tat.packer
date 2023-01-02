package lla.privat.atarixl.packer.huffman;

import lla.privat.atarixl.memory.Buffer;

// Dieser Decoder ist hier entnommen und angepasst worden
// Quelle: https://www.java-forum.org/thema/huffman-codierung.30183/

public class HuffmanDecoder {
//    private final EncodedString encoded;
    private String encoded;
    private int[] tree;
    
    public HuffmanDecoder(String encoded, int[] tree) {
      this.encoded = encoded;
      this.tree = tree;
    }

 // Decompressor for 6502
 // http://forum.6502.org/viewtopic.php?f=2&t=4642&hilit=huffman

 // Der Code zum decodieren mag schrecklich aussehen, lässt sich aber sehr einfach in
 // 6502 Assembler uebersetzen und braucht nur die tree Tabelle, die allerdings bis 1024 Bytes verschlingt
   public Buffer decompress() {
     final String bitsAsString = encoded;
     final int[] tree = this.tree;

     Buffer buffer = new Buffer();
     
     // int outcount = 0;
     int incount = 0;
     int treesize = 0;
     int treecount = 0;
     // System.out.println("decode");
     while (!((tree[treesize] == 0) && (tree[treesize + 1] == 0))) {
       treesize++;
     }
     treecount = treesize - 2;
     while (/*(outcount < dataout.length) &&*/ (incount < bitsAsString.length())) {
       char ch =bitsAsString.charAt(incount); 
       // System.out.print(ch);
       if (ch == '0') {
         int tc = tree[treecount];
         if (tc >= 0) {
           buffer.add((byte) tc);
           treecount = treesize - 2;
           // builder.append(tc);
           // outcount++;
           // System.out.println("="+tc);
         }
         else {
           treecount = (Math.abs(tc * 2) - 2);
         }
       }
       else { // if (ch == '1') {
         int tc = tree[treecount + 1];
         if (tc >= 0) {
           buffer.add((byte) tc);
           treecount = treesize - 2;
           // outcount++;
           // System.out.println("="+tcp1);
           // builder.append(tcp1);
         }
         else {
           treecount = (Math.abs(tc) * 2) - 2;
         }
       }
       incount++;
     }     
     return buffer;
   }

}
