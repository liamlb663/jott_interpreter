package group22;

import java.lang.Exception;
import java.lang.String;

public class SyntaxException extends Exception {
    public SyntaxException() {

    }

    public SyntaxException(String msg) {
        super(msg);
    }
}
