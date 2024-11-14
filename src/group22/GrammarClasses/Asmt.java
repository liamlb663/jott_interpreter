package group22.GrammarClasses;

import group22.DataType;
import group22.SemanticException;
import group22.SyntaxException;
import provided.*;

import java.util.ArrayList;
import java.util.HashMap;

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

            Token checkIdToken = tokens.get(0);
            if (!checkIdToken.getTokenType().equals(TokenType.ID_KEYWORD)) {
                throw new SyntaxException(
                        "Expected ID but saw " + checkIdToken.getTokenType().toString(),
                        checkIdToken.getFilename(),
                        checkIdToken.getLineNum()
                );
            }
            Id id = Id.parse(tokens);

            Token checkAssignToken = tokens.get(0);
            if (!checkAssignToken.getTokenType().equals(TokenType.ASSIGN)) {
                throw new SyntaxException(
                        "Expected '=' for assignment",
                        checkAssignToken.getFilename(),
                        checkAssignToken.getLineNum()
                );
            }
            tokens.remove(0);

            Expr expr = Expr.parse(tokens);

            Token checkSemiToken = tokens.get(0);
            if (!checkSemiToken.getTokenType().equals(TokenType.SEMICOLON)) {
                throw new SyntaxException(
                        "Missing semicolon at end of assignment",
                        checkSemiToken.getFilename(),
                        checkSemiToken.getLineNum()
                );
            }
            tokens.remove(0);

            return new Asmt(id, expr);
        } catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
    }

    public boolean validate() throws SemanticException {
        // TODO
    }

    public String convertToJott() {
        return id.convertToJott() + "=" + expr.convertToJott() + ";";
    }

    public boolean validateTree(
            HashMap<String, DataType> functions,
            HashMap<String, HashMap<String, DataType>> variables,
            String currentScope
    ) throws SemanticException {
        boolean idValid = id.validateTree(functions, variables, currentScope);
        boolean exprValid = expr.validateTree(functions, variables, currentScope);

        if ()
            return false;
    }

    public void execute() {
        //TODO
    }
}
