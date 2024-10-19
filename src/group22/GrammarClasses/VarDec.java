package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class VarDec implements JottTree {
    private final Type type;
    private final Id id;

    public VarDec(Type type, Id id) {
        this.type = type;
        this.id = id;
    }

    static JottTree parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.size() < 3) {
            throw new SyntaxException("Unexpected EOF when parsing VarDec", "", -1);
        }

        Type type = Type.parse(tokens);
        Id id = Id.parse(tokens);

        // Grab what should be the semi
        Token thirdToken = tokens.get(0);

        // Check if the token is a semicolon
        if (thirdToken.getTokenType() != TokenType.SEMICOLON) {
            throw new SyntaxException(
                "Missing semicolon at the end of variable declaration",
                thirdToken.getFilename(),
                thirdToken.getLineNum()
            );
        }

        return new VarDec(type, id);
    }

    public String convertToJott() {
        return type.convertToJott() + " " + id.convertToJott() + ";";
    }

    public boolean validateTree() {
        //TODO
        return false;
    }

    public void execute() {
        //TODO
    }
}
