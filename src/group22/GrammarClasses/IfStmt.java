package group22.GrammarClasses;
import group22.SyntaxException;
import provided.*;

import java.util.ArrayList;

public class IfStmt implements JottTree{
    private final Expr exprNode;
    private final Body bodyNode;
    private final ArrayList<ElseIf> elseIfNodes;
    private final Else elseNode;

    public IfStmt(Expr exprNode, Body bodyNode, ArrayList<ElseIf> elseIfNodes, Else elseNode) {
        this.exprNode = exprNode;
        this.bodyNode = bodyNode;
        this.elseIfNodes = elseIfNodes;
        this.elseNode = elseNode;
    }

    static IfStmt parseIfStmt(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new SyntaxException("Unexpected EOF", "", -1);
        }
        Token currToken = tokens.get(0);
        if(!(currToken.getTokenType() == TokenType.ID_KEYWORD && currToken.getToken().equals("If"))) {
            throw new SyntaxException("Expected If keyword", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.remove(0);
        currToken = tokens.get(0);
        if(currToken.getTokenType() != TokenType.L_BRACKET) {
            throw new SyntaxException("Expected left bracket", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.remove(0);
        Expr exprNode = Expr.parseExpr(tokens);
        currToken = tokens.get(0);
        if(currToken.getTokenType() != TokenType.R_BRACKET) {
            throw new SyntaxException("Expected right bracket", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.remove(0);
        currToken = tokens.get(0);
        if(currToken.getTokenType() != TokenType.L_BRACE) {
            throw new SyntaxException("Expected left brace", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.remove(0);
        Body bodyNode = Body.parseBody(tokens);
        currToken = tokens.get(0);
        if(currToken.getTokenType() != TokenType.R_BRACE) {
            throw new SyntaxException("Expected right brace", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.remove(0);
        ArrayList<ElseIf> elseIfNodes = new ArrayList<>();
        currToken = tokens.get(0);
        while (currToken.getTokenType() == TokenType.ID_KEYWORD && currToken.getToken().equals("Elseif")) {
            elseIfNodes.add(ElseIf.parseElseIf(tokens));
            currToken  = tokens.get(0);
        }
        Else elseNode = Else.parseElse(tokens);
        return new IfStmt(exprNode, bodyNode, elseIfNodes, elseNode);
    }

    public String convertToJott() {
        StringBuilder s = new StringBuilder("If[" + exprNode.convertToJott() + "]{" + bodyNode.convertToJott() + "}");
        for (ElseIf e : elseIfNodes) {
            s.append(e.convertToJott());
        }
        s.append(elseNode.convertToJott());
        return s.toString();
    }

    public boolean validateTree() {
        //TODO
        return false;
    }

    public void execute() {
        //TODO
    }
}
