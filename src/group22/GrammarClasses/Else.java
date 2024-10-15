package group22.GrammarClasses;
import group22.SyntaxException;
import provided.*;

import java.util.ArrayList;

public class Else implements JottTree{
    private final Body bodyNode;

    public Else(Body bodyNode) {
        this.bodyNode = bodyNode;
    }

    static Else parseElse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new SyntaxException("Unexpected eof");
        }
        Token currToken = tokens.getFirst();
        if(!(currToken.getTokenType() == TokenType.ID_KEYWORD && currToken.getToken().equals("Else"))) {
            return new Else(null);
        }
        if(!(currToken.getTokenType() == TokenType.L_BRACE && currToken.getToken().equals("{"))) {
            throw new SyntaxException("Expected left brace", currToken.getFilename(), currToken.getLineNum());
        }
        Body bodyNode = Body.parseBody(tokens);
        if(!(currToken.getTokenType() == TokenType.R_BRACE && currToken.getToken().equals("}"))) {
            throw new SyntaxException("Expected right brace", currToken.getFilename(), currToken.getLineNum());
        }
        return new Else(bodyNode);
    }

    public String convertToJott() {
        if (bodyNode == null) {
            return "";
        }
        return ("Else{" + bodyNode.convertToJott() + "}");
    }

    public boolean validateTree() {
        //TODO
        return false;
    }

    public void execute() {
        //TODO
    }
}
