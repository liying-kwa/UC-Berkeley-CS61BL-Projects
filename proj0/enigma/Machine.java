package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author
 */
public class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls. ALLROTORS contains all the
     *  available rotors. */

    int numRotors;
    int numPawls;
    Rotor[] allRotors;
    Rotor[] machineRotors;
    Permutation plugboard;

    public Machine(Alphabet alpha, int numRotors, int pawls,
            Rotor[] allRotors) {
        _alphabet = alpha;
        // FIXME - Assign any additional instance variables.
        this.numRotors = numRotors;
        this.numPawls = pawls;
        this.allRotors = allRotors;
        this.machineRotors = new Rotor[numRotors];
        this.plugboard = null;
    }

    /** Return the number of rotor slots I have. */
    public int numRotors() {
        // FIXME - How do we access the number of Rotor slots I have?
        return numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    public int numPawls() {
        // FIXME - How do we access the number of pawls I have?
        return numPawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    public void insertRotors(String[] rotors) {
        // FIXME - How do we fill this Machine with Rotors, based on names of available Rotors?
        // For each rotor slot in the machineRotors (5 rotors in eg)
        for (int i = 0; i < machineRotors.length; i++) {
            // Check the name of the rotor and match accordingly to the one in allRotors (8 total rotors in eg)
            for (int j = 0; j < allRotors.length; j++) {
                if (rotors[i].toUpperCase().equals(allRotors[j].name().toUpperCase()) ) {
                    machineRotors[i] = allRotors[j];
                }
            }
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    public void setRotors(String setting) {
        // FIXME - How do we set the positions of each Rotor in this Machine?

        // Create an array for letters of machineRotors

        char[] rotorLetters = new char[setting.length()];
        for (int i = 0; i < rotorLetters.length; i++) {
            rotorLetters[i] = setting.charAt(i);
        }

        // For machineRotors 2 to 5, set the setting (in terms of int)
        for (int i = 1; i < machineRotors.length; i++) {
            char thisRotorLetter = rotorLetters[i-1];
            int thisRotorSetting = _alphabet.toInt(thisRotorLetter);
            machineRotors[i].set(thisRotorSetting);
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    public void setPlugboard(Permutation plugboard) {
        // FIXME - How do we assign our plugboard, based on a given Permutation?
        this.plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    public int convert(int c) {
    	// HINT: This one is tough! Consider using a helper method which advances
    	//			the appropriate Rotors. Then, send the signal into the
    	//			Plugboard, through the Rotors, bouncing off the Reflector,
    	//			back through the Rotors, then out of the Plugboard again.
        // FIXME - How do we convert a single character index?

        // Advance the rotors
        advance();

        // First, comes plugboard. Check if letter in plugboard; If it is, reflect accordingly.
        char originalLetter = _alphabet.toChar(c);
        char letterAfterPlugboard = originalLetter;
        if (plugboard.cycles.indexOf(originalLetter) != -1) {
            letterAfterPlugboard = plugboard.permute(originalLetter);
        }


        // Next comes rotor 5 to 1. Convert forward the letter one by one
        char letterToConvert = letterAfterPlugboard;    // F
        for (int i = machineRotors.length-1; i >= 0; i--) {
            int indexToConvert = _alphabet.toInt(letterToConvert);  // 5
            int convertedIndex = machineRotors[i].convertForward(indexToConvert);
            char convertedLetter = _alphabet.toChar(convertedIndex);
            letterToConvert = convertedLetter;
        }

        // Next comes rotor 2 to 5 in the other direction. Convert backward the letter one by one
        for (int i = 1; i < machineRotors.length; i++) {
            int indexToConvert = _alphabet.toInt(letterToConvert);
            int convertedIndex = machineRotors[i].convertBackward(indexToConvert);
            char convertedLetter = _alphabet.toChar(convertedIndex);
            letterToConvert = convertedLetter;
        }

        // Finally, it goes through the plugboard again
        char letterToReflect = letterToConvert;
        char finalLetter = letterToReflect;
        if ( plugboard.cycles.indexOf(letterToReflect) != -1 ) {
            finalLetter = plugboard.permute(letterToReflect);
        }
        int finalIndex = _alphabet.toInt(finalLetter);

        return finalIndex;
    }

    /** Optional helper method for convert() which rotates the necessary Rotors. */
    private void advance() {
    	// FIXME - How do we make sure that only the correct Rotors are advanced?

        for (int i = 0; i < machineRotors.length; i++) {

            // Check if next rotor at notch and rotator can move
            if (i != machineRotors.length-1 && machineRotors[i].rotates()) {
                if (machineRotors[i+1].atNotch()) {
                    machineRotors[i].advance();
                    if (!machineRotors[i+1].atNotch()) {
                        machineRotors[i+1].advance();
                    }
                }
            }

            else {
                // Always advance the last rotor
                machineRotors[i].advance();
            }
        }
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    public String convert(String msg) {
    	// HINT: Strings are basically just a series of characters
        // FIXME - How do we convert an entire String?

        String newMsg = "";

        // Convert every letter one by one in the string
        for (int i = 0; i < msg.length(); i++) {
            char originalLetter = msg.charAt(i);
            originalLetter = Character.toUpperCase(originalLetter);
            if (!_alphabet.contains(originalLetter)) {
                continue;
            }
            int originalIndex = _alphabet.toInt(originalLetter);
            int newIndex = convert(originalIndex);
            char newLetter = _alphabet.toChar(newIndex);
            newMsg += newLetter;
        }

        return newMsg;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    // FIXME - How do we keep track of my available Rotors/my Rotors/my pawls/my plugboard

    // FIXME: ADDITIONAL FIELDS HERE, IF NEEDED.

    // To run this through command line, from the proj0 directory, run the following:
    // javac enigma/Machine.java enigma/Rotor.java enigma/FixedRotor.java enigma/Reflector.java enigma/MovingRotor.java enigma/Permutation.java enigma/Alphabet.java enigma/CharacterRange.java enigma/EnigmaException.java
    // java enigma/Machine
    public static void main(String[] args) {

        CharacterRange upper = new CharacterRange('A', 'Z');
        MovingRotor rotorI = new MovingRotor("I",
                new Permutation("(AELTPHQXRU) (BKNW) (CMOY) (DFG) (IV) (JZ) (S)", upper),
                "Q");
        MovingRotor rotorII = new MovingRotor("II",
                new Permutation("(FIXVYOMW) (CDKLHUP) (ESZ) (BJ) (GR) (NT) (A) (Q)", upper),
                "E");
        MovingRotor rotorIII = new MovingRotor("III",
                new Permutation("(ABDHPEJT) (CFLVMZOYQIRWUKXSG) (N)", upper),
                "V");
        MovingRotor rotorIV = new MovingRotor("IV",
                new Permutation("(AEPLIYWCOXMRFZBSTGJQNH) (DV) (KU)", upper),
                "J");
        MovingRotor rotorV = new MovingRotor("V",
                new Permutation("(AVOLDRWFIUQ)(BZKSMNHYC) (EGTJPX)", upper),
                "Z");
        FixedRotor rotorBeta = new FixedRotor("Beta",
                new Permutation("(ALBEVFCYODJWUGNMQTZSKPR) (HIX)", upper));
        FixedRotor rotorGamma = new FixedRotor("Gamma",
                new Permutation("(AFNIRLBSQWVXGUZDKMTPCOYJHE)", upper));
        Reflector rotorB = new Reflector("B",
                new Permutation("(AE) (BN) (CK) (DQ) (FU) (GY) (HW) (IJ) (LO) (MP) (RX) (SZ) (TV)", upper));
        Reflector rotorC = new Reflector("C",
                new Permutation("(AR) (BD) (CO) (EJ) (FN) (GT) (HK) (IV) (LM) (PW) (QZ) (SX) (UY)", upper));

        Rotor[] allRotors = new Rotor[9];
        allRotors[0] = rotorI;
        allRotors[1] = rotorII;
        allRotors[2] = rotorIII;
        allRotors[3] = rotorIV;
        allRotors[4] = rotorV;
        allRotors[5] = rotorBeta;
        allRotors[6] = rotorGamma;
        allRotors[7] = rotorB;
        allRotors[8] = rotorC;

        Machine machine = new Machine(upper, 5, 3, allRotors);
        machine.insertRotors(new String[]{"B", "BETA", "III", "IV", "I"});
        machine.setRotors("AXLE");
        machine.setPlugboard(new Permutation("(HQ) (EX) (IP) (TR) (BY)", upper));

        System.out.println(machine.numRotors() == 5);
        System.out.println(machine.numPawls() == 3);
        System.out.println(machine.convert(5) == 16);
        System.out.println(machine.convert(17) == 21);
        System.out.println(machine.convert("OMHISSHOULDERHIAWATHA").equals("PQSOKOILPUBKJZPISFXDW"));
        System.out.println(machine.convert("TOOK THE CAMERA OF ROSEWOOD").equals("BHCNSCXNUOAATZXSRCFYDGU"));
        System.out.println(machine.convert("Made of sliding folding rosewood").equals("FLPNXGXIXTYJUJRCAUGEUNCFMKUF"));
    }
}
