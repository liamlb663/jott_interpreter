package group22;

import group22.DataType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class ScopeManager {
    private class Variable {
        Object obj;
        DataType type;

        Variable(Object obj, DataType type) {
            this.obj = obj;
            this.type = type;
        }
    }

    public HashMap<String, DataType> functions;
    public Stack<HashMap<String, Variable>> scopes;

    public ScopeManager() {
        functions = new HashMap<>();
        scopes = new Stack<>();
        scopes.push(new HashMap<>());
    }

    public void newScope() {
        scopes.push(new HashMap<>());
    }

    public void dropScope() {
        if (scopes.size() > 1) {
            scopes.pop();
        } else {
            System.out.println("Cannot drop global scope.");
        }
    }

    public void declareFunction(String func, DataType type) {
        functions.put(func, type);
    }

    public void declareVariable(String var, DataType type) {
        scopes.peek().put(var, new Variable(null, type));
    }

    public void setVariable(String var, Object obj) {
        HashMap<String, Variable> scope = scopes.peek();

        if (scope.containsKey(var)) {
            Variable variable = scope.get(var);
            if (variable.type.isCompatible(obj)) {
                variable.obj = obj;
                return;
            } else {
                throw new IllegalArgumentException("Type mismatch for variable " + var);
            }
        }

        throw new IllegalArgumentException("Variable " + var + " is not declared in any accessible scope.");
    }

    public Object getVariable(String var) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            HashMap<String, Variable> scope = scopes.get(i);
            if (scope.containsKey(var)) {
                return scope.get(var).obj;
            }
        }
        throw new IllegalArgumentException("Variable " + var + " is not declared in any accessible scope.");
    }

    public boolean isVarDeclared(String var) {
        return scopes.peek().containsKey(var);
    }

    public boolean isFunctionDeclared(String func) {
        return functions.containsKey(func);
    }

    public void clearAll() {
        scopes.clear();
        scopes.push(new HashMap<>());
        functions.clear();
    }
}