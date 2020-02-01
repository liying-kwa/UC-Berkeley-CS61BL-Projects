package gitlet;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/* File Manager class for Gitlet, the tiny stupid version-control system.
   This class will perform all the necessary file movements and checks for checking out and
   resetting of commits/branches.
   @author Li Ying Kwa, Jeremy Chew
*/
public class FileManager {

    static boolean checkoutFile(String file, String commitHash) {

        if (commitHash.length() < 40) {
            List<String> commitHashes = Utils.plainFilenamesIn(".gitlet/commits");
            for (String commit : commitHashes) {
                if (commit.substring(0, commitHash.length()).equals(commitHash)) {
                    commitHash = commit;
                    break;
                }
            }
        }

        // First make sure that the commit with the provided hash id exists
        File comFile = new File(".gitlet/commits/" + commitHash);
        if (!comFile.isFile()) {
            System.out.println("No commit with that id exists.");
            return false;
        }
        // Reconstruct the commit object
        Commit commit = Utils.readCommit(commitHash);
        if (commit != null) {
            HashMap<String, String> contents = commit.getContents();
            // Check if file exists in the commit
            if (contents.containsKey(file)) {
                String fileHash = contents.get(file);
                return Utils.moveFile(".gitlet/objects/" + fileHash, ".", file, true);
            } else {
                System.out.println("File does not exist in that commit.");
                return false;
            }

        } else {
            // Remove this before submission
            System.out.println("A commit could not be read properly (" + commitHash + ").");
            return false;
        }
    }

    // Equivalent to reset <commitId> but without the changing of HEAD and BRANCHES
    static boolean checkoutCommit(String commitHash) {
        // First make sure that the commit with the provided hash id exists
        File comFile = new File(".gitlet/commits/" + commitHash);
        if (!comFile.isFile()) {
            System.out.println("No commit with that id exists.");
            return false;
        }
        // Reconstruct the commit object
        Commit commit = Utils.readCommit(commitHash);
        if (commit != null) {
            boolean overwritten = false;
            HashMap<String, String> contents = commit.getContents();
            // Check every file in working dir to determine if they will be overwritten
            List<String> workingDirFiles = Utils.plainFilenamesIn(".");
            for (String filePath : workingDirFiles) {
                Commit headCommit = Utils.readHeadCommit();
                boolean fileTracked = Utils.isFileTracked(headCommit, filePath);
                if (!fileTracked) {
                    // Check if it will be overwritten (but is not tracked)
                    boolean inContents = contents.containsKey(filePath);
                    if (inContents) {
                        if (!contents.get(filePath).equals(Utils.getFileHash(filePath))) {
                            // will be overwritten (while it's not tracked)
                            overwritten = true;
                            break;
                        }
                    }
                }
            }
            if (overwritten) {
                    System.out.println("There is an untracked file in the way; delete it or add it "
                            + "first.");
                return false;
            } else {
                // remove files that are tracked and not in the given commit
                for (String filePath : workingDirFiles) {
                    Commit headCommit = Utils.readHeadCommit();
                    boolean fileTracked = Utils.isFileTracked(headCommit, filePath);
                    if (fileTracked && !contents.containsKey(filePath)) {
                        File delFile = new File(filePath);
                        if (!delFile.delete()) {
                            // Remove this before submission
                            System.out.println("file could not be deleted. (" + filePath + ")");
                            return false;
                        }
                    }
                }
                // move files that are being tracked in the commit
                for (var entry : contents.entrySet()) {
                    String fileHash = entry.getValue();
                    String fileName = entry.getKey();
                    if (!Utils.moveFile(".gitlet/objects/" + fileHash, ".", fileName, true)) {
                        // Remove this before submission
                        System.out.println("file could not be moved from objects. ("
                                + fileName + ")");
                        return false;
                    }
                }
            }
        } else {
            // Remove this before submission
            System.out.println("A commit could not be read properly (" + commitHash + ").");
            return false;
        }
        return true;
    }

    static boolean checkoutCommitShortHash(String hash) {
        List<String> commitHashes = Utils.plainFilenamesIn(".gitlet/commits");
        for (String commit : commitHashes) {
            if (commit.substring(0, hash.length()).equals(hash)) {
                return checkoutCommit(commit);
            }
        }
        System.out.println("No commit with that id exists.");
        return false;
    }

    static String shortHashToLong(String hash) {
        List<String> commitHashes = Utils.plainFilenamesIn(".gitlet/commits");
        for (String commit : commitHashes) {
            if (commit.substring(0, hash.length()).equals(hash)) {
                return commit;
            }
        }
        return null;
    }
}
