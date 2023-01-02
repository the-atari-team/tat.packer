package lla.privat.atarixl.packer.pair;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class TestPair {

	@Before
	public void setUp() {
	}

	@Test
	public void testLeft() {
		Pair pair = new Pair(123,456);
		
		Assert.assertEquals(123, pair.getLeft());
	}

	@Test
	public void testRight() {
		Pair pair = new Pair(123,456);
		
		Assert.assertEquals(456, pair.getRight());
	}

	@Test
	public void testEquals() {
		Pair pair = new Pair(1,2);	
		Pair pair2 = new Pair(1,2);
		
		Assert.assertTrue(pair.equals(pair2));
	}
}
