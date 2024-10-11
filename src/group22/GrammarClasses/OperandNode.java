package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class OperandNode implements JottTree {
    static JottTree parseOperandNode(ArrayList<Token> tokens) {
        if (tokens.isEmpty()) {
            throw new SyntaxException("EOF!! :O");
        }

        Token currOperand = tokens.get(0);

        switch (currOperand.getTokenType()) {
            case ID_KEYWORD -> {
                return IdNode.parseIdNode(tokens); // Fix me :)
            }
            case NUMBER -> {
                return NumberNode.parseNumberNode(tokens);
            }
            case FC_HEADER -> {
                return FuncCall.parseFunctionCallNode(tokens); // Fix??? :3
            }
            case MATH_OP -> {
                if (currOperand.getToken().equals("-")) {
                    tokens.remove(0);
                    Token nextToken = tokens.remove(0);
                    tokens.add(0,
                            new Token(currOperand + nextToken.getToken(),
                                    nextToken.getFilename(), nextToken.getLineNum(),
                                    nextToken.getTokenType())
                    );
                }
            }
            default -> {
                //
                TokenType.Ma
            }
        }
    }
}
