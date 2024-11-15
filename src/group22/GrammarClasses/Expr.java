package group22.GrammarClasses;

import group22.DataType;
import group22.SyntaxException;
import provided.JottParser;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Expr implements JottTree {
    public final ArrayList<JottTree> subNodes;
    public String fileName;
    public int startingLineNumber;

    public Expr(ArrayList<JottTree> subNodes) {
        this.subNodes = subNodes;
        this.fileName = "";
        this.startingLineNumber = -1;
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
        var fileName = firstToken.getFilename();
        var startingLineNumber = firstToken.getLineNum();

        try {
            if (firstToken.getTokenType() == TokenType.STRING) {
                var expr = new Expr(new ArrayList<>(List.of(StringLiteral.parse(tokens))));
                expr.fileName = fileName;
                expr.startingLineNumber = startingLineNumber;
                return expr;
            } else if (firstToken.getTokenType() == TokenType.ID_KEYWORD &&
                    (firstToken.getToken().equals("True") || firstToken.getToken().equals("False"))
            ) {
                var expr = new Expr(new ArrayList<>(List.of(Bool.parse(tokens))));
                expr.fileName = fileName;
                expr.startingLineNumber = startingLineNumber;
                return expr;
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
                            var expr = new Expr(validatedNodes);
                            expr.fileName = fileName;
                            expr.startingLineNumber = startingLineNumber;
                            return expr;
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
        Token operandOne = subNodes.getFirst()
    }

    @Override
    public String convertToJott() {
        StringBuilder jottCode = new StringBuilder();

        for (JottTree currNode: subNodes) {
            jottCode.append(currNode.convertToJott());
        }

        return jottCode.toString();
    }

    public DataType getType() {

    }

    @Override
    public boolean validateTree() {
        return false;
    }

    @Override
    public void execute() {

    }
}
