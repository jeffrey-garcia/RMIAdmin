package RMIAdmin;


/**
 * The class which does all the coding and decoding of text files, using a modified version 
 * of the Vignere cipher system. Keys can only contain upper-case characters and space(s).
 * 'A' is worth 1, 'B' = 2, 'C' = 3, etc... 'Z'=26, and ' ' = 27.
 */
public class RMIClient_CipherKey {
	//default key = "I LOVE JAVA" as an array integers
	private final int [] defaultKey = {9,27,12,15,22,5,27,10,1,22,1};
	private int [] key = {9,27,12,15,22,5,27,10,1,22,1};

	/**
	 * Verifies that all of the characters in the propsed key are
	 * valid. If an invalid character is found, the method does
	 * not replace the current key and returns 'false', otherwise 
	 * it places each number representing the respective character
	 * into an array of integers. 
	 * @return true if the key was changed, false otherwise
	 * @param newString The propsed key
	 */
	public boolean changeKey(String newKey) {
		int [] tempKey = new int [newKey.length()];
		for (int i=0; i<tempKey.length; i++) {
			switch (newKey.charAt(i)) {
				case 'A': tempKey[i] = 1; break;
				case 'B': tempKey[i] = 2; break;
				case 'C': tempKey[i] = 3; break;
				case 'D': tempKey[i] = 4; break;
				case 'E': tempKey[i] = 5; break;
				case 'F': tempKey[i] = 6; break;
				case 'G': tempKey[i] = 7; break;
				case 'H': tempKey[i] = 8; break;
				case 'I': tempKey[i] = 9; break;
				case 'J': tempKey[i] = 10; break;
				case 'K': tempKey[i] = 11; break;
				case 'L': tempKey[i] = 12; break;
				case 'M': tempKey[i] = 13; break;
				case 'N': tempKey[i] = 14; break;
				case 'O': tempKey[i] = 15; break;
				case 'P': tempKey[i] = 16; break;
				case 'Q': tempKey[i] = 17; break;
				case 'R': tempKey[i] = 18; break;
				case 'S': tempKey[i] = 19; break;
				case 'T': tempKey[i] = 20; break;
				case 'U': tempKey[i] = 21; break;
				case 'V': tempKey[i] = 22; break;
				case 'W': tempKey[i] = 23; break;
				case 'X': tempKey[i] = 24; break;
				case 'Y': tempKey[i] = 25; break;
				case 'Z': tempKey[i] = 26; break;
				case ' ': tempKey[i] = 27; break;
				default: // invalid character in key
					key = defaultKey; //reset to default key
					//System.out.println(key.toString()); //Debugger
					return false;			
			}
		}
		
		if (tempKey.length > 0) {
			// key must have been good, so change it
			key = tempKey;
			
		} else {
			return false;
		}
		
		return true;
	}

	/**
	 * All encryption happens here. A string is analyzed character-by-
	 * character. The value of the appropriate key character is added
	 * and, if the result is beyond the printable range, the result is
	 * 'wrapped around' into the range.
	 * @return The encrypted string.
	 * @param inputLine The string to be encrypted.
	 */
	public String encryptLine(String inputLine) {
		// start with an empty string
		String encString = "";
		int ki=0; // pointer for the current position in the key array.
		
			for (int i=0; i<inputLine.length(); i++) {
				// for each character in the string to be encrypted, add
				// the current key value
				int c = inputLine.charAt(i) + key[ki];
				// test if the value is over 126. If so, subtract 95 to get
				// it back into the 32-126 range.
				if (c > 126)
					c-=95;
				// append the character to the string
				encString = encString + (char)c;
				ki++; // move key pointer
				// If the key has run out of character, go back to 0.
				if (ki >= key.length)
					ki = 0;
			}
			
		return encString;
	}

	/**
	 * All decryption happens here. A string is analyzed character-by-
	 * character. The value of the appropriate key character is subtracted
	 * and, if the result is below the printable range, the result is
	 * 'wrapped around' into the range.
	 * @return The decrypted string.
	 * @param inputLine The string to be decrypted.
	 */
	public String decryptLine(String inputLine) {
		// start with an empty string
		String decString = "";
		int ki=0; // pointer for the current position in the key array.
		
			for (int i=0; i<inputLine.length(); i++) {
				// for each character in the string to be decrypted, subtract
				// the current key value
				int c = inputLine.charAt(i) - key[ki];
				// test if the value is below 32. If so, add 95 to get
				// it back into the 32-126 range.
				if (c < 32)
					c+=95;
				// append the character to the string
				decString = decString + (char)c;
				ki++; // move key pointer
				// If the key has run out of character, go back to 0.
				if (ki >= key.length)
					ki = 0;
			}
		
		return decString;
	}
}
