package group22.GrammarClasses;

import group22.*;
import provided.*;

import java.util.ArrayList;

public class FuncDefParamsT implements JottTree {
    Id id;  // Parameter name
    Type type;  // Parameter type

    public FuncDefParamsT(Id id, Type type) {
        this.id = id;
        this.type = type;
    }

    public static FuncDefParamsT parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new SyntaxException("Unexpected EOF", "", -1);
        }
        Token currToken = tokens.get(0);
        if(!currToken.getTokenType().equals(TokenType.COMMA)) {
            throw new SyntaxException("Expected ','", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.remove(0);

        currToken = tokens.get(0);
        if (!currToken.getTokenType().equals(TokenType.ID_KEYWORD)) {
            throw new SyntaxException("Expected ID but saw " + currToken.getTokenType().toString(), currToken.getFilename(), currToken.getLineNum());
        }
        Id id = Id.parse(currToken);
        tokens.remove(0);

        currToken = tokens.get(0);
        if (!currToken.getTokenType().equals(TokenType.COLON)) {
            throw new SyntaxException("Expected ':' after ID token", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.remove(0);
        Type type = Type.parse(tokens);

        return new FuncDefParamsT(id, type);
    }

    public String convertToJott() {
        return "," + id.convertToJott() + ":" + type.convertToJott();
    }

    public boolean validateTree() {
        // TO DO
        return true;
    }

    public void execute() {
        // TO DO
    }
}
