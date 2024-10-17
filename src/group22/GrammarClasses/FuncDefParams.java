package group22.GrammarClasses;

import group22.SyntaxException;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class FuncDefParams {

    Id id;
    Type type;
    ArrayList<FuncDefParamsT> params;
    public FuncDefParams(Id id, Type type, ArrayList<FuncDefParamsT> params) {
        this.id = id;
        this.type = type;
        this.params = params;
    }

    public static FuncDefParams parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()){
            throw new SyntaxException("Unexpected EOF", "", -1);
        }

        Token currToken = tokens.get(0);
        if (!currToken.getTokenType().equals(TokenType.ID_KEYWORD)) {
            throw new SyntaxException("Expected ID but saw " + currToken.getTokenType().toString(), currToken.getFilename(), currToken.getLineNum());
        }
        Id id = Id.parse(currToken);
        tokens.remove(0);

        currToken = tokens.get(0);
        if (!currToken.getTokenType().equals(TokenType.COLON)) {
            throw new SyntaxException("Expected ':'", currToken.getFilename(), currToken.getLineNum());
        }
        tokens.remove(0);

        Type type = Type.parse(tokens);
        tokens.remove(0);
        ArrayList<FuncDefParamsT> params = new ArrayList<>();
        currToken = tokens.get(0);
        while(currToken != null && currToken.getTokenType().equals(TokenType.COMMA)) {
            FuncDefParamsT param = FuncDefParamsT.parse(tokens);
            params.add(param);
            tokens.remove(0);
            currToken = tokens.get(0);
        }

        return new FuncDefParams(id, type, params);
    }

    public String convertToJott() {
        StringBuilder sB = new StringBuilder();
        sB.append(id.convertToJott());
        sB.append(':');
        sB.append(type.convertToJott());
        for(FuncDefParamsT p : params) {
            sB.append(p.convertToJott());
        }
        return sB.toString();
    }

    public boolean validateTree() {
        // TO DO
    }

    public void execute() {
        // TO DO
    }

}
