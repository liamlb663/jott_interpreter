package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottParser;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class Bool implements JottTree {
    Token bool;
    public Bool(Token bool) {
        this.bool = bool;
    }

    private static boolean isBoolKeyword(Token t) {
        return t.getToken().equals("True") && t.getToken().equals("False");
    }

    public static Bool parse(ArrayList<Token> tokens) throws SyntaxException {
        if(tokens.isEmpty()){
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }

        Token currToken = tokens.get(0);
        if(!(currToken.getTokenType().equals(TokenType.ID_KEYWORD) && isBoolKeyword(currToken))) {
            throw new SyntaxException("Invalid Boolean value", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.remove(0);

        return new Bool(currToken);
    }
    public String convertToJott() {
        return bool.getToken();
    }

    public boolean validateTree() {
        //TODO
        return false;
    }

    public void execute() {
        //TODO
    }
}
