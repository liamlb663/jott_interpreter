package group22.GrammarClasses;

import group22.Data;
import group22.SyntaxException;
import provided.JottParser;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class FunctionReturn {

    Type type;

    public FunctionReturn(Type type) {
        this.type = type;
    }

    public static FunctionReturn parse(ArrayList<Token> tokens) throws SyntaxException {
        try {
            Token currToken = tokens.get(0);
            if (currToken.getToken().equals("Void")) {
                tokens.remove(0);
                return new FunctionReturn(null);
            }
            Type type = Type.parse(tokens);
            return new FunctionReturn(type);
        } catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
    }

    public String convertToJott() {
        return (type == null ? "Void" : type.convertToJott());
    }

    public boolean validateTree() {
        return true;
    }

    public Data execute() {
        return null;
    }
}
