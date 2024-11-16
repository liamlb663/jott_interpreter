package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottParser;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class Asmt implements JottTree {
    Id id;
    Expr expr;

    public Asmt(Id id, Expr expr) {
        this.id = id;
        this.expr = expr;
    }

    public static Asmt parse(ArrayList<Token> tokens) throws SyntaxException {
        try {
            if(tokens.isEmpty()){
                throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
            }

            Token currToken = tokens.get(0);
            if (!currToken.getTokenType().equals(TokenType.ID_KEYWORD)) {
                throw new SyntaxException("Expected ID but saw " + currToken.getTokenType().toString(), currToken.getFilename(), currToken.getLineNum());
            }
            Id id = Id.parse(tokens);

            currToken = tokens.get(0);
            if (!currToken.getTokenType().equals(TokenType.ASSIGN)) {
                throw new SyntaxException("Expected '=' for assignment", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            currToken = tokens.get(0);
            Expr expr = Expr.parse(tokens);
            currToken = tokens.get(0);
            if (!currToken.getTokenType().equals(TokenType.SEMICOLON)) {
                throw new SyntaxException("Missing semicolon at end of assignment", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);

            return new Asmt(id, expr);
        } catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
    }
    public String convertToJott() {
        return id.convertToJott() + "=" + expr.convertToJott() + ";";
    }

    @Override
    public boolean validateTree() throws SemanticException {
        // Get the variable name from the Id class (use the token's token)
        String varName = id.convertToJott();

        // Check if the variable on the LHS is declared in the current scope
        if (!scopeManager.isVarDeclared(varName)) {
            throw new SemanticException(
                    "Variable " + varName + " is not declared in the current scope",
                    id.id.getFilename(), id.id.getLineNum()
            );
        }

        // Get the type of the LHS variable
        DataType lhsType = scopeManager.getDataType(varName);

        // Validate the RHS expression
        DataType rhsType = expr.getType(); // Assuming Expr has a method to get its type

        // Check if the types are compatible
        if (!lhsType.isCompatible(rhsType)) {
            throw new SemanticException(
                    "Type mismatch in assignment: " + lhsType + " cannot be assigned from " + rhsType,
                    id.id.getFilename(), id.id.getLineNum()
            );
        }

        return true;
    }

    public void execute() {
        //TODO
    }
}
