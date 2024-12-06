package group22.GrammarClasses;

import group22.DataType;
import group22.SemanticException;
import group22.SyntaxException;
import group22.RuntimeException;
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

    public DataType getDataType() throws SemanticException {
        if (subNodes.size() == 1) {
            JottTree node = subNodes.get(0);

            if (node instanceof Operand) {
                return ((Operand) node).getDataType();
            } else if (node instanceof Bool) {
                return DataType.BOOLEAN;
            } else {
                return DataType.STRING;
            }
        }

        Operand firstOp = (Operand) subNodes.get(0);
        Operand secondOp = (Operand) subNodes.get(subNodes.size() - 1);

        if (firstOp.getDataType() != secondOp.getDataType()) {
            Token firstOpToken = firstOp.getToken();

            throw new SemanticException(
                    "Cannot do math operation between two different data types",
                    firstOpToken.getFilename(),
                    firstOpToken.getLineNum()
            );
        }

        if (subNodes.get(1) instanceof MathOp) {
            return firstOp.getDataType();
        } else {
            return DataType.BOOLEAN;
        }
    }

    @Override
    public String convertToJott() {
        StringBuilder jottCode = new StringBuilder();

        for (JottTree currNode : subNodes) {
            jottCode.append(currNode.convertToJott());
        }

        return jottCode.toString();
    }

    // Validate the expression's tree
    public boolean validateTree() throws SemanticException {
        // Handle the validation of each subNode in the expression
        for (JottTree subNode : subNodes) {
            // Validate individual subnodes
            if (!(subNode.validateTree())) {
                return false;
            }
        }
        // For mathematical or relational operations, ensure operands are compatible
        if (subNodes.size() > 1) {
            Token tokenFirstOp = ((Operand) subNodes.get(0)).getToken();
            DataType leftType = ((Operand) subNodes.get(0)).getDataType();
            DataType rightType = ((Operand) subNodes.get(2)).getDataType();
            // Check for type compatibility between operands
            if (leftType != rightType) {
                throw new SemanticException(
                        "Type mismatch between operands",
                        "", -1
                );
            }
            // For mathematical operations, ensure that operands are numbers (int or double)
            if (leftType == DataType.STRING) {
                throw new SemanticException(
                        "Cannot perform mathematical or relational operations on string type",
                        "", -1
                );
            } else if (subNodes.get(1) instanceof MathOp && leftType == DataType.BOOLEAN) {
                throw new SemanticException(
                        "Cannot perform math operations between two booleans",
                        tokenFirstOp.getFilename(),
                        tokenFirstOp.getLineNum()
                );
            }
        }
        return true;

    }

    private Bool getRelOpValue(String relOp, Operand firstOp, Operand secondOp) throws RuntimeException {
        Token tokenFirstOp = firstOp.getToken();
        JottTree firstValOpNode = firstOp.getValue();
        JottTree secondValOpNode = secondOp.getValue();
        DataType opDataType = Program.scopeManager.getDataType(tokenFirstOp.getToken());

        Boolean firstBool = (firstValOpNode instanceof Bool) ? firstValOpNode.convertToJott().equals("True") : null;
        Boolean secondBool = (secondValOpNode instanceof Bool) ? secondValOpNode.convertToJott().equals("True") : null;

        var firstOpVal = (opDataType == DataType.INTEGER) ? Integer.parseInt(firstValOpNode.convertToJott()) :
                Double.parseDouble(firstValOpNode.convertToJott());
        var secondOpVal = (opDataType == DataType.INTEGER) ? Integer.parseInt(secondValOpNode.convertToJott()) :
                Double.parseDouble(secondValOpNode.convertToJott());

        boolean calcBool;

        // TODO: We have to fix validation since using binary operators such as > or < on booleans isn't valid
        // TODO: Using compareTo with valid Integer or Double in some comparisons
        switch (relOp) {
            // TODO: Figure out if we need fuzzy checking because of doubles
            case ">" -> calcBool = firstOpVal > secondOpVal;
            case ">=" -> calcBool = firstOpVal >= secondOpVal;
            case "<" -> calcBool = firstOpVal < secondOpVal;
            case "<=" -> calcBool = firstOpVal <= secondOpVal;
            case "==" -> {
                if (firstBool != null) {
                    calcBool = firstBool.equals(secondBool);
                } else {
                    calcBool = firstOpVal == secondOpVal;
                }
            }
            case "!=" -> {
                if (firstBool != null) {
                    calcBool = !firstBool.equals(secondBool);
                } else {
                    calcBool = firstOpVal != secondOpVal;
                }
            }
            default -> throw new RuntimeException(
                    "Unknown RelOp of " + relOp + " used",
                    tokenFirstOp.getFilename(),
                    tokenFirstOp.getLineNum()
            );
        }

        String calcBoolStr = String.valueOf(calcBool).substring(0, 1).toUpperCase() +
                String.valueOf(calcBool).substring(1);

        return  new Bool(
                new Token(
                        calcBoolStr,
                        tokenFirstOp.getFilename(),
                        tokenFirstOp.getLineNum(),
                        TokenType.ID_KEYWORD
                )
        );
    }

    private Number getMathOpValue(String mathOp, Operand firstOp, Operand secondOp) throws RuntimeException {
        // If we're in a MathOp, then we know we must be dealing with numbers However, these can be from function
        // calls, IDs, or just hard-coded so we get it into a concrete format with getValue(). Then, we cast both based
        // on the first data type since they've been validated to be the same types
        Token tokenFirstOp = firstOp.getToken();
        Number firstOpNum = (Number) firstOp.getValue();
        Number secondOpNum = (Number) secondOp.getValue();
        DataType opDataType = Program.scopeManager.getDataType(tokenFirstOp.getToken());

        var firstOpVal = (opDataType == DataType.INTEGER) ?
                Integer.parseInt(firstOpNum.convertToJott()) : Double.parseDouble(firstOpNum.convertToJott());
        var secondOpVal = (opDataType == DataType.INTEGER) ?
                Integer.parseInt(secondOpNum.convertToJott()) : Double.parseDouble(secondOpNum.convertToJott());

        switch (mathOp) {
            case "+" -> {
                return new Number(
                        new Token(
                                String.valueOf(firstOpVal + secondOpVal),
                                tokenFirstOp.getFilename(),
                                tokenFirstOp.getLineNum(),
                                TokenType.NUMBER
                        ),
                        opDataType
                );
            }
            case "-" -> {
                return new Number(
                        new Token(
                                String.valueOf(firstOpVal - secondOpVal),
                                tokenFirstOp.getFilename(),
                                tokenFirstOp.getLineNum(),
                                TokenType.NUMBER
                        ),
                        opDataType
                );
            }
            case "*" -> {
                return new Number(
                        new Token(
                                String.valueOf(firstOpVal * secondOpVal),
                                tokenFirstOp.getFilename(),
                                tokenFirstOp.getLineNum(),
                                TokenType.NUMBER
                        ),
                        opDataType
                );
            }
            case "/" -> {
                if (secondOpVal == 0) {
                    throw new RuntimeException(
                            "Cannot divide by zero",
                            tokenFirstOp.getFilename(),
                            tokenFirstOp.getLineNum()
                    );
                }

                return new Number(
                        new Token(
                                String.valueOf(firstOpVal / secondOpVal),
                                tokenFirstOp.getFilename(),
                                tokenFirstOp.getLineNum(),
                                TokenType.NUMBER
                        ),
                        opDataType
                );
            }
            default -> throw new RuntimeException(
                    "Ran into unknown math operation",
                    tokenFirstOp.getFilename(),
                    tokenFirstOp.getLineNum()
            );
        }
    }

    public JottTree getValue() throws RuntimeException {
        if (subNodes.size() != 1) {
            var calcOp = subNodes.get(1);

            if (calcOp instanceof RelOp) {
                return getRelOpValue(calcOp.convertToJott(), (Operand) subNodes.get(0), (Operand) subNodes.get(2));
            } else if (calcOp instanceof MathOp) {
                return getMathOpValue(calcOp.convertToJott(), (Operand) subNodes.get(0), (Operand) subNodes.get(2));
            }
        }

        JottTree valNode = subNodes.get(0);

        if (valNode instanceof Operand) {
            return ((Operand) valNode).getValue();
        } else {
            return valNode;
        }
    }

    @Override
    public void execute() {

    }
}
