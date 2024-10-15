package group22.GrammarClasses;
import group22.SyntaxException;
import provided.*;

import java.util.ArrayList;

public class ElseIf implements JottTree{
    private final Expr exprNode;
    private final Body bodyNode;

    public ElseIf(Expr exprNode, Body bodyNode) {
        this.exprNode = exprNode;
        this.bodyNode = bodyNode;
    }

    static ElseIf parseElseIf(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new SyntaxException("Unexpected eof");
        }
        Token currToken = tokens.getFirst();
        if(!(currToken.getTokenType() == TokenType.ID_KEYWORD && currToken.getToken().equals("Elseif"))) {
            throw new SyntaxException("Expected Elseif keyword", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.removeFirst();
        currToken = tokens.getFirst();
        if(!(currToken.getTokenType() == TokenType.L_BRACKET && currToken.getToken().equals("["))) {
            throw new SyntaxException("Expected left bracket", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.removeFirst();
        Expr exprNode = Expr.parseExpr(tokens);
        currToken = tokens.getFirst();
        if(!(currToken.getTokenType() == TokenType.R_BRACKET && currToken.getToken().equals("]"))) {
            throw new SyntaxException("Expected right bracket", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.removeFirst();
        currToken = tokens.getFirst();
        if(!(currToken.getTokenType() == TokenType.L_BRACE && currToken.getToken().equals("{"))) {
            throw new SyntaxException("Expected left brace", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.removeFirst();
        Body bodyNode = Body.parseBody(tokens);
        currToken = tokens.getFirst();
        if(!(currToken.getTokenType() == TokenType.R_BRACE && currToken.getToken().equals("}"))) {
            throw new SyntaxException("Expected right brace", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.removeFirst();
        return new ElseIf(exprNode, bodyNode);
    }

    public String convertToJott() {
        return ("Elseif[" + exprNode.convertToJott() + "]{" +  bodyNode.convertToJott() + "}");
    }

    public boolean validateTree() {
        //TODO
        return false;
    }

    public void execute() {
        //TODO
    }
}
