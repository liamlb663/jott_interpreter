package provided;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.*;

import group22.SyntaxException;
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

    static Token numberHandlerNumFirst(String filename, FileReader inputStream) throws IOException {
        boolean decimalSeen = false;
        Token token = null;
        String tokenString = "" + (char)currentChar;
        System.out.println(tokenString);
        while((currentChar = inputStream.read()) != -1) {
            if(Character.isDigit((char)currentChar)){
                tokenString += (char)currentChar;
            } else if ((char)currentChar == '.' && !decimalSeen) {
                tokenString += (char)currentChar;
                decimalSeen = true;
            } else {
                break;
            }
        }
        token = new Token(tokenString, filename, 0, TokenType.NUMBER);
        return token;
    }
    static Token numberHandlerDotFirst(String filename, FileReader inputStream) throws IOException, SyntaxException {
        Token token = null;
        String tokenString = "" + (char)currentChar;
        while((currentChar = inputStream.read()) != -1 && Character.isDigit((char)currentChar)) {
            tokenString += (char)currentChar;
        }
        if (tokenString.equals(".")) {
            throw new SyntaxException("Decimal point must be followed by or preceded by a digit.");
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

            if (Character.isDigit(ch)){
                Token t = numberHandlerNumFirst(filename, inputStream);
                tokens.add(t);
                continue;
            }

            if (ch == '.'){
                Token t = numberHandlerDotFirst(filename, inputStream);
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
