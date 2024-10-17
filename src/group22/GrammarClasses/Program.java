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
        ArrayList<FunctionDef> functionDefs = new ArrayList<>();

        Token currToken = tokens.get(0);
        while (currToken != null && currToken.getToken().equals("Def")) {
            FunctionDef function = FunctionDef.parse(tokens);
            functionDefs.add(function);
            tokens.remove(0);
            currToken = tokens.get(0);
        }
        return new Program(functionDefs);
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

