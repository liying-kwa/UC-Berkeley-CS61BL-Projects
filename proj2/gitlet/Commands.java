package gitlet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/* Command class for Gitlet, the tiny stupid version-control system.
   This class will control the command flow of Gitlet.
   @author Li Ying Kwa, Jeremy Chew
*/
public class Commands {

    // Do the checking for proper number of args here
    public static boolean readCommand(String[] args) {
        return readInitToStatus(args);
    }

    private static boolean readInitToStatus(String[] args) {
        String command = "";
        if (args.length != 0) {
            command = args[0];
        } else {
            System.out.println("Please enter a command.");
            return false;
        }
        switch (command) {
            case "init":
                if (args.length == 1) {
                    runInit();
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "add":
                if (!gitletExists()) {
                    System.out.println("Not in an initialized gitlet directory.");
                } else if (args.length == 2) {
                    String filePath = args[1];
                    runAdd(filePath);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "commit":
                if (!gitletExists()) {
                    System.out.println("Not in an initialized gitlet directory.");
                } else if (args.length == 2) {
                    String commitMsg = args[1];
                    runCommit(commitMsg);
                } else if (args.length == 1) {
                    System.out.println("Please enter a commit message.");
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "rm":
                if (!gitletExists()) {
                    System.out.println("Not in an initialized gitlet directory.");
                } else if (args.length == 2) {
                    String filePath = args[1];
                    runRm(filePath);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "log":
                if (!gitletExists()) {
                    System.out.println("Not in an initialized gitlet directory.");
                } else if (args.length == 1) {
                    runLog();
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "global-log":
                if (!gitletExists()) {
                    System.out.println("Not in an initialized gitlet directory.");
                } else if (args.length == 1) {
                    runGlobalLog();
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "status":
                if (!gitletExists()) {
                    System.out.println("Not in an initialized gitlet directory.");
                } else if (args.length == 1) {
                    runStatus();
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            default:
                return readFindToMerge(args);
        }
        return true;
    }

    private static boolean readFindToMerge(String[] args) {
        String command = "";
        if (args.length != 0) {
            command = args[0];
        } else {
            System.out.println("Please enter a command.");
            return false;
        }
        switch (command) {
            case "find":
                if (!gitletExists()) {
                    System.out.println("Not in an initialized gitlet directory.");
                } else if (args.length == 2) {
                    String commitMsg = args[1];
                    runFind(commitMsg);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "checkout":
                if (!gitletExists()) {
                    System.out.println("Not in an initialized gitlet directory.");
                } else if (args.length == 2) {
                    runCheckout(args[1], null, null);
                } else if (args.length == 3) {
                    runCheckout(args[1], args[2], null);
                } else if (args.length == 4) {
                    runCheckout(args[1], args[2], args[3]);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "branch":
                if (!gitletExists()) {
                    System.out.println("Not in an initialized gitlet directory.");
                } else if (args.length == 2) {
                    String branchName = args[1];
                    runBranch(branchName);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "rm-branch":
                if (!gitletExists()) {
                    System.out.println("Not in an initialized gitlet directory.");
                } else if (args.length == 2) {
                    String branchName = args[1];
                    runRmBranch(branchName);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "reset":
                if (!gitletExists()) {
                    System.out.println("Not in an initialized gitlet directory.");
                } else if (args.length == 2) {
                    String commitHash = args[1];
                    runReset(commitHash);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            case "merge":
                if (!gitletExists()) {
                    System.out.println("Not in an initialized gitlet directory.");
                } else if (args.length == 2) {
                    String branchName = args[1];
                    runMerge(branchName);
                } else {
                    System.out.println("Incorrect operands.");
                }
                break;
            default:
                // not a valid command
                System.out.println("No command with that name exists.");
        }
        return true;
    }

    /* Check if .gitlet already exists
       Create a .gitlet, staging, commits and objects directory
       Create a HEAD file
       Create and save the initial Commit
       Create and save Branches object
    */
    private static boolean runInit() {
        if (Files.isDirectory(Paths.get(".gitlet"))) {
            // .gitlet already exists, print error message
            System.out.println("A gitlet version-control system already exists in the current "
                                + "directory.");
            return false;
        }
        try {
            Files.createDirectory(Paths.get(".gitlet"));
            Files.createDirectory(Paths.get(".gitlet/staging"));
            Files.createDirectory(Paths.get(".gitlet/objects"));
            Files.createDirectory(Paths.get(".gitlet/commits"));

            Commit initCommit = new Commit(null, "initial commit");
            String initHash = initCommit.getHash();

            // try to create and write to HEAD first, as well as save initCommit
            if (Utils.writeHead(initHash)) {
                if (Utils.saveObject(initCommit, ".gitlet/commits/", initHash)) {

                    // HEAD and initCommit has been created and saved successfully, create a
                    // new Branches obj
                    Branches b = new Branches();
                    if (b.createBranch("master")) {
                        // returns true or false depending if the saving is successful
                        return b.setCurrBranch("master") && Utils.saveObject(b, ".gitlet",
                                "BRANCHES");
                    }
                }
            }
            // something was not created or saved successfully, return false to trigger the flag
            return false;

        } catch (IOException e) {
            // Remove this before submission
            System.out.println("An unknown i/o error occurred in Commands.runInit.");
            e.printStackTrace();
            return false;
        }
    }

    /* Check if file exists
       Check if file exists already in staging/objects
       Move file to staging
     */
    private static boolean runAdd(String filePath) {
        File file = new File(filePath);
        // Check if file exists in working dir
        if (!file.isFile()) {
            System.out.println("File does not exist.");
            return false;
        }

        return Stager.stageFile(filePath);
    }

    // every time you run commit, you continue to track the parents commits, but you overwrite
    // them with the staging area
    // with a rem_filepath, the commit should look for a file with the same filepath and not
    // include that file when tracking
    // make sure to check that the file doesnt alr exist in objects before moving it from staging
    // to objects (as in the case of when you rm and add back the exact same file)
    static boolean runCommit(String msg) {
        // Check if there are files in staging area
        List<String> stagedFiles = Utils.plainFilenamesIn(".gitlet/staging");
        if (stagedFiles.size() == 0) {
            System.out.println("No changes added to the commit.");
            return false;
        }
        if (msg.isBlank()) {
            System.out.println("Please enter a commit message.");
            return false;
        }
        // Rebuild BRANCHES and current Commit
        String curHead = Utils.readHead();
        Commit curCommit = Utils.readHeadCommit();
        Branches branches = Utils.readBranches();
        if (curCommit != null && branches != null) {
            // Create new Commit
            Commit newCommit = new Commit(curHead, msg);
            // Get the files tracked by curCommit
            HashMap<String, String> newTracked = new HashMap<>(curCommit.getContents());
            // Iterate through staged files
            for (String filePath : stagedFiles) {
                File file = new File(".gitlet/staging/" + filePath);
                String[] fileInfo = filePath.split("_", 2);
                // fileInfo[0] is 'rem' and fileInfo[1] is filename
                // alt fileInfo is a 1-element array with fileInfo[0] being filename
                if (fileInfo[0].equals("rem")) {
                    // untrack file
                    newTracked.remove(fileInfo[1]);
                    if (!file.delete()) {
                        return false;
                    }
                } else {
                    // start tracking the new file
                    String fileHash = Utils.getFileHash(fileInfo[0]);
                    newTracked.put(fileInfo[0], fileHash);
                    if (!Utils.moveFile(".gitlet/staging/" + filePath, ".gitlet/objects",
                            fileHash,
                            false)) {
                        System.out.println(filePath);
                        return false;
                    }
                }
            }
            newCommit.setContents(newTracked);
            // Update HEAD and BRANCHES
            String newHash = newCommit.getHash();
            if (Utils.writeHead(newHash) && branches.updateBranchToHead()) {
                // Save BRANCHES and new Commit
                return Utils.saveObject(newCommit, ".gitlet/commits", newHash)
                        && Utils.saveObject(branches, ".gitlet", "BRANCHES");
            }
        }
        return false;
    }

    /* Check if file is staged, if so, unstage it
       Check if file is being tracked, if so delete from working_dir and mark it in staging
       Otherwise, no reason to remove file
     */
    private static boolean runRm(String filePath) {
        return Stager.unstageFile(filePath);
    }

    private static boolean runLog() {
        Logger.localLog();
        return true;
    }

    private static boolean runGlobalLog() {
        Logger.globalLog();
        return true;
    }

    private static boolean runStatus() {
        // displays what branches exist (in lexicographical order)
        Branches branches = Utils.readBranches();
        Object[] allBranches = branches.getAllBranches();
        Arrays.sort(allBranches);
        System.out.println("=== Branches ===");
        for (Object branch : allBranches) {
            if (branch.equals(branches.getCurrBranch())) {
                System.out.print("*");
            }
            System.out.println(branch);
        }
        System.out.println(); // Empty line

        // display staged files (in staging)
        System.out.println("=== Staged Files ===");
        for (String filePath : Utils.plainFilenamesIn(".gitlet/staging")) {
            if (!filePath.substring(0, 3).equals("rem")) {
                System.out.println(filePath);
            }
        }
        System.out.println(); // Empty line

        // display removed files (in staging)
        System.out.println("=== Removed Files ===");
        for (String filePath : Utils.plainFilenamesIn(".gitlet/staging")) {
            if (filePath.substring(0, 3).equals("rem")) {
                System.out.println(filePath.substring(4));
            }
        }
        System.out.println(); // Empty line

        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println(); // Empty line

        System.out.println("=== Untracked Files ===");
        System.out.println(); // Empty line
        return true;
    }

    private static boolean runFind(String message) {
        Logger.findCommitMsg(message);
        return true;
    }

    private static boolean runCheckout(String arg1, String arg2, String arg3) {
        if (arg2 == null && arg3 == null) {
            // checkout whole branch <arg1>
            // first get the branch head
            // checkout that commit
            // set HEAD to point to that branch head
            // set curBranch to be that branch
            Branches branches = Utils.readBranches();
            if (branches != null) {
                if (!branches.branchExists(arg1)) {
                    System.out.println("No such branch exists.");
                    return false;
                }
                if (branches.getCurrBranch().equals(arg1)) {
                    System.out.println("No need to checkout the current branch.");
                    return false;
                }
                String branchHash = branches.getBranchHash(arg1);
                boolean b = FileManager.checkoutCommit(branchHash);
                if (b) {
                    branches.setCurrBranch(arg1);
                    boolean writeHead = Utils.writeHead(branchHash);
                    boolean saveBranch = Utils.saveObject(branches, ".gitlet", "BRANCHES");
                    if (writeHead && saveBranch) {
                        return true;
                    }
                } else {
                    return false;
                }
            }
            // Remove this before submission
            System.out.println("unknown error occurred while writing to HEAD and saving BRANCHES");
            return false;
        } else if (arg3 == null && arg1.equals("--")) {
            // checkout single file <arg2> from HEAD
            return FileManager.checkoutFile(arg2, Utils.readHead());
        } else if (arg3 != null && arg2.equals("--")) {
            // checkout file <arg3> from commit <arg1>
            return FileManager.checkoutFile(arg3, arg1);
        } else {
            System.out.println("Incorrect operands.");
            return false;
        }
    }

    private static boolean runBranch(String branchName) {
        Branches branches = Utils.readBranches();
        if (branches != null) {
            if (branches.branchExists(branchName)) {
                System.out.println("A branch with that name already exists.");
                return false;
            } else {
                branches.createBranch(branchName);
                return Utils.saveObject(branches, ".gitlet", "BRANCHES");
            }
        }
        return false;
    }

    private static boolean runRmBranch(String branchName) {
        Branches branches = Utils.readBranches();
        if (branches != null) {
            int err = branches.deleteBranch(branchName);
            if (err == -1) {
                System.out.println("Cannot remove the current branch.");
                return false;
            } else if (err == -2) {
                System.out.println("A branch with that name does not exist.");
                return false;
            } else {
                return Utils.saveObject(branches, ".gitlet", "BRANCHES");
            }
        }
        return true;
    }

    private static boolean runReset(String commitHash) {
        // checkout said commit
        // update HEAD and BRANCHES
        boolean b;
        if (commitHash.length() < 40) {
            b = FileManager.checkoutCommitShortHash(commitHash);
            if (b) {
                commitHash = FileManager.shortHashToLong(commitHash);
            }
        } else {
            b = FileManager.checkoutCommit(commitHash);
        }
        if (b) {
            boolean writeHead = Utils.writeHead(commitHash);
            if (writeHead) {
                Branches branches = Utils.readBranches();
                if (branches != null) {
                    branches.updateBranchToHead();
                    if (Utils.saveObject(branches, ".gitlet", "BRANCHES")) {
                        // clear the staging area
                        List<String> stagedFiles = Utils.plainFilenamesIn(".gitlet/staging");
                        for (String file : stagedFiles) {
                            File f = new File(".gitlet/staging/" + file);
                            if (!f.delete()) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }
//        System.out.println("An unknown error occurred in Commands.runReset");
        return false;
    }

    private static boolean runMerge(String branchName) {
        return Merger.mergeBranch(branchName);
    }

    private static boolean gitletExists() {
        return new File(".gitlet").isDirectory();
    }

    public static void main(String[] args) {
        //readCommand(args);

//        runInit();
//        runAdd("test.txt");
//        runCommit("test1 commit");
//        runRm("test.txt");

        runAdd("test.txt");
//        runStatus();
//        runCheckout("abc", null, null);

//        boolean b = runCheckout("114684da0d40ddcf7edb61cfc54374f2e4578a83", "--", "test.txt");
//        System.out.println(b);
//        for (String filePath : Utils.plainFilenamesIn(".")) {
//            System.out.println(filePath + " " + Utils.isFileTracked(filePath));
//        }
//        boolean b = FileManager.checkoutCommit("743c0cfad7a0f1f6c5a5e9a3402425cc7c7aa4b0");
//        System.out.println(b);

    }
}
