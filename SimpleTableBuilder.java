import java.util.*;

public class SimpleTableBuilder extends LittleBaseListener{
    ArrayList<HashMap> tableList = new ArrayList();
    Stack<HashMap> scopeStack = new Stack();
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

    @Override public void enterIf_stmt(LittleParser.If_stmtContext ctx) {
            i++;
            HashMap<String, String> blockhash = new HashMap<>();

            blockhash.put("Symbol table ", "BLOCK " + i);
            tableList.add(blockhash);
        }

    @Override public void enterElse_part(LittleParser.Else_partContext ctx) {
        HashMap<String, String> blockhash2 = new HashMap<>();

        if(ctx.stmt_list() != null){
            i++;
            blockhash2.put("Symbol table ", "BLOCK " + i);
            tableList.add(blockhash2);
        }
    }

    @Override public void enterWhile_stmt(LittleParser.While_stmtContext ctx) {
        i++;
        HashMap<String, String> blockhash = new HashMap<>();
        blockhash.put("Symbol table ", "BLOCK " + i);
        tableList.add(blockhash);
    }

        @Override
        public void enterFunc_decl(LittleParser.Func_declContext ctx) {
            String name = ctx.id().getText();

            HashMap<String, String> FuncTable = new HashMap();

            FuncTable.put("\nSymbol table ", name);
            tableList.add(FuncTable);
        }

        @Override
        public void enterParam_decl(LittleParser.Param_declContext ctx) {
            String type = ctx.var_type().getText();
            String name = ctx.id().getText();

            HashMap<String, String> paramTable = new HashMap();

            paramTable.put("name " + name, "type " + type);
            tableList.add(paramTable);

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

        @Override
        public void enterString_decl(LittleParser.String_declContext ctx) {

            //1.extract the name, type, and value
            String name = ctx.id().getText();
            String type = "STRING";
            String value = ctx.str().getText();
            //System.out.println(name+", "+type+", "+value);

            //2. create a new symbol table entry using the above info and insert to the table at the top of the stack

            HashMap<String, String> StringTable = new HashMap();

            // add entry to hashmap
            StringTable.put("name " + name, "type " + type + " value "+ value);
            tableList.add(StringTable);

        }

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
