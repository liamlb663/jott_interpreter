package group22;

/**
 * A basic class for a SemanticException, thrown whenever a semantic error occurs.
 *
 * @author mbf1102
 */
public class SemanticException extends Exception {
    public SemanticException(String msg) {
        super(msg);
    }

    public SemanticException(String msg, String filename, int lineNumber) {
        super("Semantic Error:\n" + msg + "\n" + filename + ":" + lineNumber);
    }
}
