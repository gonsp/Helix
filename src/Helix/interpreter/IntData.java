package Helix.interpreter;

import Helix.parser.*;


public class IntData extends Data {

    public int value;


    public IntData() {
        this(0);
    }


    public IntData(IntData i) {
        this(i.value);
    }


    public IntData(int value) {
        this.value = value;
        this.type = DataType.INTEGER;
    }


    public double toDouble() {
        double result = (double) value / 100.0;
        return result;
    }


    @Override
    public void evaluateArithmetic (int op, Data data) {
        IntData d = (IntData) data;
        switch (op) {
            case HelixLexer.PLUS: value += d.value; break;
            case HelixLexer.MINUS: value -= d.value; break;
            case HelixLexer.MUL: value *= d.value; break;
            case HelixLexer.DIV: value /= d.value; break;
            case HelixLexer.MOD: value %= d.value; break;
            default: assert false;
        }
    }


    @Override
    public Data evaluateRelational (int op, Data data) {
        IntData d = (IntData) data;
        switch (op) {
            case HelixLexer.EQUAL: return new BoolData(value == d.value);
            case HelixLexer.NOT_EQUAL: return new BoolData(value != d.value);
            case HelixLexer.LT: return new BoolData(value < d.value);
            case HelixLexer.LE: return new BoolData(value <= d.value);
            case HelixLexer.GT: return new BoolData(value > d.value);
            case HelixLexer.GE: return new BoolData(value >= d.value);
            default: assert false; 
        }
        return null;
    }


    @Override
    public Data getCopy() {
        IntData copy = new IntData(this.value);
        return copy;
    }


    @Override
    public String toString() {
        return Integer.toString(value);
    }

}
