package group22;

import group22.Data;
import group22.DataType;
import group22.GrammarClasses.FBody;

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
        ArrayList<String> parameterNames;
        ArrayList<DataType> parameterTypes;
        DataType returnType;
        FBody body;

        Function(ArrayList<String> parameterNames, ArrayList<DataType> parameterTypes, DataType returnType, FBody body) {
            this.parameterNames = parameterNames;
            this.parameterTypes = parameterTypes;
            this.returnType = returnType;
            this.body = body;
        }
    }

    public HashMap<String, Function> functions;
    public Stack<HashMap<String, Variable>> scopes;
    public Stack<DataType> returnTypeStack;

    public ScopeManager() {
        functions = new HashMap<>();
        String[] concatParamNames = {"a", "b"};
        DataType[] concatParams = {DataType.STRING, DataType.STRING};
        functions.put("concat", new Function(new ArrayList<String>(Arrays.asList(concatParamNames)), new ArrayList<DataType>(Arrays.asList(concatParams)), DataType.STRING, null));
        String[] lengthParamName = {"input"};
        DataType[] lengthParams = {DataType.STRING};
        functions.put("length", new Function(new ArrayList<String>(Arrays.asList(lengthParamName)), new ArrayList<DataType>(Arrays.asList(lengthParams)), DataType.INTEGER, null));
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

    public void declareFunction(String func, DataType type, FBody body, ArrayList<DataType> paramTypes, ArrayList<String> paramNames) {
        if (paramNames.size() != paramTypes.size()) {
            throw new IllegalArgumentException("Parameter names and types must have the same length.");
        }

        functions.put(func, new Function(paramNames, paramTypes, type, body));
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

    public boolean validateArgs(ArrayList<Object> args, ArrayList<DataType> expectedTypes) {
        if (args.size() != expectedTypes.size()) {
            throw new IllegalArgumentException("Mismatch in the number of arguments and expected types");
        }

        for (int i = 0; i < args.size(); i++) {
            Object arg = args.get(i);
            DataType expectedType = expectedTypes.get(i);

            if (!expectedType.isCompatible(arg)) {
                return false;
            }
        }

        return true;
    }

    public Data executeFunction(String func, ArrayList<Object> args, String name, int number) throws RuntimeException {
        if (func.strip().toLowerCase().equals("print")) {
            if (args.get(0) instanceof String) {
                String output = (String)args.get(0);
                System.out.println(output.substring(1, output.length() - 1));
            } else {
                System.out.println(args.get(0));
            }
            return null;
        }

        if (func.strip().toLowerCase().equals("concat")) {
            return new Data((Object)((String)args.get(0) + (String)args.get(1)), DataType.STRING, "", 0);
        }

        if (func.strip().toLowerCase().equals("length")) {
            return new Data((Object)((String)args.get(0)).length(), DataType.INTEGER, "", 0);
        }

        Function function = functions.get(func);

        if (!validateArgs(args, function.parameterTypes)) {
            throw new IllegalArgumentException("Arguments do not match the function's parameter types.");
        }

        newScope(functions.get(func).returnType);

        ArrayList<DataType> paramTypes = function.parameterTypes;
        for (int i = 0; i < args.size(); i++) {
            String paramName = function.parameterNames.get(i);
            declareVariable(paramName, paramTypes.get(i));
            setVariable(paramName, args.get(i));
        }


        Data output = function.body.execute();

        dropScope();

        return output;
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
