package group22.GrammarClasses;
import group22.ScopeManager;
import group22.SyntaxException;
import provided.*;

import java.util.ArrayList;

public class Else implements JottTree{
    private final Body bodyNode;
    public final String filename;
    public final int lineNumber;

    public Else(Body bodyNode, String filename, int lineNumber) {
        this.bodyNode = bodyNode;
        this.filename = filename;
        this.lineNumber = lineNumber;
    }

    static Else parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
        try {
            Token currToken = tokens.get(0);
            var filename = currToken.getFilename();
            var lineNumber = currToken.getLineNum();
            if (!(currToken.getTokenType() == TokenType.ID_KEYWORD && currToken.getToken().equals("Else"))) {
                return new Else(null, filename, lineNumber);
            }
            tokens.remove(0);
            currToken = tokens.get(0);
            if (!(currToken.getTokenType() == TokenType.L_BRACE && currToken.getToken().equals("{"))) {
                throw new SyntaxException("Expected left brace", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            Body bodyNode = Body.parse(tokens);
            currToken = tokens.get(0);
            if (!(currToken.getTokenType() == TokenType.R_BRACE && currToken.getToken().equals("}"))) {
                throw new SyntaxException("Expected right brace", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            return new Else(bodyNode, filename, lineNumber);
        } catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
    }

    public boolean hasReturnStmt() {
//        for (BodyStmt bs : bodyNode.bodyStmts) {
//            if (bs.subNode instanceof IfStmt) {
//                var ifStmt = (IfStmt) bs.subNode;
//                if (ifStmt.elseNode)
//            }
//        }

        return bodyNode.returnStmt != null;
    }

    public String convertToJott() {
        if (bodyNode == null) {
            return "";
        }
        return ("Else{" + bodyNode.convertToJott() + "}");
    }

    public boolean validateTree(ScopeManager sm) {
        return bodyNode.validateTree(sm);
    }

    public void execute() {
        //TODO
    }
}
