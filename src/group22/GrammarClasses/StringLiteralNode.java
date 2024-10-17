package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class StringLiteralNode implements JottTree {
    private final Token stringLiteral;

    public StringLiteralNode(Token stringLiteral) {
        this.stringLiteral = stringLiteral;
    }

    static StringLiteralNode parseStringLiteralNode(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            // TODO: Catch in nodes above
            throw new UnknownError("Unexpected EOF");
        }

        Token firstToken = tokens.removeFirst();

        if (firstToken.getTokenType() != TokenType.STRING) {
            throw new SyntaxException(
                    "Didn't receive STRING token when parsing StringLiteral Node",
                    firstToken.getFilename(),
                    firstToken.getLineNum()
            );
        }

        return new StringLiteralNode(firstToken);
    }

    @Override
    public String convertToJott() {
        // This is really insecure as escaping the String is really easy, however I don't care
        return "\"" + stringLiteral.getToken() + "\"";
    }

    @Override
    public boolean validateTree() {
        return false;
    }

    @Override
    public void execute() {

    }
}