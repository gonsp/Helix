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


    public IntData(double value) {
        this((new Double(value * 100)).intValue());
    }


    public IntData(int value) {
        this.value = value;
        this.type = DataType.INTEGER;
    }


    public double toDouble() {
        double result = (double) value;
        result = result / 100D;
        return result;
    }


    @Override
    public void evaluateArithmetic (int op, Data data) {
        IntData d = (IntData) data;
        switch (op) {
            case HelixLexer.PLUS: value += d.value; break;
            case HelixLexer.MINUS: value -= d.value; break;
            case HelixLexer.MUL: value = (value * d.value) / 100; break;
            case HelixLexer.DIV: value = new Double((this.toDouble() / d.toDouble()) * 100D).intValue(); break;
            case HelixLexer.MOD: value %= d.value; break;
            default: operationNotSupported();
        }
    }


    @Override
    public BoolData evaluateRelational (int op, Data data) {
        IntData d = (IntData) data;
        switch (op) {
            case HelixLexer.EQUAL: return new BoolData(value == d.value);
            case HelixLexer.NOT_EQUAL: return new BoolData(value != d.value);
            case HelixLexer.LT: return new BoolData(value < d.value);
            case HelixLexer.LE: return new BoolData(value <= d.value);
            case HelixLexer.GT: return new BoolData(value > d.value);
            case HelixLexer.GE: return new BoolData(value >= d.value);
            default: operationNotSupported();
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
        return Double.toString(((double) value) / 100D);
    }

}
