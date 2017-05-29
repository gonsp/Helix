package Helix.interpreter;

import Helix.interpreter.controller.DroneController;
import Helix.interpreter.controller.librepilot.LibrePilotController;
import Helix.interpreter.controller.simulation.SimulationController;
import Helix.parser.HelixLexer;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class Interpreter {
    
    private DroneController droneController;

    private int indents = 0;

    /* Line of the current statement. */
    private int linenumber = -1;

    /* Stores the root of declared functions */
    private HashMap<String, HelixTree> functionTrees;

    /* File to write the execution trace. */
    private PrintWriter trace = null;


    public Interpreter(HelixTree T, boolean simulation, String tracefile) {
        assert T != null;
        // TODO remove this line
        if(!simulation) {
            droneController = new LibrePilotController();
        } else {
            droneController = new SimulationController(new GPSPosition(41.463798, 2.090397, 0));
        }
        droneController.init();



        //droneController.takeOff(5);
        droneController.east(5);
        System.out.println("HELLOOOOOOOOOOOO");
        System.exit(1000);
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


    private void executeFunction(String func_name, HelixTree args) {
        ++indents;
        HelixTree f = functionTrees.get(func_name);
        if (f == null) {
            throw new RuntimeException("function " + func_name + " was not declared");
        }
        wrttrace("Executing function: " + func_name);

        executeListInstructions(f.getChild(2));
        
        --indents;
    }


    private void executeListInstructions(HelixTree list_instr) {
        ++indents;
        wrttrace("Executing list of instructions");
        assert list_instr.getType() == HelixLexer.LIST_INSTR;
        for (HelixTree instr : list_instr) {
            executeInstruction(instr);
        }
        --indents;
    }


    private void executeInstruction(HelixTree instr) {
        ++indents;
        switch (instr.getType()) {
            case HelixLexer.ASSIGN:
                executeAssign(instr);
                break;
            case HelixLexer.DEFFUNC:
                executeDefaultFunction(instr);
                break;
            case HelixLexer.FUNCALL:
                executeFunctionCall(instr);
                break;
            case HelixLexer.RETURN:
                executeReturn(instr);
                break;
            case HelixLexer.IF:
                executeIf(instr);
                break;
            case HelixLexer.WHILE:
                executeWhile(instr);
                break;
        }
        --indents;
    }


    private void executeAssign(HelixTree assign) {
        assert assign.getType() == HelixLexer.ASSIGN;
        wrttrace("Executing assign");
    }


    private void executeDefaultFunction(HelixTree deffunc) {
        assert deffunc.getType() == HelixLexer.DEFFUNC;
        HelixTree f = deffunc.getChild(0);
        wrttrace("Executing default function: " + f.getText());
        switch (f.getType()) {
            case HelixLexer.GET_GPS:
                break;
            case HelixLexer.MOVE:
                break;
            case HelixLexer.FORWARD:
                break;
            case HelixLexer.ROTATE:
                break;
            case HelixLexer.TAKEOFF:
                break;
            case HelixLexer.LAND:
                break;
            case HelixLexer.SLEEP:
                break;
            case HelixLexer.UPF:
                break;
            case HelixLexer.DOWNF:
                break;
            case HelixLexer.RIGHT:
                break;
            case HelixLexer.LEFT:
                break;
            case HelixLexer.BACKWARDS:
                break;
            case HelixLexer.LOOKAT:
                break;
            default:
                throw new RuntimeException("What did you do to trigger this????");
        }
    }


    private void executeFunctionCall(HelixTree funcall) {
        assert funcall.getType() == HelixLexer.FUNCALL;
        wrttrace("Executing function call");
        executeFunction(funcall.getChild(0).getText(), funcall.getChild(1));
    }


    private void executeReturn(HelixTree ret) {
        assert ret.getType() == HelixLexer.RETURN;
        wrttrace("Executing return");
    }


    private void executeIf(HelixTree iftree) {
        assert iftree.getType() == HelixLexer.IF;
        wrttrace("Executing if");
    }


    private void executeWhile(HelixTree whiletree) {
        assert whiletree.getType() == HelixLexer.WHILE;
        wrttrace("Executing while");
    }
}
