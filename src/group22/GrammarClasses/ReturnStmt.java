package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class ReturnStmt implements JottTree {
    Expr expr;

    public ReturnStmt(Expr expr) {
        if (expr == null) {
            this.expr = null;
        } else {
            this.expr = expr;
        }
    }

    public static ReturnStmt parse(ArrayList<Token> tokens) throws SyntaxException {
        Expr expr = null;

        if(tokens.isEmpty()){
            throw new SyntaxException("Unexpected EOF", "", -1);
        }

        Token currToken = tokens.getFirst();
        if(currToken.getTokenType().equals(TokenType.ID_KEYWORD) && currToken.getToken().equals("Return")) {
            tokens.removeFirst();
            currToken = tokens.getFirst();
            expr = Expr.parse(currToken);
            currToken = tokens.getFirst();
            if(!currToken.getTokenType().equals(TokenType.SEMICOLON)) {
                throw new SyntaxException("Missing semicolon at end of return statement", currToken.getFilename(), currToken.getLineNum());
            } else {
                tokens.removeFirst();
            }
        }

        return new ReturnStmt(expr);
    }
    public String convertToJott() {
        if (expr == null) {
            return "";
        } else {
            return "Return " + expr.convertToJott() + ";";
        }
    }

    public boolean validateTree() {
        //TODO
        return false;
    }

    public void execute() {
        //TODO
    }
}
