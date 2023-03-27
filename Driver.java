import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;

public class Driver {
    public static void main (String args[]) {

        ANTLRInputStream input = null;
        try {
            // initialize token input stream
            input = new ANTLRInputStream(System.in);
//            matchInput(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // initialize lexer and parser
        LittleLexer lexer = new LittleLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LittleParser parser = new LittleParser(tokens);

        ParseTree tree = new parser.program();
        ParseTreeWalker ptw = new ParseTreeWalker();
        SimpleTableBuilder stb = new SimpleTableBuilder();
        ptw.walk(stb, tree);

        stb.prettyPrint();

    }// end main

}// end Driver
