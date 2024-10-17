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

    static NumberNode parseNumberNode(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new UnknownError("Unexpected EOF");
        }

        Token currToken = tokens.removeFirst();

        if (currToken.getTokenType() != TokenType.NUMBER) {
            throw new SyntaxException(
                    "Didn't receive NUMBER token when parsing for number",
                    currToken.getFilename(),
                    currToken.getLineNum()
            );
        }

        return new NumberNode(currToken);
    }

    @Override
    public String convertToJott() {
        return this.number.getToken();
    }

    @Override
    public boolean validateTree() {
        // TODO
        return false;
    }

    @Override
    public void execute() {
        // TODO
    }
}
