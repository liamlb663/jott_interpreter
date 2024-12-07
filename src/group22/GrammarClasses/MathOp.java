package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class MathOp implements JottTree {
    private final Token operator;

    public MathOp(Token operator) {
        this.operator = operator;
    }

    static MathOp parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new UnknownError("Unexpected EOF when parsing MathOP");
        }

        Token currToken = tokens.get(0);

        if (currToken.getTokenType() == TokenType.MATH_OP && isValidMathOp(currToken.getToken())) { // I think thats checked already
            tokens.remove(0); // Consume the math operator
            return new MathOp(currToken);
        } else {
            throw new SyntaxException(
                "Invalid mathematical operator",
                currToken.getFilename(),
                currToken.getLineNum()
            );
        }
    }

    public static boolean isValidMathOp(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") || token.equals("/");
    }

    public Token getToken() {
        return operator;
    }

    @Override
    public String convertToJott() {
        return operator.getToken();
    }

    @Override
    public boolean validateTree() {
        return true;
    }

    public String getValue() {
        return operator.getToken();
    }

    @Override
    public void execute() {
        // TODO
    }
}
