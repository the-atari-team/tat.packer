package lla.privat.atarixl.memory;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class Buffer {
  private byte[] buffer;
  private int index;
  
  public Buffer() {
    buffer = new byte[65536];
    Arrays.fill(buffer, (byte)0); // initialise buffer with 0
    index = 0;
  }
  
  public byte[] getBuffer() {
    return buffer;
  }

  int getIndex() {
    return index;
  }
  
  public void add(byte b) {
    buffer[index++] = b;    
  }

  public void add(int b) {
    buffer[index++] = (byte)b;    
  }

  public void set(int index, byte b) {
    buffer[index] = b;
  }

  public void set(int index, int b) {
    buffer[index] = (byte)b;
  }
  
  public int size() {
    return index;
  }

  public Integer get(int index) {
    return Integer.valueOf(Byte.toUnsignedInt(buffer[index]));
  }
  
  public String toString() {
    return new String(buffer, StandardCharsets.US_ASCII);
  }
  
}
