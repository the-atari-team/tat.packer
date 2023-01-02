package lla.privat.atarixl.packer;

// TODO:
// Occurance(Vorkommen, Auftreten) ist eine Analyse Klasse nur um zu prüfen,
// ob es Datenblöcke gibt, die mehr als einmal innerhalb des Datensegments vorkommen.
public class Occurance {

  private final byte[] memory;
  private final int minaddr;
  private final int maxaddr;
  
  
  public Occurance(byte[] memory, int minaddr, int maxaddr) {
    this.memory = memory;
    this.minaddr = minaddr;
    this.maxaddr = maxaddr;
  }
  
  public int memcmp(int indexInMemoryArray, byte b2[], int countOfBytes){
    int j;
    for(int i = 0; i < countOfBytes; i++){
        j = indexInMemoryArray + i; 
        if(memory[j] != b2[i]){
            if((memory[j] >= 0 && b2[i] >= 0)||(memory[j] < 0 && b2[i] < 0))
                return memory[j] - b2[i];
            if(memory[j] < 0 && b2[i] >= 0)
                return 1;
            if(b2[i] < 0 && memory[j] >=0)
                return -1;
        }
    }
    // memory at position indexInMemoryArray are equal to b2[] (countOfBytes)
    return 0;
}
  
  public void analyse() {
    // wir beginnen mit 2 bytes und arbeiten uns rauf
    for (int length = 2;length<255;length++) {

      System.out.println("Analyse length:" + length);
      
      byte[] block = new byte[length];
      // wir durchlaufen den ganzen Speicherbereich
      int e = maxaddr - length;

      for (int start = minaddr;start < e;start++) {

        // suchblock erstellen
        for (int i=0;i<length;i++) {
          block[i] = memory[i + start];
        }
        
        int found=0;
        // suchen ab start + length
        int s = start+length;

        for (int search=s;search<e;search++) {
          if (memcmp(search, block, length) == 0) {
            found++;
          }
        }
        if ((found * length) > ((4 * found) + length)) { // marke, position(2 byte), length(1 byte)  und einmalig dessen Datensatz
          System.out.println("Found: Block at [" + start + "] size:=" + length + " times:" + found);
        }
      }
    }
  }
}
