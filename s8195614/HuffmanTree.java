import java.util.Arrays;
import java.util.Iterator;

/**
 * HuffmanTree creates and stores a Huffman tree based on Huffman nodes (an inner class),
 * It also provide a series of methods for encoding and decoding.
 * It uses a BitFeedOut which allows a stream of bits be sent continuously
 * to be used for encoding.
 * It also uses an Iterator<Byte> which allows a stream of bits to be read continuously
 * to be used when decoding.
 *
 * @author Lucia Moura
 */

public class HuffmanTree {

	public static int EndOfText = 0; //special symbol created to indicate end of text

	HuffmanNode root = null; // root of the Huffman tree
	HuffmanNode[] leafWhereLetterIs;   // array indexed by characters, storing a reference to


	public HuffmanTree (LetterFrequencies letterFreq) {
		root = BuildTree (letterFreq.getFrequencies(), letterFreq.getLetters());

	}


	private HuffmanNode BuildTree(int[] frequencies, char[] letters) {


		HeapPriorityQueue<HuffmanNode, HuffmanNode> heap =
		    new HeapPriorityQueue<HuffmanNode, HuffmanNode>(frequencies.length + 1);

		leafWhereLetterIs = new HuffmanNode[(int)'\uffff' + 2]; // need 2^16+1 spaces
		for (int i = 0; i < (int)'\uffff' + 2; i++)
			leafWhereLetterIs[i] = null;

		for (int i = 0; i < frequencies.length; i++) {
			if (frequencies[i] > 0) {
				HuffmanNode node = new HuffmanNode( (int)letters[i], frequencies[i], null, null, null);
				leafWhereLetterIs[(int)letters[i]] = node;
				heap.insert(node, node);

			}
		}
		HuffmanNode specialNode = new HuffmanNode(EndOfText, 0, null, null, null);
		leafWhereLetterIs[EndOfText] = specialNode; // last position reserved
		heap.insert(specialNode, specialNode);



		while(heap.size() > 1) {
			HuffmanNode e1 = heap.removeMin().getValue();
			HuffmanNode e2 = heap.removeMin().getValue();
			HuffmanNode newT = new HuffmanNode(-1, e1.getFrequency() + e2.getFrequency(), null, e1, e2);
			heap.insert(newT, newT);
		}

		return heap.removeMin().getValue();

	}

	private String encodeCharacter(int c) {
		return findLetter(root, "", c);   /*** note this is returning a wrong output (always 0)***/
	}


	public void encodeCharacter (int c, BitFeedOut bfo) {
		String s = encodeCharacter(c);
		for (int i = 0; i < s.length(); i++) bfo.putNext(s.charAt(i));
	}

	public int decodeCharacter(Iterator<Byte> bit) {
		if (root == null) return Integer.MAX_VALUE; // empty tree is not valid when decoding
		HuffmanNode current = root;
		while (bit.hasNext() && !current.isLeaf()){
			if (bit.next() == 0){
				current = current.leftChild();
			} else {
				current = current.rightChild();
			}
		}
		return current.getLetter();

	}




	void printCodeTable() {
		System.out.println("**** Huffman Tree: Character Codes ****");
		if (root != null)
			traverseInOrder(root, ""); // uses inorder traversal to print the codes
		else
			System.out.println("No character codes: the tree is still empty");
		System.out.println("***************************************");

	}

	private void traverseInOrder(HuffmanNode current, String c) {
		if (current.isLeaf()) {
			if (current.getLetter() != EndOfText)
				System.out.println((char)current.getLetter() + ":" + c);
			else System.out.println("EndOfText:" + c);
		} else {
			traverseInOrder(current.leftChild(), c + "0");
			traverseInOrder(current.rightChild(), c + "1");
		}

	}

	private String findLetter(HuffmanNode current, String c, int letter){
		if (current.isLeaf()) {
			if (current.getLetter() != EndOfText)
				if (current.getLetter() == letter){
					return c;
				}
		} else {
			String x = findLetter(current.leftChild(), c + "0", letter);
			if (x != ""){
				return x;
			}
			x = findLetter(current.rightChild(), c + "1", letter);
			if (x != ""){
				return x;
			}
		}
		return "";
	}

	byte[] freqsToBytes() {
		int b = 0;
		byte [] treeBytes = new byte[(int)'\uffff' * 4];
		for (int i = 0; i < '\uffff'; i++) {
			if (leafWhereLetterIs[i] != null) {
				int freq = leafWhereLetterIs[i].getFrequency();
				char letter = (char)leafWhereLetterIs[i].getLetter();
				treeBytes[b++] = (byte)(((int)letter) / 256);
				treeBytes[b++] = (byte)(((int)letter) % 256);
				treeBytes[b++] = (byte)(freq / 256);
				treeBytes[b++] = (byte)(freq % 256);
			}
		}
		return Arrays.copyOf(treeBytes, b);
	}

	/**** inner class to Huffman tree that implements a Node in the tree ****/
	// nothing to be changed in this inner class
	public class HuffmanNode implements Comparable<HuffmanNode> {

		int letter; // if the node is a leaf it will store a letter, otherwise it store null
		int frequency; // stores the sum of the frequencies of all leaves of the tree rooted at this node
		private HuffmanNode parent, left, right; // reference to parent, left and right nodes.

		public HuffmanNode() {
			parent = left = right = null;
			frequency = -1;
		}

		public HuffmanNode(int letter, int frequency, HuffmanNode parent, HuffmanNode left, HuffmanNode right) {
			this.letter = letter;
			this.frequency = frequency;
			this.parent = parent;
			this.left = left;
			this.right = right;
		}


		boolean isLeaf() { return (left == null && right == null);}

		// getter methods

		HuffmanNode leftChild() { return left;}

		HuffmanNode rightChild() { return right;}

		HuffmanNode parent() { return parent;}

		int getLetter() {return letter;}

		int getFrequency() {return frequency;}

		// setter methods

		void setLeftChild(HuffmanNode leftVal) { left = leftVal;	}

		void setRightChild(HuffmanNode rightVal) { right = rightVal;	}

		void setParent(HuffmanNode parentVal) { parent = parentVal;	}

		void setLetter(char letterVal) { letter = letterVal;}

		void setFrequency(int freqVal) { frequency = freqVal; }

		@Override
		public int compareTo(HuffmanNode o) {
			if (this.frequency == o.frequency) {
				return this.letter - o.letter;
			} else return this.frequency - o.frequency;

		}

	}



}
