package group22.GrammarClasses;

import group22.*;
import provided.*;

import java.util.ArrayList;

public class ParamsT implements JottTree {
    private Expr exprNode;

    public ParamsT(Expr exprNode) {
        this.exprNode = exprNode;
    }

    public static ParamsT parse(ArrayList<Token> tokens) throws SyntaxException {
        try {
            if (tokens.isEmpty()) {
                throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
            }
            Token currToken = tokens.get(0);
            if (!currToken.getTokenType().equals(TokenType.COMMA)) {
                throw new SyntaxException("Comma expected before next parameter", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            if (tokens.isEmpty()) {
                throw new SyntaxException("Paramter expression expected after comma", currToken.getFilename(), currToken.getLineNum());
            }
            Expr exprNode = Expr.parse(tokens);
            return new ParamsT(exprNode);
        }
        catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
    }
    public String convertToJott() {
        String s = ", " + this.exprNode.convertToJott();
        return s;
    }

    public DataType getType() {
        return exprNode.getType();
    }

    public boolean validateTree() {
        return (exprNode.validateTree());
    }

    public void execute() {
        //TODO
    }
}
