package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

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
            if (firstToken.getTokenType() == TokenType.STRING) {
                // TODO
                return null;
            } else if (firstToken.getTokenType() == TokenType.ID_KEYWORD &&
                    (firstToken.getToken().equals("True") || firstToken.getToken().equals("False"))
            ) {
                // TODO
                return null;
            }

            ArrayList<JottTree> validatedNodes = new ArrayList<>();

            while (validatedNodes.size() < 3) {
                Token currToken = tokens.get(0);

                switch (currToken.getTokenType()) {
                    case MATH_OP -> {
                        if (validatedNodes.isEmpty() || validatedNodes.size() == 2) {
                            validatedNodes.add(OperandNode.parseOperandNode(tokens));
                        } else {
                            // TODO
                            System.out.println("Fix");
                        }
                    }
                    case ID_KEYWORD, NUMBER, FC_HEADER -> {
                        if (validatedNodes.isEmpty() || validatedNodes.size() == 2) {
                            validatedNodes.add(OperandNode.parseOperandNode(tokens));
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
                            // TODO
                            System.out.println("Fix");
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
                            return new ExprNode(validatedNodes);
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

            return new ExprNode(validatedNodes);
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
        StringBuilder jottCode = new StringBuilder();

        for (JottTree currNode: subNodes) {
            jottCode.append(currNode.convertToJott());
        }

        return jottCode.toString();
    }

    @Override
    public boolean validateTree() {
        return false;
    }

    @Override
    public void execute() {

    }
}
