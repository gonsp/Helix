package Helix.interpreter;

import Helix.parser.*;


public class BoolData extends Data {

    private boolean value;


    public BoolData() {
        this(false);
    }


    public BoolData(BoolData b) {
        this(b.value);
    }


    public BoolData(boolean value) {
        this.value = value;
        this.type = DataType.BOOL;
    }


    @Override
    public void evaluateArithmetic (int op, Data data) {
        BoolData d = (BoolData) data;
        switch (op) {
            case HelixLexer.PLUS: value = value || d.value; break;
            case HelixLexer.MINUS: value = d.value ? !value : value; break;
            case HelixLexer.MUL: value = value && d.value; break;
            default: assert false;
        }
    }


    @Override
    public Data evaluateRelational (int op, Data data) {
        BoolData d = (BoolData) data;
        switch (op) {
            case HelixLexer.EQUAL: return new BoolData(value == d.value);
            case HelixLexer.NOT_EQUAL: return new BoolData(value != d.value);
            case HelixLexer.LT: return new BoolData(!value && d.value);
            case HelixLexer.LE: return new BoolData(!(value && !d.value));
            case HelixLexer.GT: return new BoolData(value && !d.value);
            case HelixLexer.GE: return new BoolData(!(!value && d.value));
            default: assert false; 
        }
        return null;
    }


    @Override
    public Data getCopy() {
        BoolData copy = new BoolData(this.value);
        return copy;
    }


    @Override
    public String toString() {
        return value ? "true" : "false";
    }

}
