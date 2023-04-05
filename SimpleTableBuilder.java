import java.util.*;

public class SimpleTableBuilder extends LittleBaseListener{
    HashMap<String, ArrayList> hMap = new HashMap();
    ArrayList<HashMap> tableList = new ArrayList();
    Stack<HashMap> scopeStack = new Stack();
    ArrayList<String> stringslist = new ArrayList<>();

    private int i = 0;

        public void enterProgram(LittleParser.ProgramContext ctx) {
            // 1.Make a new symbol table for global
            // 2. add it to the list of symbol tables
            // 3. push it to the "scope stack"

            HashMap<String,String> GlobalTable = new HashMap();

            GlobalTable.put("Symbol table GLOBAL"," ");
            tableList.add(GlobalTable);
            scopeStack.push(GlobalTable);
        }

//        public void enterStmt(LittleParser.StmtContext ctx) {
//            i++;
//
//            HashMap<String, Integer> blockhash = new HashMap<>();
//            blockhash.put("Block", i);
//            tableList.add(blockhash);
//        }

        @Override
        public void enterFunc_decl(LittleParser.Func_declContext ctx) {
            String name = ctx.id().getText();

            HashMap<String, String> FuncTable = new HashMap();

            FuncTable.put("\nSymbol table ", name);
            tableList.add(FuncTable);
        }


        @Override
        public void enterVar_decl(LittleParser.Var_declContext ctx) {

            String name = ctx.id_list().getText();
            String[] vars = name.split(",", 0);
            for(String id : vars){

                String type = "type " + ctx.var_type().getText();
                //System.out.println("print " + name + ", " + type);


                HashMap<String, String> VarTable = new HashMap();

                VarTable.put("name " + id, type);
                tableList.add(VarTable);
            }
        }

        //@Override
//        public void enterString_decl(LittleParser.String_declContext ctx) {
//
//            //1.extract the name, type, and value
//            String name = ctx.id().getText();
//            String type = "STRING";
//            String value = ctx.str().getText();
//            //System.out.println(name+", "+type+", "+value);
//
//            //2. create a new symbol table entry using the above info and insert to the table at the top of the stack
//
//            // add entry to array list object
//            stringslist.add(name);
//            stringslist.add(type);
//            stringslist.add(value);
//
//            // add entry to hashmap
//            hMap.put(name, stringslist);
//            tableList.add(hMap);
//
//        }

        public void prettyPrint(){
            //print all symbol tables in the order they were created
            //print out symlist in order tables were made

            for (int i=0; i<tableList.size(); i++) {
                HashMap<String,ArrayList> curr = tableList.get(i);

                curr.entrySet().forEach(entry->{
                    System.out.println(entry.getKey() + " " + entry.getValue());
                });
            }// end for

        }// end print
}
