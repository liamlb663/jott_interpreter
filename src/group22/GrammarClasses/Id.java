package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottParser;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class Id implements JottTree {
    private final Token id;

    public Id(Token id) {
        this.id = id;
    }

    static Id parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new SyntaxException(
                    "Unexpected EOF",
                    JottParser.getFileName(),
                    JottParser.getLineNumber()
            );
        }

        Token currToken = tokens.remove(0);

        if (currToken.getTokenType() != TokenType.ID_KEYWORD) {
            throw new SyntaxException(
                    "Didn't receive ID_KEYWORD token when parsing for ID",
                    currToken.getFilename(),
                    currToken.getLineNum()
            );
        }

        return new Id(currToken);
    }

    @Override
    public String convertToJott() {
        return this.id.getToken();
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
