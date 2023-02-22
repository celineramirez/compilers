import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;

public class Driver {
    public static void main (String args[]) {

        int type = 0;

        try {
            // initialize token input stream
            ANTLRInputStream input = new ANTLRInputStream(System.in);

            if(matchInput(input)) {
                System.out.println("Accepted");
            }
            else {
                System.out.println("Not accepted");
            }
            
        }
        catch(IOException e) {
            e.printStackTrace();
        }

    }// end main

    public static boolean matchInput(CharStream input){

        try{
            // initialize lexer and parser
            LittleLexer lexer = new LittleLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            LittleParser parser = new LittleParser(tokens);

            parser.program();
            return true;
        }

        catch(RecognitionException e){
            return false;
        }
    }// end matchInput

}// end Driver
