package group22.GrammarClasses;
import group22.DataType;
import group22.SemanticException;
import group22.SyntaxException;
import provided.*;

import java.util.ArrayList;

public class IfStmt implements JottTree{
    private final Expr exprNode;
    private final Body bodyNode;
    private final ArrayList<ElseIf> elseIfNodes;
    private final Else elseNode;
    private final String filename;
    private final int conditionalLineNumber;
    private final int bodyLineNumber;

    public IfStmt(Expr exprNode, Body bodyNode, ArrayList<ElseIf> elseIfNodes, Else elseNode, String filename, int conditionalLineNumber, int bodyLineNumber) {
        this.exprNode = exprNode;
        this.bodyNode = bodyNode;
        this.elseIfNodes = elseIfNodes;
        this.elseNode = elseNode;
        this.filename = filename;
        this.conditionalLineNumber = conditionalLineNumber;
        this.bodyLineNumber = bodyLineNumber;
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
            var filename = currToken.getFilename();
            var conditionalLineNumber = currToken.getLineNum();
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
            var bodyLineNumber = currToken.getLineNum();
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
            return new IfStmt(exprNode, bodyNode, elseIfNodes, elseNode, filename, conditionalLineNumber, bodyLineNumber);
        } catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
    }

    private boolean condIsBool() throws SemanticException {
        if(!exprNode.getDataType().equals(DataType.BOOLEAN)) {
            throw new SemanticException("Conditional statement in If statement does not evaluate to boolean",
                    filename,
                    conditionalLineNumber);
        }
        return true;
    }

    public boolean willReturn() throws SemanticException {
        return bodyNode.returnStmt != null && uniformReturns();
    }

    // if IfStmt has a return, then all elseif/else statements need a return
    private boolean uniformReturns() throws SemanticException {
        boolean hasReturn = bodyNode.returnStmt != null;
        for (ElseIf e : elseIfNodes) {
            if (hasReturn && !e.hasReturnStmt()) {
                throw new SemanticException("ElseIf statement does not have return statement", e.filename, e.startingLineNumber);
            }
        }
        if (elseNode.bodyNode != null && elseNode.hasReturnStmt() != hasReturn) {
            throw new SemanticException("Else statement does not have return statement, but If statement does", elseNode.filename, elseNode.lineNumber);
        }
        return true;
    }

    public String convertToJott() {
        StringBuilder s = new StringBuilder("If[" + exprNode.convertToJott() + "]{" + bodyNode.convertToJott() + "}");
        for (ElseIf e : elseIfNodes) {
            s.append(e.convertToJott());
        }
        s.append(elseNode.convertToJott());
        return s.toString();
    }

    public boolean validateTree() throws SemanticException {
        boolean exprOk = exprNode.validateTree();
        boolean bodyOk = bodyNode.validateTree();

        for (ElseIf e : elseIfNodes) {
            e.validateTree();
        }
        if (elseNode != null) {
            elseNode.validateTree();
        }
        return condIsBool() && uniformReturns() && exprOk && bodyOk;
    }
    public void execute() {
        Bool cond = (Bool)exprNode.getValue();
        if (cond.getValue()) {
            bodyNode.execute();
            return;
        }
        if (elseIfNodes != null) {
            for (ElseIf e : elseIfNodes) {
                cond = (Bool)e.exprNode.getValue();
                if (cond.getValue()) {
                    e.execute();
                    return;
                }
            }
        }
        if (elseNode != null) {
                elseNode.execute();
        }
    }
}
