package lla.privat.atarixl.packer.pair;

import java.util.Arrays;

public class Block {
  private int[][] block;
  final int x,y;
  
  public Block(int x, int y) {
    block = new int[x][y];
    this.x = x;
    this.y = y;
  }
  
  public void set(int n0, int n1, int n2, int n3,int n4, int n5) {
    block[0][0] = n0;
    block[1][0] = n1;

    block[0][1] = n2;
    block[1][1] = n3;
    
    block[0][2] = n4;
    block[1][2] = n5;
  }
  
  public void set(int n0, int n1, int n2, int n3,int n4, int n5, int n6, int n7, int n8, int n9, int n10,int n11) {
    block[0][0] = n0;
    block[1][0] = n1;
    block[2][0] = n2;
    block[3][0] = n3;

    block[0][1] = n4;
    block[1][1] = n5;
    block[2][1] = n6;
    block[3][1] = n7;

    block[0][2] = n8;
    block[1][2] = n9;
    block[2][2] = n10;
    block[3][2] = n11;
  }
  
  public int get(int x, int y) {
    return block[x][y];
  }

  public String out() {
    StringBuilder buf = new StringBuilder();
    int n=0;
    for (int y=0;y<this.y;y++) {
      for (int x=0;x<this.x;x++) {
        buf.append(block[x][y]);
        n=n+1;
        if (n<(this.x*this.y)) {
          buf.append(",");
        }
      }
    }
    return buf.toString();
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.deepHashCode(block);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Block other = (Block) obj;
    return Arrays.deepEquals(block, other.block);
  }
  
  
}
