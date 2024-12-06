package group22.GrammarClasses;

import group22.DataType;
import group22.SemanticException;
import group22.SyntaxException;
import group22.RuntimeException;
import provided.*;

import java.util.ArrayList;

public class Id implements JottTree {
    final Token id;

    public Id(Token id) {
        this.id = id;
    }

    static Id parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new SyntaxException(
                    "Unexpected EOF",
                    JottParser.getFileName(),
                    JottParser.getLineNumber()
            );
        }

        Token currToken = tokens.remove(0);

        if (currToken.getTokenType() != TokenType.ID_KEYWORD) {
            throw new SyntaxException(
                    "Didn't receive ID_KEYWORD token when parsing for ID",
                    currToken.getFilename(),
                    currToken.getLineNum()
            );
        }

        return new Id(currToken);
    }

    public Token getToken() {
        return id;
    }

    public DataType getDataType() throws SemanticException {
        if (Program.scopeManager.isVarDeclared(id.getToken())) {
            return Program.scopeManager.getDataType(id.getToken());
        } else if (Program.scopeManager.isFunctionDeclared(id.getToken())) {
            return Program.scopeManager.getFunctionReturnType(id.getToken());
        } else {
            throw new SemanticException(
                    id.getToken() + " isn't declared",
                    id.getFilename(),
                    id.getLineNum()
            );
        }
    }

    @Override
    public String convertToJott() {
        return this.id.getToken();
    }

    @Override
    public boolean validateTree() throws SemanticException {
        if (Character.isUpperCase(id.getToken().charAt(0))) {
            throw new SemanticException(
                    "Variable name cannot start with capital letter for " + id.getToken(),
                    id.getFilename(),
                    id.getLineNum()
            );
        }

        return true;
    }

    public JottTree getValue() throws RuntimeException {
        if (!Program.scopeManager.isVarDeclared(id.getToken())) {
            throw new RuntimeException(
                    "Tried to get value of ID that isn't declared yet",
                    id.getFilename(),
                    id.getLineNum()
            );
        }

        Object val = Program.scopeManager.getVariable(id.getToken());

        if (val == null) {
            throw new RuntimeException(
                    "Tried to get value of ID that isn't assigned yet",
                    id.getFilename(),
                    id.getLineNum()
            );
        }

        DataType valType = Program.scopeManager.getDataType(id.getToken());

        switch (valType) {
            case INTEGER -> {
                return new Number(
                        new Token(
                                String.valueOf((int) val),
                                id.getFilename(),
                                id.getLineNum(),
                                TokenType.NUMBER
                        ),
                        valType
                );
            }
            case DOUBLE -> {
                return new Number(
                        new Token(
                                String.valueOf((double) val),
                                id.getFilename(),
                                id.getLineNum(),
                                TokenType.NUMBER
                        ),
                        valType
                );
            }
            case STRING -> {
                return new StringLiteral(
                        new Token(
                                (String) val,
                                id.getFilename(),
                                id.getLineNum(),
                                TokenType.STRING
                        )
                );
            }
            case BOOLEAN -> {
                boolean condVal = (boolean) val;
                String condValStr = String.valueOf(condVal).substring(0, 1).toUpperCase() +
                        String.valueOf(condVal).substring(1);

                return new Bool(
                        new Token(
                                condValStr,
                                id.getFilename(),
                                id.getLineNum(),
                                TokenType.ID_KEYWORD
                        )
                );
            }
            case null, default -> throw new RuntimeException(
                    "Something went HORRIBLY WRONG :OOOO",
                    id.getFilename(),
                    id.getLineNum()
            );
        }
    }

    @Override
    public void execute() {
        // TODO
    }
}
