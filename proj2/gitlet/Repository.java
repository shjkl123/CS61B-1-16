package gitlet;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

import static gitlet.Utils.*;

// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_COMMIT = join(CWD, "commit");
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File GITLET_LOG = join(GITLET_DIR, "log");
    public static final File GITLET_ADDITION_STAGE = join(GITLET_DIR, "additionStage");
    public static final File GITLET_Blobs = join(GITLET_DIR, "blobs");
    public static final File GITLET_REF = join(GITLET_DIR, "ref");
    /* TODO: fill in the rest of this class. */


    public static void setupPersistence() {
        if (!GITLET_LOG.exists()) GITLET_LOG.mkdir();
        if (!GITLET_ADDITION_STAGE.exists()) GITLET_ADDITION_STAGE.mkdir();
        if (!GITLET_Blobs.exists()) GITLET_Blobs.mkdir();
        if (!GITLET_REF.exists()) GITLET_REF.mkdir();
    }

    public static void createGitletFile() {
        if (!GITLET_DIR.exists()) GITLET_DIR.mkdir();
    }

    public static void setMasterPointer(String sha1) {
        File masterFile = join(GITLET_REF, "master.txt");
        writeContents(masterFile, sha1);
    }

    public static void setHeadPointer(String sha1) {
        File headFile = join(GITLET_REF, "head.txt");
        writeContents(headFile, sha1);
    }

    public static String getMaster() {
        File masterFile = join(GITLET_REF, "master.txt");
        return readContentsAsString(masterFile);
    }

    public static String getHead() {
        File headFile = join(GITLET_REF, "head.txt");
        return readContentsAsString(headFile);
    }


    public static void addAdditionStage(File file) {
        Blob b = new Blob(file);
        b.saveBlob(GITLET_ADDITION_STAGE);
    }

    public static void removeAdditionState() {
        Repository.GITLET_ADDITION_STAGE.delete();
        if (!Repository.GITLET_ADDITION_STAGE.exists())
            Repository.GITLET_ADDITION_STAGE.mkdir();
    }

    public static void createLogFile() {
        if (!GITLET_LOG.exists()) {
            GITLET_LOG.mkdir();
        }
    }

    public static void addLog(Commit commit) {
        String s = "===\n";
        String sha1 = sha1(commit);
        String data = commit.getTimestamp();
        String message = commit.getMessage();
        s += "commit " + sha1 + "\nData: " + data + "\n" + message;
        File file = join(GITLET_LOG, sha1 + ".txt");


    }
}
