package gitlet;

/* Driver class for Gitlet, the tiny stupid version-control system.
   @author Li Ying Kwa, Jeremy Chew
*/
public class Main {

    /* Usage: java gitlet.Main ARGS, where ARGS contains
       <COMMAND> <OPERAND> .... */
    public static void main(String... args) {
        Commands.readCommand(args);
    }

}
