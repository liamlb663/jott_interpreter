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
        try {
            if (tokens.isEmpty()) {
                throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
            }
            Token currToken = tokens.get(0);
            if (!currToken.getTokenType().equals(TokenType.COMMA)) {
                throw new SyntaxException("Expected ','", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);

            currToken = tokens.get(0);
            if (!currToken.getTokenType().equals(TokenType.ID_KEYWORD)) {
                throw new SyntaxException("Expected ID but saw " + currToken.getTokenType().toString(), currToken.getFilename(), currToken.getLineNum());
            }
            Id id = Id.parse(tokens);

            currToken = tokens.get(0);
            if (!currToken.getTokenType().equals(TokenType.COLON)) {
                throw new SyntaxException("Expected ':' after ID token", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            Type type = Type.parse(tokens);

            return new FuncDefParamsT(id, type);
        }
        catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
    }

    public String convertToJott() {
        return "," + id.convertToJott() + ":" + type.convertToJott();
    }

    public boolean validateTree() {
        Program.scopeManager.declareVariable(id.convertToJott(), DataType.fromString(type.convertToJott()));
        return true;
    }

    public Data execute() {
        return null;
    }
}
