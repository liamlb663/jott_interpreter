package group22.GrammarClasses;
import group22.SyntaxException;
import provided.*;

import java.util.ArrayList;

public class FuncCall implements JottTree{
    private final Id idNode;
    private final Params paramsNode;

    public FuncCall(Id idNode, Params paramsNode) {
        this.idNode = idNode;
        this.paramsNode = paramsNode;
    }

    static FuncCall parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new SyntaxException("Unexpected EOF", "", -1);
        }
        Token currToken = tokens.get(0);
        if (currToken.getTokenType() != TokenType.COLON) {
            throw new SyntaxException("Should be a colon", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.remove(0);
        currToken = tokens.get(0);
        if (currToken.getTokenType() != TokenType.COLON) {
            throw new SyntaxException("Should be a colon", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.remove(0);
        Id idNode = Id.parse(tokens);
        currToken = tokens.get(0);
        if (currToken.getTokenType() != TokenType.L_BRACKET) {
            throw new SyntaxException("Should be a left bracket", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.remove(0);
        Params paramsNode = Params.parse(tokens);
        currToken = tokens.get(0);
        if (currToken.getTokenType() != TokenType.R_BRACKET) {
            throw new SyntaxException("Should be a right bracket", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.remove(0);
        return new FuncCall(idNode, paramsNode);
    }

    public String convertToJott() {
        return ("::" + this.idNode.convertToJott() + "[" + this.paramsNode.convertToJott() + "]");
    }

    public boolean validateTree() {
        //TODO
        return false;
    }

    public void execute() {
        //TODO
    }
}
