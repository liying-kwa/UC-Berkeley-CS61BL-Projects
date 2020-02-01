package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author
 */
public class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */

    String cycles;

    public Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        // FIXME - Assign any additional instance variables.
        this.cycles = cycles;
    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    public int size() {
        // FIXME - How do we ask the alphabet for its size?
        return _alphabet.size();
    }

    /** Return the index result of applying this permutation to the character
     *  at index P in ALPHABET. */
    public int permute(int p) {
    	// NOTE: it might be beneficial to have one permute() method always call the other
        // FIXME - How do we use our instance variables to get the index that P permutes to?
        char oldChar = _alphabet.toChar(p);
        char newChar = permute(oldChar);
        int newIndex = _alphabet.toInt(newChar);
        return newIndex;
    }

    /** Return the index result of applying the inverse of this permutation
     *  to the character at index C in ALPHABET. */
    public int invert(int c) {
    	// NOTE: it might be beneficial to have one invert() method always call the other
        // FIXME - How do we use our instance variables to get the index that C inverts to?
        char oldChar = _alphabet.toChar(c);
        char newChar = invert(oldChar);
        int newIndex = _alphabet.toInt(newChar);
        return newIndex;
    }

    /** Return the character result of applying this permutation to the index
     * of character P in ALPHABET. */
    public char permute(char p) {
    	// NOTE: it might be beneficial to have one permute() method always call the other
        // FIXME - How do we use our instance variables to get the character that P permutes to?
        if (cycles.indexOf(p) == -1) {
            return p;
        }

        int pCycleIndex = cycles.indexOf(p);
        int newCycleIndex = 0;

        if ( cycles.charAt(pCycleIndex+1) != ')' ) {
            newCycleIndex = pCycleIndex + 1;
        }
        else {
            int index = pCycleIndex;
            while (index >= 0) {
                if (cycles.charAt(index) == '(') {
                    index += 1;
                    newCycleIndex = index;
                    break;
                }
                index -= 1;
            }
        }

        char newChar = cycles.charAt(newCycleIndex);
        return newChar;
    }

    /** Return the character result of applying the inverse of this permutation
	 * to the index of character P in ALPHABET. */
    public char invert(char c) {
    	// NOTE: it might be beneficial to have one invert() method always call the other
        // FIXME - How do we use our instance variables to get the character that C inverts to?
        if (cycles.indexOf(c) == -1) {
            return c;
        }

        int cCycleIndex = cycles.indexOf(c);
        int newCycleIndex = 0;

        if ( cycles.charAt(cCycleIndex-1) != '(' ) {
            newCycleIndex = cCycleIndex - 1;
        }
        else {
            int index = cCycleIndex;
            while (index <= cycles.length()-1) {
                if (cycles.charAt(index) == ')') {
                    index -= 1;
                    newCycleIndex = index;
                    break;
                }
                index += 1;
            }
        }
        char newChar = cycles.charAt(newCycleIndex);
        return newChar;
    }

    /** Return the alphabet used to initialize this Permutation. */
    public Alphabet alphabet() {
        return _alphabet;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    // FIXME - How do we store which letter permutes/inverts to which?

    // FIXME: ADDITIONAL FIELDS HERE, AS NEEDED
    public int nonNegativeIndex(int originalIndex) {
        if (originalIndex < 0) {
            return _alphabet.size() + originalIndex;
        }
        else {
            return originalIndex;
        }
    }
    public int notTooLargeIndex(int originalIndex) {
        if (originalIndex >= 26) {
            return originalIndex - _alphabet.size();
        }
        else {
            return originalIndex;
        }
    }

    // Some starter code for unit tests. Feel free to change these up!
    // To run this through command line, from the proj0 directory, run the following:
    // javac enigma/Permutation.java enigma/Alphabet.java enigma/CharacterRange.java enigma/EnigmaException.java
    // java enigma/Permutation
    public static void main(String[] args) {
        Permutation perm = new Permutation("(ABCDEFGHIJKLMNOPQRSTUVWXYZ)", new CharacterRange('A', 'Z'));
        System.out.println(perm.size() == 26);
        System.out.println(perm.permute('A') == 'B');
        System.out.println(perm.invert('B') == 'A');
        System.out.println(perm.permute(0) == 1);
        System.out.println(perm.invert(1) == 0);

        Permutation test = new Permutation ( "(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)", new CharacterRange('A', 'Z') );
        System.out.println(test.permute('A'));
        System.out.println(test.permute(0));
        System.out.println(test.permute('U'));
        System.out.println(test.permute(20));
        System.out.println(test.permute('B'));
        System.out.println(test.permute(1));
        System.out.println(test.permute('Y'));
    }
}
