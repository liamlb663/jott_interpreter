package group22.GrammarClasses;

import group22.DataType;
import group22.SemanticException;
import group22.SyntaxException;
import provided.JottParser;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;
import java.util.Dictionary;
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

        for (JottTree currNode: subNodes) {
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
            }
        }
        return true;

    }

    private boolean getRelOpValue(String relOp, JottTree aJ, JottTree bJ) throws SemanticException {
        JottTree a = ((Operand) aJ).getSubNode();
        JottTree b = ((Operand) bJ).getSubNode();

        Object aVal = null;
        DataType aType = null;
        Object bVal = null;
        DataType bType = null;

        if (a instanceof Id aID) {
            if (Program.scopeManager.isVarDeclared(aID.getToken().getToken())) {
                aVal = Program.scopeManager.getVariable(aID.getToken().getToken());
                aType = Program.scopeManager.getDataType(aID.getToken().getToken());
            } else {
                throw new SemanticException(String.format("Variable %s not defined in current scope", aID.getToken().getToken()), "", -1);
            }
        }
        else if (a instanceof Number) {
            aVal = ((Number) a).getValue();
            aType = ((Number) b).getDataType();
        }
        else if (a instanceof FuncCall aFC) {
            if (Program.scopeManager.isFunctionDeclared(aFC.getToken().getToken())) {
                aVal = Program.scopeManager.evaluateFunction(aFC);
                aType = Program.scopeManager.getFunctionReturnType(aFC.getToken().getToken());
            } else {
                throw new SemanticException(String.format("Function %s not defined in current scope", aFC.getToken().getToken()), "", -1);
            }
        }

        if (b instanceof Id bID) {
            if (Program.scopeManager.isVarDeclared(bID.getToken().getToken())) {
                bVal = Program.scopeManager.getVariable(bID.getToken().getToken());
                bType = Program.scopeManager.getDataType(bID.getToken().getToken());
            } else {
                throw new SemanticException(String.format("Variable %s not defined in current scope", bID.getToken().getToken()), "", -1);
            }
        }
        else if (b instanceof Number) {
            bVal = ((Number) b).getValue();
            bType = ((Number) b).getDataType();
        }
        else if (a instanceof FuncCall) {
            FuncCall bFC = (FuncCall) b;
            if (Program.scopeManager.isFunctionDeclared(bFC.getToken().getToken())) {
                bVal = Program.scopeManager.evaluateFunction(bFC);
                bType = Program.scopeManager.getFunctionReturnType(bFC.getToken().getToken());
            } else {
                throw new SemanticException(String.format("Function %s not defined in current scope", bFC.getToken().getToken()), "", -1);
            }
        }

        if(aType == null || bType == null || aVal == null || bVal == null) {
            return false;
        }

        if(!aType.equals(bType)) {
            return false;
        }
        switch(relOp){
            case "==":
                return aVal.equals(bVal);
                break;
            case "<=":
                if(aType.equals(DataType.BOOLEAN) || aType.equals(DataType.STRING)) {
                    return false; // semantic error?
                }
                else if(aType.equals(DataType.DOUBLE)) {
                    return (double)aVal <= (double)bVal;
                }
                else if(aType.equals(DataType.INTEGER)) {
                    return (int)aVal <= (int)bVal;
                }
                break;
            case ">=":
                if(aType.equals(DataType.BOOLEAN) || aType.equals(DataType.STRING)) {
                    return false; // semantic error?
                }
                else if(aType.equals(DataType.DOUBLE)) {
                    return (double)aVal >= (double)bVal;
                }
                else if(aType.equals(DataType.INTEGER)) {
                    return (int)aVal >= (int)bVal;
                }
                break;
        }

        return false;
    }

    // list[0] == type; list[1] == value;
    public List<Object> getValue() throws SemanticException {
        var list = new ArrayList<>();
        if(subNodes.size() == 1) {
            JottTree node = subNodes.get(0);
            if(node instanceof Operand) {
                return ((Operand) node).getValue();
            }
            else if (node instanceof StringLiteral) {
                list.add(DataType.STRING);
                list.add(((StringLiteral) node).getValue());
                return list;
            }
            else if (node instanceof Bool) {
                list.add(DataType.BOOLEAN);
                list.add(((Bool) node).getValue());
                return list;
            }
        }
        else {
            var op = subNodes.get(1);
            if (op instanceof RelOp) {
                boolean val = getRelOpValue(((RelOp) op).getToken().getToken(), subNodes.get(0), subNodes.get(2));
                list.add(DataType.BOOLEAN);
                list.add(val);
                return list;
            }
            else if (op instanceof MathOp) {
                var val = getMathOpValue(((MathOp) op).getValue(), subNodes.get(0), subNodes.get(2));
            }
        }
    }

    @Override
    public void execute() {

    }
}
