import java.util.Arrays;
import java.util.Iterator;

public class test {

	public static void main(String[] args){
		System.out.println(encodeCharacter((int)'z'));
	}

	public static String encodeCharacter(int c) {

		if (c >= 97 && c <= 122){
			c -= 32;
		}
		c -= 64;

		

		/*** Step in this method to be implemented by students
		 *
		 *
		 * Encode the character into its codeword using the Huffman tree.
		 * Remember that the algorithm must run in O(L)
		 * where L is the size of the codeword generated
		 *
		 *
		 ****/

		return Integer.toBinaryString(c);   /*** note this is returning a wrong output (always 0)***/
	}
}
