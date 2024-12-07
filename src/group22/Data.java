package group22;

import group22.DataType;

public class Data {
    public Object value;
    public DataType type;
    public String fileName;
    public int lineNumber;

    public Data(Object value, DataType type, String fileName, int lineNumber) {
        this.value = value;
        this.type = type;
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }
}
