package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class RelOp implements JottTree {
    private final Token operator;

    public RelOp(Token operator) {
        this.operator = operator;
    }

    static RelOp parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new UnknownError("Unexpected EOF when parsing RelOP");
        }

        Token currToken = tokens.get(0);

        if (currToken.getTokenType() == TokenType.REL_OP && isValidRelOp(currToken.getToken())) {   // Ditto already done in the tokenizer i think
            tokens.remove(0); // Consume the relational operator
            return new RelOp(currToken);
        } else {
            throw new SyntaxException(
                "Invalid relational operator",
                currToken.getFilename(),
                currToken.getLineNum()
            );
        }
    }

    // Helper function to check for valid relational operators
    public static boolean isValidRelOp(String token) {
        return token.equals("==") || token.equals(">=") || token.equals("<=") ||
               token.equals(">") || token.equals("<");
    }

    @Override
    public String convertToJott() {
        return operator.getToken();
    }

    @Override
    public boolean validateTree() {
        // Relational operations are always valid if they are correctly parsed
        return true;
    }

    @Override
    public void execute() {
        // Execution logic can be added if needed (e.g., evaluation in a comparison)
    }
}
