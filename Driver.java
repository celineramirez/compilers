import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Driver {
    public static void main (String args[]) {
    	
    	// initialize token input stream
//        CharStream input = CharStreams.FromStream(System.in);
    	ANTLRInputStream input = new ANTLRInputStream(System.in);

        // initialize lexer
        Little lexer = new Little(input);
        
        // intialize token buffer
        CommonTokenStream token = new CommonTokenStream(lexer);
        
        /* getAllTokens() returns array list of all tokens in buffer
         * For loop iterates through list and prints each token
         * -INCOMPLETE-
         */
        for (int i = 0; i < getAllTokens().size(); i++) {
        	
        	//get token at i and print type and value
//        	System.out.println("TOKEN:" + getAllTokens().getText() + "\n Value:" getAllTokens(i).value());
        	
        	System.out.println(getAllTokens().get(i)+ "\n");
        }

    }
}
