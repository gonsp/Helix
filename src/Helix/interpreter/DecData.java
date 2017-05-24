package Helix.interpreter;

import Helix.parser.*;


public class DecData extends Data {
    private static double epsilon = 0.005;

    private double value;


    public DecData() {
        this.value = 0.0;
    }


    public DecData(double value) {
        this.value = value;
    }


    @Override
    public void evaluateArithmetic (int op, Data data) {
        DecData d = (DecData) data;
        switch (op) {
            case HelixLexer.PLUS: value += d.value; break;
            case HelixLexer.MINUS: value -= d.value; break;
            case HelixLexer.MUL: value *= d.value; break;
            case HelixLexer.DIV: value /= d.value; break;
            default: assert false;
        }
    }


    @Override
    public Data evaluateRelational (int op, Data data) {
        DecData d = (DecData) data;
        switch (op) {
            case HelixLexer.EQUAL: return new BoolData(value >= d.value - epsilon && value <= d.value + epsilon);
            case HelixLexer.NOT_EQUAL: return new BoolData(value < d.value - epsilon && value > d.value + epsilon);
            case HelixLexer.LT: return new BoolData(value < d.value);
            case HelixLexer.LE: return new BoolData(value <= d.value);
            case HelixLexer.GT: return new BoolData(value > d.value);
            case HelixLexer.GE: return new BoolData(value >= d.value);
            default: assert false; 
        }
        return null;
    }


    public String toString() {
        return Double.toString(value);
    }

}
