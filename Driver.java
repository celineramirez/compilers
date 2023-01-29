import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Driver {
    public static void main (String args[]) {
    	
    	// initialize token input stream
    	ANTLRInputStream input = new ANTLRInputStream(System.in);

        // initialize lexer
        Little lexer = new Little(input);
        
        // intialize token buffer
        CommonTokenStream token = new CommonTokenStream(lexer);
        
        // print tokens (incomplete)
        while(token.hasMoreTokens()) {
        	System.out.println(token.nextToken());
        }
        
//      System.out.println("TOKEN:" + getAllTokens().getText() + "\n Value:" getAllTokens(i).value());
//      System.out.println(getAllTokens().get(i)+ "\n");


    }
}
