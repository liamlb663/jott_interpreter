import group22.ScopeManager;
import group22.SyntaxException;
import provided.JottParser;
import provided.JottTokenizer;
import provided.JottTree;
import provided.Token;

import java.util.ArrayList;

public class Jott {
    public static void main(String[] args) throws SyntaxException {
        if (args.length == 0) {
            System.out.println("Usage: java Jott <filename>");
            return;
        }

        String filename = args[0];
        ArrayList<Token> jottTokens = JottTokenizer.tokenize(filename);
        JottTree jottTree = JottParser.parse(jottTokens);
    }
}