package Helix.interpreter;

import Helix.parser.*;

public abstract class Data {
    public enum DataType {VOID, BOOL, DECIMAL, POSITION};

    public DataType type;

    public abstract void evaluateArithmetic (int op, Data d);
    public abstract Data evaluateRelational (int op, Data d);
}
