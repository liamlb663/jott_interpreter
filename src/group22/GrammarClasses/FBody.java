package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class FBody implements JottTree {
    ArrayList<VarDec> varDecs;
    Body body;

    public FBody(ArrayList<VarDec> varDecs, Body body) {
        this.varDecs = varDecs;
        this.body = body;
    }

    private static boolean tokenIsType(Token t) {
        return t.getToken().equals("Double") || t.getToken().equals("Integer") ||
                t.getToken().equals("String") || t.getToken().equals("Boolean");
    }

    public static FBody parse(ArrayList<Token> tokens) throws SyntaxException {
        if(tokens.isEmpty()){
            throw new SyntaxException("Unexpected EOF", "", -1);
        }
        ArrayList<VarDec> varDecs = new ArrayList<>();

        Token currToken = tokens.get(0);
        while(currToken.getTokenType().equals(TokenType.ID_KEYWORD) && tokenIsType(currToken)) {
            VarDec varDec = VarDec.parse(currToken);
            varDecs.add(varDec);
            tokens.remove(0);
            currToken = tokens.get(0);
        }

        Body body = Body.parse(tokens);

        return new FBody(varDecs, body);
    }
    public String convertToJott() {
        StringBuilder sB = new StringBuilder();
        for(VarDec vd : varDecs) {
            sB.append(vd.convertToJott());
        }
        sB.append(body.convertToJott());
        return sB.toString();
    }

    public boolean validateTree() {
        //TODO
        return false;
    }

    public void execute() {
        //TODO
    }
}
