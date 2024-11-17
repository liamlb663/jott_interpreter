package group22.GrammarClasses;

import group22.DataType;
import group22.SemanticException;
import group22.SyntaxException;
import provided.JottParser;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class Operand implements JottTree {
    private final JottTree subNode;

    public Operand(JottTree node) {
        this.subNode = node;
    }

    static JottTree parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new SyntaxException(
                    "Unexpected EOF",
                    JottParser.getFileName(),
                    JottParser.getLineNumber()
            );
        }

        Token currToken = tokens.get(0);

        try {
            switch (currToken.getTokenType()) {
                case ID_KEYWORD -> {
                    return new Operand(Id.parse(tokens));
                }
                case NUMBER -> {
                    return new Operand(Number.parse(tokens));
                }
                case FC_HEADER -> {
                    return new Operand(FuncCall.parse(tokens));
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

                    tokens.add(0,
                            new Token(currToken + nextToken.getToken(),
                                    nextToken.getFilename(),
                                    nextToken.getLineNum(),
                                    nextToken.getTokenType()
                            )
                    );

                    return new Operand(Number.parse(tokens));
                }
                default -> throw new SyntaxException(
                        "Invalid token found when parsing Operand",
                        currToken.getFilename(),
                        currToken.getLineNum()
                );
            }
        } catch (Exception e) {
            // If we get anything that isn't a SyntaxException, that probably means it was an unexpected EOF
            // In this case, use our last saved Token for the filename and line number
            throw new SyntaxException(
                    "Unexpected EOF",
                    JottParser.getFileName(),
                    JottParser.getLineNumber()
            );
        }
    }

    public Token getToken() {
        if (subNode instanceof Id) {
            return ((Id) subNode).getToken();
        } else if (subNode instanceof FuncCall) {
            return ((FuncCall) subNode).getToken();
        } else {
            return ((Number) subNode).getToken();
        }
    }

    public DataType getDataType() throws SemanticException {
        if (subNode instanceof Id) {
            return ((Id) subNode).getDataType();
        } else if (subNode instanceof FuncCall) {
            return ((FuncCall) subNode).getDataType();
        } else {
            return ((Number) subNode).getDataType();
        }
    }

    @Override
    public String convertToJott() {
        return subNode.convertToJott();
    }

    @Override
    public boolean validateTree() throws SemanticException {
        return subNode.validateTree();
    }

    @Override
    public void execute() {
        // TODO
    }
}
