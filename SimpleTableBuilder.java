import java.util.*;
import java.util.ArrayList;

public class SimpleTableBuilder extends LittleBaseListen

    @Override
    public void enterProgram(LittleParser.ProgramContext ctx) {
    //1.Make a new symbol table for global
        SimpleTableBuilder stb = new SimpleTableBuilder();
        ArrayList<SimpleTableBuilder> symlist = new ArrayList<SimpleTableBuilder>();
        //Stack<>
    //2. add it to the list of symbol tables

    //3. push it to the "scope stack"
    }

    @Override
    public void enterString_decl(LittleParse.String_declContext ctx) {
        //1.extract the name, type, and value
        String name = ctx.id().getText();
        String type = "STRING";
        String value = ctx.str().getText();
        System.out.println(name+", "+type+", "+value);

        //2. create a new symbol table entry using the above info and insert to the table at the top of the stack

    }

    public void prettyPrint(){
        //print all symbol tables in the order they were created
        //print out symlist in order tables were made
    }
}
