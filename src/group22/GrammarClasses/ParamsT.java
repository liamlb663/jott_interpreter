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
        if(tokens.isEmpty()){
            throw new SyntaxException("Unexpected EOF", "", -1);
        }
        Token currToken = tokens.get(0);
        if(!currToken.getTokenType().equals(TokenType.COMMA)) {
            throw new SyntaxException("Comma expected before next parameter", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.remove(0);
        if(tokens.isEmpty()) {
            throw new SyntaxException("Paramter expression expected after comma", currToken.getFilename(), currToken.getLineNum());
        }
        Expr exprNode = Expr.parse(tokens);
        return new ParamsT(exprNode);
    }
    public String convertToJott() {
        String s = ", " + this.exprNode.convertToJott();
        return s;
    }

    public boolean validateTree() {
        //TODO
        return false;
    }

    public void execute() {
        //TODO
    }
}
