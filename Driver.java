import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;

public class Driver {
    public static void main (String args[]) {

        try {
            // initialize token input stream
            ANTLRInputStream input = new ANTLRInputStream(System.in);

            // initialize lexer
            Little lexer = new Little(input);

            while(true) {
                Token token = lexer.nextToken();
                if(token.getType() == Little.EOF){
                    break;
                }

                System.out.println("TOKEN:" + token.getType() + "\n Value:" + token.getText());
            }
        }
        catch(IOException e) {
            System.out.println("IO Exception");
        }

    }
}
