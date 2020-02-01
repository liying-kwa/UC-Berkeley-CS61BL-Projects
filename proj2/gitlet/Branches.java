package gitlet;

import java.io.Serializable;
import java.util.HashMap;

/* Branches class for Gitlet, the tiny stupid version-control system.
   This class will store all the branches and the pointer they refer to.
   @author Li Ying Kwa, Jeremy Chew
*/
public class Branches implements Serializable {

    private String currBranch;
    private HashMap<String, String> branches;

    public Branches() {
        branches = new HashMap<>();
    }

    boolean createBranch(String branchName) {
        if (!branches.containsKey(branchName)) {
            String head = Utils.readHead();
            if (head != null) {
                branches.put(branchName, head);
                return true;
            }
        }
        return false;
    }

    boolean setCurrBranch(String branchName) {
        if (branches.containsKey(branchName)) {
            currBranch = branchName;
            return true;
        } else {
            return false;
        }
    }

    String getBranchHash(String branchName) {
        if (branchExists(branchName)) {
            return branches.get(branchName);
        }
        return null;
    }

    String getCurrBranch() {
        return currBranch;
    }

    boolean updateBranchToHead() {
        String head = Utils.readHead();
        if (head != null) {
            branches.replace(currBranch, head);
            return true;
        }
        return false;
    }

    boolean branchExists(String branchName) {
        return branches.containsKey(branchName);
    }

    // returns 0 if success, -1 if currBranch is branchName, -2 if branchName doesn't exist
    int deleteBranch(String branchName) {
        if (currBranch.equals(branchName)) {
            return -1;
        } else if (!branches.containsKey(branchName)) {
            return -2;
        } else {
            branches.remove(branchName);
            return 0;
        }
    }

    Object[] getAllBranches() {
        return branches.keySet().toArray();
    }
}
