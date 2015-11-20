package com.pukkonen.logsig;

import static org.junit.Assert.assertEquals;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import com.pukkonen.logsig.MerkleTree.Node;

public class MerkleTreeTest {

	@Test
	public void testMerkleTreeSize() {
		
		ArrayList<String> digests = new ArrayList<String>();
		digests.add("one");
		digests.add("two");
		digests.add("three");
		
		MerkleTree tree = new MerkleTree(digests);
		
		HashMap<String, Node> map = tree.getLeafMap();
		assertEquals(3, map.size());
	}
}
