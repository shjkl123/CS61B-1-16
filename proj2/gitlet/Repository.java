package gitlet;

import javax.swing.text.FieldView;
import javax.swing.text.html.StyleSheet;
import java.awt.*;
import java.io.File;

import java.util.*;
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
    public static final File GITLET_HEAD = join(GITLET_DIR, "Head");
    public static final File GITLET_HEADS = join(GITLET_REFS, "heads");
    public static final File GITLET_MASTER = join(GITLET_HEADS, "master");
    public static final File GITLET_ADDSTAGE = join(GITLET_DIR, "addStage");
    public static final File GITLET_REMOVESTAGE = join(GITLET_DIR, "removeStage");

    /* TODO: fill in the rest of this class. */
    //all dir
    public static void setupPersistence() {
        if (!GITLET_DIR.exists()) GITLET_DIR.mkdir();
        if (!GITLET_OBJECTS.exists()) GITLET_OBJECTS.mkdir();
        if (!GITLET_COMMITS.exists()) GITLET_COMMITS.mkdir();
        if (!GITLET_BLOBS.exists()) GITLET_BLOBS.mkdir();
        if (!GITLET_REFS.exists()) GITLET_REFS.mkdir();
        if (!GITLET_HEADS.exists()) GITLET_HEADS.mkdir();
        if (!GITLET_HEAD.exists()) GITLET_HEAD.mkdir();
        if (!GITLET_ADDSTAGE.exists()) GITLET_ADDSTAGE.mkdir();
        if (!GITLET_REMOVESTAGE.exists()) GITLET_REMOVESTAGE.mkdir();
    }

    //head commit also current working commit
    public static Commit getHeadCommit() {
        /*String commitPath = readContentsAsString(join(GITLET_HEAD, "head"));
        File pos = join(GITLET_COMMITS, commitPath);
        return readObject(pos, Commit.class);*/
        String branchName = readContentsAsString(join(GITLET_HEAD, "head"));
        return getBranchCommit(branchName);
    }

    public static void getAllFileInDir(File dir, List<File> s) {
        assert dir.isDirectory();
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) getAllFileInDir(f, s);
            else s.add(f);
        }
    }

    public static void deleteAllFileInDir(File dir) {
        assert dir.isDirectory();
        File[] files = dir.listFiles();
        for (File f : files) {
            if (f.isDirectory()) deleteAllFileInDir(f);
            else f.delete();
        }
    }

    //help next two function
    private static boolean isFileInStage(String fileNameSha1, File pos) {
        assert pos.isDirectory();
        File[] files = pos.listFiles();
        assert files != null;
        for (File f : files) {
            if (f.getName().equals(fileNameSha1)) return true;
        }
        return false;
    }

    public static boolean isFileInAddStage(String fileNameSha1) {
        return isFileInStage(fileNameSha1, GITLET_ADDSTAGE);
    }

    public static boolean isFileInRemoveStage(String fileNameSha1) {
        return isFileInStage(fileNameSha1, GITLET_REMOVESTAGE);
    }

    //return true if addStage is empty
    public static boolean isEmptyInAddStage() {
        return GITLET_ADDSTAGE.listFiles().length == 0;
    }

    //return true if removeStage is empty
    public static boolean isEmptyInRemoveStage() {
        return GITLET_REMOVESTAGE.listFiles().length == 0;
    }

    //remove all stageFile and add them to commitMap
    public static void saveAllAddStageFile(Map<String, String> commitPathToBlobId) {
        File[] files = GITLET_ADDSTAGE.listFiles();
        for (File f : files) {
            Blob b = readObject(f, Blob.class);
            commitPathToBlobId.put(f.getName(), b.getId());
            b.saveBlob();
            f.delete();
        }
    }

    public static void deleteAllRemoveStageFile() {
        deleteAllFileInDir(GITLET_REMOVESTAGE);
    }

    public static String getCurrentBranchName() {
        return readContentsAsString(join(GITLET_HEAD, "head"));
    }

    //change head commit
    public static void setHead(String masterName) {
       File file = join(GITLET_HEAD, "head");
       writeContents(file, masterName);
    }

    //change branch commit
    public static void setBranch(Commit cmt, String branchName) {
        File file = join(GITLET_HEADS, branchName);
        writeContents(file, cmt.toString());
    }

    //change master commit
    public static void setMaster(Commit cmt) {
        setBranch(cmt, "master");
    }

    public static void addFileToAddStage(File file) {
        Blob b = new Blob(file);
        File pos = join(GITLET_ADDSTAGE, sha1(file.getName()));
        writeObject(pos, b);
    }

    public static Blob getBlob(String blobId) {
        File dir = join(GITLET_BLOBS, blobId.substring(0, 2));
        return readObject(join(dir, blobId.substring(2)), Blob.class);
    }

    public static void addCommitFileToRemoveStage(String fileName, Map<String, String> pathToBlobId) {
        String blobId = pathToBlobId.get(sha1(fileName));
        Blob b = getBlob(blobId);
        File file = join(GITLET_REMOVESTAGE, sha1(fileName));
        writeObject(file, b);
    }

    public static void init() {
        if (GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system " +
                    "already exists in the current directory.");
            System.exit(0);
        }
        Repository.setupPersistence();
        Commit initialCommit = new Commit();
        initialCommit.saveCommit();
        setMaster(initialCommit);
        setHead("master");
    }

    private static boolean isFileInCWD(String fileName) {
        return join(CWD, fileName).exists();
    }

    public static void add(String fileName) {
        if (!isFileInCWD(fileName) && !isFileInRemoveStage(sha1(fileName))) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        if (isFileInRemoveStage(sha1(fileName))) {
            join(GITLET_REMOVESTAGE, sha1(fileName)).delete();
        }
        if (isFileInCWD(fileName)) {
            File file = join(CWD, fileName);
            Commit currentCommit = getHeadCommit();
            if (currentCommit.isStoredFile(file)) {
                if (isFileInAddStage(sha1(fileName)))
                    join(GITLET_ADDSTAGE, sha1(fileName)).delete();
            } else {
                addFileToAddStage(file);
            }
        }
    }

    public static void commit(String message) {
        if (Repository.isEmptyInAddStage() && Repository.isEmptyInRemoveStage()) {
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }
        if (message.isEmpty()) {
            System.out.println("Please enter a commit message");
            System.exit(0);
        }
        Commit cmt = new Commit(message);
        cmt.saveCommit();
        deleteAllRemoveStageFile();
        saveAllAddStageFile(cmt.getPathToBlobID());
        setBranch(cmt, getCurrentBranchName());
    }

    public static void rm(String fileName) {
        Commit currentCommit = getHeadCommit();
        File file = join(CWD, fileName);
        if (isFileInAddStage(sha1(fileName))) {
            join(GITLET_ADDSTAGE, sha1(fileName)).delete();
        } else if (currentCommit.isStoredFile(fileName)) {
            addCommitFileToRemoveStage(fileName, currentCommit.getPathToBlobID());
            if (file.exists()) file.delete();
        } else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }

    private static void helpLog(Commit cmt, boolean isMerged) {
        System.out.println("===");
        System.out.println("commit " + cmt.toString());
        if (isMerged) {
            System.out.print("Merge: ");
            System.out.print(cmt.getParent(0).toString().substring(0, 7));
            System.out.print(" ");
            System.out.println(cmt.getParent(1).toString().substring(0, 7));
        }
        System.out.println("Date: " + cmt.getTimeStamp());
        System.out.println(cmt.getMessage());
        //wait is need to create a new line for the initial commit
        System.out.println();
    }

    public static void log() {
        Commit cmt = getHeadCommit();
        while (true) {
            List<Commit> parents = cmt.getParents();
            boolean flag = parents.size() == 2;
            helpLog(cmt, flag);
            if (parents.isEmpty()) break;
            cmt = parents.get(0);
        }
    }

    public static void globalLog() {
        File[] files = GITLET_COMMITS.listFiles();
        for (File f : files) {
            Commit cmt = readObject(f, Commit.class);
            helpLog(cmt, false);
        }
    }

    public static void find(String message) {
        File[] files = GITLET_COMMITS.listFiles();
        boolean flag = true;
        for (File f : files) {
            Commit cmt = readObject(f, Commit.class);
            if (cmt.getMessage().equals(message)) {
                System.out.println(cmt.toString());
                flag = false;
            }
        }
        if (flag) {
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }
    }

    private static boolean isCurrentBranch(String branchName) {
        /*Commit currentCommit = getHeadCommit();
        String commitId = currentCommit.toString();
        File file = join(GITLET_HEADS, branchName);
        String fileContent = readContentsAsString(file);
        return fileContent.equals(commitId);*/
        return getCurrentBranchName().equals(branchName);
    }

    private static void helpStatusBranches() {
        File[] files = GITLET_HEADS.listFiles();
        List<String> branchNames = new ArrayList<>();
        //Commit currentCommit = getHeadCommit();
        //String commitId = currentCommit.toString();
        for (File f : files) {
            //String fileContent = readContentsAsString(f);
            if (isCurrentBranch(f.getName()))
                System.out.println("*" + f.getName());
            else
                branchNames.add(f.getName());
        }
        Collections.sort(branchNames);
        for (String branchName : branchNames)
            System.out.println(branchName);
    }

    private static void helpStatusStageFiles(File pos) {
        File[] files = pos.listFiles();
        List<String> fileNames = new ArrayList<>();
        for (File f : files) {
            Blob b = readObject(f, Blob.class);
            fileNames.add(b.getFileName());
        }
        Collections.sort(fileNames);
        for (String fileName : fileNames)
            System.out.println(fileName);
    }

    //wait to test when the branch done
    public static void status() {
        System.out.println("=== Branches ===");
        helpStatusBranches();
        System.out.println();
        System.out.println("=== Staged Files ===");
        helpStatusStageFiles(GITLET_ADDSTAGE);
        System.out.println();
        System.out.println("=== Removed Files ===");
        helpStatusStageFiles(GITLET_REMOVESTAGE);
        System.out.println();
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();
        System.out.println("=== Untracked Files ===");
    }

    //help the next function
    private static void helpCheckOutFile(String fileName, Commit cmt) {
        Blob fileBlob = cmt.getBlob(fileName);
        if (fileBlob == null) {
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        File pos = join(CWD, fileName);
        writeContents(pos, (Object) fileBlob.getFileByte());
    }

    public static void checkOutUseFileName(String fileName) {
        Commit cmt = getHeadCommit();
        helpCheckOutFile(fileName, cmt);
    }

    private static Commit getCommitUseShortCommitId(String commitId) {
        File[] files = GITLET_COMMITS.listFiles();
        for (File f : files) {
            if (f.getName().startsWith(commitId))
                return readObject(f, Commit.class);
        }
        return null;
    }

    //wait
    public static void checkOutUseCommitIdAndFileName(String commitId, String fileName) {
        //File commitFile = join(GITLET_COMMITS, commitId);
        Commit cmt = getCommitUseShortCommitId(commitId);
        if (cmt == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        //Commit cmt = readObject(commitFile, Commit.class);
        helpCheckOutFile(fileName, cmt);
    }

    //return true if the branch is existed
    private static boolean isExistBranchName(String branchName) {
        return join(GITLET_HEADS, branchName).exists();
    }

    private static Commit getBranchCommit(String branchName) {
        String commitId = readContentsAsString(join(GITLET_HEADS, branchName));
        return readObject(join(GITLET_COMMITS, commitId), Commit.class);
    }

    private static void helpCheckOutAndReset(Commit branchCommit) {
        Commit headCommit = getHeadCommit();

        List<Blob> branchCommitBlob = branchCommit.getAllBlob();
        for (Blob b : branchCommitBlob) {
            if (!headCommit.isStoredFile(b.getFileName())
                    && isFileInCWD(b.getFileName())) {
                System.out.println("There is an untracked file in the way; " +
                        "delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        List<File> fileList = new ArrayList<>();
        getAllFileInDir(CWD, fileList);

        for (Blob b : branchCommitBlob) {
            String fileName = b.getFileName();
            File f = join(CWD, fileName);
            writeContents(f, (Object) b.getFileByte());
        }

        for (File f : fileList) {
            if (headCommit.isStoredFile(f.getName())
                    && !branchCommit.isStoredFile(f.getName()))
                f.delete();
        }

        deleteAllFileInDir(GITLET_ADDSTAGE);
        deleteAllFileInDir(GITLET_REMOVESTAGE);
    }

    public static void checkOutUseBranchName(String branchName) {
        if (!isExistBranchName(branchName)) {
            System.out.println("No such branch exists.");
            System.exit(0);
        } else if (isCurrentBranch(branchName)) {
            System.out.println("No need to checkout the current branch.");
            System.exit(0);
        }

        helpCheckOutAndReset(getBranchCommit(branchName));
        setHead(branchName);
    }

    public static void branch(String branchName) {
        if (isExistBranchName(branchName)) {
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        Commit cmt = getHeadCommit();
        File pos = join(GITLET_HEADS, branchName);
        writeContents(pos, cmt.toString());
    }

    public static void rmBranch(String branchName) {
        if (!isExistBranchName(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        } else if (isCurrentBranch(branchName)) {
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        File pos = join(GITLET_HEADS, branchName);
        pos.delete();
    }

    public static void reset(String commitId) {
        Commit cmt = getCommitUseShortCommitId(commitId);
        if (cmt == null) {
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        helpCheckOutAndReset(cmt);
        setBranch(cmt, getCurrentBranchName());
    }

    private static Commit getCommit(String commitId) {
        File pos = join(GITLET_COMMITS, commitId);
        return readObject(pos, Commit.class);
    }

    private static Set<String> getParentsCommitId(String commitId) {
        Commit cmt = getCommit(commitId);
        return cmt.getAllParentId();
    }

    //use bfs track the commit and its depth
    private static Map<String, Integer> BFS(Commit cmt) {
        //this is question
        Map<String, Integer> map = new HashMap<>();
        Queue<String> queue = new LinkedList<>();
        queue.add(cmt.toString());
        map.put(cmt.toString(), 1);
        while (!queue.isEmpty()) {
            String p = queue.remove();
            Set<String> parents = getParentsCommitId(p);
            for (String q : parents) {
                if (!map.containsKey(q)) {
                    map.put(q, map.get(p) + 1);
                    queue.add(q);
                }
            }
        }
        return map;
    }

    private static Commit getSplitCommit(String branchName) {
        //it is the question
        Commit currentCommit = getHeadCommit();
        Commit branchCommit = getBranchCommit(branchName);
        Map<String, Integer> currentMap = BFS(currentCommit);
        Map<String, Integer> branchMap = BFS(branchCommit);
        String minCommitId = null;
        int minDepth = Integer.MAX_VALUE;
        for (String cmtId: currentMap.keySet()) {
            if (branchMap.containsKey(cmtId)) {
                int deep = branchMap.get(cmtId);
                if (deep < minDepth) {
                    minDepth = deep;
                    minCommitId = cmtId;
                }
            }
        }
        return getCommit(minCommitId);
    }

    private static Set<String> getAllFileName(Commit headCommit, Commit branchCommit
            , Commit splitCommit) {
        Set<String> allFileName = new HashSet<>();
        allFileName.addAll(headCommit.getAllFileName());
        allFileName.addAll(branchCommit.getAllFileName());
        allFileName.addAll(splitCommit.getAllFileName());
        return allFileName;
    }

    //return true if the file is change in current commit compare prev commit
    //don't generate null pointer
    private static boolean isFileChanged(Commit prevCommit, Commit currentCommit
            , String fileName) {
        boolean flag1 = prevCommit.isStoredFile(fileName);
        boolean flag2 = currentCommit.isStoredFile(fileName);
        if (flag1 != flag2) return true;
        if (flag1) {
            Blob prevBlob = prevCommit.getBlob(fileName);
            Blob currentBlob = currentCommit.getBlob(fileName);
            return !prevBlob.toString().equals(currentBlob.toString());
        }
        return false;
    }

    private static void changeCWDFile(Blob b) {
        File file = join(CWD, b.getFileName());
        writeContents(file, (Object) b.getFileByte());
    }

    private static void addFileToAddStage(Blob b) {
        File pos = join(GITLET_ADDSTAGE, sha1(b.getFileName()));
        writeObject(pos, b);
    }

    private static void deleteCWDFile(String fileName) {
        File pos = join(CWD, fileName);
        pos.delete();
    }

    private static void addFileToRemoveStage(Blob b) {
        File pos = join(GITLET_REMOVESTAGE, b.getFileName());
        writeObject(pos, b);
    }

    public static void merge(String branchName) {
        if (!isEmptyInAddStage() || !isEmptyInRemoveStage()) {
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if (!isExistBranchName(branchName)) {
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if (isCurrentBranch(branchName)) {
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        Commit currentCommit = getHeadCommit();
        Commit branchCommit = getBranchCommit(branchName);
        Commit splitCommit = getSplitCommit(branchName);
        if (currentCommit.equals(splitCommit)) {
            System.out.println("Current branch fast-forwarded.");
            checkOutUseBranchName(branchName);
            System.exit(0);
        }
        if (branchCommit.equals(splitCommit)) {
            System.out.println("Given branch is an " +
                    "ancestor of the current branch.");
            System.exit(0);
        }
        Set<String> fileNames = getAllFileName(currentCommit, branchCommit, splitCommit);
        for (String fileName : fileNames) {
            //check
            if (!currentCommit.isStoredFile(fileName)
                    && isFileInCWD(fileName)) {
                System.out.println("There is an untracked file in the way; " +
                        "delete it, or add and commit it first.");
                System.exit(0);
            }
        }

        for (String fileName : fileNames) {
            //big if and else if and else
            if (!isFileChanged(splitCommit, currentCommit, fileName) &&
                    isFileChanged(splitCommit, branchCommit, fileName)) {
                if (splitCommit.isStoredFile(fileName)) {
                    Blob b = splitCommit.getBlob(fileName);
                    if (branchCommit.isStoredFile(fileName)) {
                        //case 1
                        addFileToAddStage(b);
                        changeCWDFile(branchCommit.getBlob(fileName));
                    } else {
                        //case 6
                        addFileToRemoveStage(b);
                        deleteCWDFile(fileName);
                    }
                } else if (branchCommit.isStoredFile(fileName)) {
                    //case 5
                    Blob b = branchCommit.getBlob(fileName);
                    addFileToAddStage(b);
                    changeCWDFile(b);
                }
            } else if (isFileChanged(splitCommit, currentCommit, fileName)
                    && !isFileChanged(splitCommit, branchCommit, fileName)) {
                if (currentCommit.isStoredFile(fileName)) {
                    //case 2 and case 4
                    Blob b = currentCommit.getBlob(fileName);
                    changeCWDFile(b);
                } else {
                    //case 7
                    deleteCWDFile(fileName);
                }
            } else if (isFileChanged(splitCommit, currentCommit, fileName)
                    && isFileChanged(splitCommit, branchCommit, fileName)) {
                if (!isFileChanged(currentCommit, branchCommit, fileName)) {
                    //case 3a
                    if (currentCommit.isStoredFile(fileName)) {
                        Blob b = currentCommit.getBlob(fileName);
                        changeCWDFile(b);
                    } else {
                        deleteCWDFile(fileName);
                    }
                } else {
                    //case 3b
                    //got a conflict
                    String currentContent = "";
                    String branchContent = "";
                    if (currentCommit.isStoredFile(fileName))
                        currentContent = currentCommit.getBlob(fileName).getFileContent();
                    /*else
                        currentContent = "\n";*/
                    if (branchCommit.isStoredFile(fileName))
                        branchContent = branchCommit.getBlob(fileName).getFileContent();
                    /*else
                        branchContent = "\n";*/
                    File pos = join(CWD, fileName);
                    String s = "<<<<<<< HEAD\n";
                    s += currentContent;
                    s += "=======\n";
                    s += branchContent;
                    s += ">>>>>>>\n";
                    writeContents(pos, s);
                    System.out.println("Encountered a merge conflict.");
                }
            }
        }
        //create a commit
        List<Commit> parents = new ArrayList<>();
        parents.add(currentCommit);
        parents.add(branchCommit);
        Commit cmt = new Commit("Merged " + branchName + " into " +
                getCurrentBranchName() + ".", parents);
        cmt.saveMergeCommit(currentCommit);
        setBranch(cmt, getCurrentBranchName());
    }
}