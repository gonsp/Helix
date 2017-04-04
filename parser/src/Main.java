package Helix;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
import org.antlr.stringtemplate.*;

import java.io.*;

import parser.*;

public class Main {

    /** The file name of the program. */
    private static String infile = null;
    /** Name of the file representing the AST. */
    private static boolean printAST = false;
    /** Flag indicating that the AST must be written in dot format. */
    private static boolean dotformat = false;
      
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
        CommonTree t = (CommonTree)result.getTree();

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
    }

    private static boolean readOptions(String[] args) {
        for(String arg : args) {
            if(!printAST && arg.equals("-ast")) {
                printAST = true;
            } else if(!dotformat && arg.equals("-dot")) {
                dotformat = true;
            } else if(infile == null) {
                infile = arg;
            } else {
                System.out.println("Invalid argument: " + arg);
                return false;
            }
        }
        return true;
    }
}

