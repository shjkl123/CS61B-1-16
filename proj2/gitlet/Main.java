package gitlet;

import java.util.Arrays;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */

    public static void incorrectOperandsHint(String[] args, int minLength, int maxLength) {
        if (args.length > maxLength || args.length < minLength) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        // TODO: what if args is empty?
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }

        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                // TODO: handle the `init` command
                incorrectOperandsHint(args, 1,1);
                Operation.init();
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                incorrectOperandsHint(args, 2, 2);
                Operation.add(args[1]);
                break;
            // TODO: FILL THE REST IN
            case "commit" :
                if (args.length == 1) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                Operation.commit(args);
                break;
            case "rm" :
                incorrectOperandsHint(args, 2, 2);
                Operation.rm(args[1]);
                break;
            case "log" :
                incorrectOperandsHint(args, 1, 1);
                Operation.log();
                break;
            case "global-log" :
                incorrectOperandsHint(args, 1, 1);
                Operation.globalLog();
                break;
            case "find" :
                incorrectOperandsHint(args, 2, Integer.MAX_VALUE);
                Operation.find(args);
                break;
            case "status" :
                incorrectOperandsHint(args, 1, 1);
                Operation.status();
                break;
            case "checkout" :
                incorrectOperandsHint(args, 2, 4);
                Operation.checkout(args);
                break;
            default:
                System.out.println("No command with that name exists.");
                System.exit(0);
        }
    }
}
