package group22;

import java.util.HashMap;
import java.util.Stack;

public class ScopeManager {
    private Stack<HashMap<String, Object>> scopes;

    public ScopeManager() {
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

    public void setInt(String name, int value) {
        scopes.peek().put(name, value);
    }

    public void setDouble(String name, double value) {
        scopes.peek().put(name, value);
    }

    public void setString(String name, String value) {
        scopes.peek().put(name, value);
    }

    public void setBoolean(String name, boolean value) {
        scopes.peek().put(name, value);
    }

    public Integer getInt(String name) {
        Object value = scopes.peek().get(name);
        if (value instanceof Integer) {
            return (Integer) value;
        }
        throw new IllegalArgumentException("Variable " + name + " is not of type int or is not in the current scope.");
    }

    public Double getDouble(String name) {
        Object value = scopes.peek().get(name);
        if (value instanceof Double) {
            return (Double) value;
        }
        throw new IllegalArgumentException("Variable " + name + " is not of type double or is not in the current scope.");
    }

    public String getString(String name) {
        Object value = scopes.peek().get(name);
        if (value instanceof String) {
            return (String) value;
        }
        throw new IllegalArgumentException("Variable " + name + " is not of type String or is not in the current scope.");
    }

    public Boolean getBoolean(String name) {
        Object value = scopes.peek().get(name);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        throw new IllegalArgumentException("Variable " + name + " is not of type boolean or is not in the current scope.");
    }

    public boolean isVarAvailable(String name) {
        return scopes.peek().containsKey(name);
    }

    public DataType getDatatype
}
