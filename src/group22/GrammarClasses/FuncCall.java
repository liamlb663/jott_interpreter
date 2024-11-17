package group22.GrammarClasses;
import group22.DataType;
import group22.SyntaxException;
import group22.SemanticException;
import group22.GrammarClasses.Program;
import provided.*;

import java.util.ArrayList;

public class FuncCall implements JottTree{
    private final Id idNode;
    private final Params paramsNode;

    public FuncCall(Id idNode, Params paramsNode) {
        this.idNode = idNode;
        this.paramsNode = paramsNode;
    }

    static FuncCall parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
        try {
            Token currToken = tokens.get(0);
            if (currToken.getTokenType() != TokenType.FC_HEADER) {
                throw new SyntaxException("Should be a fc_header", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            Id idNode = Id.parse(tokens);
            currToken = tokens.get(0);
            if (currToken.getTokenType() != TokenType.L_BRACKET) {
                throw new SyntaxException("Should be a left bracket", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            Params paramsNode = Params.parse(tokens);
            currToken = tokens.get(0);
            if (currToken.getTokenType() != TokenType.R_BRACKET) {
                throw new SyntaxException("Should be a right bracket", currToken.getFilename(), currToken.getLineNum());
            }
            tokens.remove(0);
            return new FuncCall(idNode, paramsNode);
        } catch (IndexOutOfBoundsException e) {
            throw new SyntaxException("Unexpected EOF", JottParser.getFileName(), JottParser.getLineNumber());
        }
    }

    public DataType getDataType() throws SemanticException {
        Token idToken = idNode.getToken();

        if (!Program.scopeManager.isFunctionDeclared(idToken.getToken())) {
            throw new SemanticException(
                    idToken.getToken() + " is being called as a function, but isn't defined",
                    idToken.getFilename(),
                    idToken.getLineNum()
            );
        }

        return Program.scopeManager.getFunctionReturnType(idNode.convertToJott());
    }

    public Token getToken() {
        return idNode.getToken();
    }

    public String convertToJott() {
        return ("::" + this.idNode.convertToJott() + "[" + this.paramsNode.convertToJott() + "]");
    }

    public boolean validateTree() throws SemanticException {

        if (!Program.scopeManager.isFunctionDeclared(idNode.convertToJott())) {
            throw new SemanticException("Function " + idNode.convertToJott() + " not found!", idNode.id.getFilename(), idNode.id.getLineNum());
        }

        if (Program.scopeManager.getFunctionParameterTypes(idNode.convertToJott()) != paramsNode.getTypes()) {
            throw new SemanticException("Function " + idNode.convertToJott() + " called with wrong Parameters!", idNode.id.getFilename(), idNode.id.getLineNum());
        }

        return false;
    }

    public void execute() {
        //TODO
    }
}
