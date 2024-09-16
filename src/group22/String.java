package group22;

import provided.Token;
import provided.TokenType;

import java.io.FileReader;
import java.io.IOException;

public class String extends Token {

    private final java.lang.String value;

    /**
     * Creates an instance of a token
     *
     * @param token    the token string
     * @param filename the name of the file the token came from
     * @param lineNum  the number of the line in the file that the token appears on
     * @param type     the type of this token
     */
    public String(java.lang.String token,
                  java.lang.String filename,
                  int lineNum,
                  TokenType type,
                  java.lang.String value
    ) {
        super(token, filename, lineNum, type);
        this.value = value;
    }

    public java.lang.String getValue() {
        return value;
    }

    public static boolean isValidStringCharacter(char input) {
        return (input == ' ') ||
                (input >= 'a' && input <= 'z') ||
                (input >= 'A' && input <= 'Z') ||
                (input >= '0' && input <= '9');
    }

    public static String processString(FileReader inputStream) throws IOException {
        StringBuilder currString = new StringBuilder();
        int currChar;

        while((currChar = inputStream.read()) != -1) {
            if (!isValidStringCharacter((char) currChar)) {
                currString.append((char) currChar); // This won't be used yet
            } else {
                if (currChar == '"') {
                    return new String(null, null, 0, null, currString.toString());
                } else {
                    break;
                }
            }
        }

        return SyntaxException();
    }
}
