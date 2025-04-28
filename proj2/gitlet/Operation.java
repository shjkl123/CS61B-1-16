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
        Repository.addHeads("master", initialCommit);
    }

    public static void add(String fileName) {
        File file = join(Repository.CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Repository.addFileToAddStage(file);
    }

    private static String getCommitMessageFromArgs(String[] args) {
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]);
        }
        return String.valueOf(message);
    }

    public static void commit(String[] args) {
        Repository.commit(getCommitMessageFromArgs(args));
    }

    public static void rm(String fileName) {
        Repository.rm(fileName);
    }

    public static void log() {
        Repository.log();
    }

    public static void globalLog() {
        Repository.globalLog();
    }

    public static void find(String[] args) {
        Repository.find(getCommitMessageFromArgs(args));
    }

    public static void status() {
        Repository.status();
    }

    public static void checkout(String[] args) {
        if (args.length == 4) {
            String commitId = args[1];
            String fileName = args[3];
            Repository.checkoutCommitFile(commitId, fileName);
        } else if (args.length == 3) {
            String fileName = args[2];
            Repository.checkoutFileName(fileName);
        } else if (args.length == 2) {
            String branchName = args[1];
            Repository.checkoutBranch(branchName);
        }
    }
}
