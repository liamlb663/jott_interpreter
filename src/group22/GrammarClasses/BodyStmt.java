package group22.GrammarClasses;

import group22.SyntaxException;
import provided.JottTree;
import provided.Token;
import provided.TokenType;

import java.util.ArrayList;

public class BodyStmt implements JottTree {
    public final JottTree subNode;
    public BodyStmt(JottTree node) {
        this.subNode = node;
    }

    static BodyStmt parse(ArrayList<Token> tokens) throws SyntaxException {
        if (tokens.isEmpty()) {
            throw new UnknownError("Unexpected EOF when parsing Operand");
        }

        Token currentToken = tokens.get(0);

        try {
            switch (currentToken.getTokenType()) {
                case ID_KEYWORD -> {
                    Token nextToken = tokens.get(1);
                    if (nextToken.getTokenType().equals(TokenType.ASSIGN)) {
                        return new BodyStmt(Asmt.parse(tokens));
                    } else if (currentToken.getToken().equals("If")){
                        return new BodyStmt(IfStmt.parse(tokens));
                    } else if (currentToken.getToken().equals("While")){
                        return new BodyStmt(WhileLoop.parseWhileLoop(tokens));
                    } else {
                        throw new SyntaxException(
                            "Invalid token after ID keyword when parsing BodyStmt",
                            currentToken.getFilename(),
                            currentToken.getLineNum()
                        );
                    }
                }
                case FC_HEADER -> {
                    FuncCall funcCall = FuncCall.parse(tokens);
                    currentToken = tokens.get(0);
                    if (!currentToken.getTokenType().equals(TokenType.SEMICOLON)) {
                        throw new SyntaxException(
                                "Expected semicolon",
                                currentToken.getFilename(),
                                currentToken.getLineNum()
                        );
                    }
                    tokens.remove(0);
                    return new BodyStmt(funcCall);
                }
                default -> throw new SyntaxException(
                    "Invalid token found when parsing BodyStmt",
                    currentToken.getFilename(),
                    currentToken.getLineNum()
                );
            }
        } catch (UnknownError e) {
            throw new SyntaxException(
                e.getMessage(),
                currentToken.getFilename(),
                currentToken.getLineNum()
            );
        }
    }

    @Override
    public String convertToJott() {
        return subNode.convertToJott() + (subNode instanceof FuncCall ? ";" : "");
    }

    @Override
    public boolean validateTree() {
        return subNode.validateTree();
    }

    @Override
    public void execute() {
        subNode.execute();
    }
}
