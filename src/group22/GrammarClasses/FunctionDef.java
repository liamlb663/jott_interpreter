package group22.GrammarClasses;

import group22.*;
import provided.*;

import java.util.ArrayList;

public class FunctionDef implements JottTree {
    private Id id;  // The function name
    private FuncDefParams params;  // The function parameters
    private FunctionReturn returnType;  // The function return
    private FBody body;  // The function body

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

    public boolean validateTree() {
        // TO DO
        return true;
    }

    public void execute() {
        // TO DO
    }

}
