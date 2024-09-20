package group22;

import java.lang.Exception;
import java.lang.String;

/**
 * A basic class for a SyntaxException, thrown whenever a syntax error occurs.
 *
 * @author mbf1102, cng8060
 */
public class SyntaxException extends Exception {
    public SyntaxException() {

    }

    public SyntaxException(String msg) {
        super(msg);
    }

    public SyntaxException(String msg, String filename, int lineNumber) {
        super(msg + "\n" + filename + ":" + lineNumber);
    }
}
