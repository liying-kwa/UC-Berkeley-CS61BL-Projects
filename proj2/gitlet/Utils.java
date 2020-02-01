package gitlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;


/* Assorted utilities.
   @author P. N. Hilfinger, Li Ying Kwa, Jeremy Chew
*/
class Utils {

    /* SHA-1 HASH VALUES. */

    /* Returns the SHA-1 hash of the concatenation of VALS, which may be any
       mixture of byte arrays and Strings. */
    static String sha1(Object... vals) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            for (Object val : vals) {
                if (val instanceof byte[]) {
                    md.update((byte[]) val);
                } else if (val instanceof String) {
                    md.update(((String) val).getBytes(StandardCharsets.UTF_8));
                } else {
                    throw new IllegalArgumentException("improper type to sha1");
                }
            }
            Formatter result = new Formatter();
            for (byte b : md.digest()) {
                result.format("%02x", b);
            }
            return result.toString();
        } catch (NoSuchAlgorithmException excp) {
            throw new IllegalArgumentException("System does not support SHA-1");
        }
    }

    /* Returns the SHA-1 hash of the concatenation of the strings in VALS. */
    static String sha1(List<Object> vals) {
        return sha1(vals.toArray(new Object[vals.size()]));
    }

    /* FILE DELETION */

    /* Deletes FILE if it exists and is not a directory.  Returns true if FILE
       was deleted, and false otherwise.  Refuses to delete FILE and throws
       IllegalArgumentException unless the directory designated by FILE also
       contains a directory named .gitlet. */
    static boolean restrictedDelete(File file) {
        if (!(new File(file.getParentFile(), ".gitlet")).isDirectory()) {
            throw new IllegalArgumentException("not .gitlet working directory");
        }
        if (!file.isDirectory()) {
            return file.delete();
        } else {
            return false;
        }
    }

    /* Deletes the file named FILE if it exists and is not a directory. Returns
       true if FILE was deleted, and false otherwise. Refuses to delete FILE and
       throws IllegalArgumentException unless the directory designated by FILE
       also contains a directory named .gitlet. */
    static boolean restrictedDelete(String file) {
        return restrictedDelete(new File(file));
    }

    /* READING AND WRITING FILE CONTENTS */

    /* Return the entire contents of FILE as a byte array. FILE must be a normal
       file. Throws IllegalArgumentException in case of problems. */
    static byte[] readContents(File file) {
        if (!file.isFile()) {
            throw new IllegalArgumentException("must be a normal file");
        }
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /* Write the entire contents of BYTES to FILE, creating or overwriting it as
       needed. Throws IllegalArgumentException in case of problems. */
    static void writeContents(File file, byte[] bytes) {
        try {
            if (file.isDirectory()) {
                throw
                    new IllegalArgumentException("cannot overwrite directory");
            }
            Files.write(file.toPath(), bytes);
        } catch (IOException excp) {
            throw new IllegalArgumentException(excp.getMessage());
        }
    }

    /* OTHER FILE UTILITIES */

    /* Return the concatenation of FIRST and OTHERS into a File designator,
       analogous to the {@link java.nio.file.Paths.#get(String, String[])}
       method. */
    static File join(String first, String... others) {
        return Paths.get(first, others).toFile();
    }

    /* Return the concatenation of FIRST and OTHERS into a File designator,
       analogous to the {@link java.nio.file.Paths.#get(String, String[])}
       method. */
    static File join(File first, String... others) {
        return Paths.get(first.getPath(), others).toFile();
    }

    /* DIRECTORIES */

    /* Filter out all but plain files. */
    private static final FilenameFilter PLAIN_FILES =
        new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isFile();
            }
        };

    /* Returns a list of the names of all plain files in the directory DIR, in
       lexicographic order as Java Strings. Returns null if DIR does not denote
       a directory. */
    static List<String> plainFilenamesIn(File dir) {
        String[] files = dir.list(PLAIN_FILES);
        if (files == null) {
            return null;
        } else {
            Arrays.sort(files);
            return Arrays.asList(files);
        }
    }

    /* Returns a list of the names of all plain files in the directory DIR, in
       lexicographic order as Java Strings. Returns null if DIR does not denote
       a directory. */
    static List<String> plainFilenamesIn(String dir) {
        return plainFilenamesIn(new File(dir));
    }

    /* Serialises and saves Object to disk */
    static boolean saveObject(Object obj, String path, String fname) {
        File outFile = new File(path + "/" + fname);
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outFile));
            out.writeObject(obj);
            out.close();
        } catch (IOException e) {
            // Remove this before submission
            System.out.println("An unknown i/o error occurred in Utils.saveObject.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* Reads serialised Object from disk */
    static Object readObject(String path, String fname) {
        Object obj;
        File inFile = new File(path + "/" + fname);
        try {
            ObjectInputStream inp = new ObjectInputStream(new FileInputStream(inFile));
            obj = inp.readObject();
            inp.close();
        } catch (IOException | ClassNotFoundException e) {
            // Remove this before submission
            System.out.println("An unknown io or class error occurred in Utils.readObject.");
            e.printStackTrace();
            return null;
        }
        return obj;
    }

    /* Write to HEAD file */
    static boolean writeHead(String newHead) {
        try {
            Writer fWriter = new FileWriter(".gitlet/HEAD", false);
            fWriter.write(newHead);
            fWriter.close();
        } catch (IOException e) {
            // Remove this before submission
            System.out.println("An unknown i/o error occurred in Utils.writeHead.");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* Read from HEAD file */
    static String readHead() {
        String retStr = "";
        try {
            for (String line : Files.readAllLines(Paths.get(".gitlet/HEAD"))) {
                retStr += line;
            }
        } catch (IOException e) {
            // Remove this before submission
            System.out.println("An unknown i/o error occurred in Utils.readHead.");
            e.printStackTrace();
            return null;
        }
        return retStr.trim();
    }

    /* Copy/Move file at location <from> to location <to> */
    static boolean moveFile(String from, String to, String newFilename, boolean copy) {
        File f = new File(from);
        if (f.isFile()) {
            try {
                if (copy) {
                    Path pTo = Paths.get(to + "/" + newFilename);
                    Path pFrom = Paths.get(from);
                    Files.copy(pFrom, pTo, StandardCopyOption.REPLACE_EXISTING);
                    return true;
                } else {
                    // check if file already exists in <to>
                    File checkFile = new File(to + "/" + newFilename);
                    if (checkFile.isFile()) {
                        // no need to rename the file, just delete the old one
                        boolean b = f.delete();
                        if (b) {
                            return true;
                        } else {
                            // Remove this before submission
                            System.out.println("failed to delete the existing file.");
                            return false;
                        }
                    }
                    return f.renameTo(new File(to + "/" + newFilename));
                }
            } catch (IOException e) {
                // Remove this before submission
                System.out.println("An unknown i/o error occurred in Utils.moveFile.");
                e.printStackTrace();
                return false;
            }
        }
        // Remove this before submission
        System.out.println("File not found so could not be moved. " + from);
        return false;
    }

    /* Read file contents and returns a hash */
    static String getFileHash(String file) {
        try {
            File f = new File(file);
            if (f.isFile()) {
                var lines = Files.readAllLines(f.toPath());
                lines.add(file);
                return sha1(lines.toArray());
            }
        } catch (IOException e) {
            // Remove this before submission
            System.out.println("An unknown i/o error occurred in Utils.getFileHash.");
            e.printStackTrace();
            return null;

        }
        return null;
    }

    /* Read and return BRANCHES */
    static Branches readBranches() {
        return (Branches) readObject(".gitlet", "BRANCHES");
    }

    /* Read and return HEAD Commit */
    static Commit readHeadCommit() {
        return (Commit) readObject(".gitlet/commits", readHead());
    }

    static Commit readCommit(String commitHash) {
        return (Commit) readObject(".gitlet/commits", commitHash);
    }

    /* Checks if a file is being tracked in the commit */
    static boolean isFileTracked(Commit commit, String filePath) {
        if (commit != null) {
//            HashMap<String, String> ctns = commit.getContents();
//            return ctns.containsKey(filePath) && ctns.get(filePath).equals(getFileHash(filePath));
            return commit.getContents().containsKey(filePath);
        }
        return false;
    }

    static String readFile(String filePath) {
        String retStr = "";
        try {
//            for (String line : Files.readAllLines(Paths.get(filePath))) {
//                System.out.println(line);
//                retStr += line;
//            }
            String data = new String(Files.readAllBytes(Paths.get(filePath)));
            retStr += data;
        } catch (IOException e) {
            // Remove this before submission
            System.out.println("An unknown i/o error occurred in Utils.readHead.");
            e.printStackTrace();
            return "";
        }
        return retStr;
    }

    static boolean writeFile(String filePath, String text) {
        try {
            Writer fWriter = new FileWriter(filePath, false);
            fWriter.write(text);
            fWriter.close();
        } catch (IOException e) {
            // Remove this before submission
            System.out.println("An unknown i/o error occurred in Utils.writeHead.");
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
