package provided;

import java.io.FileReader;
import java.io.IOException;
import java.lang.*;

//import group22.String;
import group22.SyntaxException;

/**
 * This class is responsible for tokenizing Jott code.
 *
 * @author lpa6230,
 **/

import java.util.ArrayList;

public class JottTokenizer {
    static int currentChar = -1;

    static void commentHandler(FileReader inputStream) throws IOException {
        currentChar = -1;

        char ch;
        while ((ch = (char)inputStream.read()) != -1) {
            if (ch == '\n') return;
        }

        // In Case of error I'm not sure what to do?
        // This could only happen if EOF comes after a '#' which idk what happens
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
        int inputAscii;
        int lineNum = 1;
        char currChar;

        while ((inputAscii = inputStream.read()) != -1) {

            currChar = (char) inputAscii;

            if (currChar == '\n') {
                lineNum++;
            } else if (currChar == '"') {
                tokens.add(processString(filename, inputStream, lineNum));
            } else if (currChar == '#') {
                commentHandler(inputStream);
            } else if (Character.isDigit(currChar)){
                Token t = numberHandlerNumFirst(filename, inputStream);
                tokens.add(t);
            } else if (currChar == '.'){
                Token t = numberHandlerDotFirst(filename, inputStream);
                tokens.add(t);
            }
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
