package group22.GrammarClasses;
import group22.SyntaxException;
import provided.*;

import java.util.ArrayList;

public class Params implements JottTree {
    private final Expr exprNode;
    private final ArrayList<ParamsT> paramsTNodes;

    public Params(Expr exprNode, ArrayList<ParamsT> paramsTNodes) {
        this.exprNode = exprNode;
        this.paramsTNodes = paramsTNodes;
    }

    static Params parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new SyntaxException("Unexpected EOF", "", -1);
        }
        Token currToken = tokens.get(0);
        if (currToken.getTokenType() == TokenType.R_BRACKET) { //if epsilon
            return new Params(null, null);
        } else { //if <expr><params_t>*...
            Expr exprNode = Expr.parse(tokens);
            ArrayList<ParamsT> paramsTNodes = new ArrayList<>();
            currToken = tokens.get(0);
            while (currToken.getTokenType() == TokenType.COMMA) {
                paramsTNodes.add(ParamsT.parse(tokens));
                currToken = tokens.get(0);
            }
            return new Params(exprNode, paramsTNodes);
        }
    }

    public String convertToJott() {
        if (exprNode == null) {
            return "";
        }
        StringBuilder s = new StringBuilder(exprNode.convertToJott());
        for( ParamsT t : paramsTNodes ) {
            s.append(t.convertToJott());
        }
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
