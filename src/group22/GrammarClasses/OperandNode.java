package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottParser;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OperandNode implements JottTree {
    private final JottTree subNode;
    public static final List<TokenType> OPERAND_TYPES = Arrays.asList(
            TokenType.ID_KEYWORD,
            TokenType.NUMBER,
            TokenType.MATH_OP,
            TokenType.FC_HEADER
    );

    public OperandNode(JottTree node) {
        this.subNode = node;
    }

    static JottTree parseOperandNode(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            // TODO: Have this caught by parsing function in node above
            throw new UnknownError("Unexpected EOF when parsing Operand");
        }

        Token currToken = tokens.get(0);

        try {
            switch (currToken.getTokenType()) {
                case ID_KEYWORD -> {
                    return new OperandNode(IdNode.parseIdNode(tokens));
                }
                case NUMBER -> {
                    return new OperandNode(NumberNode.parseNumberNode(tokens));
                }
                case FC_HEADER -> {
                    return null; // TODO: Fix actual call in FuncCall class :3 | parsedOperand = new OperandNode(FuncCall.parseFunctionCallNode(tokens));
                }
                case MATH_OP -> {
                    if (!currToken.getToken().equals("-")) {
                        throw new SyntaxException(
                                "Got invalid MATH_OP token for operand",
                                currToken.getFilename(),
                                currToken.getLineNum()
                        );
                    }

                    Token nextToken = tokens.get(1);

                    if (nextToken.getTokenType() != TokenType.NUMBER) {
                        throw new SyntaxException(
                                "Minus sign not followed by num token when parsing for Operand",
                                currToken.getFilename(),
                                currToken.getLineNum()
                        );
                    }

                    tokens.remove(0);
                    tokens.remove(0);

                    tokens.add(
                            0,
                            new Token(currToken + nextToken.getToken(),
                                    nextToken.getFilename(),
                                    nextToken.getLineNum(),
                                    nextToken.getTokenType()
                            )
                    );

                    return new OperandNode(NumberNode.parseNumberNode(tokens));
                }
                default -> throw new SyntaxException(
                        "Invalid token found when parsing Operand",
                        currToken.getFilename(),
                        currToken.getLineNum()
                );
            }
        } catch (UnknownError e) {
            // If we get anything that isn't a SyntaxException, that probably means it was an unexpected EOF
            // In this case, use our last saved Token for the filename and line number
            throw new SyntaxException(
                    e.getMessage(),
                    currToken.getFilename(),
                    currToken.getLineNum()
            );
        }
    }

    @Override
    public String convertToJott() {
        return subNode.convertToJott();
    }

    @Override
    public boolean validateTree() {
        // TODO
        return false;
    }

    @Override
    public void execute() {
        // TODO
    }
}
