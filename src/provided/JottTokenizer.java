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

    static void commentHandler(FileReader inputStream) throws IOException {
        char ch;
        while ((ch = (char)inputStream.read()) != -1) {
            if (ch == '\n') return;
        }

        // In Case of error I'm not sure what to do?
        // This could only happen if EOF comes after a '#' which idk what happens
    }

    static ArrayList<Token> processFile(String filename, FileReader inputStream) throws IOException {
        ArrayList<Token> tokens = new ArrayList<>();

        for (;;) {
            int inputChar = inputStream.read();
            if (inputChar == -1) break;    // EOF

            char ch = (char)inputChar;

            if (Character.isWhitespace(ch)) continue;

            if (ch == '#') {
                commentHandler(inputStream);
                continue;
            }

            tokens.add(new Token("" + ch, filename, 0, TokenType.STRING));
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
