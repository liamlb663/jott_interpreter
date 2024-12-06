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
        switch (Program.scopeManager.getDataType(id.getToken())) {
            case DOUBLE, INTEGER -> {
                return new Number(id, Program.scopeManager.getDataType(id.getToken()));
            }
            case STRING -> {
                return new StringLiteral(id);
            }
            case BOOLEAN -> {
                return new Bool(id);
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
