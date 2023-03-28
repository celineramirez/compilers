import java.util.*;

public class SimpleTableBuilder extends LittleBaseListener{
    HashMap<String, ArrayList> hMap = new HashMap();
    ArrayList<HashMap<String, ArrayList>> tableList = new ArrayList();
    Stack<HashMap<String, ArrayList>> scopeStack = new Stack();
    ArrayList<String> stringslist = new ArrayList<>();

    int i = 0;

        public void enterProgram(LittleParser.ProgramContext ctx) {
        //1.Make a new symbol table for global


        // ?need list and stack??
            //2. add it to the list of symbol tables
            tableList.add(hMap);

            //3. push it to the "scope stack"
            scopeStack.push(hMap);
        }
        
        public void enterStmt(LittleParser.StmtContext ctx) {
            i++;

            HashMap<String, Integer> blockhash = new HashMap<>();
            blockhash.put("Block", i);
            tableList.add(hMap);
        }

        
        public void enterFunc_decl(LittleParser.Func_declarationsContext ctx) {
            String type = ctx.func_decl().any_type().getText();
            String name = ctx.func_decl().id().getText();

            hMap.put(name, stringslist);
            tableList.add(hMap);
        }



        @Override
        public void enterVar_decl(LittleParser.Var_declContext ctx) {

            String name = ctx.id_list().getText();
            String type = ctx.var_type().getText();
            System.out.println(name + ", " + type);

            stringslist.add(name);
            stringslist.add(type);

            hMap.put(name, stringslist);
            tableList.add(hMap);
        }

        @Override
        public void enterString_decl(LittleParser.String_declContext ctx) {

            //1.extract the name, type, and value
            String name = ctx.id().getText();
            String type = "STRING";
            String value = ctx.str().getText();
            System.out.println(name+", "+type+", "+value);

            //2. create a new symbol table entry using the above info and insert to the table at the top of the stack

            // add entry to array list object
            stringslist.add(name);
            stringslist.add(type);
            stringslist.add(value);

            // add entry to hashmap
            hMap.put(name, stringslist);
            tableList.add(hMap);

        }

        public void prettyPrint(){
            //print all symbol tables in the order they were created
            //print out symlist in order tables were made

//            for (String name: hMap.keySet()) {
//                String key = name.toString();
//                String type = hMap.get(name).toString();
//                String value = hMap.get(name).toString();
//                System.out.println("Symbol Table: " + key
//                        + "\nname " + name + "type " + type + "value " + value);
//            }

            System.out.println(tableList);

//            for (String keys : hMap.keySet())
//            {
//                System.out.println(keys);
//            }
        }
}
