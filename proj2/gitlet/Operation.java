package gitlet;

import java.io.File;
import java.io.IOException;
import static gitlet.Utils.*;

public class Operation {

    public static void init()  {
        if (Repository.GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system " +
                    "already exists in the current directory.");
            System.exit(0);
        }
        Repository.createGitletFile();
        Repository.setupPersistence();
        Commit innitialCommit = new Commit("initial commit", null);
        innitialCommit.saveCommit();
        Repository.setHeadPointer(sha1(innitialCommit));
        Repository.setMasterPointer(sha1(innitialCommit));
    }

    public static void add(String fileName) {
        File file = join(Repository.CWD, fileName);
        if (!file.exists()) {
            System.out.println("File does not exist.");
            System.exit(0);
        }
        Repository.addAdditionStage(file);
    }

    public static void commit(String message) {
        String master = Repository.getMaster();
        Commit parent = Commit.getCommit(master);
        Commit cmt = new Commit(message, parent);
        cmt.saveCommit();
        Repository.setMasterPointer(sha1(cmt));

    }
}
