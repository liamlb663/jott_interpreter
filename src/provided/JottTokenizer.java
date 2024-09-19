package provided;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import provided.TokenType;

/**
 * This class is responsible for tokenizing Jott code.
 *
 * @author lpa6230,
 **/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JottTokenizer {
    static int currentChar = -1;
    static int lineNum = 0;

    static void commentHandler(FileReader inputStream) throws IOException {
        currentChar = -1;

        int ch;
        while ((ch = inputStream.read()) != -1) {
            if ((char)ch == '\n') {
                lineNum++;
                return;
            }
        }
    }

    static Token colonFcHeaderHandler(FileReader inputStream, String filename) throws IOException {
        // Colon has been read
        currentChar = inputStream.read();

        if (currentChar == -1) {
            return null;
        }

        if (currentChar != ':') {
            if (currentChar == '\n') {
                lineNum++;
            }

            // Return without flushing Character
            return new Token(":", filename, lineNum, TokenType.COLON);
        }

        // Return and flush Character
        currentChar = -1;
        return new Token("::", filename, lineNum, TokenType.FC_HEADER);
    }

    static ArrayList<Token> processFile(String filename, FileReader inputStream) throws IOException {
        ArrayList<Token> tokens = new ArrayList<>();

        for (;;) {
            if (currentChar == -1) {
                currentChar = inputStream.read();
                if (currentChar == -1) break;    // EOF
            }
            char ch = (char)currentChar;

            if (Character.isWhitespace(ch)) {
                if (ch == '\n') lineNum++;

                currentChar = -1;
                continue;
            }

            if (ch == '#') {
                commentHandler(inputStream);
                continue;
            }

            if (ch == ':') {
                Token output = colonFcHeaderHandler(inputStream, filename);
                if (output == null) {
                    tokens.add(output);
                }

                continue;
            }

            // Top row
            Map<Character, TokenType> tokenMap = new HashMap<>();
            tokenMap.put(',', TokenType.COMMA);
            tokenMap.put(']', TokenType.R_BRACKET);
            tokenMap.put('[', TokenType.L_BRACKET);
            tokenMap.put('}', TokenType.R_BRACE);
            tokenMap.put('{', TokenType.L_BRACE);

            if (tokenMap.containsKey(ch)) {
                tokens.add(new Token("" + ch, filename, lineNum, tokenMap.get(ch)));
                currentChar = -1;
                continue;
            }

            currentChar = -1;
        }

        return tokens;
    }

	/**
     * Takes in a filename and tokenizes that file into Tokens
     * based on the rules of the Jott Language
     * @param filename the name of the file to tokenize; can be relative or absolute path
     * @return an ArrayList of Jott Tokens
     */
    public static ArrayList<Token> tokenize(String filename){
        try {
            FileReader inputStream = new FileReader(filename);

            return processFile(filename, inputStream);
        } catch(IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch(Exception e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return null;
	}
}
