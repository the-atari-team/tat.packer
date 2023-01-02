package lla.privat.atarixl.packer;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class TestDatalist {

	@Test
	  public void testReadPLAYSA_RLE_and_Huffman() throws Exception {
	  
	    List<String> filenames = new ArrayList<>();
	    filenames.add("src/test/resources/PLAYSA.DAT");
	    filenames.add("src/test/resources/PLAYSB.DAT");
	    filenames.add("nil");

	    String tmpdir = System.getProperty("java.io.tmpdir");
	    Datalist datalistSUT = new Datalist(tmpdir + "/output.txt", filenames, null);
	    datalistSUT.create();
	    Assert.assertEquals(533, datalistSUT.getFullCompressedLength());
	    Assert.assertFalse(datalistSUT.isUsePair());
	  }

	  @Test
	  public void testReadPLAYS0_RLE_and_Huffman() throws Exception {
	  
	    List<String> filenames = new ArrayList<>();
	    filenames.add("src/test/resources/PLAYS0.DAT");
	    filenames.add("nil");

	    String tmpdir = System.getProperty("java.io.tmpdir");
	    Datalist datalistSUT = new Datalist(tmpdir + "/output_nopair.txt", filenames, null);
	    datalistSUT.create();

	    Assert.assertEquals(274, datalistSUT.getFullCompressedLength());
	  }

    @Test
    public void testReadPLAYS0_Pair_RLE_and_Huffman() throws Exception {
    
      List<String> filenames = new ArrayList<>();
      filenames.add("src/test/resources/PLAYS0.DAT");
      filenames.add("nil");

      String tmpdir = System.getProperty("java.io.tmpdir");
      Datalist datalistSUT = new Datalist(tmpdir + "/output_pairs.txt", filenames, Datalist.Type.PAIRS);
      datalistSUT.create();
      Assert.assertEquals(121, datalistSUT.getFullCompressedLength());
      Assert.assertTrue(datalistSUT.isUsePair());
    }

    private List<String> createList() {
      List<String> filenames = new ArrayList<>();
      filenames.add("src/test/resources/PLAYS01.DAT");
      filenames.add("src/test/resources/PLAYS02.DAT");
      filenames.add("src/test/resources/PLAYS03.DAT");
      filenames.add("src/test/resources/PLAYS04.DAT");
      filenames.add("src/test/resources/PLAYS05.DAT");
      filenames.add("src/test/resources/PLAYS06.DAT");
      filenames.add("src/test/resources/PLAYS07.DAT");
      filenames.add("src/test/resources/PLAYS08.DAT");
      filenames.add("src/test/resources/PLAYS09.DAT");
      filenames.add("src/test/resources/PLAYS10.DAT");
      filenames.add("src/test/resources/PLAYS11.DAT");
      filenames.add("src/test/resources/PLAYS12.DAT");
      filenames.add("src/test/resources/PLAYS13.DAT");
      filenames.add("src/test/resources/PLAYS14.DAT");
      filenames.add("src/test/resources/PLAYS15.DAT");
      filenames.add("src/test/resources/PLAYS16.DAT");
      filenames.add("src/test/resources/PLAYS17.DAT");
      filenames.add("src/test/resources/PLAYS18.DAT");
      filenames.add("src/test/resources/PLAYS19.DAT");
      filenames.add("src/test/resources/PLAYS20.DAT");
      filenames.add("src/test/resources/PLAYS21.DAT");
      filenames.add("src/test/resources/PLAYS22.DAT");
      filenames.add("src/test/resources/PLAYS23.DAT");
      filenames.add("src/test/resources/PLAYS24.DAT");
      filenames.add("src/test/resources/PLAYS25.DAT");
      filenames.add("src/test/resources/PLAYS26.DAT");
      filenames.add("src/test/resources/PLAYS27.DAT");
      filenames.add("src/test/resources/PLAYS28.DAT");
      filenames.add("src/test/resources/PLAYS29.DAT");
      filenames.add("src/test/resources/PLAYS30.DAT");
      filenames.add("src/test/resources/PLAYS31.DAT");
      filenames.add("src/test/resources/PLAYS32.DAT");
      filenames.add("src/test/resources/PLAYS33.DAT");
      filenames.add("src/test/resources/PLAYS34.DAT");
      filenames.add("src/test/resources/PLAYS35.DAT");
      filenames.add("src/test/resources/PLAYS36.DAT");
      filenames.add("src/test/resources/PLAYS37.DAT");
      filenames.add("src/test/resources/PLAYS38.DAT");
      filenames.add("src/test/resources/PLAYS39.DAT");
      filenames.add("src/test/resources/PLAYS40.DAT");
      filenames.add("nil");
      return filenames;
    }
    
    @Test
    public void testReadPLAYS01_Pair_RLE_and_Huffman() throws Exception {
    
      List<String> filenames = createList();
      String tmpdir = System.getProperty("java.io.tmpdir");
      Datalist datalistSUT = new Datalist(tmpdir + "/output_pairs_2.txt", filenames, Datalist.Type.PAIRS);
      datalistSUT.create();
      Assert.assertEquals(3078, datalistSUT.getFullCompressedLength());
      Assert.assertTrue(datalistSUT.isUsePair());
    }

    @Test
    public void testReadPLAYS01_Block_RLE_and_Huffman() throws Exception {
    
      List<String> filenames = new ArrayList<>();
      filenames.add("src/test/resources/PLAYS01.DAT");
      filenames.add("nil");

      String tmpdir = System.getProperty("java.io.tmpdir");
      Datalist datalistSUT = new Datalist(tmpdir + "/output_blocks.txt", filenames, Datalist.Type.BLOCKS);
      datalistSUT.create();
      Assert.assertEquals(181, datalistSUT.getFullCompressedLength());
      Assert.assertTrue(datalistSUT.isUseBlocks());
    }

    @Test
    public void testReadPLAYS01_to_09_Block_RLE_and_Huffman() throws Exception {
    
      List<String> filenames = createList();

      String tmpdir = System.getProperty("java.io.tmpdir");
      Datalist datalistSUT = new Datalist(tmpdir + "/output_blocks.txt", filenames, Datalist.Type.BLOCKS);
      datalistSUT.create();
      Assert.assertEquals(2100, datalistSUT.getFullCompressedLength());
      Assert.assertTrue(datalistSUT.isUseBlocks());
    }

}
