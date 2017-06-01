package Helix.interpreter;

import Helix.parser.*;


public class StringData extends Data {

    public String value;


    public StringData() {
        this("");
    }


    public StringData(StringData b) {
        this(b.value);
    }


    public StringData(String value) {
        this.value = value;
        this.type = DataType.STRING;
    }


    @Override
    public void evaluateArithmetic(int op, Data data) {
        StringData d = (StringData) data;
        switch (op) {
            case HelixLexer.PLUS: value = value + d.value; break;
            case HelixLexer.MINUS: value = value.replace(d.value, ""); break;
            default: operationNotSupported();
        }
    }


    @Override
    public BoolData evaluateRelational(int op, Data data) {
        StringData d = (StringData) data;
        switch (op) {
            case HelixLexer.EQUAL: return new BoolData(value.equals(d.value));
            case HelixLexer.NOT_EQUAL: return new BoolData(!value.equals(d.value));
            default: operationNotSupported();
        }
        return null;
    }


    @Override
    public Data getCopy() {
        StringData copy = new StringData(this.value);
        return copy;
    }


    @Override
    public String toString() {
        return value;
    }

}
