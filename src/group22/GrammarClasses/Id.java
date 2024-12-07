package group22.GrammarClasses;

import group22.*;
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

    @Override
    public Data execute() throws RuntimeException{
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

        return new Data(
                val,
                valType,
                id.getFilename(),
                id.getLineNum()
        );
    }
}
