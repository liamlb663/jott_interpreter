package group22.GrammarClasses;

import group22.*;
import group22.RuntimeException;
import group22.GrammarClasses.Program;
import provided.*;

import java.util.ArrayList;

public class FunctionDef implements JottTree {
    public Id id;  // The function name
    public FuncDefParams params;  // The function parameters
    public FunctionReturn returnType;  // The function return
    public FBody body;  // The function body

    public FunctionDef(Id id, FuncDefParams params, FunctionReturn returnType, FBody body) {
        this.id = id;
        this.params = params;
        this.returnType = returnType;
        this.body = body;
    }

    public static FunctionDef parse(ArrayList<Token> tokens) throws SyntaxException {
        try {
            if (tokens.isEmpty()) {
                throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
            }
            Token currToken = tokens.get(0);
            if (!currToken.getToken().equals("Def")) {
                throw new SyntaxException("Expected 'Def' Keyword but got " + currToken.getToken(), currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            currToken = tokens.get(0);
            if (!currToken.getTokenType().equals(TokenType.ID_KEYWORD)) {
                throw new SyntaxException("Expected ID but got " + currToken.getTokenType().toString(), currToken.getFilename(), currToken.getLineNum());
            }
            Id id = Id.parse(tokens);
            currToken = tokens.get(0);
            if (!currToken.getTokenType().equals(TokenType.L_BRACKET)) {
                throw new SyntaxException("Expected '[' but got " + currToken.getTokenType().toString(), currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            currToken = tokens.get(0);
            FuncDefParams params = null;
            if (!currToken.getTokenType().equals(TokenType.R_BRACKET)) {
                params = FuncDefParams.parse(tokens);
            }
            currToken = tokens.get(0);
            if (!currToken.getTokenType().equals(TokenType.R_BRACKET)) {
                throw new SyntaxException("Expected ']' but got " + currToken.getTokenType().toString(), currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            currToken = tokens.get(0);
            if (!currToken.getTokenType().equals(TokenType.COLON)) {
                throw new SyntaxException("Expected ':' but got " + currToken.getTokenType().toString(), currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            FunctionReturn returnType = FunctionReturn.parse(tokens);
            currToken = tokens.get(0);
            if (!currToken.getTokenType().equals(TokenType.L_BRACE)) {
                throw new SyntaxException("Expected '{' but got " + currToken.getTokenType().toString(), currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            FBody body = FBody.parse(tokens);
            currToken = tokens.get(0);
            if (!currToken.getTokenType().equals(TokenType.R_BRACE)) {
                throw new SyntaxException("Expected '}' but got " + currToken.getTokenType().toString(), currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            return new FunctionDef(id, params, returnType, body);
        }
        catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
    }

    public String convertToJott() {
        return "Def " + id.convertToJott() + "[" + (params == null ? "" : params.convertToJott()) + "]: " +
                returnType.convertToJott() + "{" + body.convertToJott() + "}";
    }

    void registerFunction() throws SemanticException{
        if (id.convertToJott().equals("main")) {
            if (params != null) {
                throw new SemanticException("Main function should not take any parameters", id.id.getFilename(), id.id.getLineNum());
            }
            if (returnType.type != null) {
                throw new SemanticException("Main function should not return any value", id.id.getFilename(), id.id.getLineNum());
            }
        }

        Program.scopeManager.newScope(DataType.fromString(returnType.convertToJott()));
            if (params != null) {params.validateTree();}
            body.validateTree();
        Program.scopeManager.dropScope();

        // Declare function after to avoid potential recursion
        ArrayList<DataType> declareParamTypes = new ArrayList<>();
        if (params != null) {declareParamTypes = params.getParamTypes();}
        ArrayList<String> declareParamNames = new ArrayList<>();
        if (params != null) {declareParamNames = params.getParamNames();}
        Program.scopeManager.declareFunction(
            id.convertToJott(),
            DataType.fromString(returnType.convertToJott()),
            body,
            declareParamTypes,
            declareParamNames
        );

    }

    public boolean validateTree() throws SemanticException {

        id.validateTree();
        returnType.validateTree();

        registerFunction();

        return true;
    }

    public Data execute() throws RuntimeException {
        return null;
    }

}
