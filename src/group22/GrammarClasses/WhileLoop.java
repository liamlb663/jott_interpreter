package group22.GrammarClasses;
import group22.DataType;
import group22.SemanticException;
import group22.SyntaxException;
import provided.*;

import java.util.ArrayList;

public class WhileLoop implements JottTree{
    private final Expr exprNode;
    private final Body bodyNode;
    private final String filename;
    private final int conditionalLineNumber;

    public WhileLoop(Expr exprNode, Body bodyNode, String filename, int conditionalLineNumber) {
        this.exprNode = exprNode;
        this.bodyNode = bodyNode;
        this.filename = filename;
        this.conditionalLineNumber = conditionalLineNumber;
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
            var filename = currToken.getFilename();
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
            return new WhileLoop(exprNode, bodyNode, filename, conditionalLineNumber);
        } catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
    }

    private boolean condIsBool() throws SemanticException {
        if(!exprNode.getDataType().equals(DataType.BOOLEAN)) {
            throw new SemanticException("Conditional statement in While loop does not evaluate to boolean",
                    filename,
                    conditionalLineNumber);
        }
        return true;
    }

    public String convertToJott() {
        return ("While[" + exprNode.convertToJott() + "]{" +  bodyNode.convertToJott() + "}");
    }

    public boolean validateTree() throws SemanticException {
        boolean exprOk = exprNode.validateTree();
        boolean bodyOk = bodyNode.validateTree();

        return condIsBool() && exprOk && bodyOk;
    }

    public void execute() {
        //TODO
    }
}
