package gitlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/* Stager class for Gitlet, the tiny stupid version-control system.
   This class will aid in staging and unstaging files.
   @author Li Ying Kwa, Jeremy Chew
*/
public class Stager {

    // check if file is marked for removal (untracked)
    // check if file is already staged
    // check if file is in parent commit (being tracked)
    static boolean stageFile(String filePath) {
        String fileHash = Utils.getFileHash(filePath);
        // Check if file is marked for removal
        File removeFile = new File(".gitlet/staging/rem_" + filePath);
        if (removeFile.isFile()) {
            if (!removeFile.delete()) {
                return false;
            }
        }
        // Check if exact same file exists in staging
        File stagedFile = new File(".gitlet/staging/" + fileHash + "_" + filePath);
        if (stagedFile.isFile()) {
            // file already exists in staging
            // remove
            System.out.println("in staging alr");
            return false;
        }
        // Check if exact same file exists in the current Commit
        Commit commit = Utils.readHeadCommit();
        if (commit != null) {
            HashMap<String, String> contents = commit.getContents();
            if (!contents.containsKey(filePath)) {
                return Utils.moveFile(filePath, ".gitlet/staging", filePath, true);
            } else {
                if (!Utils.getFileHash(filePath).equals(contents.get(filePath))) {
                    return Utils.moveFile(filePath, ".gitlet/staging", filePath, true);
                } else {
                    return true;
                }
            }
        } else {
            // Remove this before submission
            System.out.println("An error occurred while reading the current HEAD commit.");
            return false;
        }
        // Copy file over to staging
//        return Utils.moveFile(filePath, ".gitlet/staging", filePath, true);

    }

    static boolean unstageFile(String filePath) {
        boolean inStaging = false, inCommit = false;
        // Check if file exists in staging
        String fileHash = Utils.getFileHash(filePath);
        File stagedFile = new File(".gitlet/staging/" + filePath);
        if (stagedFile.isFile()) {
            inStaging = true;
            // unstage file
            if (!stagedFile.delete()) {
                // Remove this before submission
                System.out.println("Unable to delete file in staging.");
                return false;
            }
        }
        // Check if file is tracked in current commit
        Commit commit = Utils.readHeadCommit();
        if (commit != null) {
            if (commit.getContents().containsKey(filePath)) {
                inCommit = true;
                try {
                    File remFile = new File(".gitlet/staging/rem_" + filePath);
                    if (remFile.createNewFile()) {
                        // delete filePath
                        File file = new File(filePath);
                        return file.isFile() && file.delete();
                    }
                } catch (IOException e) {
                    // Remove this before submission
                    System.out.println("An unknown i/o error occurred in Commands.runRm.");
                    e.printStackTrace();
                    return false;
                }
            }
        }
        if (inStaging || inCommit) {
            return true;
        } else {
            System.out.println("No reason to remove the file.");
            return false;
        }
    }
}
