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

    public boolean validateTree() {
        //TODO
        return false;
    }

    public void execute() {
        //TODO
    }
}
