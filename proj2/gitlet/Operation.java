package gitlet;

import java.io.File;

import static gitlet.Utils.*;

public class Operation {

    public static void init() {
        if (Repository.GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already " +
                    "exists in the current directory.");
            System.exit(0);
        }
        Repository.createGitletFile();
        Repository.setupPersistence();
        Commit initialCommit = new Commit("initial commit", null, null);
        initialCommit.saveCommit();
        Repository.setMaster(initialCommit);
        Repository.setHead(initialCommit);
    }

    public static void add(String fileName) {
        File file = join(Repository.CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        if (Repository.isStoredFile(file)) return;
        Repository.addFileToAddStage(file);
    }

    public static void commit(String[] args) {
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]);
        }

        Repository.commit(String.valueOf(message));
    }

    public static void rm(String fileName) {
        File file = join(fileName);
        if (Repository.isFileInAddStage(file)) {
            Repository.deleteFileInAddStage(file);
        } else if (Repository.isFileInCurrentCommit(file)) {
            Repository.addFileToRemoveStage(file);
            if (Repository.isFileInCWD(file)) file.delete();
            else Repository.deleteFileInCurrentCommit(file);
        } else {
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }
    }
}
