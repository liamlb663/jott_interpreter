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

    private Data getRelOpValue(String relOp) throws RuntimeException {
        Data firstVal = subNodes.get(0).execute();
        Data secondVal = subNodes.get(2).execute();

        boolean calcBool;

        // TODO: We have to fix validation since using binary operators such as > or < on booleans isn't valid
        switch (relOp) {
            // TODO: Figure out if we need fuzzy checking because of doubles
            case ">" -> {
                if (firstVal.type == DataType.INTEGER) {
                    calcBool = ((int) firstVal.value) > ((int) secondVal.value);
                } else {
                    calcBool = ((double) firstVal.value) > ((double) secondVal.value);
                }
            }
            case ">=" -> {
                if (firstVal.type == DataType.INTEGER) {
                    calcBool = ((int) firstVal.value) >= ((int) secondVal.value);
                } else {
                    calcBool = ((double) firstVal.value) >= ((double) secondVal.value);
                }
            }
            case "<" -> {
                if (firstVal.type == DataType.INTEGER) {
                    calcBool = ((int) firstVal.value) < ((int) secondVal.value);
                } else {
                    calcBool = ((double) firstVal.value) < ((double) secondVal.value);
                }
            }
            case "<=" -> {
                if (firstVal.type == DataType.INTEGER) {
                    calcBool = ((int) firstVal.value) <= ((int) secondVal.value);
                } else {
                    calcBool = ((double) firstVal.value) <= ((double) secondVal.value);
                }
            }
            case "==" -> {
                if (firstVal.type == DataType.BOOLEAN) {
                    calcBool = ((boolean) firstVal.value) == (boolean) secondVal.value;
                } else if (firstVal.type == DataType.STRING) {
                    calcBool = ((String) firstVal.value).equals((String) secondVal.value);
                } else if (firstVal.type == DataType.INTEGER) {
                    calcBool = ((int) firstVal.value) == ((int) secondVal.value);
                } else {
                    calcBool = ((double) firstVal.value) == ((double) secondVal.value);
                }
            }
            case "!=" -> {
                if (firstVal.type == DataType.BOOLEAN) {
                    calcBool = ((boolean) firstVal.value) ^ (boolean) secondVal.value; // this will XOR it
                } else if (firstVal.type == DataType.STRING) {
                    calcBool = !((String) firstVal.value).equals((String) secondVal.value);
                } else if (firstVal.type == DataType.INTEGER) {
                    calcBool = ((int) firstVal.value) != ((int) secondVal.value);
                } else {
                    calcBool = ((double) firstVal.value) != ((double) secondVal.value);
                }
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
        // If we're in a MathOp, then we know we must be dealing with numbers However, these can be from function
        // calls, IDs, or just hard-coded so we get it into a concrete format with getValue(). Then, we cast both based
        // on the first data type since they've been validated to be the same types
        Data firstOpNum = subNodes.get(0).execute();
        Data secondOpNum = subNodes.get(2).execute();

        var firstOpVal = (firstOpNum.type == DataType.INTEGER) ? (int) firstOpNum.value : (double) firstOpNum.value;
        var secondOpVal = (secondOpNum.type == DataType.INTEGER) ? (int) secondOpNum.value : (double) secondOpNum.value;

        switch (mathOp) {
            case "+" -> {
                return new Data(
                        firstOpVal + secondOpVal,
                        firstOpNum.type,
                        firstOpNum.fileName,
                        firstOpNum.lineNumber
                );
            }
            case "-" -> {
                return new Data(
                        firstOpVal - secondOpVal,
                        firstOpNum.type,
                        firstOpNum.fileName,
                        firstOpNum.lineNumber
                );
            }
            case "*" -> {
                return new Data(
                        firstOpVal * secondOpVal,
                        firstOpNum.type,
                        firstOpNum.fileName,
                        firstOpNum.lineNumber
                );
            }
            case "/" -> {
                if (secondOpVal == 0) {
                    throw new RuntimeException(
                            "Cannot divide by zero",
                            firstOpNum.fileName,
                            firstOpNum.lineNumber
                    );
                }

                return new Data(
                        firstOpVal / secondOpVal,
                        firstOpNum.type,
                        firstOpNum.fileName,
                        firstOpNum.lineNumber
                );
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
