package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class NumberNode implements JottTree {
    private final Token number;

    public NumberNode(Token number) {
        this.number = number;
    }

    static NumberNode parseNumberNode(ArrayList<Token> tokens) {
        if (tokens.isEmpty()) {
            throw new SyntaxException("Unexpected EOF"); // FIXME
        }

        Token currNumber = tokens.remove(0);

        if (currNumber.getTokenType() != TokenType.NUMBER) {
            throw new SyntaxException("Uh oh!!! ;((((");
        }

        return new NumberNode(currNumber);
    }

    public String convertToJott() {
        return this.number.getToken();
    }
}
