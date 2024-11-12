package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottParser;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;
import java.util.List;

public class Expr implements JottTree {
    private final ArrayList<JottTree> subNodes;

    public Expr(ArrayList<JottTree> subNodes) {
        this.subNodes = subNodes;
    }

    static Expr parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new SyntaxException(
                    "Unexpected EOF",
                    JottParser.getFileName(),
                    JottParser.getLineNumber()
            );
        }

        Token firstToken = tokens.get(0);

        try {
            if (firstToken.getTokenType() == TokenType.STRING) {
                return new Expr(new ArrayList<>(List.of(StringLiteral.parse(tokens))));
            } else if (firstToken.getTokenType() == TokenType.ID_KEYWORD &&
                    (firstToken.getToken().equals("True") || firstToken.getToken().equals("False"))
            ) {
                return new Expr(new ArrayList<>(List.of(Bool.parse(tokens))));
            }

            ArrayList<JottTree> validatedNodes = new ArrayList<>();

            while (validatedNodes.size() < 3) {
                Token currToken = tokens.get(0);

                switch (currToken.getTokenType()) {
                    case MATH_OP -> {
                        if (validatedNodes.isEmpty() || validatedNodes.size() == 2) {
                            validatedNodes.add(Operand.parse(tokens));
                        } else {
                            validatedNodes.add(MathOp.parse(tokens));
                        }
                    }
                    case ID_KEYWORD, NUMBER, FC_HEADER -> {
                        if (validatedNodes.isEmpty() || validatedNodes.size() == 2) {
                            validatedNodes.add(Operand.parse(tokens));
                        } else {
                            throw new SyntaxException(
                                    "Received unexpected " + currToken.getTokenType().toString() + " token",
                                    currToken.getFilename(),
                                    currToken.getLineNum()
                            );
                        }
                    }
                    case REL_OP -> {
                        if (validatedNodes.size() == 1) {
                            validatedNodes.add(RelOp.parse(tokens));
                        } else {
                            throw new SyntaxException(
                                    "Received unexpected REL_OP token",
                                    currToken.getFilename(),
                                    currToken.getLineNum()
                            );
                        }
                    }
                    default -> {
                        if (!validatedNodes.isEmpty()) {
                            return new Expr(validatedNodes);
                        } else {
                            throw new SyntaxException(
                                    "Received unexpected " + currToken.getTokenType().toString() + " token",
                                    currToken.getFilename(),
                                    currToken.getLineNum()
                            );
                        }
                    }
                }
            }

            return new Expr(validatedNodes);
        } catch (Exception e) {
            throw new SyntaxException(
                    "Unexpected EOF",
                    JottParser.getFileName(),
                    JottParser.getLineNumber()
            );
        }
    }

    private boolean isValidMathExpression() {
        Token operandOne = Operand.getOperandToken((Operand) subNodes.getFirst());
    }

    @Override
    public String convertToJott() {
        StringBuilder jottCode = new StringBuilder();

        for (JottTree currNode: subNodes) {
            jottCode.append(currNode.convertToJott());
        }

        return jottCode.toString();
    }

    @Override
    public boolean validateTree() {
        if (subNodes.size() == 1) {
            return subNodes.getFirst().validateTree();
        } else if (MathOp.isValidMathOp(subNodes.get(1).convertToJott())) {
            // TODO

        } else if (RelOp.isValidRelOp(subNodes.get(1).convertToJott())) {
            // TODO
        }
    }

    @Override
    public void execute() {

    }
}
