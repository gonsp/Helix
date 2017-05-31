package Helix.interpreter;

import Helix.parser.*;

public abstract class Data {
    public enum DataType {VOID, BOOLEAN, POSITION, INTEGER};

    public DataType type;

    public abstract void evaluateArithmetic (int op, Data d);
    public abstract BoolData evaluateRelational (int op, Data d);
    public abstract Data getCopy();
    public abstract String toString();

    public void operationNotSupported() {
        throw new RuntimeException("Operation not supported");
    }
}
