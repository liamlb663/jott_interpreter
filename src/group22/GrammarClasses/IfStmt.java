package group22.GrammarClasses;
import group22.DataType;
import group22.ScopeManager;
import group22.SemanticException;
import group22.SyntaxException;
import provided.*;

import java.util.ArrayList;
import java.util.HashMap;

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

    static IfStmt parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
        try {
            Token currToken = tokens.get(0);
            if (!(currToken.getTokenType() == TokenType.ID_KEYWORD && currToken.getToken().equals("If"))) {
                throw new SyntaxException("Expected If keyword", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            currToken = tokens.get(0);
            if (currToken.getTokenType() != TokenType.L_BRACKET) {
                throw new SyntaxException("Expected left bracket", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            Expr exprNode = Expr.parse(tokens);
            currToken = tokens.get(0);
            if (currToken.getTokenType() != TokenType.R_BRACKET) {
                throw new SyntaxException("Expected right bracket", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            currToken = tokens.get(0);
            if (currToken.getTokenType() != TokenType.L_BRACE) {
                throw new SyntaxException("Expected left brace", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            Body bodyNode = Body.parse(tokens);
            currToken = tokens.get(0);
            if (currToken.getTokenType() != TokenType.R_BRACE) {
                throw new SyntaxException("Expected right brace", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            ArrayList<ElseIf> elseIfNodes = new ArrayList<>();
            currToken = tokens.get(0);
            while (currToken.getTokenType() == TokenType.ID_KEYWORD && currToken.getToken().equals("Elseif")) {
                elseIfNodes.add(ElseIf.parse(tokens));
                currToken = tokens.get(0);
            }
            Else elseNode = Else.parse(tokens);
            return new IfStmt(exprNode, bodyNode, elseIfNodes, elseNode);
        } catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
    }

    public String convertToJott() {
        StringBuilder s = new StringBuilder("If[" + exprNode.convertToJott() + "]{" + bodyNode.convertToJott() + "}");
        for (ElseIf e : elseIfNodes) {
            s.append(e.convertToJott());
        }
        s.append(elseNode.convertToJott());
        return s.toString();
    }

    private boolean condIsBool() throws SemanticException {
        if(exprNode.subNodes.size() == 1) {
            if (exprNode.subNodes.get(0) instanceof Id) {
                var id = (Id) exprNode.subNodes.get(0);
                ScopeManager.
            }
            return false;
        }
    }

    public boolean willReturn() throws SemanticException {
        return bodyNode.returnStmt != null && uniformReturns();
    }

    // if IfStmt has a return, then all elseif/else statements need a return
    private boolean uniformReturns() throws SemanticException {
        boolean hasReturn = bodyNode.returnStmt != null;
        for (ElseIf e : elseIfNodes) {
            if (hasReturn && !e.hasReturnStmt()) {
                throw new SemanticException("ElseIf statement does not have return statement", "", 0);
            }
        }
        if (elseNode != null && elseNode.hasReturnStmt() != hasReturn) {
            throw new SemanticException("Else statement does not have return statement, but If statement does", "", 0);
        }
        return true;
    }

    public boolean validateTree() throws SemanticException {
        for (ElseIf e : elseIfNodes) {
            if (!e.validateTree()) {
                return false;
            }
        }
        if (elseNode != null) {
            if (!elseNode.validateTree()) {
                return false;
            }
        }
        return condIsBool() && uniformReturns() && exprNode.validateTree() && bodyNode.validateTree();
    }

    public void execute() {
        //TODO
    }
}
