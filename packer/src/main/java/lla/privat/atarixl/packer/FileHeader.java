package lla.privat.atarixl.packer;

import java.util.ArrayList;
import java.util.List;

public class FileHeader {

  List<Integer> compressed;

  public FileHeader(int minaddr, int maxaddr) {
    compressed = new ArrayList<>();

    // TODO: maxaddr setzen
    
    // init compressed
    compressed.add(0xFF);
    compressed.add(0xFF);

    compressed.add(minaddr % 256);
    compressed.add(minaddr / 256);
    
    compressed.add(maxaddr % 256);
    compressed.add(maxaddr / 256);
  }
  
  public List<Integer> getCompressed() {
    return compressed;
  }


}
