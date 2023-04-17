import java.util.*;

public class SimpleTableBuilder extends LittleBaseListener {

    ArrayList<String> codeList = new ArrayList<>(); // list of IR Code

    // global constants
    String[] varlist = null; // gather variable list
    String type = null; // Get the type of the variables collected in varlist
    int reg_count = 0; // register counter
    String arith_val = null; // gets the current id/literal being parsed
    String[] string_list = null;

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
        System.out.println(Arrays.toString(varlist));
    }

    @Override public void enterString_decl(LittleParser.String_declContext ctx) {
        string_list = ctx.id().getText().split(",");

    }

    // enter a read statement
    @Override
    public void enterRead_stmt(LittleParser.Read_stmtContext ctx) {

        // get the variables in the read statement
        String[] read_Var = ctx.id_list().getText().split(",");

        // if float, READF
        for (String var : read_Var) {
            if (type.compareTo("FLOAT") == 0) {
                codeList.add(";READF " + var);
            }
            //if int, it is READI (example in test1.out)
            else if (type.compareTo("INT") == 0) {
                codeList.add(";READI " + var);
            }

            //condition if string
        }
    }

    @Override
    public void enterWrite_stmt(LittleParser.Write_stmtContext ctx) {
        // same as read_stmt algorithm
        String[] write_Var = ctx.id_list().getText().split(",");
        for (String var : write_Var) {
            if (type.compareTo("FLOAT") == 0) {
                codeList.add(";WRITEF " + var);
            } else if (type.compareTo("INT") == 0) {
                codeList.add(";WRITEI " + var);
            }
            else if(string_list.equals(var)){
                codeList.add(";WRITES" + var);
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
        System.out.println(expr);

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

                    String reg;
                    int regc = reg_count;
                    String store = null;
                    op_code = ";DIV" + t;
                    String store_code = ";STORE" + t;

                    // if div does not contain literal, don't change register count
                    // otherwise use original register count and place into register 1 up
                    System.out.println("LINE 237: Need to figure out division algorithm for test3\n");

                    if (isLit()) {
                        store = store_code + register + " " + arith_val +
                                "\n" + op_code + " " + register + " $T";
                        reg_count++;
                        store += reg_count;
                        codeList.add(store);
                    } else {
                        expr = ctx.expr().getText();
                        the_expr = expr.split("[/]");

                        if (the_expr.length == 2) {
                            arithStmt = op_code + the_expr[0] + " " + the_expr[1] + " " + register;
                            codeList.add(arithStmt);
                        }
                    }
                    break;
            }
            // There is a store at the end of every arithmetic operation
            storeVars(ctx, register, op, leftHandSide);
        }
    }// end assign_expr

    public String VarType() {
        // return the variable type initial, appends to op_code in assign_expr
        if (type.compareTo("FLOAT") == 0) {
            return "F ";
        } else if (type.compareTo("INT") == 0) {
            return "I ";
        }
        return " ";
    }

    // Write STORE ir code
    public void storeVars(LittleParser.Assign_exprContext ctx, String register, String op, String lhs) {
        String t = VarType();
        String op_code;
        String store;
        op_code = ";STORE" + t;

        if (op == null) {
            store = op_code + ctx.expr().getText() + " " + register + "\n" +
                    op_code + register + " " + lhs;
            codeList.add(store);
        } else { // for end of arithmetic operations
            store = op_code + register + " " + lhs;
            codeList.add(store);
        }
    }

    // check if integer or float literal, for DIV only
    public Boolean isLit() {
        float val = 0;
        try {
            val = Float.parseFloat(arith_val);
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