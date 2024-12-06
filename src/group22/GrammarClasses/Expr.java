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
            } else if (subNodes.get(1) instanceof RelOp && leftType == DataType.BOOLEAN) {
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

        var firstOpVal = (firstValOpNode instanceof Bool) ? firstValOpNode.convertToJott().equals("True") :
                (opDataType == DataType.INTEGER) ? Integer.parseInt(firstValOpNode.convertToJott()) :
                        Double.parseDouble(firstValOpNode.convertToJott());
        var secondOpVal = (secondValOpNode instanceof Bool) ? secondValOpNode.convertToJott().equals("True") :
                (opDataType == DataType.INTEGER) ? Integer.parseInt(secondValOpNode.convertToJott()) :
                        Double.parseDouble(secondValOpNode.convertToJott());

        // TODO: We have to fix validation since using binary operators such as > or < on booleans isn't valid
        // TODO: Using compareTo with valid Integer or Double in some comparisons
        switch (relOp) {
            case ">" -> {
                boolean calcBool = firstOpVal.compareTo(secondOpVal);
            }
            case ">=" -> {
                //
            }
            case "<" -> {
                //
            }
            case "<=" -> {
                //
            }
            case "==" -> {
                boolean calcBool = firstOpVal.equals(secondOpVal);

                return new Bool(
                        new Token(
                                String.valueOf(calcBool),
                                tokenFirstOp.getFilename(),
                                tokenFirstOp.getLineNum(),
                                TokenType.ID_KEYWORD
                        )
                );
            }
            case "!=" -> {
                //
            }
        }


        JottTree a = ((Operand) firstOp).getSubNode();
        JottTree b = ((Operand) secondOp).getSubNode();

        Object aVal = null;
        DataType aType = null;
        Object bVal = null;
        DataType bType = null;

        String filename = "";
        int lineNumber = -1;

        if (a instanceof Id aID) {
            filename = aID.getToken().getFilename();
            lineNumber = aID.getToken().getLineNum();
            if (Program.scopeManager.isVarDeclared(aID.getToken().getToken())) {
                aVal = Program.scopeManager.getVariable(aID.getToken().getToken());
                if(aVal == null) {
                    throw new RuntimeException("Attempting to retrieve value of unassigned variable", id.getToken().getFilename(), id.getToken().getLineNum());
                }
                aType = Program.scopeManager.getDataType(aID.getToken().getToken());
            } else {
                throw new RuntimeException(String.format("Variable %s not defined in current scope", aID.getToken().getToken()), filename, lineNumber);
            }
        } else if (a instanceof Number aNum) {
            filename = aNum.getToken().getFilename();
            lineNumber = aNum.getToken().getLineNum();
            aVal = aNum.getValue();
            aType = aNum.getDataType();
        } else if (a instanceof FuncCall aFC) {
            filename = aFC.getToken().getFilename();
            lineNumber = aFC.getToken().getLineNum();
            if (Program.scopeManager.isFunctionDeclared(aFC.getToken().getToken())) {
                aVal = aFC.evaluateFunction();
                aType = Program.scopeManager.getFunctionReturnType(aFC.getToken().getToken());
            } else {
                throw new RuntimeException(String.format("Function %s not defined in current scope", aFC.getToken().getToken()), filename, lineNumber);
            }
        }

        if (b instanceof Id bID) {
            if (Program.scopeManager.isVarDeclared(bID.getToken().getToken())) {
                bVal = Program.scopeManager.getVariable(bID.getToken().getToken());
                if(bVal == null) {
                    throw new RuntimeException("Attempting to retrieve value of unassigned variable", bID.getToken().getFilename(), bID.getToken().getLineNum());
                }
                bType = Program.scopeManager.getDataType(bID.getToken().getToken());
            } else {
                throw new RuntimeException(String.format("Variable %s not defined in current scope", bID.getToken().getToken()), filename, lineNumber);
            }
        } else if (b instanceof Number) {
            bVal = ((Number) b).getValue();
            bType = ((Number) b).getDataType();
        } else if (a instanceof FuncCall) {
            FuncCall bFC = (FuncCall) b;
            if (Program.scopeManager.isFunctionDeclared(bFC.getToken().getToken())) {
                bVal = bFC.evaluateFunction();
                bType = Program.scopeManager.getFunctionReturnType(bFC.getToken().getToken());
            } else {
                throw new RuntimeException(String.format("Function %s not defined in current scope", bFC.getToken().getToken()), filename, lineNumber);
            }
        }

        if (aType == null || bType == null || aVal == null || bVal == null) {
            return new Bool(new Token(String.valueOf(false), filename, lineNumber, TokenType.ID_KEYWORD));
        }

        if (!aType.equals(bType)) {
            return new Bool(new Token(String.valueOf(false), filename, lineNumber, TokenType.ID_KEYWORD));
        }
        switch (relOp) {
            case "==":
                return new Bool(new Token(String.valueOf(aVal.equals(bVal)), filename, lineNumber, TokenType.ID_KEYWORD));
            case "<=":
                if (aType.equals(DataType.BOOLEAN) || aType.equals(DataType.STRING)) {
                    return new Bool(new Token(String.valueOf(false), filename, lineNumber, TokenType.ID_KEYWORD));
                } else if (aType.equals(DataType.DOUBLE)) {
                    return new Bool(new Token(String.valueOf((double) aVal <= (double) bVal), filename, lineNumber, TokenType.ID_KEYWORD));
                } else if (aType.equals(DataType.INTEGER)) {
                    return new Bool(new Token(String.valueOf((int) aVal <= (int) bVal), filename, lineNumber, TokenType.ID_KEYWORD));
                }
                break;
            case ">=":
                if (aType.equals(DataType.BOOLEAN) || aType.equals(DataType.STRING)) {
                    return new Bool(new Token(String.valueOf(false), filename, lineNumber, TokenType.ID_KEYWORD));
                } else if (aType.equals(DataType.DOUBLE)) {
                    return new Bool(new Token(String.valueOf((double) aVal >= (double) bVal), filename, lineNumber, TokenType.ID_KEYWORD));
                } else if (aType.equals(DataType.INTEGER)) {
                    return new Bool(new Token(String.valueOf((int) aVal >= (int) bVal), filename, lineNumber, TokenType.ID_KEYWORD));
                }
                break;
        }
        return new Bool(new Token(String.valueOf(false), filename, lineNumber, TokenType.ID_KEYWORD));
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
            JottTree sub = ((Operand) valNode).getSubNode();

            if(sub instanceof Number num) {
                return num;
            }
            else if (sub instanceof Id id) {
                if (Program.scopeManager.isVarDeclared(id.getToken().getToken())) {
                    DataType type = Program.scopeManager.getDataType(id.convertToJott());
                    var variable = Program.scopeManager.getVariable(id.convertToJott());
                    if(variable == null) {
                        throw new RuntimeException("Attempting to retrieve value of unassigned variable", id.getToken().getFilename(), id.getToken().getLineNum());
                    }
                    switch (type) {
                        case BOOLEAN:
                            return new Bool(new Token(variable.toString(), id.getToken().getFilename(), id.getToken().getLineNum(), TokenType.ID_KEYWORD));
                        case DOUBLE:
                            return new Number(new Token(variable.toString(), id.getToken().getFilename(), id.getToken().getLineNum(), TokenType.NUMBER), DataType.DOUBLE);
                        case INTEGER:
                            return new Number(new Token(variable.toString(), id.getToken().getFilename(), id.getToken().getLineNum(), TokenType.NUMBER), DataType.INTEGER);
                        case STRING:
                            return new StringLiteral(new Token(variable.toString(), id.getToken().getFilename(), id.getToken().getLineNum(), TokenType.STRING));
                    }
                } else {
                    throw new RuntimeException(String.format("Variable not defined in scope for expression: %s", convertToJott()));
                }
            }
            else if (sub instanceof FuncCall fc) {
                if (Program.scopeManager.isFunctionDeclared(fc.getToken().getToken())) {
                    return fc.evaluateFunction();
                } else {
                    throw new RuntimeException(String.format("Function not defined in scope for expression: %s", convertToJott()));
                }
            }
        } else if (valNode instanceof StringLiteral) {
            return valNode;
        } else {
            return valNode;
        }
    }

    @Override
    public void execute() {

    }
}
