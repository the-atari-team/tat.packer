package lla.privat.atarixl.memory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Memory {

  private final static Logger LOGGER = LoggerFactory.getLogger(Memory.class);
  
  final private Buffer memory;

  int min_address;
  int max_address;
  
  int start_address;  // will only set if given in File with 2|4 Bytes length
  int init_address;   // only accept if start also given
  
  public Memory() {
    memory = new Buffer();
  }

  public Memory(File filename) throws IOException {
    this();

    if (!filename.exists()) {
      throw new FileNotFoundException("ERROR: Given file " + filename.getAbsolutePath() +" does not exist.");
    }
    readBinaerFile(filename);
  }

  public int getMinAddr() {
    return min_address;
  }
  
  public void setMinAddr(int i) {
    min_address = i;
  }

  public int getMaxAddr() {
    return max_address;
  }

  public void setMaxAddr(int i) {
    max_address = i;
  }
  
  public int get(int index) {
    return memory.get(index);
  }
  
  public void set(int index, int value) {
    memory.set(index, value);
  }
  
  public int size() {
    return max_address - min_address + 1;
  }
  
  /**
   * readBinaerFile read the whole file into memory
   * It starts with $FFFF,
   * then 2 bytes for startaddr
   * then 2 byte endaddr
   * endaddr - startaddr + 1 bytes.
   * this will repeat, until 
   * startaddr == 736 (Jump start)
   * @return
   * @throws IOException if there is a read problem, IllegalStateException if there is a structure problem
   */
  
  boolean readBinaerFile(File file) throws IOException {

    // LOGGER.info("Read file: '{}'", file.getName());

    min_address = 65535;
    max_address = 0;
    
    boolean hasMoreBytes = true;
    
    // FileInputstream will automatically closed
    try(FileInputStream fis = new FileInputStream(file)) {

      int low = fis.read();
      int high = fis.read();
      if (low != 255 || high != 255) {
        throw new IllegalStateException("File must start with $FF, $FF bytes");
      }
      
      while(hasMoreBytes) {
        low = fis.read();
        high = fis.read();
        int startaddr = low + 256*high;

        if (startaddr == -257) {    // keine weiteren Bytes vorhanden
          break;
        }
        
        if (startaddr == 0xFFFF) {
          low = fis.read();
          high = fis.read();
          startaddr = low + 256*high; 
        }
        low = fis.read();
        high = fis.read();
        int endaddr = low + 256*high;
  
        int countBytes = endaddr - startaddr + 1;

        if (startaddr > 960 && endaddr < 0xc000) {
          min_address = Math.min(min_address, startaddr);
          max_address = Math.max(max_address, endaddr);
        }
        
        if (startaddr == 736 && countBytes == 2) {
          hasMoreBytes = false;
          low = fis.read();
          high = fis.read();
          start_address = low + 256*high;
        }
        else if (startaddr == 736 && countBytes == 4) {
          hasMoreBytes = false;
          low = fis.read();
          high = fis.read();
          start_address = low + 256*high;

          low = fis.read();
          high = fis.read();
          init_address = low + 256*high;          
        }
        else if (startaddr == 738 && countBytes == 2) {
          throw new IllegalStateException("We do not accept init() call in our Packer.");
        }
        else {
          for (int i=startaddr;i<=endaddr;i++) {
            int value = fis.read();
            memory.set(i, value);
          }
        }
      }
    }
    catch (FileNotFoundException e) {
      e.printStackTrace();
      return false;
    }
    catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    
    return true;
  }

}
