package group22.GrammarClasses;

import group22.Data;
import group22.DataType;
import group22.RuntimeException;
import group22.SyntaxException;
import group22.GrammarClasses.*;
import provided.JottParser;
import provided.Token;
import provided.TokenType;

import java.awt.List;
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
        try {
            if (tokens.isEmpty()) {
                throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
            }

            Token currToken = tokens.get(0);

            if (currToken.getTokenType().equals(TokenType.R_BRACKET)) {
                return new FuncDefParams(null, null, null);
            }

            if (!currToken.getTokenType().equals(TokenType.ID_KEYWORD)) {
                throw new SyntaxException("Expected ID but saw " + currToken.getTokenType().toString(), currToken.getFilename(), currToken.getLineNum());
            }
            Id id = Id.parse(tokens);

            currToken = tokens.get(0);
            if (!currToken.getTokenType().equals(TokenType.COLON)) {
                throw new SyntaxException("Expected ':'", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);

            Type type = Type.parse(tokens);
            ArrayList<FuncDefParamsT> params = new ArrayList<>();
            currToken = tokens.get(0);
            while (currToken != null && currToken.getTokenType().equals(TokenType.COMMA)) {
                FuncDefParamsT param = FuncDefParamsT.parse(tokens);
                params.add(param);
                currToken = tokens.get(0);
            }

            return new FuncDefParams(id, type, params);
        }
        catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
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

    public ArrayList<DataType> getParamTypes() {
        ArrayList<DataType> output = new ArrayList<>();

        output.add(DataType.fromString(type.convertToJott()));
        for (FuncDefParamsT param : params) {
            output.add(DataType.fromString(param.type.convertToJott()));
        }

        return output;
    }

    public ArrayList<String> getParamNames() {
        ArrayList<String> output = new ArrayList<>();

        output.add(id.convertToJott());
        for (FuncDefParamsT param : params) {
            output.add(param.id.convertToJott());
        }

        return output;
    }

    public boolean validateTree() {
        Program.scopeManager.declareVariable(id.convertToJott(), DataType.fromString(type.convertToJott()));

        for (FuncDefParamsT param : params) {
            param.validateTree();
        }

        return true;
    }

    public Data execute() throws RuntimeException {
        return null;
    }

}
