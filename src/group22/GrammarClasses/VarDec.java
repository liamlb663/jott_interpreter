package group22.GrammarClasses;

import group22.DataType;
import group22.ScopeManager;
import group22.SemanticException;
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

    static VarDec parse(ArrayList<Token> tokens) throws SyntaxException {
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
        tokens.remove(0);

        return new VarDec(type, id);
    }

    public String convertToJott() {
        return type.convertToJott() + " " + id.convertToJott() + ";";
    }

    public boolean validateTree() throws SemanticException {
        // Will throw if not valid, so don't need to check boolean value
        id.validateTree();

        Token idToken = id.getToken();
        boolean idAlreadyMade = Program.scopeManager.isVarDeclared(idToken.getToken());

        // Check if variable is already made, if it is then there is a problem
        if (idAlreadyMade) {
            throw new SemanticException(
                    "The variable " + idToken.getToken() + " is already declared",
                    idToken.getFilename(),
                    idToken.getLineNum()
            );
        }

        // This should just be true since parsing validates it, but fuck it why not
        type.validateTree();

        Program.scopeManager.declareVariable(idToken.getToken(), DataType.fromString(type.convertToJott()));
        return true;
    }

    public void execute() {
        //TODO
    }
}
