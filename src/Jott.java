import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import group22.RuntimeException;
import group22.SyntaxException;
import group22.SemanticException;
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
            tokens = JottTokenizer.tokenize(fileName);
            if (tokens == null) {
                System.err.println("Error: Tokenizer returned null. Please check the input file.");
                return;
            }
        } catch (Exception e) {
            System.err.println("Error during tokenization: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        JottTree root;
        try {
            root = JottParser.parse(tokens);
        } catch (SyntaxException e) {
            System.err.println(e.getMessage());
            return;
        }

        try {
            root.validateTree();
        } catch (SemanticException e) {
            System.err.println(e.getMessage());
            return;
        }

        try {
            root.execute();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            return;
        }
    }
}
