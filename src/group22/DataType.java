package group22;

public enum DataType {
    DOUBLE,
    INTEGER,
    BOOLEAN,
    STRING,
    VOID;

    public boolean isCompatible(Object value) {
        if (value == null) {
            return this == VOID; // VOID type is compatible with null
        }

        switch (this) {
            case INTEGER:
                return value instanceof Integer;
            case DOUBLE:
                return value instanceof Double;
            case BOOLEAN:
                return value instanceof Boolean;
            case STRING:
                return value instanceof String;
            case VOID:
                return false; // VOID should not have any compatible non-null value
            default:
                return false; // Undefined behavior for unexpected cases
        }
    }
}
