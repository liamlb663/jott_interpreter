package group22;

import group22.DataType;

public class Data {
    public Object value;
    public DataType type;
    public String fileName;
    public int fineNumber;

    public Data(Object value, DataType type, String fileName, int fineNumber) {
        this.value = value;
        this.type = type;
        this.fileName = fileName;
        this.fineNumber = fineNumber;
    }
}
