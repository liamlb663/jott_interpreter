package group22.GrammarClasses;
import group22.*;
import provided.*;

import java.util.ArrayList;

public class ElseIf implements JottTree{
    private final Expr exprNode;
    private final Body bodyNode;
    public final String filename;
    public final int lineNumber;

    public ElseIf(Expr exprNode, Body bodyNode, String filename, int lineNumber) {
        this.exprNode = exprNode;
        this.bodyNode = bodyNode;
        this.filename = filename;
        this.lineNumber = lineNumber;
    }

    static ElseIf parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
        try {
            Token currToken = tokens.get(0);
            if (!(currToken.getTokenType() == TokenType.ID_KEYWORD && currToken.getToken().equals("Elseif"))) {
                throw new SyntaxException("Expected Elseif keyword", currToken.getFilename(), currToken.getLineNum());
            }
            var filename = currToken.getFilename();
            var lineNumber = currToken.getLineNum();
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
            return new ElseIf(exprNode, bodyNode, filename, lineNumber);
        } catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
    }

    private boolean condIsBool(ScopeManager sm) throws SemanticException {
        if (exprNode.subNodes.size() == 1) {
            if (exprNode.subNodes.get(0) instanceof Id id) {
                return id.getIdDatatype() == DataType.BOOLEAN;
            } else if (exprNode.subNodes.get(0) instanceof FuncCall fc) {
                return sm.getFunctionReturnType(fc.getName()).equals(DataType.BOOLEAN);
            }
            throw new SemanticException("Conditional statement in ElseIf does not evaluate to boolean",
                    exprNode.fileName,
                    exprNode.startingLineNumber);
        }
        if (exprNode.subNodes.size() == 3) {
            if (!(exprNode.subNodes.get(1) instanceof RelOp)) {
                throw new SemanticException("Conditional statement in ElseIf does not evaluate to boolean",
                        exprNode.fileName,
                        exprNode.startingLineNumber);
            };
            return true;
        }
        throw new SemanticException("Conditional statement does not evaluate to boolean", "", -1);
    }

    public boolean hasReturnStmt() {
        return bodyNode.returnStmt != null;
    }

    public String convertToJott() {
        return ("Elseif[" + exprNode.convertToJott() + "]{" +  bodyNode.convertToJott() + "}");
    }

    public boolean validateTree(ScopeManager sm) throws SemanticException {
        return exprNode.validateTree(sm) && bodyNode.validateTree(sm) && condIsBool(sm);
    }

    public void execute() {
        //TODO
    }
}
