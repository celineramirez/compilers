import java.util.*;

public class SimpleTableBuilder extends LittleBaseListener {

    ArrayList<String> codeList = new ArrayList<>(); // list of IR Code

    // global constants
    String[] varlist = null; // gather variable list
    String type = null; // Get the type of the variables collected in varlist
    int reg_count = 0; // register counter
    String arith_val = null; // gets the current id/literal being parsed
    String[] string_list = null;
    HashMap<String,String[]> varmap = new HashMap<String, String[]>();

    public void enterProgram(LittleParser.ProgramContext ctx) {
        codeList.add(";IR code");
    }

    @Override
    public void enterFunc_decl(LittleParser.Func_declContext ctx) {
        String name = ctx.id().getText();
        String text = ctx.getText();

        codeList.add(";LABEL " + name);

        if (text.contains("BEGIN")) {
            codeList.add(";LINK");
        }
    }

    @Override
    public void exitFunc_decl(LittleParser.Func_declContext ctx) {
        codeList.add(";RET");
    }

    @Override
    public void enterVar_decl(LittleParser.Var_declContext ctx) {
        type = ctx.var_type().getText();
        varlist = ctx.id_list().getText().split(",");

        varmap.put(type, varlist); // put the type and variable list into a hashmap

        for (Map.Entry<String, String[]> entry : varmap.entrySet()) {
            // Get the key and value for the current entry
            String key = entry.getKey();
            String[] value = entry.getValue();

            // Print out the key and the corresponding value
            System.out.print(key + ": ");
            for (String str : value) {
                System.out.print(str + " ");
            }
            System.out.println();
        }
    }

    @Override public void enterString_decl(LittleParser.String_declContext ctx) {
        string_list = ctx.id().getText().split(",");
        type = "STRING";
        varmap.put(type, string_list);
    }

    // enter a read statement
    @Override
    public void enterRead_stmt(LittleParser.Read_stmtContext ctx) {

        // get the variables in the read statement
        String[] read_Var = ctx.id_list().getText().split(",");

        for(int i = 0; i < read_Var.length; i++) {
            String find = read_Var[i];
            String the_type = null;

            for (Map.Entry<String, String[]> entry : varmap.entrySet()) {
                String type = entry.getKey();
                String[] variables = entry.getValue();
                for (String variable : variables) {
                    if (variable.trim().equals(find)) {
                        the_type = type;
                        break;
                    }
                }
                if (the_type != null) {
                    break;
                }
            }
            if (the_type.compareTo("FLOAT") == 0) {
                codeList.add(";READF " + find);
            }
            else if (the_type.compareTo("INT") == 0) {
                codeList.add(";READI " + find);
            }
            else if (the_type.compareTo("STRING") == 0) {
                codeList.add(";READS " + find);
            }
        }
    }

    @Override
    public void enterWrite_stmt(LittleParser.Write_stmtContext ctx) {
        // same as read_stmt algorithm
        String[] write_Var = ctx.id_list().getText().split(",");

        // find the variables used in the write statement in the hashmap, and return their type
        for(int i = 0; i < write_Var.length; i++) {
            String find = write_Var[i];
            String the_type = null;

            for (Map.Entry<String, String[]> entry : varmap.entrySet()) {
                String type = entry.getKey();
                String[] variables = entry.getValue();
                for (String variable : variables) {
                    if (variable.trim().equals(find)) {
                        the_type = type;
                        break;
                    }
                }
                if (the_type != null) {
                    break;
                }
            }
            if (the_type.compareTo("FLOAT") == 0) {
                codeList.add(";WRITEF " + find);
            }
            else if (the_type.compareTo("INT") == 0) {
                codeList.add(";WRITEI " + find);
            }
            else if (the_type.compareTo("STRING") == 0) {
                codeList.add(";WRITES " + find);
            }
        }
    }

    @Override
    public void enterPrimary(LittleParser.PrimaryContext ctx) {
        arith_val = ctx.getText(); // get current expression value
    }

