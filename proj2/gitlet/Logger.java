package gitlet;

import java.util.List;

/* Logger class for Gitlet, the tiny stupid version-control system.
   This class will generate commit logs and find commit messages.
   @author Li Ying Kwa, Jeremy Chew
*/
public class Logger {

    static void localLog() {
        String curHash = Utils.readHead();
        while (curHash != null) {
            // Rebuild the commit object
            Commit curCommit = Utils.readCommit(curHash);
            if (curCommit != null) {
                System.out.print(curCommit.getCommitLog());
                curHash = curCommit.getParentHash();
            } else {
                // Remove this before submission
                System.out.println("A commit could not be read properly (" + curHash + ").");
            }
        }
    }

    static void globalLog() {
        List<String> commits = Utils.plainFilenamesIn(".gitlet/commits");
        for (String commitHash : commits) {
            Commit commit = Utils.readCommit(commitHash);
            if (commit != null) {
                System.out.print(commit.getCommitLog());
            } else {
                // Remove this before submission
                System.out.println("A commit could not be read properly (" + commitHash + ").");
            }
        }
    }

    static void findCommitMsg(String msg) {
        boolean foundAny = false;
        List<String> commits = Utils.plainFilenamesIn(".gitlet/commits");
        for (String commitHash : commits) {
            Commit commit = Utils.readCommit(commitHash);
            if (commit != null) {
                if (commit.getMessage().contains(msg)) {
                    System.out.println(commit.getHash());
                    foundAny = true;
                }
            } else {
                // Remove this before submission
                System.out.println("A commit could not be read properly (" + commitHash + ").");
            }
        }
        if (!foundAny) {
            System.out.println("Found no commit with that message.");
        }
    }
}
