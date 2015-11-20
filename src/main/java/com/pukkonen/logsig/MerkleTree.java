package com.pukkonen.logsig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * MerkleTree is an implementation of a Merkle binary hash tree where the leaves
 * are signatures of some underlying data structure that is not explicitly part
 * of the tree.
 * 
 * The internal leaves of the tree are signatures of its two child nodes. If an
 * internal node has only one child, then the signature of the child node is
 * adopted ("promoted").
 */
public class MerkleTree {

	/**
	 * Maps each leaf digest to a generated leaf node
	 */
	private HashMap<String, Node> leafMap;

	/**
	 * Create a MerkleTree from a list of leaf digests. Merkle tree is built
	 * from the bottom up.
	 *
	 * @param digests
	 *            array of leaf digests (bottom level)
	 */
	public MerkleTree(ArrayList<String> digests) {
		leafMap = new HashMap<String, Node>();
		constructTree(digests);
	}

	/**
	 * Create a tree from the bottom up starting from the leaf digests.
	 *
	 * @param digests
	 *            array of leaf digests (bottom level)
	 */
	private void constructTree(List<String> digests) {
		if (digests.size() <= 1) {
			throw new IllegalArgumentException("Must include at least two digests to construct a Merkle tree");
		}

		List<Node> parents = bottomLevel(digests);

		while (parents.size() > 1) {
			parents = internalLevel(parents);
		}

		// Node root = parents.get(0);
		// KSISignature signature = signTree(root.digest);
	}

	/**
	 * Constructs the bottom part of the tree - leaf nodes and their immediate
	 * parents.
	 *
	 * @param digests
	 *            array of leaf digests (bottom level)
	 * @return list of bottom level nodes
	 */
	private List<Node> bottomLevel(List<String> digests) {
		List<Node> parents = new ArrayList<Node>(digests.size() / 2);

		for (int i = 0; i < digests.size() - 1; i += 2) {
			String digestL = digests.get(i);
			String digestR = digests.get(i + 1);

			Node leafL = constructLeafNode(digestL);
			Node leafR = constructLeafNode(digestR);

			leafMap.put(digestL, leafL);
			leafMap.put(digestR, leafR);

			Node parent = constructInternalNode(leafL, leafR);
			parents.add(parent);
		}

		// promote last node in case of odd number of nodes
		if (digests.size() % 2 != 0) {
			String digestL = digests.get(digests.size() - 1);
			Node leaf = constructLeafNode(digestL);
			leafMap.put(digestL, leaf);

			Node parent = constructInternalNode(leaf, null);
			parents.add(parent);
		}

		return parents;
	}

	private Node constructLeafNode(String digest) {
		Node leaf = new Node();
		leaf.digest = digest;
		return leaf;
	}

	/**
	 * Constructs an internal level of the tree.
	 *
	 * @param children
	 *            list of children nodes on the previous level
	 * @return list of nodes on the current level
	 */
	private List<Node> internalLevel(List<Node> children) {
		List<Node> parents = new ArrayList<Node>(children.size() / 2);

		for (int i = 0; i < children.size() - 1; i += 2) {
			Node child1 = children.get(i);
			Node child2 = children.get(i + 1);

			Node parent = constructInternalNode(child1, child2);
			parents.add(parent);
		}

		// promote last node in case of odd number of nodes
		if (children.size() % 2 != 0) {
			Node child = children.get(children.size() - 1);
			Node parent = constructInternalNode(child, null);
			parents.add(parent);
		}

		return parents;
	}

	/**
	 * Constructs an internal node based on its left and right child.
	 *
	 * @param childL
	 *            left child for the current node (always not null)
	 * @param childR
	 *            right child for the current node (can be null)
	 * @return internal node
	 */
	private Node constructInternalNode(Node childL, Node childR) {
		Node parent = new Node();

		if (childR == null) {
			parent.digest = childL.digest;
		} else {
			parent.digest = DigestUtils.sha1Hex(childL.digest + childR.digest);
			childR.parent = parent;
		}
		childL.parent = parent;

		parent.left = childL;
		parent.right = childR;

		return parent;
	}

	public HashMap<String, Node> getLeafMap() {
		return this.leafMap;
	}

	/**
	 * Find a leaf node by a line from input file
	 */
	public Node findLeafNode(String str) throws Exception {
		return this.getLeafMap().get(DigestUtils.sha1Hex(str));
	}

	/**
	 * Sign a digest with KSI library
	 */
//	 private KSISignature signTree(String rootDigest) {
//	
//		 SimpleHttpClient simpleHttpClient = ...
//		
//		 KSIBuilder builder = new KSIBuilder().
//		 setKsiProtocolSignerClient(simpleHttpClient).
//		 setKsiProtocolExtenderClient(simpleHttpClient).
//		 setKsiProtocolPublicationsFileClient(simpleHttpClient).
//		 setPublicationsFileTrustedCertSelector(new
//		 X509CertificateSubjectRdnSelector("E=test@test.com"));
//		
//		 KSI ksi = builder.build();
//		
//		 KSISignature signature = ksi.sign(rootDigest.getBytes());
//		
//		 ksi.close();
//		 
//		 return signature;
//	 }

	/**
	 * Internal nodes will have at least one child (always on the left). Leaf
	 * nodes will have no children (left = right = null). Root node will have no
	 * parent (parent = null).
	 */
	static class Node {
		public String digest;
		public Node parent;
		public Node left;
		public Node right;
	}
}