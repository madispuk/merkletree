package com.pukkonen.logsig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.commons.codec.digest.DigestUtils;

public class TreeBuilder {

	private ArrayList<String> digests;

	public TreeBuilder() {
		digests = new ArrayList<String>();
	}

	/**
	 * Build merkle tree from a file. Each line of the file is digested and
	 * used as a leaf.
	 */
	public MerkleTree buildMerkleTree(String fileName) throws Exception {
		InputStream is = this.getClass().getResourceAsStream(fileName);
		BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

		String line;
		while ((line = br.readLine()) != null) {
			digests.add(DigestUtils.sha1Hex(line));
		}

		return new MerkleTree(digests);
	}
}
