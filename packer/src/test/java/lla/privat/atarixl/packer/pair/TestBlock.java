package lla.privat.atarixl.packer.pair;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TestBlock {

	@Before
	public void setUp() {
	}

	@Test
	public void testGet0_0() {
		Block block = new Block(4,3);
		block.set(0,1,2,3,40,41,42,43,80,81,82,83);
		
		Assert.assertEquals(0, block.get(0,0));
	}

	@Test
	public void testGet1_1() {
		Block block = new Block(4,3);
    block.set(0,1,2,3,40,41,42,43,80,81,82,83);
		
		Assert.assertEquals(41, block.get(1,1));
	}

	@Test
	public void testEquals() {
		Block block = new Block(4,3);	
    block.set(0,1,2,3,40,41,42,43,80,81,82,83);

    Block block2 = new Block(4,3);
    block2.set(0,1,2,3,40,41,42,43,80,81,82,83);

		Assert.assertTrue(block.equals(block2));
	}
	
  @Test
  public void testOut() {
    Block block2 = new Block(4,3);
    block2.set(0,1,2,3,40,41,42,43,80,81,82,83);
    Assert.assertEquals("0,1,2,3,40,41,42,43,80,81,82,83", block2.out());
  }

  @Test
  public void testOut2x3() {
    Block block2 = new Block(2,3);
    block2.set(0,1,2,3,4,5);
    Assert.assertEquals("0,1,2,3,4,5", block2.out());
  }
}
