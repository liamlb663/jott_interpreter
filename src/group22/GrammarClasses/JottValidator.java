package group22.GrammarClasses;

import group22.DataType;
import group22.ScopeManager;
import group22.SemanticException;
import provided.JottTree;

import java.util.HashMap;

public class JottValidator {
    public static boolean isJottValid(JottTree jottTree) throws SemanticException {
        HashMap<String, DataType> functions = new HashMap<>();
        HashMap<String, HashMap<String, DataType>> variables = new HashMap<>();
        String currentScope = "";

        return jottTree.validateTree(
                functions,
                variables,
                currentScope
        );
    }
}
