import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.*;

public class Driver {
    public static void main (String args[]) {

        int type = 0;

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

                type = token.getType();

                System.out.print("Token Type: ");
                switch(type) {
                    case 1:
                        System.out.println("KEYWORDS");
                        break;
                    case 2:
                        System.out.println("IDENTIFIER");
                        break;
                    case 3:
                        System.out.println("INTLITERAL");
                        break;
                    case 4:
                        System.out.println("FLOATLITERAL");
                        break;
                    case 5:
                        System.out.println("STRINGLITERAL");
                        break;
                    case 6:
                        System.out.println("COMMENT");
                        break;
                    case 7:
                        System.out.println("OPERATORS");
                        break;
                    case 8:
                        System.out.println("WS");
                        break;
                    default:
                        break;
                }
                System.out.println("Value: " + token.getText());
            }
        }
        catch(IOException e) {
            System.out.println("IO Exception");
        }

    }
}
