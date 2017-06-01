package Helix.interpreter;

import Helix.parser.*;


public class NumData extends Data {
    private static long PREC_I = 1000;
    private static double PREC_D = PREC_I;

    public long value;


    public NumData() {
        this(0);
    }


    public NumData(NumData i) {
        this(i.value);
    }


    public NumData(double value) {
        this((new Double(value * PREC_D)).intValue());
    }


    public NumData(int value) {
        this.value = value;
        this.type = DataType.NUMBER;
    }


    public double toDouble() {
        double result = (double) value;
        result = result / PREC_D;
        return result;
    }


    @Override
    public void evaluateArithmetic(int op, Data data) {
        NumData d = (NumData) data;
        switch (op) {
            case HelixLexer.PLUS: value += d.value; break;
            case HelixLexer.MINUS: value -= d.value; break;
            case HelixLexer.MUL: value = (value * d.value) / PREC_I; break;
            case HelixLexer.DIV: 
                checkDivZero(d);
                value = new Double((this.toDouble() / d.toDouble()) * PREC_D).longValue(); 
                break;
            case HelixLexer.MOD: 
                checkInteger(this);
                checkInteger(d);
                checkDivZero(d);
                value %= d.value;
                break;
            default: operationNotSupported();
        }
    }


    @Override
    public BoolData evaluateRelational(int op, Data data) {
        NumData d = (NumData) data;
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
        NumData copy = new NumData(this.value);
        return copy;
    }

    @Override
    public String toString() {
        return Double.toString(((double) value) / PREC_D);
    }


    private static void checkInteger(NumData d) {
        if (d.value % PREC_I > 0) {
            throw new RuntimeException("Integer operation on decimal data");
        }
    }


    private static void checkDivZero(NumData d) {
        if (d.value == 0) {
            throw new RuntimeException("Division by zero");
        }
    }

}
