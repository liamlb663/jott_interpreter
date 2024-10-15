package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;
import java.util.Arrays;

public class ExprNode implements JottTree {
    private final ArrayList<JottTree> subNodes;

    public ExprNode(ArrayList<JottTree> subNodes) {
        this.subNodes = subNodes;
    }

    static ExprNode parseExprNode(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            // TODO: Catch in nodes above
            throw new UnknownError("Unexpected EOF");
        }

        Token firstToken = tokens.get(0);

        try {
            if (OperandNode.OPERAND_TYPES.contains(firstToken.getTokenType())) {
                if (firstToken.getTokenType() == TokenType.ID_KEYWORD &&
                        (firstToken.getToken().equals("True") || firstToken.getToken().equals("False"))
                ) {
                    // TODO
                    return new ExprNode(Arrays.asList(Bool.parseBoolNode(firstToken)));
                }

                Token secondToken = tokens.get(1);

                if ()

                if (secondToken.getTokenType() == TokenType.REL_OP) {
                    TokenType thirdToken
                }
            } else if (firstToken.getTokenType() == TokenType.STRING) {
                // TODO
                return new ExprNode(Arrays.asList(StringNode.parseStringNode(firstToken)));
            } else {
                throw new SyntaxException(
                        "Invalid token found when parsing Expr",
                        firstToken.getFilename(),
                        firstToken.getLineNum()
                );
            }
        } catch (UnknownError e) {
            throw new SyntaxException(
                    e.getMessage(),
                    firstToken.getFilename(),
                    firstToken.getLineNum()
            );
        }
    }

    @Override
    public String convertToJott() {
        return null;
    }

    @Override
    public boolean validateTree() {
        return false;
    }

    @Override
    public void execute() {

    }
}
