import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;

public class Driver {
    public static void main (String args[]) {

        try {
            // initialize token input stream
            ANTLRInputStream input = new ANTLRInputStream(System.in);
            matchInput(input);
        }
        catch(IOException e) {
            e.printStackTrace();
        }

    }// end main

    public static void matchInput(CharStream input){

        // initialize lexer and parser
        LittleLexer lexer = new LittleLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LittleParser parser = new LittleParser(tokens);

        parser.program();
        parser.removeErrorListeners();

        int num_errs = parser.getNumberOfSyntaxErrors();

        if(num_errs==0) {
            System.out.println("Accepted");
        }
        else {
            System.out.println("Not accepted");
        }
    }// end matchInput

}// end Driver