    @Override
    public void enterAssign_expr(LittleParser.Assign_exprContext ctx) {

        String op = null; // operator currently being parsed
        String op_code; // IR Code operator
        String register; // append to reg_count, register to store current value in
        String leftHandSide; // get the left hand side of the assignment statement
        String expr; // get entire right hand side
        String[] the_expr; // tokenize rhs

        // make new register for assignments
        reg_count++;
        register = "$T" + reg_count;
        leftHandSide = ctx.id().getText();

        expr = ctx.expr().getText(); // get entire rhs
        the_expr = expr.split("[+]|[-]|[*]|[/]"); // get operands

        // check if parenthesis exist
        if (expr.contains("(")) {
            op = expr.substring(2, 3);
            expr = expr.replaceAll("[()]", "");
            the_expr = expr.split("[+]|[-]|[*]|[/]");
        } else {
            // get the operator
            if (ctx.expr().expr_prefix().addop() != null && !ctx.expr().expr_prefix().addop().isEmpty()) {
                op = ctx.expr().expr_prefix().addop().getText();
            } else if (ctx.expr().factor().factor_prefix().mulop() != null && !ctx.expr().factor().factor_prefix().mulop().isEmpty()) {
                op = ctx.expr().factor().factor_prefix().mulop().getText();
            }
        }

        // unary assignment, a := 1
        if (op == null) {
            storeVars(ctx, register, op, leftHandSide);
        }

        // operator exists, binary assignment a := b + c
        else {

            String arithStmt; // IR code to enter in codeList
            String t = VarType(); // type of variable currently being parsed

            switch (op) {

                case "+":
                    op_code = ";ADD" + t;

                    if (the_expr.length == 2) {
                        arithStmt = op_code + the_expr[0] + " " + the_expr[1] + " " + register;
                        codeList.add(arithStmt);
                    }
                    break;
                case "-":
                    op_code = ";SUB" + t;

                    if (the_expr.length == 2) {
                        arithStmt = op_code + the_expr[0] + " " + the_expr[1] + " " + register;
                        codeList.add(arithStmt);
                    }
                    break;
                case "*":
                    op_code = ";MULT" + t;

                    if (the_expr.length == 2) {
                        arithStmt = op_code + the_expr[0] + " " + the_expr[1] + " " + register;
                        codeList.add(arithStmt);
                    }
                    break;

                case "/":

                    String store = null;
                    op_code = ";DIV" + t;
                    String store_code = ";STORE" + t;
                    String numbers= expr.replaceAll("[^0-9.]", "");
                    System.out.println("numbers " + numbers);

                    expr = ctx.expr().getText();
                    the_expr = expr.split("[/]");
                    arith_val = numbers;


                    // if div does not contain literal, don't change register count
                    // otherwise use original register count and place into register 1 up
                    System.out.println("LINE 237: Need to figure out division algorithm for test3\n");

                    //if current arith val is literal, store in register first
                    //else continue
                    if (isLit()) {

                        store = store_code + arith_val + " " + register;
                        the_expr[1] = register;
                        reg_count++;
                        codeList.add(store);
                    }
                    if (the_expr.length == 2) {
                        arithStmt = op_code + the_expr[0] + " " + the_expr[1] + " " + register;
                        codeList.add(arithStmt);
                    }
                    break;
            }
            // There is a store at the end of every arithmetic operation
            storeVars(ctx, register, op, leftHandSide);

        }
    }// end assign_expr

    public String VarType() {
        // find the variables used in the write statement in the hashmap, and return their type
        String find = arith_val;
        String the_type = null;

        System.out.println("vartype is " + arith_val);
        //if it is a literal
        if(isLit()){
            if(arith_val.contains(".")){
                return "F ";
            }
            else{
                return "I ";
            }
        }
        else {
            for (Map.Entry<String, String[]> entry : varmap.entrySet()) {
                String types = entry.getKey();
                String[] variables = entry.getValue();
                for (String variable : variables) {
                    if (variable.trim().equals(find)) {
                        the_type = types;
                        break;
                    }
                }
                if (the_type != null) {
                    break;
                }
            }
            if (the_type != null) {
                if (the_type.compareTo("FLOAT") == 0) {
                    return "F ";
                } else if (the_type.compareTo("INT") == 0) {
                    return "I ";
                } else if (the_type.compareTo("STRING") == 0) {
                    return "S ";
                }
            }
        }
        return " ";
    }

    // Write STORE ir code
    public void storeVars(LittleParser.Assign_exprContext ctx, String register, String op, String lhs) {
        String t = VarType();
        String op_code;
        String store;
        op_code = ";STORE" + t;

        System.out.println("op " + op);
        if (op == null) {
            store = op_code + ctx.expr().getText() + " " + register + "\n" +
                    op_code + register + " " + lhs;
            codeList.add(store);
        }
        else { // for end of arithmetic operations
            store = op_code + register + " " + lhs;
            codeList.add(store);
        }
    }

    // check if integer or float literal, for DIV only
    public Boolean isLit() {
        try {
            Float.parseFloat(arith_val);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void prettyPrint() {
        // print the IR code
        for (String code : codeList) {
            System.out.println(code);
        }
    }// end print
}// end class
