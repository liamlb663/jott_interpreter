package group22.GrammarClasses;

import group22.DataType;
import group22.SemanticException;
import group22.SyntaxException;
import provided.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Id implements JottTree {
    private final Token id;

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

    public boolean validate() throws SemanticException {
        if (!JottValidator.scopeManager.isVarAvailable(id.getToken())) {
            throw new SemanticException(
                    id.getToken() + " isn't defined in assignment",
                    id.getFilename(),
                    id.getLineNum()
            );
        }

        return true;
    }

    public DataType getIdDatatype() throws SemanticException {
        validate();

        //
    }

    @Override
    public String convertToJott() {
        return this.id.getToken();
    }

    @Override
    public boolean validateTree(
            HashMap<String, DataType> functions,
            HashMap<String, HashMap<String, DataType>> variables,
            String currentScope
    ) throws SemanticException {
        HashMap<String, DataType> currScopeVars = variables.get(currentScope);

        if (currScopeVars.get(id.getToken()) == null) {
            throw new SemanticException(
                    id.getToken() + " isn't defined in assignment",
                    id.getFilename(),
                    id.getLineNum()
            );
        }

        return true;
    }

    @Override
    public void execute() {
        // TODO
    }
}
