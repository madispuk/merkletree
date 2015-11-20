package com.pukkonen.logsig;

import com.pukkonen.logsig.MerkleTree.Node;

public class App {
	public static void main(String[] args) {
		try {
			MerkleTree tree = new TreeBuilder().buildMerkleTree("/sample.log");
			
			Node n = tree.findLeafNode("Play chess");
			if (n == null) {
				throw new Exception("No such line in the file");
			}
			
			printHashChain(n);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Print hash chain on the console from the leaf node till the root node.
	 * Leaves are on the level 0.
	 */
	public static void printHashChain(Node node) {

		System.out.print("Level 0: ");
		System.out.println(node.digest);

		int count = 1;
		while (node.parent != null) {
			System.out.print("Level " + count + ": ");
			System.out.println(node.parent.digest);
			node = node.parent;
			count++;
		}
	}
}
