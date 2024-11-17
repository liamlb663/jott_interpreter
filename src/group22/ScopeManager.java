package group22;

import group22.DataType;

import javax.xml.crypto.Data;
import java.util.*;

public class ScopeManager {
    private class Variable {
        Object obj;
        DataType type;

        Variable(Object obj, DataType type) {
            this.obj = obj;
            this.type = type;
        }
    }

    private class Function {
        ArrayList<DataType> parameterTypes;
        DataType returnType;

        Function(ArrayList<DataType> parameterTypes, DataType returnType) {
            this.parameterTypes = parameterTypes;
            this.returnType = returnType;
        }
    }

    public HashMap<String, Function> functions;
    public Stack<HashMap<String, Variable>> scopes;
    public Stack<DataType> returnTypeStack;

    public ScopeManager() {
        functions = new HashMap<>();
        DataType[] concatParams = {DataType.STRING, DataType.STRING};
        functions.put("concat", new Function(new ArrayList<DataType>(Arrays.asList(concatParams)), DataType.STRING));
        DataType[] lengthParams = {DataType.STRING};
        functions.put("length", new Function(new ArrayList<DataType>(Arrays.asList(lengthParams)), DataType.INTEGER));
        //print is not added here but rather handled in FuncCall's validateTree and execute

        scopes = new Stack<>();
        scopes.push(new HashMap<>());
        returnTypeStack = new Stack<>();
        returnTypeStack.push(null); // Global scope has no return type
    }

    public void newScope(DataType returnType) {
        scopes.push(new HashMap<>());
        returnTypeStack.push(returnType);
    }

    public void dropScope() {
        if (scopes.size() > 1) {
            scopes.pop();
            returnTypeStack.pop();
        } else {
            System.out.println("Cannot drop global scope.");
        }
    }

    public void declareFunction(String func, DataType type, ArrayList<DataType> params) {
        functions.put(func, new Function(params, type));
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

        throw new IllegalArgumentException("Variable " + var + " is not declared in current scope.");
    }

    public Object getVariable(String var) {
        HashMap<String, Variable> scope = scopes.peek();
        if (scope.containsKey(var)) {
            return scope.get(var).obj;
        }
        throw new IllegalArgumentException("Variable " + var + " is not declared in current scope.");
    }

    public DataType getDataType(String var) {
        HashMap<String, Variable> scope = scopes.peek();
        if (scope.containsKey(var)) {
            return scope.get(var).type;
        }
        throw new IllegalArgumentException("Variable " + var + " is not declared in current scope.");
    }

    public boolean isVarDeclared(String var) {
        return scopes.peek().containsKey(var);
    }

    public boolean isFunctionDeclared(String func) {
        return functions.containsKey(func);
    }

    public ArrayList<DataType> getFunctionParameterTypes(String func) {
        if (functions.containsKey(func)) {
            return functions.get(func).parameterTypes;
        }
        throw new IllegalArgumentException("Function " + func + " is not declared.");
    }

    public DataType getFunctionReturnType(String func) {
        if (functions.containsKey(func)) {
            return functions.get(func).returnType;
        }
        throw new IllegalArgumentException("Function " + func + " is not declared.");
    }

    public DataType getCurrentReturnType() {
        if (!returnTypeStack.isEmpty()) {
            return returnTypeStack.peek();
        }
        throw new IllegalStateException("No return type set for the current scope.");
    }

    public void validateReturnType(Object value) {
        if (returnTypeStack.isEmpty()) {
            throw new IllegalStateException("No return type set for the current scope.");
        }

        DataType expectedType = returnTypeStack.peek();
        if (!expectedType.isCompatible(value)) {
            throw new IllegalArgumentException("Return value type does not match the function's return type.");
        }
    }

    public void clearAll() {
        scopes.clear();
        scopes.push(new HashMap<>());
        functions.clear();
        returnTypeStack.clear();
        returnTypeStack.push(null);
    }
}
