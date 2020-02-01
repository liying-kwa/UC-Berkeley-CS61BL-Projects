package gitlet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/* Merger class for Gitlet, the tiny stupid version-control system.
   This class will handle merging, merging checks, and additional merge operations.
   @author Li Ying Kwa, Jeremy Chew
*/
public class Merger {

    static boolean mergeBranch(String branchName) {
        // check for uncommitted changes
        // check that branchName exists
        // check that branchName isnt curBranch
        // find the split point
        // given branch is ancestor of current branch (split point is given branch)
        // current branch fast forwarded (split point is current branch)
        List<String> stagedFiles = Utils.plainFilenamesIn(".gitlet/staging");
        if (stagedFiles.size() > 0) {
            System.out.println("You have uncommitted changes.");
            return false;
        }

        Branches branches = Utils.readBranches();
        if (branches != null) {
            if (!branches.branchExists(branchName)) {
                System.out.println("A branch with that name does not exist.");
                return false;
            }

            if (branchName.equals(branches.getCurrBranch())) {
                System.out.println("Cannot merge a branch with itself.");
                return false;
            }

            // find the split point
            // get the commit hash which branchName is pointing at
            String commitHash = branches.getBranchHash(branchName);
            String splitHash = findSplitPoint(commitHash);

            if (splitHash != null) {
                // case 1: split point is given branch
                if (commitHash.equals(splitHash)) {
                    System.out.println("Given branch is an ancestor of the current branch.");
                    return true;
                }
                // case 2: split point is current branch
                if (Utils.readHead().equals(splitHash)) {
                    if (FileManager.checkoutCommit(commitHash)) {
                        System.out.println("Current branch fast-forwarded.");
                        return true;
                    }
                    return false;
                }
                // case 3: normal merge
                // first get a list of all files in the split point, given branch and cur branch
                if (!mergeChecks(splitHash, commitHash)) {
                    System.out.println("There is an untracked file in the way; "
                            + "delete it or add it first.");
                    return false;
                } else {
                    mergeFiles(splitHash, commitHash, branchName, branches.getCurrBranch());
                    return true;
                }
            }
        }
        // Remove this before submission
        System.out.println("Could not read branches");
        return false;
    }

    private static boolean mergeFiles(String split, String commit, String oBr, String cBr) {
        Commit splitCommit = Utils.readCommit(split);
        Commit givenCommit = Utils.readCommit(commit);
        Commit curCommit = Utils.readHeadCommit();
        if (splitCommit != null && givenCommit != null && curCommit != null) {
            HashMap<String, String> splitFiles = splitCommit.getContents();
            HashMap<String, String> givenFiles = givenCommit.getContents();
            HashMap<String, String> curFiles = curCommit.getContents();
            Set<String> untrackedFiles = new HashSet<String>();
            for (String file : Utils.plainFilenamesIn(".")) {
                if (!Utils.isFileTracked(curCommit, file)) {
                    untrackedFiles.add(file);
                }
            }
            Set<String> allFiles = new HashSet<String>();
            allFiles.addAll(splitFiles.keySet());
            allFiles.addAll(givenFiles.keySet());
            allFiles.addAll(curFiles.keySet());
            boolean conflicts = false;
            for (String file : allFiles) {
                if (splitFiles.containsKey(file) && givenFiles.containsKey(file)
                        && curFiles.containsKey(file)) {
                    if (!splitFiles.get(file).equals(givenFiles.get(file))
                            && splitFiles.get(file).equals(curFiles.get(file))) {
                        // checkout and stage file
                        FileManager.checkoutFile(file, commit);
                        Stager.stageFile(file);
                        continue;
                    } else if (!splitFiles.get(file).equals(curFiles.get(file))
                            && splitFiles.get(file).equals(givenFiles.get(file))) {
                        continue;
                    }
                } else if (!givenFiles.containsKey(file) && splitFiles.containsKey(file)
                        && curFiles.containsKey(file)) {
                    if (splitFiles.get(file).equals(curFiles.get(file))) {
                        // untrack file
                        Stager.unstageFile(file);
                        continue;
                    }
                } else if (!givenFiles.containsKey(file) && !splitFiles.containsKey(file)
                        && curFiles.containsKey(file)) {
                    continue;
                } else if (givenFiles.containsKey(file) && !splitFiles.containsKey(file)
                        && !curFiles.containsKey(file)) {
                    // checkout and stage
                    FileManager.checkoutFile(file, commit);
                    Stager.stageFile(file);
                    continue;
                } else if (givenFiles.containsKey(file) && splitFiles.containsKey(file)
                        && !curFiles.containsKey(file)) {
                    if (givenFiles.get(file).equals(splitFiles.get(file))) {
                        continue;
                    }
                }
                conflicts = true;
                String retFile = "<<<<<<< HEAD\n";
                if (curFiles.containsKey(file) && !givenFiles.containsKey(file)) {
                    retFile += Utils.readFile(".gitlet/objects/" + curFiles.get(file));
                    retFile += "=======\n";
                } else if (!curFiles.containsKey(file) && givenFiles.containsKey(file)) {
                    retFile += "=======\n";
                    retFile += Utils.readFile(".gitlet/objects/" + givenFiles.get(file));
                } else {
                    retFile += Utils.readFile(".gitlet/objects/" + curFiles.get(file));
                    retFile += "=======\n";
                    retFile += Utils.readFile(".gitlet/objects/" + givenFiles.get(file));
                }
                retFile += ">>>>>>>\n";
                Utils.writeFile("./" + file, retFile);
            }
            if (conflicts) {
                System.out.println("Encountered a merge conflict.");
                return true;
            } else {
                return Commands.runCommit("Merged " + cBr + " with " + oBr + ".");
            }
        }
        return false;
    }

