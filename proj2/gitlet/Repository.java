package gitlet;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public static final File GITLET_DIR = join(CWD, ".gitlet");
    public static final File GITLET_OBJECTS = join(GITLET_DIR, "objects");
    public static final File GITLET_COMMITS = join(GITLET_OBJECTS, "commits");
    public static final File GITLET_BLOBS = join(GITLET_OBJECTS, "blobs");
    public static final File GITLET_REFS = join(GITLET_DIR, "refs");
    public static final File GITLET_MASTER = join(GITLET_REFS, "master");
    public static final File GITLET_HEADS = join(GITLET_REFS, "heads");
    public static final File GITLET_HEAD = join(GITLET_DIR, "Head");
    public static final File GITLET_ADDSTAGE = join(GITLET_DIR, "addStage");
    public static final File GITLET_REMOVESTAGE = join(GITLET_DIR, "removeStage");
    /* TODO: fill in the rest of this class. */

    public static void createGitletFile() {
        if (!GITLET_DIR.exists()) GITLET_DIR.mkdir();
    }

    public static void setupPersistence() {
        if (!GITLET_OBJECTS.exists()) GITLET_OBJECTS.mkdir();
        if (!GITLET_COMMITS.exists()) GITLET_COMMITS.mkdir();
        if (!GITLET_BLOBS.exists()) GITLET_BLOBS.mkdir();
        if (!GITLET_REFS.exists()) GITLET_REFS.mkdir();
        if (!GITLET_MASTER.exists()) GITLET_MASTER.mkdir();
        if (!GITLET_HEADS.exists()) GITLET_HEADS.mkdir();
        if (!GITLET_HEAD.exists()) GITLET_HEAD.mkdir();
        if (!GITLET_ADDSTAGE.exists()) GITLET_ADDSTAGE.mkdir();
        if (!GITLET_REMOVESTAGE.exists()) GITLET_REMOVESTAGE.mkdir();
    }

    public static void setHead(Commit cmt) {
        String sha1 = cmt.toString();
        File file = join(GITLET_HEAD, "Head.txt");
        writeContents(file, sha1);
    }

    public static void setMaster(Commit cmt) {
        String sha1 = cmt.toString();
        File file = join(GITLET_MASTER, "master.txt");
        writeContents(file, sha1);
    }

    private static Commit getHeadCommit() {
        File file = join(GITLET_HEAD, "Head.txt");
        File pos = join(GITLET_COMMITS, readContentsAsString(file));
        return readObject(pos, Commit.class);
    }

    private static Commit getMasterCommit() {
        File file = join(GITLET_MASTER, "master.txt");
        File pos = join(GITLET_COMMITS, readContentsAsString(file));
        return readObject(pos, Commit.class);
    }

    public static boolean isStoredFile(File file) {
        Blob b = new Blob(file);
        File pos = join(GITLET_BLOBS, b.toString().substring(0, 2));
        if (!pos.exists()) return false;
        File[] files = pos.listFiles();
        assert files != null;
        for (File f : files) {
            if (b.toString().substring(2).equals(f.getName()))
                return true;
        }
        return false;
    }

    private static void addFileToStage(File file, File stagePos) {
        Blob b = new Blob(file);
        String fileName = file.getName();
        String s = sha1(fileName);
        File dir = join(stagePos, s.substring(0, 2));
        if (!dir.exists()) dir.mkdir();
        File pos = join(dir, s.substring(2));
        writeObject(pos, b);
    }

    public static void addFileToAddStage(File file) {
        addFileToStage(file, GITLET_ADDSTAGE);
    }

    public static void addFileToRemoveStage(File file) {
        addFileToStage(file, GITLET_REMOVESTAGE);
    }

    public static void deleteFileInAddStage(File file) {
        String sha1 = sha1(file.getName());
        File dir = join(GITLET_ADDSTAGE, sha1.substring(0, 2));
        File f = join(dir, sha1.substring(2));
        f.delete();
    }

    private static void getAllFiles(File file, List<File> allFiles) {
        assert file.isDirectory();
        File[] files = file.listFiles();
        assert files != null;
        for (File f : files) {
            if (f.isDirectory()) getAllFiles(f, allFiles);
            else allFiles.add(f);
        }
    }
    //it will remove all files in a dir
    private static void deleteDirFiles(File dir) {
        assert dir.isDirectory();
        File[] files = dir.listFiles();
        assert files != null;
        for (File f : files) {
            f.delete();
        }
    }

    private static Blob[] helpGetAllFileAndRemoveInStage(File blobPos) {
        File files = join(blobPos);
        File[] fileList = files.listFiles();
        assert fileList != null;
        List<File> blobFiles = new ArrayList<>();
        getAllFiles(blobPos, blobFiles);
        Blob[] blobs = new Blob[blobFiles.size()];
        int i = 0;
        for (File f : blobFiles) {
            Blob b = readObject(f, Blob.class);
            b.saveBlob();
            blobs[i++] = b;
            f.delete();
        }
        deleteDirFiles(blobPos);
        return blobs;
    }

    public static void commit(String message) {
        Blob[] b1 = helpGetAllFileAndRemoveInStage(GITLET_ADDSTAGE);
        Blob[] b2 = helpGetAllFileAndRemoveInStage(GITLET_REMOVESTAGE);
        //change list to arrays
        Blob[] blobs = new Blob[b1.length + b2.length];
        if (blobs.length == 0) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        System.arraycopy(b1, 0, blobs, 0, b1.length);
        System.arraycopy(b2,0, blobs, b1.length, b2.length);
        Commit cmt = new Commit(message, getMasterCommit(), blobs);
        cmt.saveCommit();
        setMaster(cmt);
        //setHead(cmt);
    }


    public static boolean isFileInAddStage(File file) {
        String s = sha1(file.getName());
        File dir = join(GITLET_ADDSTAGE, s.substring(0, 2));
        assert dir.isDirectory();
        if (!dir.exists()) return false;
        File[] files = dir.listFiles();
        assert files != null;
        for (File f : files) {
            if (f.getName().equals(s.substring(2))) return true;
        }
        return false;
    }

    public static boolean isFileInCWD(File file) {
        File f = join(Repository.CWD, file.getName());
        return f.exists();
    }

    public static boolean isFileInCurrentCommit(File file) {
        Commit cmt = getMasterCommit();
        return cmt.isFileInCommit(file);
    }

    public static void deleteFileInCurrentCommit(File file) {
        Commit cmt = getHeadCommit();
        cmt.removeFile(file);
    }
}

