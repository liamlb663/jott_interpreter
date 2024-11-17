package group22.GrammarClasses;

import group22.*;
import provided.*;

import java.util.ArrayList;

public class Program implements JottTree {
    static ScopeManager scopeManager = new ScopeManager();
    ArrayList<FunctionDef> functionDefs;

    String fileName;
    int fileNumber;

    public Program(ArrayList<FunctionDef> functionDefs, String name, int number) {
        this.functionDefs = functionDefs;
        this.fileName = name;
        this.fileNumber = number;
    }

    public static Program parse(ArrayList<Token> tokens) throws SyntaxException {
        int number = tokens.getLast().getLineNum();
        String name = tokens.getLast().getFilename();
        try {
            ArrayList<FunctionDef> functionDefs = new ArrayList<>();

            Token currToken = tokens.get(0);
            while (currToken != null && currToken.getToken().equals("Def")) {
                FunctionDef function = FunctionDef.parse(tokens);
                functionDefs.add(function);
                if (tokens.isEmpty()) {
                    currToken = null;
                } else {
                    currToken = tokens.get(0);
                }
            }
            if (!tokens.isEmpty()) {
                Token nextToken = tokens.get(0);
                throw new SyntaxException("Expected EOF, found " + nextToken.getTokenType(), nextToken.getFilename(), nextToken.getLineNum());
            }
            return new Program(functionDefs, name, number);
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

    public boolean validateTree() throws SemanticException {
        for (FunctionDef funcDef : functionDefs) {
            funcDef.validateTree();
        }

        if (!scopeManager.isFunctionDeclared("main")) {
            throw new SemanticException("Missing main function", fileName, fileNumber);
        }

        return true;
    }

    public void execute() {
        // TO DO
    }
}

