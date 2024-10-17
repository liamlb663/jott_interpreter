package group22.GrammarClasses;

import group22.SyntaxException;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class FunctionReturn {

    Type type;

    public FunctionReturn(Type type) {
        this.type = type;
    }

    public static FunctionReturn parse(ArrayList<Token> tokens) throws SyntaxException {
        Type type = Type.parse(tokens);
        return new FunctionReturn(type);
    }

    public String convertToJott() {
        return type.convertToJott();
    }

    public boolean validateTree() {
        // TO DO
        return true;
    }

    public void execute() {
        // TO DO
    }
}
