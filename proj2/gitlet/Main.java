package gitlet;

/** Driver class for Gitlet, a subset of the Git version-control system.
 *  @author TODO
 */
public class Main {

    public static void HintIncorrectOperands(String[] args, int min, int max) {
        if (args.length > max || args.length < min) {
            System.out.println("Incorrect operands.");
            System.exit(0);
        }
    }

    /** Usage: java gitlet.Main ARGS, where ARGS contains
     *  <COMMAND> <OPERAND1> <OPERAND2> ... 
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Please enter a command.");
            System.exit(0);
        }
        // TODO: what if args is empty?
        String firstArg = args[0];
        switch(firstArg) {
            case "init":
                HintIncorrectOperands(args, 1, 1);
                Operation.init();
                // TODO: handle the `init` command
                break;
            case "add":
                // TODO: handle the `add [filename]` command
                HintIncorrectOperands(args, 2, 2);
                Operation.add(args);
                break;
            case "commit":
                if (args.length < 2) {
                    System.out.println("Please enter a commit message.");
                    System.exit(0);
                }
                Operation.commit(args);
                break;
            case "rm":
                HintIncorrectOperands(args, 2, 2);
                Operation.rm(args);
                break;
            case "log":
                HintIncorrectOperands(args, 1, 1);
                Operation.log();
                break;
            case "global-log":
                HintIncorrectOperands(args, 1, 1);
                Operation.globalLog();
                break;
            case "find":
                if (args.length < 2) {
                    System.out.println("Please enter a commit message");
                    System.exit(0);
                }
                Operation.find(args);
                break;
            case "status":
                HintIncorrectOperands(args, 1, 1);
                Operation.status();
                break;
            case "checkout":
                Operation.checkOut(args);
                break;
            case "branch":
                HintIncorrectOperands(args, 2, 2);
                Operation.branch(args);
                break;
            case "rm-branch":
                HintIncorrectOperands(args, 2, 2);
                Operation.rmBranch(args);
                break;
            default:
                System.out.println("No command with that name exists.");
                break;
            // TODO: FILL THE REST IN
        }
    }
}
