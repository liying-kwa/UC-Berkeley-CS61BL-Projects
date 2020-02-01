package gitlet;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

/* Commit class for Gitlet, the tiny stupid version-control system.
   This class will store all information relating to a single Commit object.
   @author Li Ying Kwa, Jeremy Chew
*/
public class Commit implements Serializable {

    private String selfHash;
    private String parentHash;
    private String message;
    private Date timestamp;

    // Maps file names to their hashes.
    private HashMap<String, String> contents;

    public Commit(String parentHash, String message) {
        this.parentHash = parentHash;
        this.message = message;
        this.timestamp = new Date();

        // Generate self hash using the above values
        String hashable = Objects.requireNonNullElse(parentHash, "null");
        selfHash = Utils.sha1(hashable, message, timestamp.toString());

        // Initialise contents to be empty
        contents = new HashMap<>();
    }

    /* Returns the information of this commit for logging purposes. */
    String getCommitLog() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String retStr = "===\n";
        retStr += "Commit " + selfHash + "\n";
        retStr += format.format(timestamp) + "\n";
        retStr += message + "\n\n";
        return retStr;
    }

    // Basic getters
    String getHash() {
        return selfHash;
    }
    String getParentHash() {
        return parentHash;
    }
    String getMessage() {
        return message;
    }
    HashMap<String, String> getContents() {
        return contents;
    }

    // Basic setters
    void setContents(HashMap<String, String> contents) {
        this.contents = contents;
    }
}
