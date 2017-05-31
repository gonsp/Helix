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

    /* Line of the current statement. */
    private int linenumber = -1;

    /* Stores the root of declared functions */
    private HashMap<String, HelixTree> functionTrees;

    /* Stack */
    private Stack stack;


    public Interpreter(HelixTree T, boolean simulation, String tracefile) {
        assert T != null;
        if(!simulation) {
            droneController = new LibrePilotController();
        } else {
            droneController = new SimulationController(new GPSPosition(41.387940, 2.113464, 90.5), 0);
        }
        droneController.init();

        mapFunctions(T);
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
            functionTrees.put(func_name, def);
        }
    }


    public int getLinenumber() {
        return linenumber;
    }


    public void run() {
        executeFunction("main", null);
    }


    public String getStackTrace() {
        return "STACK TRACE";
    }


    public String getStackTrace(int i) {
        return "STACK TRACE " + (new Integer(i)).toString();
    }


    private Data executeFunction(String func_name, HelixTree args) {
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

        Data ret = null;
        switch (instr.getType()) {

            case HelixLexer.ASSIGN:
                executeAssign(instr);
                return null;

            case HelixLexer.DEFFUNC:
                return executeDefaultFunction(instr);

            case HelixLexer.FUNCALL:
                return executeFunction(instr.getChild(0).getText(), instr.getChild(1));

            case HelixLexer.RETURN:
                if (instr.getChildCount() != 0) {
                    return evaluateExpression(instr.getChild(0));
                }
                ret = new IntData();
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
        System.out.println("Assigning " + assign.getText());

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
        Data dexpr = new IntData(val);
        stack.defineVariable(id, dexpr);
    }


    private void assignAttrib(HelixTree access, HelixTree expr) {
        IntData dexpr = (IntData) evaluateExpression(expr);
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
        System.out.println("Assign coord access, " + cexpr.getText());
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

        ArrayList<Data> args_values = listArguments(deffunc.getChild(1));
        int n_args = args_values.size();

        Data d;

        switch (f.getType()) {

            case HelixLexer.GET_POS:
                assert n_args == 0;
                return droneController.getPos();

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
                try {
                    TimeUnit.SECONDS.sleep(((IntData) d).value / 100);
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

            case HelixLexer.MOVETO:
                assert n_args == 1;
                d = args_values.get(0);
                checkDataType(d, Data.DataType.POSITION);
                droneController.moveTo((Position) d);
                break;

            case HelixLexer.SET_DIR:
                assert n_args == 1;
                d = args_values.get(0);
                checkDataType(d, Data.DataType.INTEGER);
                droneController.setDirection(((IntData) d).toDouble());
                break;

            case HelixLexer.GET_DIR:
                assert n_args == 0;
                return new IntData(droneController.getDirection());

            case HelixLexer.NORTH:
                assert n_args == 1;
                d = args_values.get(0);
                checkDataType(d, Data.DataType.INTEGER);
                droneController.north(((IntData) d).toDouble());
                break;

            case HelixLexer.SOUTH:
                assert n_args == 1;
                d = args_values.get(0);
                checkDataType(d, Data.DataType.INTEGER);
                droneController.south(((IntData) d).toDouble());
                break;

            case HelixLexer.EAST:
                assert n_args == 1;
                d = args_values.get(0);
                checkDataType(d, Data.DataType.INTEGER);
                droneController.east(((IntData) d).toDouble());
                break;

            case HelixLexer.WEST:
                assert n_args == 1;
                d = args_values.get(0);
                checkDataType(d, Data.DataType.INTEGER);
                droneController.west(((IntData) d).toDouble());
                break;

        }

        return null;
    }


    private Data evaluateExpression(HelixTree expr) {
        Data result = null;

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