    private static boolean mergeChecks(String splitHash, String commitHash) {
        Commit splitCommit = Utils.readCommit(splitHash);
        Commit givenCommit = Utils.readCommit(commitHash);
        Commit curCommit = Utils.readHeadCommit();

        if (splitCommit != null && givenCommit != null && curCommit != null) {
            HashMap<String, String> splitFiles = splitCommit.getContents();
            HashMap<String, String> givenFiles = givenCommit.getContents();
            HashMap<String, String> curFiles = curCommit.getContents();

            Set<String> untrackedFiles = new HashSet<String>();
            for (String file : Utils.plainFilenamesIn(".")) {
                if (!Utils.isFileTracked(curCommit, file)) {
                    untrackedFiles.add(file);
                }
            }

            Set<String> allFiles = new HashSet<String>();
            allFiles.addAll(splitFiles.keySet());
            allFiles.addAll(givenFiles.keySet());
            allFiles.addAll(curFiles.keySet());

            for (String file : allFiles) {
                if (splitFiles.containsKey(file) && givenFiles.containsKey(file)
                        && curFiles.containsKey(file)) {
                    if (!splitFiles.get(file).equals(givenFiles.get(file))
                            && splitFiles.get(file).equals(curFiles.get(file))) {
                        // checkout and stage file
                        if (untrackedFiles.contains(file)) {
                            return false;
                        }
                    } else {
                        // the files are different and require the weird merging crap
                        if (untrackedFiles.contains(file)) {
                            return false;
                        }
                    }
                } else if (!givenFiles.containsKey(file) && splitFiles.containsKey(file)
                        && curFiles.containsKey(file)) {
                    if (splitFiles.get(file).equals(curFiles.get(file))) {
                        // untrack file
                        if (untrackedFiles.contains(file)) {
                            return false;
                        }
                    } else {
                        continue;
                    }
                } else if (!givenFiles.containsKey(file) && !splitFiles.containsKey(file)
                        && curFiles.containsKey(file)) {
                    // do nothing here
                    continue;
                } else if (givenFiles.containsKey(file) && !splitFiles.containsKey(file)
                        && !curFiles.containsKey(file)) {
                    // checkout and stage
                    if (untrackedFiles.contains(file)) {
                        return false;
                    }
                } else if (givenFiles.containsKey(file) && splitFiles.containsKey(file)
                        && !curFiles.containsKey(file)) {
                    if (givenFiles.get(file).equals(splitFiles.get(file))) {
                        continue;
                    }
                }

                if (untrackedFiles.contains(file)) {
                    return false;
                }
            }
        }
        return true;
    }

    /* Finds the split point between HEAD and the Commit with commitHash */
    private static String findSplitPoint(String commitHash) {
        LinkedList<String> curCommitList = new LinkedList<String>();
        LinkedList<String> otherCommitList = new LinkedList<String>();

        // Rebuild the commit tree (start with the current HEAD)
        String tempHash = Utils.readHead();
        Commit headCommit = Utils.readHeadCommit();
        while (tempHash != null) {
            curCommitList.addFirst(tempHash);
            headCommit = Utils.readCommit(tempHash);
            tempHash = headCommit.getParentHash();
        }
        // Rebuild the commit tree for the other commit
        tempHash = commitHash;
        Commit otherCommit = Utils.readCommit(commitHash);
        while (tempHash != null) {
            otherCommitList.addFirst(tempHash);
            otherCommit = Utils.readCommit(tempHash);
            tempHash = otherCommit.getParentHash();
        }

        // continuously pop the first element off both lists, and check to see if the next one is
        // different. if so, the popped element is the split point
        // alt. if one is empty and the other isn't the popped element is still the split point
        int minLength = Math.min(curCommitList.size(), otherCommitList.size());
        for (int i = 0; i < minLength; i++) {
            String curSplit = curCommitList.removeFirst();
            String othSplit = otherCommitList.removeFirst();
            if (curSplit.equals(othSplit)) {
                // check to see if either is now empty
                if (curCommitList.isEmpty() || otherCommitList.isEmpty()) {
                    return curSplit;
                }
                // check to see if the next first is different
                if (!curCommitList.getFirst().equals(otherCommitList.getFirst())) {
                    return curSplit;
                }
            } else {
                // Remove this before submission
                System.out.println("Somehow, something went wrong when finding the split point.");
                return null;
            }
        }
        // Remove this before submission
        System.out.println("Somehow, something went wrong when finding the split point.");
        return null;
    }
}
