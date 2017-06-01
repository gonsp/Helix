package Helix.interpreter;

import Helix.interpreter.controller.DroneController;
import Helix.interpreter.controller.librepilot.LibrePilotController;
import Helix.interpreter.controller.simulation.SimulationController;
import Helix.parser.HelixLexer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Interpreter {

    private DroneController droneController;

    /* Stores the root of declared functions */
    private HashMap<String, HelixTree> functionTrees;

    /* Stack */
    private Stack stack;

    /* Line number */
    private int linenumber = -1;

    private PrintWriter trace = null;

    private int function_nesting = -1;


    public Interpreter(HelixTree T, boolean simulation, String tracefile) {
        assert T != null;
        if(!simulation) {
            droneController = new LibrePilotController();
        } else {
            droneController = new SimulationController(new GPSPosition(41.387940, 2.113464, 90.5), 0);
        }

        droneController.init();
        mapFunctions(T);
        stack = new Stack();

        if (tracefile != null) {
            try {
                trace = new PrintWriter(new FileWriter(tracefile));
            } catch (IOException e) {
                System.err.println(e);
                System.exit(1);
            }
        }
    }


    private void mapFunctions(HelixTree T) {
        assert T != null && T.getType() == HelixLexer.LIST_FUNCTIONS;
        functionTrees = new HashMap<String, HelixTree>();
        for (HelixTree def : T) {
            assert def.getType() == HelixLexer.DEF;
            String func_name = def.getChild(0).getText();
            if (functionTrees.containsKey(func_name)) {
                throw new RuntimeException("Multiple definitions of function " + func_name);
            }
            functionTrees.put(func_name, def);
        }
    }


    public void run() {
        executeFunction("main", null);
    }


    public String getStackTrace() {
        return stack.getStackTrace(getLinenumber());
    }


    public String getStackTrace(int i) {
        return stack.getStackTrace(getLinenumber(), i);
    }


    private Data executeFunction(String func_name, HelixTree args) {
        HelixTree f = functionTrees.get(func_name);
        if (f == null) {
            throw new RuntimeException("function " + func_name + " was not declared");
        }
        ArrayList<Data> args_values = listArguments(f, args);

        if (trace != null) traceFunctionCall(f, args_values);

        stack.pushActivationRecord(func_name, getLinenumber());
        setLinenumber(f);

        HelixTree p = f.getChild(1);
        int nparam = p.getChildCount();
        for (int i = 0; i < nparam; ++i) {
            String param_name = p.getChild(i).getText();
            stack.defineVariable(param_name, args_values.get(i));
        }

        Data ret = executeListInstructions(f.getChild(2));
        if (ret == null) {
            ret = new NumData();
            ret.type = Data.DataType.VOID;
        }

        ArrayList<Data> pars_values = listParameters(f);
        
        if (trace != null) traceReturn(f, ret, args_values);

        stack.popActivationRecord();
        copyRefArguments(f, args, pars_values);

        return ret;
    }


    private Data executeListInstructions(HelixTree list_instr) {
        assert list_instr.getType() == HelixLexer.LIST_INSTR;

        Data ret = null;
        for (HelixTree instr : list_instr) {
            ret = executeInstruction(instr);
            if (ret != null) return ret;
        }

        return null;
    }


    private Data executeInstruction(HelixTree instr) {
        assert instr != null;
        setLinenumber(instr);

        Data ret = null;
        switch (instr.getType()) {

            case HelixLexer.ASSIGN:
                executeAssign(instr);
                return null;

            case HelixLexer.DEFFUNC:
                return executeDefaultFunction(instr);

            case HelixLexer.FUNCALL:
                executeFunction(instr.getChild(0).getText(), instr.getChild(1));
                return null;

            case HelixLexer.RETURN:
                if (instr.getChildCount() != 0) {
                    return evaluateExpression(instr.getChild(0));
                }
                ret = new NumData();
                ret.type = Data.DataType.VOID;
                return ret;

            case HelixLexer.IF:
                ret = evaluateExpression(instr.getChild(0));
                checkDataType(ret, Data.DataType.BOOLEAN);
                if (((BoolData) ret).value) {
                    return executeListInstructions(instr.getChild(1));
                }
                if (instr.getChildCount() == 3) {
                    return executeListInstructions(instr.getChild(2));
                }
                return null;

            case HelixLexer.WHILE:
                while (true) {
                    ret = evaluateExpression(instr.getChild(0));
                    checkDataType(ret, Data.DataType.BOOLEAN);
                    if (!((BoolData) ret).value) {
                        return null;
                    }
                    ret = executeListInstructions(instr.getChild(1));
                    if (ret != null) return ret;
                }

        }
        return null;
    }


    private void executeAssign(HelixTree assign) {
        assert assign.getType() == HelixLexer.ASSIGN;

        HelixTree access = assign.getChild(0);
        HelixTree expr = assign.getChild(1);
        switch (access.getType()) {
            case HelixLexer.ID:
                assignId(access, expr);
                break;

            case HelixLexer.ATTRIB:
                assignAttrib(access, expr);
                break;

            case HelixLexer.COORDACCESS:
                assignCoordAccess(access, expr);
                break;
        }
    }


    private void assignId(HelixTree access, HelixTree expr) {
        String id = access.getText();
        Data dexpr = evaluateExpression(expr);
        stack.defineVariable(id, dexpr);
    }


    private void assignId(HelixTree access, double val) {
        String id = access.getText();
        Data dexpr = new NumData(val);
        stack.defineVariable(id, dexpr);
    }


    private void assignAttrib(HelixTree access, HelixTree expr) {
        NumData dexpr = (NumData) evaluateExpression(expr);
        assignAttrib(access, dexpr.toDouble());
    }


    private void assignAttrib(HelixTree access, double val) {
        String id = access.getChild(0).getText();
        Position a = (Position) stack.getVariable(id);
        if (a == null) {
            a = new Position();
        }
        switch (access.getChild(1).getType()) {
            case HelixLexer.LAT:
                a.lat = val;
                break;
            case HelixLexer.LNG:
                a.lng = val;
                break;
            case HelixLexer.ALT:
                a.alt = val;
                break;
        }
        stack.defineVariable(id, a);
    }


    private void assignCoordAccess(HelixTree caccess, HelixTree cexpr) {
        HelixTree a_lat, a_lng, a_alt;
        a_lat = caccess.getChild(0);
        a_lng = caccess.getChild(1);
        a_alt = caccess.getChild(2);

        Data d = evaluateExpression(cexpr);
        checkDataType(d, Data.DataType.POSITION);

        Position p = (Position) d;

        if (a_lat.getType() != HelixLexer.VOIDACCESS) {
            if (a_lat.getType() == HelixLexer.ID) {
                assignId(a_lat, p.lat);
            } else {
                assignAttrib(a_lat, p.lat);
            }
        }

        if (a_lng.getType() != HelixLexer.VOIDACCESS) {
            if (a_lng.getType() == HelixLexer.ID) {
                assignId(a_lng, p.lng);
            } else {
                assignAttrib(a_lng, p.lng);
            }
        }

        if (a_alt.getType() != HelixLexer.VOIDACCESS) {
            if (a_alt.getType() == HelixLexer.ID) {
                assignId(a_alt, p.alt);
            } else {
                assignAttrib(a_alt, p.alt);
            }
        }
    }


    private Data executeDefaultFunction(HelixTree deffunc) {
        assert deffunc.getType() == HelixLexer.DEFFUNC;
        HelixTree f = deffunc.getChild(0);
        String func_name = f.getText();

        ArrayList<Data> args_values = listArguments(deffunc.getChild(1));
        int n_args = args_values.size();

        Data d;

        switch (f.getType()) {

            case HelixLexer.GET_POS:
                checkNumArgs(n_args, 0, func_name);
                return droneController.getPos();

            case HelixLexer.MOVE:
                checkNumArgs(n_args, 1, func_name);
                d = args_values.get(0);
                checkDataType(d, Data.DataType.POSITION);
                droneController.moveTo((Position)d);
                break;

            case HelixLexer.FORWARD:
                checkNumArgs(n_args, 1, func_name);
                d = args_values.get(0);
                checkDataType(d, Data.DataType.NUMBER);
                droneController.forward(((NumData) d).toDouble());
                break;

            case HelixLexer.BACKWARD:
                checkNumArgs(n_args, 1, func_name);
                d = args_values.get(0);
                checkDataType(d, Data.DataType.NUMBER);
                droneController.backward(((NumData) d).toDouble());
                break;

            case HelixLexer.RIGHT:
                checkNumArgs(n_args, 1, func_name);
                d = args_values.get(0);
                checkDataType(d, Data.DataType.NUMBER);
                droneController.right(((NumData) d).toDouble());
                break;

            case HelixLexer.LEFT:
                checkNumArgs(n_args, 1, func_name);
                d = args_values.get(0);
                checkDataType(d, Data.DataType.NUMBER);
                droneController.left(((NumData) d).toDouble());
                break;

            case HelixLexer.ROTATE:
                checkNumArgs(n_args, 1, func_name);
                d = args_values.get(0);
                checkDataType(d, Data.DataType.NUMBER);
                droneController.rotate(((NumData) d).toDouble());
                break;

            case HelixLexer.TAKEOFF:
                checkNumArgs(n_args, 1, func_name);
                d = args_values.get(0);
                checkDataType(d, Data.DataType.NUMBER);
                droneController.takeOff(((NumData) d).toDouble());
                break;

            case HelixLexer.LAND:
                checkNumArgs(n_args, 0, func_name);
                droneController.land();
                break;

            case HelixLexer.SLEEP:
                checkNumArgs(n_args, 1, func_name);
                d = args_values.get(0);
                checkDataType(d, Data.DataType.NUMBER);
                try {
                    TimeUnit.SECONDS.sleep(((NumData) d).value / 100);
                }
                catch (Exception e){
                    throw new RuntimeException(e);
                }
                break;

            case HelixLexer.UPF:
                checkNumArgs(n_args, 1, func_name);
                d = args_values.get(0);
                checkDataType(d, Data.DataType.NUMBER);
                droneController.up(((NumData) d).toDouble());
                break;

            case HelixLexer.DOWNF:
                d = args_values.get(0);
                checkDataType(d, Data.DataType.NUMBER);
                droneController.down(((NumData) d).toDouble());
                break;

            case HelixLexer.LOOKAT:
                d = args_values.get(0);
                checkDataType(d, Data.DataType.POSITION);
                droneController.lookAt((Position) d);
                break;

            case HelixLexer.PRINT:
                checkNumArgs(n_args, 1, func_name);
                d = args_values.get(0);
                System.out.println(d.toString());
                break;

            case HelixLexer.MOVETO:
                checkNumArgs(n_args, 1, func_name);
                d = args_values.get(0);
                checkDataType(d, Data.DataType.POSITION);
                droneController.moveTo((Position) d);
                break;

            case HelixLexer.SET_DIR:
                checkNumArgs(n_args, 1, func_name);
                d = args_values.get(0);
                checkDataType(d, Data.DataType.NUMBER);
                droneController.setDirection(((NumData) d).toDouble());
                break;

            case HelixLexer.GET_DIR:
                checkNumArgs(n_args, 0, func_name);
                return new NumData(droneController.getDirection());

            case HelixLexer.NORTH:
                checkNumArgs(n_args, 1, func_name);
                d = args_values.get(0);
                checkDataType(d, Data.DataType.NUMBER);
                droneController.north(((NumData) d).toDouble());
                break;

            case HelixLexer.SOUTH:
                checkNumArgs(n_args, 1, func_name);
                d = args_values.get(0);
                checkDataType(d, Data.DataType.NUMBER);
                droneController.south(((NumData) d).toDouble());
                break;

            case HelixLexer.EAST:
                checkNumArgs(n_args, 1, func_name);
                d = args_values.get(0);
                checkDataType(d, Data.DataType.NUMBER);
                droneController.east(((NumData) d).toDouble());
                break;

            case HelixLexer.WEST:
                checkNumArgs(n_args, 1, func_name);
                d = args_values.get(0);
                checkDataType(d, Data.DataType.NUMBER);
                droneController.west(((NumData) d).toDouble());
                break;

        }

        return null;
    }


    private Data evaluateExpression(HelixTree expr) {
        assert expr != null;

        Data result = null;

        int previous_line = getLinenumber();
        setLinenumber(expr);

        int type = expr.getType();
        int nchild = expr.getChildCount();

        HelixTree l_child = expr.getChild(0);
        HelixTree r_child = expr.getChild(1);
        HelixTree a_child = expr.getChild(2);

        Data l_data;
        Data r_data;
        Data a_data;

        Data return_data = null;

        if (nchild == 0) {
            switch (type) {
                case HelixLexer.NUM:
                    return_data = new NumData(expr.getNumValue());
                    break;

                case HelixLexer.STRING:
                    String text = expr.getText();
                    return_data = new StringData(text.substring(1, text.length() - 1));
                    break;

                case HelixLexer.BOOLEAN:
                    return_data = new BoolData(expr.getBoolValue());
                    break;

                case HelixLexer.ID:
                    Data d = stack.getVariable(expr.getText());
                    if (d == null) {
                        throw new RuntimeException("Variable not declared: " +  expr.getText());
                    }
                    return_data = d.getCopy();
                    break;
            }
        }

        else if (nchild == 1) {
            switch (type) {
                case HelixLexer.NOT:
                    l_data = evaluateExpression(l_child);
                    checkDataType(l_data, Data.DataType.BOOLEAN);
                    ((BoolData) l_data).value = !((BoolData) l_data).value;
                    return_data = l_data;
                    break;

                case HelixLexer.PLUS:
                    l_data = evaluateExpression(l_child);
                    checkDataType(l_data, Data.DataType.NUMBER);
                    return_data = l_data;
                    break;

                case HelixLexer.MINUS:
                    l_data = evaluateExpression(l_child);
                    checkDataType(l_data, Data.DataType.NUMBER);
                    ((NumData) l_data).value = -((NumData) l_data).value;
                    return_data = l_data;
                    break;
            }
        }

        else if (nchild == 2) {
            switch (type) {
                case HelixLexer.CONCAT:
                    l_data = evaluateExpression(l_child);
                    r_data = evaluateExpression(r_child);
                    return_data = new StringData(l_data.toString() + r_data.toString());
                    break;

                case HelixLexer.OR:
                    l_data = evaluateExpression(l_child);
                    checkDataType(l_data, Data.DataType.BOOLEAN);
                    if (((BoolData) l_data).value) {
                        return_data = l_data;
                        break;
                    }
                    r_data = evaluateExpression(r_child);
                    checkDataType(l_data, Data.DataType.BOOLEAN);
                    return_data = r_data;
                    break;

                case HelixLexer.AND:
                    l_data = evaluateExpression(l_child);
                    checkDataType(l_data, Data.DataType.BOOLEAN);
                    if (!((BoolData) l_data).value) {
                        return_data = l_data;
                        break;
                    }
                    r_data = evaluateExpression(r_child);
                    checkDataType(l_data, Data.DataType.BOOLEAN);
                    return_data = r_data;
                    break;

                case HelixLexer.EQUAL:
                case HelixLexer.NOT_EQUAL:
                case HelixLexer.LT:
                case HelixLexer.LE:
                case HelixLexer.GT:
                case HelixLexer.GE:
                    l_data = evaluateExpression(l_child);
                    r_data = evaluateExpression(r_child);
                    checkDataType(l_data, r_data);
                    return_data = l_data.evaluateRelational(type, r_data);
                    break;

                case HelixLexer.PLUS:
                case HelixLexer.MINUS:
                case HelixLexer.MUL:
                case HelixLexer.DIV:
                case HelixLexer.MOD:
                    l_data = evaluateExpression(l_child);
                    r_data = evaluateExpression(r_child);
                    checkDataType(l_data, r_data);
                    l_data.evaluateArithmetic(type, r_data);
                    return_data = l_data;
                    break;

                case HelixLexer.ATTRIB:
                    l_data = evaluateExpression(l_child).getCopy();
                    checkDataType(l_data, Data.DataType.POSITION);
                    switch (r_child.getType()) {
                        case HelixLexer.LAT:
                            return_data = new NumData(((Position) l_data).lat);
                            break;
                        case HelixLexer.LNG:
                            return_data = new NumData(((Position) l_data).lng);
                            break;
                        case HelixLexer.ALT:
                            return_data = new NumData(((Position) l_data).alt);
                            break;
                    }
                    break;

                case HelixLexer.DEFFUNC:
                    return_data = executeDefaultFunction(expr);
                    break;

                case HelixLexer.FUNCALL:
                    return_data = executeFunction(l_child.getText(), r_child);
                    break;

            }
        }

        else if (nchild == 3) {
            switch (type) {
                case HelixLexer.COORD:
                    l_data = evaluateExpression(l_child);
                    r_data = evaluateExpression(r_child);
                    a_data = evaluateExpression(a_child);
                    checkDataType(l_data, Data.DataType.NUMBER);
                    checkDataType(r_data, Data.DataType.NUMBER);
                    checkDataType(a_data, Data.DataType.NUMBER);
                    double lat, lng, alt;
                    lat = ((NumData) l_data).toDouble();
                    lng = ((NumData) r_data).toDouble();
                    alt = ((NumData) a_data).toDouble();
                    return_data = new Position(lat, lng, alt);
            }
        }

        setLinenumber(previous_line);
        return return_data;
    }


    private ArrayList<Data> listArguments(HelixTree func, HelixTree args) {
        if (args != null) {
            setLinenumber(args);
        }

        HelixTree pars = func.getChild(1);
        int n = pars.getChildCount();

        ArrayList<Data> result = new ArrayList<Data>();

        int nargs = args == null ? 0 : args.getChildCount();
        if (n != nargs) {
            throw new RuntimeException(
                    "Incorrect number of parameters calling function " + func.getChild(0).getText()
                    );
        }
        for (int i = 0; i < n; ++i) {
            HelixTree p = pars.getChild(i);
            HelixTree a = args.getChild(i);
            setLinenumber(a);
            if (p.getType() == HelixLexer.PVALUE) {
                result.add(i, evaluateExpression(a));
            }
            else {
                if (a.getType() != HelixLexer.ID) {
                    throw new RuntimeException("Wrong argument for pass by reference");
                }
                Data d = stack.getVariable(a.getText());
                result.add(i, d);
            }
        }
        return result;
    }


    private ArrayList<Data> listArguments(HelixTree args) {
        if (args != null) {
            setLinenumber(args);
        }
        ArrayList<Data> result = new ArrayList<Data>();
        for (HelixTree c : args) {
            setLinenumber(c);
            Data d = evaluateExpression(c);
            result.add(d);
        }
        return result;
    }


    private ArrayList<Data> listParameters(HelixTree f) {
        ArrayList<Data> result = new ArrayList<Data>();
        for (HelixTree param : f.getChild(1)) {
            result.add(stack.getVariable(param.getText()));
        }
        return result;
    }


    private void copyRefArguments(HelixTree f, HelixTree args, ArrayList<Data> values) {
        HelixTree pars = f.getChild(1);
        int nchilds = pars.getChildCount();
        for (int i = 0; i < nchilds; ++i) {
            HelixTree p = pars.getChild(i);
            HelixTree a = args.getChild(i);
            if (p.getType() == HelixLexer.PREF) {
                stack.defineVariable(a.getText(), values.get(i));
            }
        }

    }


    private void checkDataType(Data da, Data db) {
        if (da.type != db.type) {
            throw new RuntimeException("Incompatible types");
        }
    }


    private void checkDataType(Data data, Data.DataType type) {
        if (data.type != type) {
            throw new RuntimeException("Unexpected type. Expected " + type.toString());
        }
    }


    private void checkNumArgs(int current, int expected, String func_name) {
        if (current != expected) {
            throw new RuntimeException(
                    "Incorrect number of parameters calling function " + func_name
                    );
        }
    }


    public int getLinenumber() { 
        return linenumber; 
    }

    
    public void setLinenumber(HelixTree t) {
        linenumber = t.getLine();
    }


    public void setLinenumber(int l) {
        linenumber = l;
    }


    private void traceFunctionCall(HelixTree f, ArrayList<Data> arg_values) {
        function_nesting++;
        HelixTree params = f.getChild(1);
        int nargs = params.getChildCount();
        
        for (int i=0; i < function_nesting; ++i) trace.print("|   ");

        // Print function name and parameters
        trace.print(f.getChild(0) + "(");
        for (int i = 0; i < nargs; ++i) {
            if (i > 0) trace.print(", ");
            HelixTree p = params.getChild(i);
            if (p.getType() == HelixLexer.PREF) trace.print("&");
            trace.print(p.getText() + "=" + arg_values.get(i));
        }
        trace.print(") ");
        
        if (function_nesting == 0) trace.println("<entry point>");
        else trace.println("<line " + getLinenumber() + ">");
    }


    private void traceReturn(HelixTree f, Data result, ArrayList<Data> arg_values) {
        for (int i=0; i < function_nesting; ++i) trace.print("|   ");
        function_nesting--;
        trace.print("return");
        if (result.type != Data.DataType.VOID) trace.print(" " + result);
        
        // Print the value of arguments passed by reference
        HelixTree params = f.getChild(1);
        int nargs = params.getChildCount();
        for (int i = 0; i < nargs; ++i) {
            HelixTree p = params.getChild(i);
            if (p.getType() == HelixLexer.PVALUE) continue;
            trace.print(", &" + p.getText() + "=" + arg_values.get(i));
        }
        
        trace.println(" <line " + getLinenumber() + ">");
        if (function_nesting < 0) trace.close();
    }

}
