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

    static Token equalsHandler(String filename, FileReader inputStream) throws IOException {
        Token token = null;
        String tokenString = "" + currentChar;
        currentChar = inputStream.read();
        if (currentChar == '=') {
            tokenString += currentChar;
            token = new Token(tokenString, filename, 0, TokenType.REL_OP);
        } else {
            token = new Token(tokenString, filename, 0, TokenType.ASSIGN);
        }
        return token;
    }

    static Token angleBracketHandler(String filename, FileReader inputStream) throws IOException {
        Token token = null;
        String tokenString = "" + currentChar;
        currentChar = inputStream.read();
        if (currentChar == '=') {
            tokenString += currentChar;
        }
        token = new Token(tokenString, filename, 0, TokenType.REL_OP);
        return token;
    }

    static Token exclamationHandler(String filename, FileReader inputStream) throws IOException, SyntaxException {
        Token token = null;
        String tokenString = "" + currentChar;
        currentChar = inputStream.read();
        if (currentChar == '=') {
            tokenString += currentChar;
        }
        if (tokenString == '!') {
            throw new SyntaxException("Exclamation mark must be followed by an equals sign.");
        }
        token = new Token(tokenString, filename, 0, TokenType.REL_OP);
        return token;
    }

    static ArrayList<Token> processFile(String filename, FileReader inputStream) throws IOException {
        ArrayList<Token> tokens = new ArrayList<>();

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

            if (ch == '=') {
                Token t = equalsHandler(filename, inputStream);
                tokens.add(t);
                continue;
            }

            if (ch == '<' || ch == '>') {
                Token t = angleBracketHandler(filename, inputStream);
                tokens.add(t);
                continue;
            }

            if (ch == '!') {
                Token t = exclamationHandler(filename, inputStream);
                tokens.add(t);
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
