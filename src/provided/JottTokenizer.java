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

    public static boolean isValidStringCharacter(char input) {
        return (input == ' ') ||
                (input >= 'a' && input <= 'z') ||
                (input >= 'A' && input <= 'Z') ||
                (input >= '0' && input <= '9');
    }

    public static Token processString(String filename, FileReader inputStream, int lineNum) throws IOException, SyntaxException {
        StringBuilder currString = new StringBuilder();
        int currStringChar;

        while((currStringChar = inputStream.read()) != -1) {
            char asciiToChar = (char) currStringChar;

            if (isValidStringCharacter(asciiToChar)) {
                currString.append(asciiToChar);
            } else {
                if (currStringChar == '"') {
                    return new Token("\"" + currString + "\"", filename, lineNum, TokenType.STRING);
                } else {
                    throw new SyntaxException("Got invalid character of '" + asciiToChar + "'", filename, lineNum);
                }
            }
        }

        throw new SyntaxException("Missing ending \" for the String token", filename, lineNum);
    }

    static Token numberHandlerNumFirst(String filename, FileReader inputStream) throws IOException {
        Token token = null;
        String tokenString = "" + currentChar;
        while((currentChar = inputStream.read()) != -1 && Character.isDigit(currentChar)) {
            tokenString += currentChar;
        }
        currentChar = inputStream.read();
        if (currentChar == '.') {
            tokenString += currentChar;
        } else if (Character.isDigit(currentChar)) {
            do {
                tokenString += currentChar;
            } while ((currentChar = inputStream.read()) != -1 && Character.isDigit(currentChar));
        }
        token = new Token(tokenString, filename, 0, TokenType.NUMBER);

        return token;
    }
    static Token numberHandlerDotFirst(String filename, FileReader inputStream) throws IOException, SyntaxException {
        Token token = null;
        String tokenString = "" + currentChar;
        while((currentChar = inputStream.read()) != -1 && Character.isDigit(currentChar)) {
            tokenString += currentChar;
        }
        if (tokenString.equals(".")) {
            throw new SyntaxException();
        }
        token = new Token(tokenString, filename, 0, TokenType.NUMBER);
        return token;
    }

    static ArrayList<Token> processFile(String filename, FileReader inputStream) throws IOException, SyntaxException {
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
