package com.pukkonen.logsig;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;

import com.pukkonen.logsig.MerkleTree.Node;

public class TreeBuilderTest {

	@Test
	public void testTreeBuilder() throws Exception {
		MerkleTree tree = new TreeBuilder().buildMerkleTree("/sample.log");
		Node n = tree.findLeafNode("Play chess");
		assertEquals(n.digest, DigestUtils.sha1Hex("Play chess"));
		
		n = tree.findLeafNode("Unknown line");
		assertNull(n);
	}
}
