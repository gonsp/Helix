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

    private int indents = 0;

    /* Line of the current statement. */
    private int linenumber = -1;

    /* Stores the root of declared functions */
    private HashMap<String, HelixTree> functionTrees;

    /* Stack */
    private Stack stack;


    public Interpreter(HelixTree T, boolean simulation, String tracefile) {
        assert T != null;
        // TODO remove this line
        if(!simulation) {
            droneController = new LibrePilotController();
        } else {
            droneController = new SimulationController(new GPSPosition(41.387940, 2.113464, 90.5), 0);
        }
        droneController.init();

        /*// Testing
          droneController.takeOff(5);
          for(int i = 0; i < 4; ++i) {
          droneController.west(10);
          droneController.south(10);
          droneController.east(10);
          droneController.north(10);
          droneController.up(5);
          }
          droneController.land();*/

        //        droneController.takeOff(5);
        //        for(int i = 0; i < 4*4; ++i) {
        //            droneController.forward(10);
        //            droneController.roll(-90);
        //            droneController.up(1);
        //        }
        //        droneController.land();

        //        droneController.takeOff(10);
        //        droneController.lookAt(new Position(1, 0, 0));
        //        droneController.forward(100);
        //        droneController.land();

        mapFunctions(T);
        //droneController.init();
        mapFunctions(T);
        stack = new Stack();
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
            System.out.println("DEFINED FUNCTION " + func_name);
            functionTrees.put(func_name, def);
        }
    }


    public int getLinenumber() {
        return linenumber;
    }


    public void run() {
        wrttrace("Running");
        executeFunction("main", null);
    }


    public String getStackTrace() {
        return "STACK TRACE";
    }


    public String getStackTrace(int i) {
        return "STACK TRACE " + (new Integer(i)).toString();
    }


    private void wrttrace(String msg) {
        for (int i = 0; i < indents; ++i) {
            System.out.print("   |");
        }
        System.out.println(msg);
    }


    private Data executeFunction(String func_name, HelixTree args) {
        ++indents;
        wrttrace("Executing function: " + func_name);

        HelixTree f = functionTrees.get(func_name);
        if (f == null) {
            throw new RuntimeException("function " + func_name + " was not declared");
        }

        ArrayList<Data> args_values = listArguments(f, args);

        stack.pushActivationRecord();

        HelixTree p = f.getChild(1);
        int nparam = p.getChildCount();
        for (int i = 0; i < nparam; ++i) {
            String param_name = p.getChild(i).getText();
            stack.defineVariable(param_name, args_values.get(i));
        }

        Data ret = executeListInstructions(f.getChild(2));
        ArrayList<Data> pars_values = listParameters(f);
        stack.popActivationRecord();
        copyRefArguments(f, args, pars_values);

        --indents;
        return ret;
    }


    private Data executeListInstructions(HelixTree list_instr) {
        assert list_instr.getType() == HelixLexer.LIST_INSTR;
        ++indents;
        wrttrace("Executing list of instructions");

        Data ret = null;
        for (HelixTree instr : list_instr) {
            ret = executeInstruction(instr);
            if (ret != null) return ret;
        }

        --indents;
        return null;
    }


    private Data executeInstruction(HelixTree instr) {
        ++indents;

        Data ret = null;
        switch (instr.getType()) {

            case HelixLexer.ASSIGN:
                executeAssign(instr);
                return null;

            case HelixLexer.DEFFUNC:
                return executeDefaultFunction(instr);

            case HelixLexer.FUNCALL:
                return executeFunctionCall(instr);

            case HelixLexer.RETURN:
                return executeReturn(instr);

            case HelixLexer.IF:
                executeIf(instr);
                return null;

            case HelixLexer.WHILE:
                executeWhile(instr);
                return null;

        }
        --indents;
        return null;
    }


    private void executeAssign(HelixTree assign) {
        assert assign.getType() == HelixLexer.ASSIGN;
        wrttrace("Executing assign");

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


    private void assignIdAccess(HelixTree access, HelixTree expr) {
        switch (access.getType()) {
            case HelixLexer.ID:
                assignId(access, expr);
                break;

            case HelixLexer.ATTRIB:
                assignAttrib(access, expr);
        }
    }


    private void assignId(HelixTree access, HelixTree expr) {
        String id = access.getText();
        Data dexpr = evaluateExpression(expr);
        wrttrace("Assigned variable " + id + " value " + dexpr.toString());
        stack.defineVariable(id, dexpr);
    }


    private void assignAttrib(HelixTree access, HelixTree expr) {
        IntData dexpr = (IntData) evaluateExpression(expr);
        String id = access.getChild(0).getText();
        Position a = (Position) stack.getVariable(id);
        if (a == null) {
            a = new Position();
        }
        switch (access.getChild(1).getType()) {
            case HelixLexer.LAT:
                a.lat = dexpr.toDouble();
                break;
            case HelixLexer.LNG:
                a.lng = dexpr.toDouble();
                break;
            case HelixLexer.ALT:
                a.alt = dexpr.toDouble();
                break;
        }
        stack.defineVariable(id, a);
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

        if (cexpr.getType() == HelixLexer.COORD) {
            assignIdAccess(a_lat, cexpr.getChild(0));
            assignIdAccess(a_lng, cexpr.getChild(1));
            assignIdAccess(a_alt, cexpr.getChild(2));
        }
        else {
            Position pexpr = (Position) evaluateExpression(cexpr);
            assignAttrib(a_lat, pexpr.lat);
            assignAttrib(a_lng, pexpr.lng);
            assignAttrib(a_alt, pexpr.lat);
        }
    }


    private Data executeDefaultFunction(HelixTree deffunc) {
        assert deffunc.getType() == HelixLexer.DEFFUNC;
        HelixTree f = deffunc.getChild(0);
        wrttrace("Executing default function: " + f.getText());

        ArrayList<Data> args_values = listArguments(deffunc.getChild(1));
        int n_args = args_values.size();

        Data d;

        switch (f.getType()) {

            case HelixLexer.GET_GPS:
                assert n_args == 0;
                return droneController.getGPS();

            case HelixLexer.MOVE:
                assert n_args == 1;
                d = args_values.get(0);
                checkDataType(d, Data.DataType.POSITION);
                droneController.moveTo((Position)d);
                break;

            case HelixLexer.FORWARD:
                assert n_args == 1;
                d = args_values.get(0);
                checkDataType(d, Data.DataType.INTEGER);
                droneController.forward(((IntData) d).toDouble());
                break;

            case HelixLexer.BACKWARDS:
                assert n_args == 1;
                d = args_values.get(0);
                checkDataType(d, Data.DataType.INTEGER);
                droneController.backward(((IntData) d).toDouble());
                break;

            case HelixLexer.RIGHT:
                assert n_args == 1;
                d = args_values.get(0);
                checkDataType(d, Data.DataType.INTEGER);
                droneController.right(((IntData) d).toDouble());
                break;

            case HelixLexer.LEFT:
                assert n_args == 1;
                d = args_values.get(0);
                checkDataType(d, Data.DataType.INTEGER);
                droneController.left(((IntData) d).toDouble());
                break;

            case HelixLexer.ROTATE:
                assert n_args == 1;
                d = args_values.get(0);
                checkDataType(d, Data.DataType.INTEGER);
                droneController.rotate(((IntData) d).toDouble());
                break;

            case HelixLexer.TAKEOFF:
                assert n_args == 1;
                d = args_values.get(0);
                checkDataType(d, Data.DataType.INTEGER);
                droneController.takeOff(((IntData) d).toDouble());
                break;

            case HelixLexer.LAND:
                droneController.land();
                assert n_args == 0;
                break;

            case HelixLexer.SLEEP:
                assert n_args == 1;
                d = args_values.get(0);
                checkDataType(d, Data.DataType.INTEGER);
                System.out.println("sleep for " + d.toString());
                try {
                    TimeUnit.SECONDS.sleep(2);
                }
                catch (Exception e){
                    System.out.println(e.toString());
                }
                break;

            case HelixLexer.UPF:
                assert n_args == 1;
                d = args_values.get(0);
                checkDataType(d, Data.DataType.INTEGER);
                droneController.up(((IntData) d).toDouble());
                break;

            case HelixLexer.DOWNF:
                d = args_values.get(0);
                checkDataType(d, Data.DataType.INTEGER);
                droneController.down(((IntData) d).toDouble());
                break;

            case HelixLexer.LOOKAT:
                d = args_values.get(0);
                checkDataType(d, Data.DataType.POSITION);
                droneController.lookAt((Position) d);
                break;

            case HelixLexer.PRINT:
                assert n_args == 1;
                d = args_values.get(0);
                System.out.println("PRINT: " + d.toString());
                break;

        }

        return null;
    }


    private Data executeFunctionCall(HelixTree funcall) {
        assert funcall.getType() == HelixLexer.FUNCALL;
        wrttrace("Executing function call");
        return executeFunction(funcall.getChild(0).getText(), funcall.getChild(1));
    }


    private Data executeReturn(HelixTree ret) {
        assert ret.getType() == HelixLexer.RETURN;
        wrttrace("Executing return");
        return null;
    }


    private void executeIf(HelixTree iftree) {
        assert iftree.getType() == HelixLexer.IF;
        wrttrace("Executing if");
        Data cond = evaluateExpression(iftree.getChild(0));
        checkDataType(cond, Data.DataType.BOOLEAN);
        HelixTree ins_if = iftree.getChild(1);
        HelixTree ins_el = iftree.getChild(2);
        if (((BoolData) cond).value) {
            executeListInstructions(ins_if);
        }
        else if (ins_el != null) {
            executeListInstructions(ins_el);
        }
    }


    private void executeWhile(HelixTree whiletree) {
        assert whiletree.getType() == HelixLexer.WHILE;
        wrttrace("Executing while");
        HelixTree ins = whiletree.getChild(1);
        Data cond = evaluateExpression(whiletree.getChild(0));
        checkDataType(cond, Data.DataType.BOOLEAN);
        while (((BoolData) cond).value) {
            executeListInstructions(ins);
            cond = evaluateExpression(whiletree.getChild(0));
            checkDataType(cond, Data.DataType.BOOLEAN);
        }
    }


    private Data evaluateExpression(HelixTree expr) {
        Data result = null;

        wrttrace("Evaluating expression " + expr.getText());

        int type = expr.getType();
        int nchild = expr.getChildCount();

        HelixTree l_child = expr.getChild(0);
        HelixTree r_child = expr.getChild(1);
        HelixTree a_child = expr.getChild(2);

        Data l_data;
        Data r_data;
        Data a_data;

        if (nchild == 0) {
            switch (type) {
                case HelixLexer.NUM:
                    return new IntData(expr.getNumValue());

                case HelixLexer.BOOLEAN:
                    return new BoolData(expr.getBoolValue());

                case HelixLexer.ID:
                    Data d = stack.getVariable(expr.getText());
                    return d.getCopy();
            }
        }

        else if (nchild == 1) {
            switch (type) {
                case HelixLexer.NOT:
                    l_data = evaluateExpression(l_child);
                    checkDataType(l_data, Data.DataType.BOOLEAN);
                    ((BoolData) l_data).value = !((BoolData) l_data).value;
                    return l_data;

                case HelixLexer.PLUS:
                    l_data = evaluateExpression(l_child);
                    checkDataType(l_data, Data.DataType.INTEGER);
                    return l_data;

                case HelixLexer.MINUS:
                    l_data = evaluateExpression(l_child);
                    checkDataType(l_data, Data.DataType.INTEGER);
                    ((IntData) l_data).value = -((IntData) l_data).value;
                    return l_data;

            }
        }

        else if (nchild == 2) {
            switch (type) {
                case HelixLexer.OR:
                    l_data = evaluateExpression(l_child);
                    checkDataType(l_data, Data.DataType.BOOLEAN);
                    if (((BoolData) l_data).value) {
                        return l_data;
                    }
                    r_data = evaluateExpression(r_child);
                    checkDataType(l_data, Data.DataType.BOOLEAN);
                    return r_data;

                case HelixLexer.AND:
                    l_data = evaluateExpression(l_child);
                    checkDataType(l_data, Data.DataType.BOOLEAN);
                    if (!((BoolData) l_data).value) {
                        return l_data;
                    }
                    r_data = evaluateExpression(r_child);
                    checkDataType(l_data, Data.DataType.BOOLEAN);
                    return r_data;

                case HelixLexer.EQUAL:
                case HelixLexer.NOT_EQUAL:
                case HelixLexer.LT:
                case HelixLexer.LE:
                case HelixLexer.GT:
                case HelixLexer.GE:
                    l_data = evaluateExpression(l_child);
                    r_data = evaluateExpression(r_child);
                    checkDataType(l_data, r_data);
                    return l_data.evaluateRelational(type, r_data);

                case HelixLexer.PLUS:
                case HelixLexer.MINUS:
                case HelixLexer.MUL:
                case HelixLexer.DIV:
                case HelixLexer.MOD:
                    l_data = evaluateExpression(l_child);
                    r_data = evaluateExpression(r_child);
                    checkDataType(l_data, r_data);
                    l_data.evaluateArithmetic(type, r_data);
                    return l_data;

                case HelixLexer.ATTRIB:
                    l_data = stack.getVariable(l_child.getChild(0).getText()).getCopy();
                    checkDataType(l_data, Data.DataType.POSITION);
                    switch (r_child.getType()) {
                        case HelixLexer.LAT:
                            return new IntData(((Position) l_data).lat);
                        case HelixLexer.LNG:
                            return new IntData(((Position) l_data).lat);
                        case HelixLexer.ALT:
                            return new IntData(((Position) l_data).lat);
                    }

                case HelixLexer.DEFFUNC:
                    return executeDefaultFunction(expr);

                case HelixLexer.FUNCALL:
                    return executeFunction(l_child.getText(), r_child);

            }
        }

        else if (nchild == 3) {
            switch (type) {
                case HelixLexer.COORD:
                    l_data = evaluateExpression(l_child);
                    r_data = evaluateExpression(r_child);
                    a_data = evaluateExpression(a_child);
                    checkDataType(l_data, Data.DataType.INTEGER);
                    checkDataType(r_data, Data.DataType.INTEGER);
                    checkDataType(a_data, Data.DataType.INTEGER);
                    double lat, lng, alt;
                    lat = ((IntData) l_data).toDouble();
                    lng = ((IntData) r_data).toDouble();
                    alt = ((IntData) a_data).toDouble();
                    return new Position(lat, lng, alt);
            }
        }

        return null;
    }


    private ArrayList<Data> listArguments(HelixTree func, HelixTree args) {
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
            if (p.getType() == HelixLexer.PVALUE) {
                System.out.println("By value");
                result.add(i, evaluateExpression(a));
            }
            else {
                if (a.getType() != HelixLexer.ID) {
                    throw new RuntimeException("Wrong argument for pass by reference");
                }
                System.out.println("By ref");
                Data d = stack.getVariable(a.getText());
                result.add(i, d);
            }
        }
        return result;
    }


    private ArrayList<Data> listArguments (HelixTree args) {
        ArrayList<Data> result = new ArrayList<Data>();
        for (HelixTree c : args) {
            Data d = evaluateExpression(c);
            result.add(d);
        }
        return result;
    }


    private ArrayList<Data> listParameters (HelixTree f) {
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
        assert da.type == db.type;
    }


    private void checkDataType(Data data, Data.DataType type) {
        assert data.type == type;
    }



}
