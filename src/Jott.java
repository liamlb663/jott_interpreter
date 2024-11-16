import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import provided.JottParser;
import provided.JottTokenizer;
import provided.JottTree;
import provided.Token;

public class Jott {
    public static void main(String[] args) {
        // Ensure a file name is provided
        if (args.length == 0) {
            System.err.println("Error: No file name provided. Please specify a Jott code file.");
            return;
        }

        String fileName = args[0]; // File name is passed as a command-line argument
        ArrayList<Token> tokens;
        try {
            tokens = JottTokenizer.tokenize("parserTestCases/" + fileName);
        } catch (Exception e) {
            System.err.println("Error during tokenization: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Parse the tokens into a Jott tree
        JottTree root;
        try {
            root = JottParser.parse(tokens);
        } catch (Exception e) {
            System.err.println("Error during parsing: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        // Perform further processing with the parsed tree (if necessary)
        System.out.println("Parsing completed successfully.");
        System.out.println("Root of the JottTree: " + root);
    }
}
