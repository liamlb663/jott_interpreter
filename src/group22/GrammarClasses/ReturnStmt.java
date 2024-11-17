package group22.GrammarClasses;

import group22.DataType;
import group22.SyntaxException;
import group22.SemanticException;
import group22.GrammarClasses.Program;
import provided.JottParser;
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
        try {
            Expr expr = null;

            if (tokens.isEmpty()) {
                throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
            }

            Token currToken = tokens.get(0);
            if (currToken.getTokenType().equals(TokenType.ID_KEYWORD) && currToken.getToken().equals("Return")) {
                tokens.remove(0);
                currToken = tokens.get(0);
                expr = Expr.parse(tokens);
                currToken = tokens.get(0);
                if (!currToken.getTokenType().equals(TokenType.SEMICOLON)) {
                    throw new SyntaxException("Missing semicolon at end of return statement", currToken.getFilename(), currToken.getLineNum());
                } else {
                    tokens.remove(0);
                }
            }

            return new ReturnStmt(expr);
        }
        catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
    }
    public String convertToJott() {
        if (expr == null) {
            return "";
        } else {
            return "Return " + expr.convertToJott() + ";";
        }
    }

    public boolean validateTree() throws SemanticException {
        if (Program.scopeManager.getCurrentReturnType() != expr.getDataType()) {
            throw new SemanticException("Type Differs from proper return type of function");
        }

        return (true);
    }

    public void execute() {
        //TODO
    }
}
