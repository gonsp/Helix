package Helix.interpreter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

public class Stack {
    private LinkedList<HashMap<String, Data>> stack;
    private HashMap<String, Data> currentAR;

    class StackTraceItem {
        public String fname;
        public int line;
        public StackTraceItem(String name, int l) {
            fname = name;
            line = l;
        }
    }

    LinkedList<StackTraceItem> stackTrace;


    public Stack() {
        stack = new LinkedList<HashMap<String, Data>>();
        currentAR = null;
        stackTrace = new LinkedList<StackTraceItem>();
    }


    public void pushActivationRecord(String name, int line) {
        currentAR = new HashMap<String, Data>();
        stack.addLast(currentAR);
        stackTrace.addLast(new StackTraceItem(name, line));
    }


    public void popActivationRecord() {
        stack.removeLast();
        currentAR = stack.isEmpty() ? null : stack.getLast();
        stackTrace.removeLast();
    }


    public void defineVariable(String name, Data data) {
        currentAR.put(name, data);
    }


    public Data getVariable(String name) {
        Data d = currentAR.get(name);
        return d;
    }


    public String getStackTrace(int current_line) {
        int size = stackTrace.size();
        ListIterator<StackTraceItem> itr = stackTrace.listIterator(size);
        StringBuffer trace = new StringBuffer("---------------%n| Stack trace |%n---------------%n");
        trace.append("** Depth = ").append(size).append("%n");
        while (itr.hasPrevious()) {
            StackTraceItem it = itr.previous();
            trace.append("|> ").append(it.fname).append(": line ").append(current_line).append("%n");
            current_line = it.line;
        }
        return trace.toString();
    }


    public String getStackTrace(int current_line, int nitems) {
        int size = stackTrace.size();
        if (2*nitems >= size) return getStackTrace(current_line);
        ListIterator<StackTraceItem> itr = stackTrace.listIterator(size);
        StringBuffer trace = new StringBuffer("---------------%n| Stack trace |%n---------------%n");
        trace.append("** Depth = ").append(size).append("%n");
        int i;
        for (i = 0; i < nitems; ++i) {
           StackTraceItem it = itr.previous();
           trace.append("|> ").append(it.fname).append(": line ").append(current_line).append("%n");current_line = it.line;
        }
        trace.append("|> ...%n");
        for (; i < size-nitems; ++i) current_line = itr.previous().line;
        for (; i < size; ++i) {
           StackTraceItem it = itr.previous();
           trace.append("|> ").append(it.fname).append(": line ").append(current_line).append("%n");current_line = it.line;
        }
        return trace.toString();
    } 
}
