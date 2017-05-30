package Helix.interpreter;

import java.util.HashMap;
import java.util.LinkedList;

public class Stack {
    private LinkedList<HashMap<String, Data>> stack;
    private HashMap<String, Data> currentAR;


    public Stack() {
        stack = new LinkedList<HashMap<String, Data>>();
        currentAR = null;
    }


    public void pushActivationRecord() {
        currentAR = new HashMap<String, Data>();
        stack.addLast(currentAR);
    }


    public void popActivationRecord() {
        stack.removeLast();
        currentAR = stack.isEmpty() ? null : stack.getLast();
    }


    public void defineVariable(String name, Data data) {
        currentAR.put(name, data.getCopy()); // TODO: Must be copy?
    }


    public Data getVariable(String name) {
        Data d = currentAR.get(name);
        return d;
    }
}
