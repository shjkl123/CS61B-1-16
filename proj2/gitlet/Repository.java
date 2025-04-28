package gitlet;

import java.io.File;
import java.util.*;

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

    /**
     * The current working directory.
     */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /**
     * The .gitlet directory.
     */
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

    public static void addHeads(String branchName, Commit cmt) {
        File file = join(GITLET_HEADS, branchName);
        writeContents(file, cmt.getId());
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

    private static void addFileToStage(File file, File stagePos) {
        Blob b = new Blob(file);
        String fileName = file.getName();
        File pos = join(stagePos, fileName);
        writeObject(pos, b);
    }

    public static void addFileToAddStage(File file) {
        addFileToStage(file, GITLET_ADDSTAGE);
    }

    public static void addFileToRemoveStage(File file) {
        addFileToStage(file, GITLET_REMOVESTAGE);
    }

    public static void deleteFileInAddStage(File file) {
        join(GITLET_ADDSTAGE, file.getName()).delete();
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
        System.arraycopy(b2, 0, blobs, b1.length, b2.length);
        Commit[] master = new Commit[1];
        master[0] = getMasterCommit();
        Commit cmt = new Commit(message, master, blobs);
        cmt.saveCommit();
        setMaster(cmt);
        setHead(cmt);
        addHeads("master", cmt);
    }


    public static boolean isFileInAddStage(File file) {
        File pos = join(GITLET_ADDSTAGE, file.getName());
        return pos.exists();
    }

    public static boolean isFileInCWD(File file) {
        return join(Repository.CWD, file.getName()).exists();
    }

    public static boolean isFileInCurrentCommit(File file) {
        Commit cmt = getMasterCommit();
        return cmt.isFileInCommit(file);
    }

    public static void deleteFileInCurrentCommit(File file) {
        Commit cmt = getHeadCommit();
        cmt.removeFile(file);
    }

    public static void rm(String fileName) {
        File file = join(fileName);
        if (isFileInAddStage(file)) {
            deleteFileInAddStage(file);
        } else if (isFileInCurrentCommit(file)) {
            addFileToRemoveStage(file);
            if (isFileInCWD(file)) file.delete();
            else deleteFileInCurrentCommit(file);
        } else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    private static void helpLog(Commit p, boolean isMergePrint, boolean isNewLine) {
        String id = p.getId();
        String timeStamp = p.getTimeStamp();
        String message = p.getMessage();
        System.out.println("===");
        System.out.println("commit " + id);
        if (isMergePrint) {
            System.out.println("Merge: " + p.getParent().get(0).getId().substring(0, 7) +
                    " " + p.getParent().get(1).getId().substring(0, 7));
        }
        System.out.println("Date: " + timeStamp);
        System.out.println(message);
        if (isNewLine) System.out.println();
    }

    public static void log() {
        Commit p = getMasterCommit();
        boolean isMergeCommit = false;
        while (true) {
            helpLog(p, isMergeCommit, !p.isInitCommit());
            List<Commit> parent = p.getParent();
            isMergeCommit = parent.size() >= 2;
            if (!p.isInitCommit()) p = p.getParent().get(0);
            else break;
        }
    }

    public static void globalLog() {
        File[] files = GITLET_COMMITS.listFiles();
        assert files != null;
        for (int i = 0; i < files.length; i++) {
            Commit cmt = readObject(files[i], Commit.class);
            helpLog(cmt, false, i != files.length - 1);
        }
    }

    public static void find(String message) {
        File[] files = GITLET_COMMITS.listFiles();
        assert files != null;
        boolean isExist = false;
        for (File f : files) {
            Commit cmt = readObject(f, Commit.class);
            String commitMessage = cmt.getMessage();
            if (commitMessage.equals(message)) {
                System.out.println(cmt.getId());
                isExist = true;
            }
        }
        if (!isExist) {
            System.out.println("Found no commit with that message.");
        }
    }

    private static void helpStatusStagePrint(File pos) {
        List<File> addStageFiles = new ArrayList<>();
        List<String> addStageFileNames = new ArrayList<>();
        getAllFiles(pos, addStageFiles);
        for (File f : addStageFiles) {
            addStageFileNames.add(readObject(f, Blob.class).getFileName());
        }
        Collections.sort(addStageFileNames);
        for (String s : addStageFileNames)
            System.out.println(s);
    }


    public static void status() {
        File[] branchFiles = GITLET_HEADS.listFiles();
        assert branchFiles != null;
        List<String> branchNames = new ArrayList<>();
        Commit headCommit = getHeadCommit();
        System.out.println("=== Branches ===");
        for (File f : branchFiles) {
            String fileCommitId = readContentsAsString(f);
            if (fileCommitId.equals(headCommit.getId())) {
                System.out.println("*" + f.getName());
            } else {
                branchNames.add(f.getName());
            }
        }
        Collections.sort(branchNames);
        for (String b : branchNames)
            System.out.println(b);
        System.out.println();
        System.out.println("=== Staged Files ===");
        helpStatusStagePrint(GITLET_ADDSTAGE);
        System.out.println();
        System.out.println("===Removed Files===");
        helpStatusStagePrint(GITLET_REMOVESTAGE);
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===\n");
        System.out.println("=== Untracked Files ===\n");
    }

    private static void helpCheckOut(Commit cmt, String fileName) {
        if (!cmt.isFileInCommit(fileName)) {
            System.out.println("File does not exist in that commit");
            System.exit(0);
        }
        String fileContent = cmt.getFileBlob(fileName).getFileContent();
        File f = join(Repository.CWD, fileName);
        writeContents(f, fileContent);
    }

    private static String getCurrentBranchName() {
        File[] files = GITLET_HEADS.listFiles();
        assert files != null;
        String currentCommitId = getHeadCommit().getId();
        for (File f : files) {
            if (readContentsAsString(f).equals(currentCommitId))
                return f.getName();
        }
        return null;
    }

    public static void checkoutFileName(String fileName) {
        helpCheckOut(getHeadCommit(), fileName);
    }

    public static void checkoutCommitFile(String commitId, String fileName) {
        File pos = join(GITLET_COMMITS, commitId);
        if (!pos.exists()) {
            System.out.println("No commit with that id exists");
            System.exit(0);
        }
        Commit cmt = readObject(pos, Commit.class);
        helpCheckOut(cmt, fileName);
    }

    public static Commit getCommit(String branchName) {
        File file = join(GITLET_HEADS, branchName);
        String pos = readContentsAsString(file);
        return readObject(join(GITLET_COMMITS, pos), Commit.class);
    }

    public static void checkoutBranch(String branchName) {
        String currentBranchName = getCurrentBranchName();
        if (branchName.equals(currentBranchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }
        Commit currentBranchCommit = getCommit(currentBranchName);
        Commit NowBranchCommit = getCommit(branchName);
        List<File> files = NowBranchCommit.getAllFiles();
        //wait
    }
}

