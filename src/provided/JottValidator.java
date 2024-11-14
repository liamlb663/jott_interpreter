package provided;

import group22.ScopeManager;

public class JottValidator {
    public static ScopeManager scopeManager = new ScopeManager();

    public static boolean isJottValid(JottTree jottTree) {
        return jottTree.validateTree();
    }
}
