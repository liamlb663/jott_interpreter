package group22.GrammarClasses;

import group22.Data;
import group22.RuntimeException;
import group22.SyntaxException;
import provided.JottParser;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;
import java.util.Arrays;

public class Type implements JottTree {
    private final Token type;
    private static final String[] VALID_TYPES = {"Double", "Integer", "String", "Boolean"};

    public Type(Token type) {
        this.type = type;
    }

    static Type parse(ArrayList<Token> tokens) throws SyntaxException {
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
                    "Invalid token found when parsing Type",
                    currToken.getFilename(),
                    currToken.getLineNum()
            );
        }

        if (!Arrays.asList(VALID_TYPES).contains(currToken.getToken())) {
            throw new SyntaxException(
                    "Received invalid datatype of " + currToken.getToken() + " for Type token",
                    currToken.getFilename(),
                    currToken.getLineNum()
            );
        }

        return new Type(currToken);
    }

    @Override
    public String convertToJott() {
        return type.getToken();
    }

    @Override
    public boolean validateTree() {
        return true;
    }

    @Override
    public Data execute() throws RuntimeException {
        return null;
    }
}
