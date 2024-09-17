package group22;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import provided.Token;
import provided.TokenType;

public class IdKeyword {
    private StringBuilder tokenBuilder = new StringBuilder();
    private ArrayList<Token> tokens;
    private String filename;  // The filename to associate with tokens
    private int lineNum;      // The line number for the tokens
    public IdKeyword(ArrayList<Token> tokens, String filename, int lineNum) {
        this.tokens = tokens;
        this.filename = filename;
        this.lineNum = lineNum;
    }

    public int processCharacter(char ch, FileReader inputStream) throws IOException {
        while (Character.isLetterOrDigit(ch)) {
            tokenBuilder.append(ch);
            // Grab the next character from the input stream
            int nextChar = inputStream.read();
            if (nextChar == -1) {
                break; // EOF
            }
            ch = (char) nextChar;
        }
        // If we encounter a character that is not alphanumeric then finalize the token
        if (tokenBuilder.length() > 0) {
            tokens.add(new Token(tokenBuilder.toString(), filename, lineNum, TokenType.ID_KEYWORD));
            tokenBuilder.setLength(0); // Clear the token builder for the next token
        }
        // Push back the non alphanumeric character to the stream
        return ch; // Return the character back to the Tokenizer
    }
}
