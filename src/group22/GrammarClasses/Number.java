package group22.GrammarClasses;

import group22.Data;
import group22.DataType;
import group22.RuntimeException;
import group22.SyntaxException;
import provided.JottParser;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class Number implements JottTree {
    private final Token number;
    private final DataType type;

    public Number(Token number, DataType type) {
        this.number = number;
        this.type = type;
    }

    static Number parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new SyntaxException(
                    "Unexpected EOF",
                    JottParser.getFileName(),
                    JottParser.getLineNumber()
            );
        }

        Token currToken = tokens.remove(0);

        if (currToken.getTokenType() != TokenType.NUMBER) {
            throw new SyntaxException(
                    "Didn't receive NUMBER token when parsing for number",
                    currToken.getFilename(),
                    currToken.getLineNum()
            );
        }

        DataType type = currToken.getToken().contains(".") ? DataType.DOUBLE : DataType.INTEGER;
        return new Number(currToken, type);
    }

    public DataType getDataType() {
        return this.type;
    }

    public Token getToken() {
        return number;
    }

    @Override
    public String convertToJott() {
        return this.number.getToken();
    }

    @Override
    public boolean validateTree() {
        return true;
    }

    @Override
    public Data execute() throws RuntimeException {
        if (type == DataType.DOUBLE) {
            return new Data(
                    Double.parseDouble(number.getToken()),
                    type,
                    number.getFilename(),
                    number.getLineNum()
            );
        } else {
            return new Data(
                    Integer.parseInt(number.getToken()),
                    type,
                    number.getFilename(),
                    number.getLineNum()
            );
        }
    }
}
