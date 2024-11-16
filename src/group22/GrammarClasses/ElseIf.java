package group22.GrammarClasses;
import group22.DataType;
import group22.SemanticException;
import group22.SyntaxException;
import provided.*;

import java.util.ArrayList;

public class ElseIf implements JottTree{
    private final Expr exprNode;
    private final Body bodyNode;
    public final String filename;
    public final int startingLineNumber;
    public final int conditionalLineNumber;

    public ElseIf(Expr exprNode, Body bodyNode, String filename, int startingLineNumber, int conditionalLineNumber) {
        this.exprNode = exprNode;
        this.bodyNode = bodyNode;
        this.filename = filename;
        this.startingLineNumber = startingLineNumber;
        this.conditionalLineNumber = conditionalLineNumber;
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
            var startingLineNumber = currToken.getLineNum();
            tokens.remove(0);
            currToken = tokens.get(0);
            if (!(currToken.getTokenType() == TokenType.L_BRACKET && currToken.getToken().equals("["))) {
                throw new SyntaxException("Expected left bracket", currToken.getFilename(), currToken.getLineNum());
            }
            var conditionalLineNumber = currToken.getLineNum();
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
            return new ElseIf(exprNode, bodyNode, filename, startingLineNumber, conditionalLineNumber);
        } catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
    }

    private boolean condIsBool() throws SemanticException {
        if(!exprNode.getDataType().equals(DataType.BOOLEAN)) {
            throw new SemanticException("Conditional statement in ElseIf statement does not evaluate to boolean",
                    filename,
                    conditionalLineNumber);
        }
        return true;
    }

    public boolean hasReturnStmt() {
        return bodyNode.returnStmt != null;
    }

    public String convertToJott() {
        return ("Elseif[" + exprNode.convertToJott() + "]{" +  bodyNode.convertToJott() + "}");
    }

    public boolean validateTree() throws SemanticException {
        return exprNode.validateTree() && bodyNode.validateTree() && condIsBool();
    }

    public void execute() {
        //TODO
    }
}
