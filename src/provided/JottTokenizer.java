package provided;

import java.lang.String;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import provided.TokenType;

import group22.IdKeyword;

/**
 * This class is responsible for tokenizing Jott code.
 *
 * @author lpa6230,
 **/

import java.util.ArrayList;

public class JottTokenizer {
    static int currentChar = -1;
    static int lineNum = 1;

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

    static Token idKeywordHandler(String filename, FileReader inputStream) throws IOException {
        Token token = null;
        String tokenString = "" + (char) currentChar;
        while ((currentChar = inputStream.read()) != -1) { // EOF condition
            if (Character.isLetterOrDigit(currentChar)) {
                tokenString += (char) currentChar;
            }
            else {
                break;
            }
        }
        // create a new token for the token string that got built
        token = new Token(tokenString, filename, lineNum, TokenType.ID_KEYWORD);
        return token;
    }

    static ArrayList<Token> processFile(String filename, FileReader inputStream) throws IOException {
        ArrayList<Token> tokens = new ArrayList<>();
        lineNum = 1;

        for (;;) {
            if (currentChar == -1) {
                int inputChar = inputStream.read();
                if (inputChar == -1) break;    // EOF

                currentChar = inputChar;
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

            if (Character.isLetter(ch)) {
                Token id_keyword = idKeywordHandler(filename, inputStream);
                tokens.add(id_keyword);
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
