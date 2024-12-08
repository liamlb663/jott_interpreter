package group22.GrammarClasses;

import group22.*;
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
            DataType leftType = ((Operand) subNodes.get(0)).getDataType();
            DataType rightType = ((Operand) subNodes.get(2)).getDataType();
            Token leftToken = ((Operand) subNodes.get(0)).getToken();
            // Check for type compatibility between operands
            if (leftType != rightType) {
                throw new SemanticException(
                        "Type mismatch between operands",
                        leftToken.getFilename(),
                        leftToken.getLineNum()
                );
            }
            if (leftType == DataType.STRING || leftType == DataType.BOOLEAN) {
                // For mathematical operations, ensure that operands are numbers (int or double)
                if (subNodes.get(1) instanceof MathOp) {
                    throw new SemanticException(
                            "Cannot perform mathematical operations on string or boolean type",
                            leftToken.getFilename(),
                            leftToken.getLineNum()
                    );
                }
                // If relational operation, booleans and strings can only do equality comparisons
                else {
                    RelOp comparison = (RelOp) subNodes.get(1);
                    String comparisonString = comparison.getToken().getToken();
                    if (!(comparisonString.equals("==") || comparisonString.equals("!="))) {
                        throw new SemanticException(
                                "Cannot perform non-equality relational operations on string or boolean type",
                                leftToken.getFilename(),
                                leftToken.getLineNum()
                        );
                    }
                }
            }
        }
        return true;

    }

    private Data getRelOpValue(String relOp) throws RuntimeException {
        Data firstVal = subNodes.get(0).execute();
        Data secondVal = subNodes.get(2).execute();

        boolean calcBool;

        // TODO: We have to fix validation since using binary operators such as > or < on booleans isn't valid
        switch (relOp) {
            // TODO: Figure out if we need fuzzy checking because of doubles
            case ">" -> {
                if (firstVal.type == DataType.INTEGER) {
                    calcBool = ((Integer) firstVal.value) > ((Integer) secondVal.value);
                } else {
                    calcBool = ((Double) firstVal.value) > ((Double) secondVal.value);
                }
            }
            case ">=" -> {
                if (firstVal.type == DataType.INTEGER) {
                    calcBool = ((Integer) firstVal.value) >= ((Integer) secondVal.value);
                } else {
                    calcBool = ((Double) firstVal.value) >= ((Double) secondVal.value);
                }
            }
            case "<" -> {
                if (firstVal.type == DataType.INTEGER) {
                    calcBool = ((Integer) firstVal.value) < ((Integer) secondVal.value);
                } else {
                    calcBool = ((Double) firstVal.value) < ((Double) secondVal.value);
                }
            }
            case "<=" -> {
                if (firstVal.type == DataType.INTEGER) {
                    calcBool = ((Integer) firstVal.value) <= ((Integer) secondVal.value);
                } else {
                    calcBool = ((Double) firstVal.value) <= ((Double) secondVal.value);
                }
            }
            case "==" -> {
                calcBool = firstVal.value.equals(secondVal.value);
            }
            case "!=" -> {
                calcBool = !firstVal.value.equals(secondVal.value);
            }
            default -> throw new RuntimeException(
                    "Unknown RelOp of " + relOp + " used",
                    firstVal.fileName,
                    firstVal.lineNumber
            );
        }

        return new Data(
                calcBool,
                firstVal.type,
                firstVal.fileName,
                firstVal.lineNumber
        );
    }

    private Data getMathOpValue(String mathOp) throws RuntimeException {
        Data firstOpNum = subNodes.get(0).execute();
        Data secondOpNum = subNodes.get(2).execute();

        switch (mathOp) {
            case "+" -> {
                if (firstOpNum.type == DataType.INTEGER) {
                    return new Data(
                            ((Integer) firstOpNum.value) + ((Integer) secondOpNum.value),
                            firstOpNum.type,
                            firstOpNum.fileName,
                            firstOpNum.lineNumber
                    );
                } else {
                    return new Data(
                            ((Double) firstOpNum.value) + ((Double) secondOpNum.value),
                            firstOpNum.type,
                            firstOpNum.fileName,
                            firstOpNum.lineNumber
                    );
                }
            }
            case "-" -> {
                if (firstOpNum.type == DataType.INTEGER) {
                    return new Data(
                            ((Integer) firstOpNum.value) - ((Integer) secondOpNum.value),
                            firstOpNum.type,
                            firstOpNum.fileName,
                            firstOpNum.lineNumber
                    );
                } else {
                    return new Data(
                            ((Double) firstOpNum.value) - ((Double) secondOpNum.value),
                            firstOpNum.type,
                            firstOpNum.fileName,
                            firstOpNum.lineNumber
                    );
                }
            }
            case "*" -> {
                if (firstOpNum.type == DataType.INTEGER) {
                    return new Data(
                            ((Integer) firstOpNum.value) * ((Integer) secondOpNum.value),
                            firstOpNum.type,
                            firstOpNum.fileName,
                            firstOpNum.lineNumber
                    );
                } else {
                    return new Data(
                            ((Double) firstOpNum.value) * ((Double) secondOpNum.value),
                            firstOpNum.type,
                            firstOpNum.fileName,
                            firstOpNum.lineNumber
                    );
                }
            }
            case "/" -> {
                if (firstOpNum.type == DataType.INTEGER) {
                    if (((Integer) secondOpNum.value) == 0) {
                        throw new RuntimeException(
                                "Cannot divide by zero",
                                firstOpNum.fileName,
                                firstOpNum.lineNumber
                        );
                    }

                    return new Data(
                            ((Integer) firstOpNum.value) / ((Integer) secondOpNum.value),
                            firstOpNum.type,
                            firstOpNum.fileName,
                            firstOpNum.lineNumber
                    );
                } else {
                    if (((Double) secondOpNum.value) == 0) {
                        throw new RuntimeException(
                                "Cannot divide by zero",
                                firstOpNum.fileName,
                                firstOpNum.lineNumber
                        );
                    }

                    return new Data(
                            ((Double) firstOpNum.value) / ((Double) secondOpNum.value),
                            firstOpNum.type,
                            firstOpNum.fileName,
                            firstOpNum.lineNumber
                    );
                }
            }
            default -> throw new RuntimeException(
                    "Ran into unknown math operation",
                    firstOpNum.fileName,
                    firstOpNum.lineNumber
            );
        }
    }

    @Override
    public Data execute() throws RuntimeException {
        if (subNodes.size() != 1) {
            var calcOp = subNodes.get(1);

            if (calcOp instanceof RelOp) {
                return getRelOpValue(calcOp.convertToJott());
            } else if (calcOp instanceof MathOp) {
                return getMathOpValue(calcOp.convertToJott());
            }
        }

        return subNodes.get(0).execute();
    }
}
