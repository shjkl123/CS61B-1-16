package gitlet;

import java.util.Objects;

public class Operation {
    public static void init() {
        Repository.init();
    }

    public static void add(String[] args) {
        String fileName = args[1];
        Repository.add(fileName);
    }

    private static String getMessage(String[] args) {
        StringBuilder message = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            message.append(args[i]);
        }
        return message.toString();
    }

    public static void commit(String[] args) {
        Repository.commit(getMessage(args));
    }

    public static void rm(String[] args) {
        String fileName = args[1];
        Repository.rm(fileName);
    }

    public static void log() {
        Repository.log();
    }

    public static void globalLog() {
        Repository.globalLog();
    }

    public static void find(String[] args) {
        Repository.find(getMessage(args));
    }

    public static void status() {
        Repository.status();
    }

    public static void checkOut(String[] args) {
        if (args.length == 3) {
            if (!args[1].equals("--")) {
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            Repository.checkOutUseFileName(args[2]);
        } else if (args.length == 4) {
            if (!args[2].equals("--")) {
                System.out.println("Incorrect operands");
                System.exit(0);
            }
            Repository.checkOutUseCommitIdAndFileName(args[1], args[3]);
        } else if (args.length == 2) {
            Repository.checkOutUseBranchName(args[1]);
        } else {
            System.out.println("Incorrect operands");
            System.exit(0);
        }
    }
}
