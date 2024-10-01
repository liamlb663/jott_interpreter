package group22.GrammarClasses;
import group22.SyntaxException;
import provided.*;

import java.util.ArrayList;

public class FunctionCallNode implements JottTree{
    public FunctionCallNode(IdNode idNode, ParamsNode paramsNode) {
        this.idNode = idNode;
        this.paramsNode = paramsNode;
    }

    static FunctionCallNode parseFunctionCallNode(ArrayList<Token> tokens) {
        if (tokens.isEmpty()) {
            throw new SyntaxException("Unexpected eof");
        }
        Token currToken = tokens.getFirst();
        if (currToken.getTokenType() != TokenType.COLON) {
            throw new SyntaxException("Should be a colon");
        }
        tokens.removeFirst();
        if (currToken.getTokenType() != TokenType.COLON) {
            throw new SyntaxException("Should be a colon");
        }
        tokens.removeFirst();
        IdNode idNode = IdNode.parseIDNode(tokens);
        if (currToken.getTokenType() != TokenType.L_BRACKET) {
            throw new SyntaxException("Should be a left bracket");
        }
        tokens.removeFirst();
        ParamsNode paramsNode = ParamsNode.parseParamsNode(tokens);
        if (currToken.getTokenType() != TokenType.R_BRACKET) {
            throw new SyntaxException("Should be a right bracket");
        }
        tokens.removeFirst();
        return new FunctionCallNode(idNode, paramsNode);
    }

    public String convertToJott() {
        String s = ("::" + this.idNode.convertToJott() + "[" + this.paramsNode.convertToJott() + "]");
        return s;
    }
}
