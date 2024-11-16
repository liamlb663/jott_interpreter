package group22.GrammarClasses;

import group22.DataType;
import group22.ScopeManager;
import group22.SemanticException;
import group22.SyntaxException;
import provided.JottParser;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;
import java.util.List;

import static group22.GrammarClasses.Program.scopeManager;


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

    @Override
    public String convertToJott() {
        StringBuilder jottCode = new StringBuilder();

        for (JottTree currNode: subNodes) {
            jottCode.append(currNode.convertToJott());
        }

        return jottCode.toString();
    }

    public DataType getType() throws SemanticException {
        // Get type of the subnode (operand, literal, or variable)
        JottTree subNode = subNodes.get(0);
        // If the subNode is an ID, fetch its type from the scope
        if (subNodes.size() == 1 && subNode instanceof Id id) {
            String varName = id.id.getToken();  // Get the variable's name
            if (!scopeManager.isVarDeclared(varName)) {
                throw new SemanticException(
                        "Variable " + varName + " is not declared in the current scope",
                        id.id.getFilename(),
                        id.id.getLineNum()
                );
            }
            return scopeManager.getDataType(varName);  // Return the type of the variable from the scope
        }
        // If it's a literal value, determine its type
        else if (subNodes.size() == 1) {
            if (subNode instanceof StringLiteral) {
                return DataType.STRING;  // A string literal
            } else if (subNode instanceof Bool) {
                return DataType.BOOLEAN;  // A boolean literal
            } else if (subNode instanceof Number) {
                String numValue = ((Number) subNode).number.getToken();  // Get the string representation of the number
                // Check if the number contains a decimal point (double)
                if (numValue.contains(".")) {
                    return DataType.DOUBLE;
                } else {
                    return DataType.INTEGER;
                }
            }
        }
        return DataType.VOID;
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
            DataType leftType = subNodes.get(0).getType();
            DataType rightType = subNodes.get(2).getType();
            // Check for type compatibility between operands
            if (leftType != rightType) {
                throw new SemanticException(
                        "Type mismatch between operands",
                        "", -1
                );
            }
            // For mathematical operations, ensure that both operands are numbers (int or double)
            if (leftType == DataType.STRING || rightType == DataType.STRING) {
                throw new SemanticException(
                        "Cannot perform mathematical or relational operations on string type",
                        "", -1
                );
            }
        }
        return true;
    }


    @Override
    public void execute() {

    }
}
