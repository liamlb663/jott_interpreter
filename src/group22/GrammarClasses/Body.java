package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class Body implements JottTree {
    ArrayList<BodyStmt> bodyStmts;
    ReturnStmt returnStmt;

    public Body(ArrayList<BodyStmt> bodyStmts, ReturnStmt returnStmt) {
        this.bodyStmts = bodyStmts;
        this.returnStmt = returnStmt;
    }

    public static Body parse(ArrayList<Token> tokens) throws SyntaxException {
        if(tokens.isEmpty()){
            throw new SyntaxException("Unexpected EOF", "", -1);
        }
        ArrayList<BodyStmt> bodyStmts = new ArrayList<>();
        ReturnStmt returnStmt = null;

        Token currToken = tokens.get(0);
        while(currToken.getTokenType().equals(TokenType.ID_KEYWORD)) {
            try {
                BodyStmt bodyStmt = BodyStmt.parse(tokens);
                bodyStmts.add(bodyStmt);
                currToken = tokens.get(0);
            } catch (SyntaxException sE) {
                returnStmt = ReturnStmt.parse(tokens);
                break;
            }
        }

        if (returnStmt == null) {
            throw new SyntaxException("Missing return statement in body", currToken.getFilename(), currToken.getLineNum());
        }

        return new Body(bodyStmts, returnStmt);
    }
    public String convertToJott() {
        StringBuilder sB = new StringBuilder();
        for(BodyStmt bs : bodyStmts) {
            sB.append(bs.convertToJott());
        }
        sB.append(returnStmt.convertToJott());
        return sB.toString();
    }

    public boolean validateTree() {
        //TODO
        return false;
    }

    public void execute() {
        //TODO
    }
}
