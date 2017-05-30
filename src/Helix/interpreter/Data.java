package Helix.interpreter;

import Helix.parser.*;

public abstract class Data {
    public enum DataType {VOID, BOOL, POSITION, INTEGER};

    public DataType type;

    public abstract void evaluateArithmetic (int op, Data d);
    public abstract Data evaluateRelational (int op, Data d);
    public abstract Data getCopy();
    public abstract String toString();
}
