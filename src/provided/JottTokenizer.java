package provided;

import java.lang.String;
import java.io.FileReader;
import java.io.IOException;
import java.lang.*;

import group22.SyntaxException;
import provided.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is responsible for tokenizing Jott code.
 *
 * @author lpa6230, mbf1102, jjj7632, aom3091, cng8060
 */
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

    public static boolean isValidStringCharacter(char input) {
        return (input == ' ') ||
                (input >= 'a' && input <= 'z') ||
                (input >= 'A' && input <= 'Z') ||
                (input >= '0' && input <= '9');
    }

    public static Token processString(String filename, FileReader inputStream) throws IOException, SyntaxException {
        StringBuilder currString = new StringBuilder();

        while((currentChar = inputStream.read()) != -1) {
            char asciiToChar = (char) currentChar;

            if (isValidStringCharacter(asciiToChar)) {
                currString.append(asciiToChar);
            } else {
                if (asciiToChar == '"') {
                    currentChar = inputStream.read();
                    return new Token("\"" + currString + "\"", filename, lineNum, TokenType.STRING);
                } else {
                    String errorChar = "'" + asciiToChar + "'";

                    if (Character.isWhitespace(asciiToChar)) {
                        errorChar = "whitespace that isn't a space";
                    }

                    throw new SyntaxException("Got invalid character of " + errorChar, filename, lineNum);
                }
            }
        }

        throw new SyntaxException("Missing ending \" for the String token", filename, lineNum);
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

    static Token numberHandlerNumFirst(String filename, FileReader inputStream) throws IOException {
        boolean decimalSeen = false;
        Token token = null;
        String tokenString = "" + (char)currentChar;

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
        token = new Token(tokenString, filename, lineNum, TokenType.NUMBER);
        return token;
    }
  
    static Token numberHandlerDotFirst(String filename, FileReader inputStream) throws IOException, SyntaxException {
        Token token = null;
        String tokenString = "" + (char)currentChar;
        while((currentChar = inputStream.read()) != -1 && Character.isDigit((char)currentChar)) {
            tokenString += (char)currentChar;
        }
        if (tokenString.equals(".")) {
            throw new SyntaxException("Decimal point must be followed by or preceded by a digit.", filename, lineNum);
        }
        token = new Token(tokenString, filename, lineNum, TokenType.NUMBER);
        return token;
    }

    static Token equalsHandler(String filename, FileReader inputStream) throws IOException {
        Token token = null;
        String tokenString = "" + (char)currentChar;
        currentChar = inputStream.read();
        char ch = (char)currentChar;
        if (ch == '=') {
            tokenString += ch;
            token = new Token(tokenString, filename, lineNum, TokenType.REL_OP);
            currentChar = inputStream.read();
        } else {
            token = new Token(tokenString, filename, lineNum, TokenType.ASSIGN);
        }
        return token;
    }

    static Token angleBracketHandler(String filename, FileReader inputStream) throws IOException {
        Token token = null;
        String tokenString = "" + (char)currentChar;
        currentChar = inputStream.read();
        char ch = (char)currentChar;
        if (ch == '=') {
            tokenString += ch;
            currentChar = inputStream.read();
        }
        token = new Token(tokenString, filename, lineNum, TokenType.REL_OP);
        return token;
    }

    static Token exclamationHandler(String filename, FileReader inputStream) throws IOException, SyntaxException {
        Token token = null;
        String tokenString = "" + (char)currentChar;
        currentChar = inputStream.read();
        char ch = (char)currentChar;
        if (ch == '=') {
            tokenString += ch;
            currentChar = inputStream.read();
        }
        if (tokenString.equals("!")) {
            throw new SyntaxException("Exclamation mark must be followed by an equals sign.", filename, lineNum);
        }
        token = new Token(tokenString, filename, lineNum, TokenType.REL_OP);
        return token;
    }

    static ArrayList<Token> processFile(String filename, FileReader inputStream) throws IOException, SyntaxException {
        ArrayList<Token> tokens = new ArrayList<>();
        lineNum = 1;

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

            if (ch == '"') {
                tokens.add(processString(filename, inputStream));
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

            if (ch == ':') {
                Token output = colonFcHeaderHandler(inputStream, filename);
                if (output != null) {
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
            tokenMap.put(';', TokenType.SEMICOLON);

            tokenMap.put('+', TokenType.MATH_OP);
            tokenMap.put('-', TokenType.MATH_OP);
            tokenMap.put('*', TokenType.MATH_OP);
            tokenMap.put('/', TokenType.MATH_OP);

            if (tokenMap.containsKey(ch)) {
                tokens.add(new Token("" + ch, filename, lineNum, tokenMap.get(ch)));
                currentChar = -1;
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
        } catch(SyntaxException e) {
            System.err.println("Syntax error: " + e.getMessage());
        }

        return null;
	}
}
