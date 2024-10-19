package provided;

/**
 * This class is responsible for paring Jott Tokens
 * into a Jott parse tree.
 *
 * @author Max Frohman, Sapphire Godard, Andrei Makarov, Joshua Justice, Liam Ahearn
 */

import group22.SyntaxException;
import group22.GrammarClasses.Program;

import java.util.ArrayList;

public class JottParser {
    static int lineNumber;
    static String fileName;

    /**
     * Parses an ArrayList of Jotton tokens into a Jott Parse Tree.
     * @param tokens the ArrayList of Jott tokens to parse
     * @return the root of the Jott Parse Tree represented by the tokens.
     *         or null upon an error in parsing.
     */
    public static JottTree parse(ArrayList<Token> tokens) throws SyntaxException {
		if (tokens.isEmpty()) {
            lineNumber = 0;
            fileName = "";
        } else {
            lineNumber = tokens.get(tokens.size() - 1).getLineNum();
            fileName = tokens.get(tokens.size() - 1).getFilename();
        }

        return Program.parse(tokens);
    }

    public static int getLineNumber() {
        return JottParser.lineNumber;
    }

    public static String getFileName() {
        return JottParser.fileName;
    }
}
