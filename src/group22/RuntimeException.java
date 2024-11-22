package group22;

/**
 * A basic class for a RuntimeException, thrown whenever a runtime error occurs.
 *
 * @author mbf1102
 */
public class RuntimeException extends Exception {
    public RuntimeException(String msg) {
        super(msg);
    }

    public RuntimeException(String msg, String filename, int lineNumber) {
        super("Runtime Error:\n" + msg + "\n" + filename + ":" + lineNumber);
    }
}
