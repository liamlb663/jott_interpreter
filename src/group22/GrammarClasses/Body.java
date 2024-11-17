package group22.GrammarClasses;

import group22.DataType;
import group22.SemanticException;
import group22.SyntaxException;
import provided.JottParser;
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
        try {
            if (tokens.isEmpty()) {
                throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
            }
            ArrayList<BodyStmt> bodyStmts = new ArrayList<>();
            ReturnStmt returnStmt = null;

            Token currToken = tokens.get(0);
            while ((currToken.getTokenType().equals(TokenType.ID_KEYWORD) && !currToken.getToken().equals("Return")) || currToken.getTokenType().equals(TokenType.FC_HEADER)) {
                BodyStmt bodyStmt = BodyStmt.parse(tokens);
                bodyStmts.add(bodyStmt);
                currToken = tokens.get(0);
            }
            returnStmt = ReturnStmt.parse(tokens);

            if (returnStmt == null) {
                throw new SyntaxException("Missing return statement in body", currToken.getFilename(), currToken.getLineNum());
            }

            return new Body(bodyStmts, returnStmt);
        }
        catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
    }
    public String convertToJott() {
        StringBuilder sB = new StringBuilder();
        for(BodyStmt bs : bodyStmts) {
            sB.append(bs.convertToJott());
        }
        sB.append(returnStmt.convertToJott());
        return sB.toString();
    }

    public boolean validateTree() throws SemanticException {
        boolean returnOK = false;
        for (BodyStmt b : bodyStmts) {
            b.validateTree();
        }
        returnStmt.validateTree();
        if (!Program.scopeManager.getCurrentReturnType().equals(DataType.VOID)) {
            if (returnStmt.expr != null) {
                returnOK = true;
            }
            else {
                for (BodyStmt b : bodyStmts) {
                    if (b.subNode instanceof IfStmt) {
                        if (((IfStmt) b.subNode).willReturn()) {
                            returnOK = true;
                        }
                    }
                }
            }
        }
        else {
            returnOK = true;
        }
        if (!returnOK) {
            throw new SemanticException("Function will not always return a value");
        }

        return true;
    }

    public void execute() {
        //TODO
    }
}
