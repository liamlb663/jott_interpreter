package group22.GrammarClasses;

import group22.*;
import provided.*;

import java.util.ArrayList;

public class Program implements JottTree {
    ArrayList<FunctionDef> functionDefs;

    public Program(ArrayList<FunctionDef> functionDefs) {
        this.functionDefs = functionDefs;
    }

    public static Program parse(ArrayList<Token> tokens) throws SyntaxException {
        try {
            ArrayList<FunctionDef> functionDefs = new ArrayList<>();

            Token currToken = tokens.get(0);
            while (currToken != null && currToken.getToken().equals("Def")) {
                FunctionDef function = FunctionDef.parse(tokens);
                functionDefs.add(function);
                currToken = tokens.get(0);
            }
            return new Program(functionDefs);
        }
        catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
        catch (SyntaxException e) {
            // Catch the SyntaxException and return null if it occurs
            System.err.println("Syntax error: " + e.getMessage());
            return null;
        }
    }

    public String convertToJott() {
        StringBuilder sb = new StringBuilder();
        for (FunctionDef funcDef : functionDefs) {
            sb.append(funcDef.convertToJott());
        }
        return sb.toString();
    }

    public boolean validateTree() {
        // TO DO
        return true;
    }

    public void execute() {
        // TO DO
    }
}

