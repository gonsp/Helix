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
            //droneController = new LibrePilotController();
        } else {
            droneController = new SimulationController(new GPSPosition(41.463798, 2.090397, 0));
        }
        droneController.init();

        //droneController.up(5);
        //droneController.south(5);
        //droneController.land();
        // Testing
//        droneController.takeOff(10);
//        for(int i = 0; i < 4; ++i) {
//            droneController.north(10);
//            droneController.east(10);
//            droneController.south(10);
//            droneController.west(15);
//            droneController.up(5);
//        }
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

        HelixTree p = f.getChild(1);
        int nparam = p.getChildCount();

        stack.pushActivationRecord();

        for (int i = 0; i < nparam; ++i) {
            String param_name = p.getChild(i).getText();
            stack.defineVariable(param_name, args_values.get(i));
        }

        Data ret = executeListInstructions(f.getChild(2));
        stack.popActivationRecord();

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
        ++indents;
        String id = access.getText();
        wrttrace("Assigned variable " + id);
        Data dexpr = evaluateExpression(expr);
        stack.defineVariable(id, dexpr);
        --indents;
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
        
        ArrayList<Data> args_values = getArgList(deffunc.getChild(1));
        int n_args = args_values.size();
        switch (f.getType()) {
            case HelixLexer.GET_GPS:
                assert n_args == 0;
                return executeGetgps();
            case HelixLexer.MOVE:
                assert n_args == 1;
                executeMove(args_values.get(0));
                break;
            case HelixLexer.FORWARD:
                assert n_args == 1;
                executeForward(args_values.get(0));
                break;
            case HelixLexer.BACKWARDS:
                assert n_args == 1;
                executeBackwards(args_values.get(0));
                break;
            case HelixLexer.RIGHT:
                assert n_args == 1;
                executeRight(args_values.get(0));
                break;
            case HelixLexer.LEFT:
                assert n_args == 1;
                executeLeft(args_values.get(0));
                break;
            case HelixLexer.ROTATE:
                assert n_args == 1;
                executeRotate(args_values.get(0));
                break;
            case HelixLexer.TAKEOFF:
                assert n_args == 1;
                executeTakeoff(args_values.get(0));
                break;
            case HelixLexer.LAND:
                assert n_args == 0;
                executeLand();
                break;
            case HelixLexer.SLEEP:
                assert n_args == 1;
                executeSleep(args_values.get(0));
                break;
            case HelixLexer.UPF:
                assert n_args == 1;
                executeUp(args_values.get(0));
                break;
            case HelixLexer.DOWNF:
                assert n_args == 1;
                executeDown(args_values.get(0));
                break;
            case HelixLexer.LOOKAT:
                assert n_args == 1;
                executeLookat(args_values.get(0));
                break;
            case HelixLexer.PRINT:
                assert n_args == 1;
                executePrint(args_values.get(0));
                break;
            default:
                throw new RuntimeException("What did you do to trigger this????");
        }
        return null;
    }


    private Data executeGetgps (){
        return droneController.getGPS();
    }


    private void executeMove (Data d) {
        assert d.type == Data.DataType.POSITION;
        droneController.moveTo((Position)d);
    }


    private void executeForward (Data d) {
        assert d.type == Data.DataType.INTEGER;
        IntData data = (IntData) d;
        droneController.forward(data.toDouble());
    }


    private void executeBackwards (Data d) {
        assert d.type == Data.DataType.INTEGER;
        IntData data = (IntData) d;
        droneController.backward(data.toDouble());
    }


    private void executeRight (Data d) {
        assert d.type == Data.DataType.INTEGER;
        IntData data = (IntData) d;
        droneController.right(data.toDouble());
    }


    private void executeLeft (Data d) {
        assert d.type == Data.DataType.INTEGER;
        IntData data = (IntData) d;
        droneController.left(data.toDouble());
    }


    private void executeRotate (Data d) {
        assert d.type == Data.DataType.INTEGER;
        IntData data = (IntData) d;
        droneController.roll(data.toDouble());
    }


    private void executeTakeoff (Data d) {
        assert d.type == Data.DataType.INTEGER;
        IntData data = (IntData) d;
        droneController.takeOff(data.toDouble());
    }


    private void executeLand () {
        droneController.land();
    }


    private void executeSleep (Data d) {
        // TODO
        assert d.type == Data.DataType.INTEGER;
        IntData data = (IntData) d;
        try {
            Thread.sleep(data.value);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    private void executeUp (Data d) {
        assert d.type == Data.DataType.INTEGER;
        IntData data = (IntData) d;
        droneController.up(data.toDouble());
    }


    private void executeDown (Data d) {
        assert d.type == Data.DataType.INTEGER;
        IntData data = (IntData) d;
        droneController.down(data.toDouble());
    }


    private void executeLookat (Data d) {
        assert d.type == Data.DataType.POSITION;
        Position data = (Position) d;
        droneController.lookAt(data);
    }


    private void executePrint(Data d) {
        System.out.println("PRINT: " + d.toString());
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
    }


    private void executeWhile(HelixTree whiletree) {
        assert whiletree.getType() == HelixLexer.WHILE;
        wrttrace("Executing while");
    }


    private Data evaluateExpression(HelixTree expr) {
        Data result = null;

        wrttrace("Evaluating expre");
        int type = expr.getType();
        int nchild = expr.getChildCount();

        if (nchild == 0) {
            switch (type) {
                case HelixLexer.NUM:
                    result = new IntData(expr.getNumValue());
                    break;

                case HelixLexer.BOOLEAN:
                    result = new BoolData(false);
                    break;

                case HelixLexer.ID:
                    Data d = stack.getVariable(expr.getText());
                    return d.getCopy();
            }
        }

        else if (nchild == 1) {
            switch (type) {
                case HelixLexer.NOT:
                    break;

                case HelixLexer.PLUS:
                    break;

                case HelixLexer.MINUS:
                    break;
            }
        }

        else /* if (nchild == 2) */ {
            switch (type) {
                case HelixLexer.OR:
                    break;

                case HelixLexer.AND:
                    break;

                case HelixLexer.EQUAL:
                    break;

                case HelixLexer.NOT_EQUAL:
                    break;

                case HelixLexer.LT:
                    break;

                case HelixLexer.LE:
                    break;

                case HelixLexer.GT:
                    break;

                case HelixLexer.GE:
                    break;

                case HelixLexer.PLUS:
                    break;

                case HelixLexer.MINUS:
                    break;

                case HelixLexer.MUL:
                    break;

                case HelixLexer.DIV:
                    break;

                case HelixLexer.MOD:
                    break;

                case HelixLexer.ATTRIB:
                    break;

                case HelixLexer.DEFFUNC:
                    break;

                case HelixLexer.FUNCALL:
                    break;

            }
        }

        if (nchild == 3) {
            switch (type) {
                case HelixLexer.COORD:
                    break;

                case HelixLexer.COORDACCESS:
                    break;
            }
        }

        return result;
    }


    private ArrayList<Data> listArguments(HelixTree f, HelixTree args) {
        HelixTree pars = f.getChild(1);

        ArrayList<Data> result = new ArrayList<Data>();
        int n = pars.getChildCount();

        int nargs = args == null ? 0 : args.getChildCount();
        if (n != nargs) {
            throw new RuntimeException(
                    "Incorrect number of parameters calling function " + f.getChild(0).getText()
                    );
        }

        for (int i = 0; i < n; ++i) {
            HelixTree p = pars.getChild(i);
            HelixTree a = args.getChild(i);
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


    private ArrayList<Data> getArgList(HelixTree args) {
        ArrayList<Data> result = new ArrayList<Data>();
        for (HelixTree c : args) {
            Data d = evaluateExpression(c);
            result.add(d);
        }
        return result;
    }

}
