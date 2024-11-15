package group22.GrammarClasses;
import group22.SyntaxException;
import provided.*;

import java.util.ArrayList;

public class WhileLoop implements JottTree{
    private final Expr exprNode;
    private final Body bodyNode;

    public WhileLoop(Expr exprNode, Body bodyNode) {
        this.exprNode = exprNode;
        this.bodyNode = bodyNode;
    }

    static WhileLoop parseWhileLoop(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
        try {
            Token currToken = tokens.get(0);
            if (!(currToken.getTokenType() == TokenType.ID_KEYWORD && currToken.getToken().equals("While"))) {
                throw new SyntaxException("Expected While keyword", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            currToken = tokens.get(0);
            if (!(currToken.getTokenType() == TokenType.L_BRACKET && currToken.getToken().equals("["))) {
                throw new SyntaxException("Expected left bracket", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            Expr exprNode = Expr.parse(tokens);
            currToken = tokens.get(0);
            if (!(currToken.getTokenType() == TokenType.R_BRACKET && currToken.getToken().equals("]"))) {
                throw new SyntaxException("Expected right bracket", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            currToken = tokens.get(0);
            if (!(currToken.getTokenType() == TokenType.L_BRACE && currToken.getToken().equals("{"))) {
                throw new SyntaxException("Expected left brace", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            Body bodyNode = Body.parse(tokens);
            currToken = tokens.get(0);
            if (!(currToken.getTokenType() == TokenType.R_BRACE && currToken.getToken().equals("}"))) {
                throw new SyntaxException("Expected right brace", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            return new WhileLoop(exprNode, bodyNode);
        } catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
    }

    public String convertToJott() {
        return ("While[" + exprNode.convertToJott() + "]{" +  bodyNode.convertToJott() + "}");
    }

    public boolean validateTree() {
        return false;
    }

    public void execute() {
        //TODO
    }
}
