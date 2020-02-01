package enigma;

import static enigma.EnigmaException.*;

/** Superclass that represents a rotor in the enigma machine.
 *  @author Li Ying Kwa
 */
public class Rotor {
    int setting;

    /** A rotor named NAME whose permutation is given by PERM. */
    public Rotor(String name, Permutation perm) {
        _name = name;
        _permutation = perm;
        // FIXME - Assign any additional instance variables.
        this.setting = 0;
    }

    /** Return my name. */
    public String name() {
        return _name;
    }

    /** Return my alphabet. */
    public Alphabet alphabet() {
        return _permutation.alphabet();
    }

    /** Return my permutation. */
    public Permutation permutation() {
        return _permutation;
    }

    /** Return the size of my alphabet. */
    public int size() {
        return _permutation.size();
    }

    /** Return true if and only if I have a ratchet and can move. */
    public boolean rotates() {
        return false;
    }

    /** Return true if and only if I reflect. */
    public boolean reflecting() {
        return false;
    }

    /** Return my current setting. */
    public int setting() {
        // FIXME - How do we keep track of my current position?
        return setting;
    }

    /** Set setting() to POSN.  */
    public void set(int posn) {
        // FIXME - How do we update our current position, based on an alphabet index?
        setting = posn;
    }

    /** Set setting() to character CPOSN. */
    public void set(char cposn) {
        // FIXME - How do we update our current position, based on an alphabet character?
        System.out.println(cposn);
        int index = alphabet().toInt(cposn);
        System.out.println(index);
        setting = index;
    }

    /** Return the conversion of P (an integer in the range 0..size()-1)
     *  according to my permutation. */
    public int convertForward(int p) {
        // FIXME - How do we permute the index P, taking into account my current position?
        int charEnteringIndex = p + setting;
        charEnteringIndex = permutation().notTooLargeIndex(charEnteringIndex);
        int indexAfterPerm = _permutation.permute(charEnteringIndex);
        int charLeavingIndex = indexAfterPerm - setting;
        charLeavingIndex = permutation().nonNegativeIndex(charLeavingIndex);
        return charLeavingIndex;
    }

    /** Return the conversion of C (an integer in the range 0..size()-1)
     *  according to the inverse of my permutation. */
    public int convertBackward(int c) {
        // FIXME - How do we invert the index E, taking into account my current position?
        int charEnteringIndex = c + setting;
        charEnteringIndex = permutation().notTooLargeIndex(charEnteringIndex);
        int indexAfterPerm = _permutation.invert(charEnteringIndex);
        int charLeavingIndex = indexAfterPerm - setting;
        charLeavingIndex = permutation().nonNegativeIndex(charLeavingIndex);
        return charLeavingIndex;
    }

    /** Returns true if and only if I am positioned to allow the rotor
     * to my left to advance. */
    public boolean atNotch() {
        return false;
    }

    /** Advance me one position, if possible. By default, does nothing. */
    public void advance() {
    }

    @Override
    public String toString() {
        return "Rotor " + _name;
    }

    /** My name. */
    private final String _name;

    /** The permutation implemented by this rotor in its 0 position. */
    private Permutation _permutation;

    // FIXME - How do we keep track of what position I am in?
    public int getSetting() {
        return setting;
    }

    // FIXME: ADDITIONAL FIELDS HERE, AS NEEDED

}
