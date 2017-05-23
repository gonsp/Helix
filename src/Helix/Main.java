package Helix;

import Helix.interpreter.HelixTree;
import Helix.interpreter.HelixTreeAdaptor;
import Helix.interpreter.Interpreter;
import Helix.parser.HelixLexer;
import Helix.parser.HelixParser;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CharStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.DOTTreeGenerator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;



public class Main {

    /** The file name of the program. */
    private static String infile = null;
    /** Name of the file representing the AST. */
    private static boolean printAST = false;
    /** Flag indicating that the AST must be written in dot format. */
    private static boolean dotformat = false;
    /** Flag to indicate wether the program must be executed after parsing. */
    private static boolean execute = true;
    /** Flag to indicate if the program is going to be simulated or just executed in a real drone. */
    private static boolean simulation = false;
    /** Name of the file storing the trace of the program*/
    private static String tracefile = "trace32741329";

    /** Main program that invokes the parser and the interpreter. */
    public static void main(String[] args) throws Exception {

        // Parser for command line options
        if (!readOptions(args)) {
            System.out.println("Usage: helix <file.hx> [-ast] [-dot]");
            System.exit(1);
        }

        // Parsing of the input file
        
        CharStream input = null;
        try {
            input = new ANTLRFileStream(infile);
        } catch (IOException e) {
            System.err.println ("Error: file " + infile + " could not be opened.");
            System.exit(1);
        }

        // Creates the lexer
        HelixLexer lex = new HelixLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lex);

        // Creates and runs the parser. As a result, an AST is created
        HelixParser parser = new HelixParser(tokens);
        HelixTreeAdaptor adaptor = new HelixTreeAdaptor();
        parser.setTreeAdaptor(adaptor);
        HelixParser.prog_return result = null;
        try {
            result = parser.prog();
        } catch (Exception e) {} // Just catch the exception (nothing to do)
        
        // Check for parsing errors
        int nerrors = parser.getNumberOfSyntaxErrors();
        if (nerrors > 0) {
            System.err.println (nerrors + " errors detected. " +
                                "The program has not been executed.");
            System.exit(1);
        }

        // Get the AST
        HelixTree t = (HelixTree)result.getTree();

        // Generate a file for the AST (option -ast file)
        if(printAST) {
            System.out.println(t.toStringTree());
        }
        if(dotformat) {
            File ast = new File(infile + ".dot");
            BufferedWriter output = new BufferedWriter(new FileWriter(ast));
            DOTTreeGenerator gen = new DOTTreeGenerator();
            output.write(gen.toDOT(t).toString());
            output.close();
        }

        if (execute) {
            Interpreter I = null;
            int linenumber = -1;
            try {
                I = new Interpreter(t, simulation, tracefile);
                I.Run();
            } catch(RuntimeException e) {
                if (I != null) {
                    linenumber = I.getLinenumber();
                }
                System.err.print("runtime error");
                if (linenumber < 0) System.err.print(".");
                else System.err.print(" (" + infile + ", line " + linenumber + "): ");
                System.err.println(e.getMessage() + ".");
                e.printStackTrace();
            } catch (StackOverflowError e) {
                if (I != null) {
                    linenumber = I.getLinenumber();
                }
                System.err.print("stack overflow error");
                if (linenumber < 0) System.err.print(".");
                else System.err.print(" (" + infile + ", line " + linenumber + "): ");
                e.printStackTrace();
            }
        }

    }

    private static boolean readOptions(String[] args) {
        for(String arg : args) {
            if(!printAST && arg.equals("-ast")) {
                printAST = true;
            } else if(!dotformat && arg.equals("-dot")) {
                dotformat = true;
            } else if(infile == null) {
                infile = arg;
            } else if(arg.equals("-noexec")) {
                execute = false;
            } else if(arg.equals("-simulation")) {
                simulation = true;
            } else {
                System.out.println("Invalid argument: " + arg);
                return false;
            }
        }
        return true;
    }
}

