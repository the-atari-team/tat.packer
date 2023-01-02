// cdw by 'The Atari Team' 2021
// licensed under https://creativecommons.org/licenses/by-sa/2.5/[Creative Commons Licenses]

package lla.privat.atarixl.packer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lla.privat.atarixl.memory.Memory;

/**
 * Atari 8bit Packer 1988 - 2021
 *
 * @author lars <dot> langhans <at> gmx <dot> de
 *
 */
public class Main {

  private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

  private final File inputFile;
  private final int verboseLevel;
  private final String outputpath;
  private final String outputname;
  private final List<String> filenames;
  
  private Memory memory;

  protected Main() {
    this.inputFile = null;
    this.verboseLevel = 0;
    this.outputname = "";
    this.outputpath = "";
    this.filenames = new ArrayList<>();
  }
  
  public Main(File inputFile, int verboseLevel, String outputpath, String outputname) throws IOException {
    this.inputFile = inputFile;
    this.verboseLevel = verboseLevel;
    this.outputpath = outputpath;
    this.outputname = outputname;
  
    filenames = new ArrayList<>();
    memory = new Memory(inputFile);
  }

  public Memory getMemory() {
    return memory;
  }
  
  private void start() throws IOException {
  }
  
  public static void usage() {
    LOGGER.info("Usage:");
    LOGGER.info(" java -jar xl-packer.jar [OPTIONS] [FILES]+");
    LOGGER.info("Packer for Atari XL COM File.");
    LOGGER.info("Should generate smaller file.");
    LOGGER.info("");
    LOGGER.info(" -v level | --verbose level - be more verbose");
    LOGGER.info(" --data                     - DATA Mode");
    LOGGER.info(" -of filename               - set output filename, where the ASM file will be stored.");
    LOGGER.info(" -op outputpath             - set output path");
    LOGGER.info(" -p | --pairs | --usepairs  - set pairs mode");
    LOGGER.info(" -h | --help                - display this help and exit.");
  }
  
  
  public static void main(final String[] args) throws IOException {
    if (args.length < 1) {
      LOGGER.error("No parameter given");
      usage();
      System.exit(1);
    }

    final String currentWorkingDirectory = System.getProperty("user.dir");

    int index = 0;

    int verboseLevel = 0;
    String filename = "";
    String outputfilename = "";
    String outputpath = "";
    Datalist.Type type = null;
    boolean datamode = false;
    List<String> filenames = new ArrayList<>();
    
    while (args.length > index) {
      String parameter = args[index];

      if (parameter.equals("-v") || parameter.startsWith("--verbose")) {
        verboseLevel = Integer.valueOf(args[index + 1]);
        ++index;
      }
      else if (parameter.equals("-of") || parameter.startsWith("--outputfilename")) {        
        outputfilename = args[index + 1];
        LOGGER.info("Will write to file: {}", outputfilename);
        ++index;
      }
      else if (parameter.equals("-op") || parameter.startsWith("--outputpath")) {
        outputpath = args[index + 1];
        LOGGER.info("Will write to path: {}", outputpath);
        ++index;
      }
      else if (parameter.equals("-D") || parameter.startsWith("--data")) {
          LOGGER.info("DATA Mode active");
          datamode = true;
        }
      else if (parameter.equals("-p") || parameter.startsWith("--usepairs") || parameter.startsWith("--pairs")) {
        LOGGER.info("Pairs Mode active");
        type = Datalist.Type.PAIRS;
      }
      else if (parameter.equals("-b") || parameter.startsWith("--useblocks") || parameter.startsWith("--blocks")) {
        LOGGER.info("Block Mode active");
        type = Datalist.Type.BLOCKS;
      }
      else if (parameter.equals("-V") || parameter.startsWith("--version")) {
        LOGGER.info("This is a WNF-Project (Will never finished), but version is 1.0.0");
        System.exit(1);
      }
      else if (parameter.equals("-h") || parameter.startsWith("--help")) {
        usage();
        System.exit(0);
      }
      else {
    	  if (parameter.startsWith("-")) {
    		  LOGGER.error("It seems you use a wrong parameter: {}", parameter);
    		  System.exit(1);
    	  }
        filename = parameter;
        filenames.add(filename);
      }
      ++index;
    }
    if (filenames.size() > 1 && datamode == false) {
      LOGGER.error("If more than one filename is given, datamode must be set.");
      System.exit(1);
    }
    
    try {
      File file = new File(filename);
      
      if (!file.exists()) {
        throw new FileNotFoundException("Can't find file:" + filename);
      }
      
      String basename = "";
      if (datamode) {
        Datalist datalist = new Datalist(outputfilename, filenames, type);
        datalist.create();
      }
      else {
        if (outputpath.length() == 0) {
          basename = new File(file.getAbsolutePath()).getParent();
          outputpath = basename;
        }

        String outputname = filename.replace(".COM", ".PAC");
        if (filename.equals(outputname)) {
          throw new IllegalArgumentException("Filename and Outputname must not have the same name");
        }
        final Main main = new Main(file, verboseLevel, outputpath, outputname);
        main.start();
      }
    }
    catch (final IllegalArgumentException e) {
      LOGGER.error("ERROR: {}", e.getMessage());
      throw new IllegalArgumentException(e);
    }
  }
  
}
