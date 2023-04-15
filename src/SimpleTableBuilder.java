import java.util.*;

public class SimpleTableBuilder extends LittleBaseListener {

    ArrayList<String> codeList = new ArrayList<>();
    ArrayList<HashMap> tableList = new ArrayList();
    AbstractSyntaxTree ast = new AbstractSyntaxTree();

    // global constants
    String[] varlist = null; // gather variable list to check which
    String type = null; // variable type, need for READ and WRITE stmts
    int reg_count = 0;
    String arith_val = null;

//    @Override
//    public void enterPrimary(LittleParser.PrimaryContext ctx) {
//        String val = ctx.getText();
//        String intval, floatval = null;
//        String op = null;
//
//        if (ctx.INTLITERAL() != null) {
//            intval = ctx.getText();
////            ast.insert(intval);
//        } else if (ctx.FLOATLITERAL() != null) {
//            floatval = ctx.getText();
////            ast.insert(floatval);
//        } else if (val.compareTo("(") == 0) {
//
//            if (!ctx.expr().expr_prefix().addop().isEmpty()) {
//                op = ctx.expr().expr_prefix().addop().getText();
//            } else if (!ctx.expr().factor().factor_prefix().mulop().isEmpty()) {
//                op = ctx.expr().factor().factor_prefix().mulop().getText();
//            }
//
//            switch (op) {
//                case "+":
//                    ast.insert("ADDI ");
//                case "-":
//                    ast.insert("SUBI ");
//                case "*":
//                    ast.insert("MULTI ");
//                case "/":
//                    ast.insert("DIVI ");
//
//                default:
//                    System.out.println("null value");
//            }
//
//            if (ctx.INTLITERAL() != null) {
//                intval = ctx.getText();
//                ast.insert(intval);
//            } else if (ctx.FLOATLITERAL() != null) {
//                floatval = ctx.getText();
//                ast.insert(floatval);
//            }
//        }
//
//    }


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
    public void enterPrimary(LittleParser.PrimaryContext ctx) {
        arith_val = ctx.getText();
    }

    @Override
    public void enterVar_decl(LittleParser.Var_declContext ctx) {
        type = ctx.var_type().getText();
        varlist = ctx.id_list().getText().split(",");
    }

    // enter a read statement
    @Override
    public void enterRead_stmt(LittleParser.Read_stmtContext ctx) {

        String[] read_Var = ctx.id_list().getText().split(",");
        //get the type of the parameters in the read statement
        //if float, add READF
        for (String var : read_Var) {
            if (type.compareTo("FLOAT") == 0) {
                codeList.add(";READF " + var);
            }
            //if int, it is READI (example in test1.out)
            else if (type.compareTo("INT") == 0) {
                codeList.add(";READI " + var);
            }
        }
        // copy this same algorithm to enterWrite_stmt
    }

    @Override
    public void enterWrite_stmt(LittleParser.Write_stmtContext ctx) {
        String[] write_Var = ctx.id_list().getText().split(",");
        for (String var : write_Var) {
            if (type.compareTo("FLOAT") == 0) {
                codeList.add(";WRITEF " + var);
            } else if (type.compareTo("INT") == 0) {
                codeList.add(";WRITEI " + var);
            }
        }
    }

    @Override
    public void enterAssign_expr(LittleParser.Assign_exprContext ctx) {
        String op = null;
        String op_code = null;
        String register = null;

        // get the operator
        if (ctx.expr().expr_prefix().addop() != null && !ctx.expr().expr_prefix().addop().isEmpty()) {
            op = ctx.expr().expr_prefix().addop().getText();
        } else if (ctx.expr().factor().factor_prefix().mulop() != null && !ctx.expr().factor().factor_prefix().mulop().isEmpty()) {
            op = ctx.expr().factor().factor_prefix().mulop().getText();
        }

        String prim = null;

//        LittleParser.PrimaryContext primaryContext = ctx.expr().factor().postfix_expr().primary();
//        if (primaryContext != null) {
////            if (!primaryContext.isEmpty()) {
////                prim = primaryContext.getText();
////            }
//            if (primaryContext.id() != null && !primaryContext.id().isEmpty()) {
//                prim = primaryContext.id().getText();
//            }
//            else if (primaryContext.INTLITERAL() != null) {
//                prim = primaryContext.INTLITERAL().getText();
//            }
//            else if (primaryContext.FLOATLITERAL() != null) {
//                prim = primaryContext.FLOATLITERAL().getText();
//            }
//        }
//        System.out.println("prim " + prim);

//        // check if parenthesis exist
//            if(ctx.expr().expr_prefix().factor().postfix_expr().primary() != null &&
//                    !ctx.expr().expr_prefix().factor().postfix_expr().primary().isEmpty()){
//                prim = ctx.expr().expr_prefix().factor().postfix_expr().primary().getText();
//
//            }
//        // check if variable
//        else if (ctx.expr().expr_prefix().factor().postfix_expr().primary().id() != null &&
//                !ctx.expr().expr_prefix().factor().postfix_expr().primary().id().isEmpty()){
//            prim = ctx.expr().expr_prefix().factor().postfix_expr().primary().id().getText();
//        }
//        else if (ctx.expr().expr_prefix().factor().postfix_expr().primary().INTLITERAL() != null){
//            prim = ctx.expr().expr_prefix().factor().postfix_expr().primary().INTLITERAL().getText();
//        }
//        else if (ctx.expr().expr_prefix().factor().postfix_expr().primary().FLOATLITERAL() != null){
//            prim = ctx.expr().expr_prefix().factor().postfix_expr().primary().FLOATLITERAL().getText();
//        }
//        System.out.println(prim);


        String leftHandSide = null;

        if (ctx.getText().contains(":=")) {
            reg_count++;
            register = "T" + reg_count;
            leftHandSide = ctx.id().getText();
        }

        // unary assignment expression
        if (op == null) {
            storeVars(ctx, register, op, leftHandSide);
        }


        // binary assignment expressions
        else {

            String expr = null;
            String[] the_expr = null;
            String arithStmt = null;
            String t = VarType();

            switch (op) {

                case "+":
                    op_code = ";ADD" + t;
                    expr = ctx.expr().getText();
                    the_expr = expr.split("[+]");

                    if (the_expr.length == 2) {
                        arithStmt = op_code + the_expr[0] + " " + the_expr[1] + " $" + register;
                        codeList.add(arithStmt);
                    }
                    break;
                case "-":
                    op_code = ";SUB" + t;
                    expr = ctx.expr().getText();
                    the_expr = expr.split("[-]");

                    if (the_expr.length == 2) {
                        arithStmt = op_code + the_expr[0] + " " + the_expr[1] + " $" + register;
                        codeList.add(arithStmt);
                    }
                    break;
                case "*":
                    op_code = ";MULT" + t;
                    expr = ctx.expr().getText();
                    the_expr = expr.split("[*]");

                    if (the_expr.length == 2) {
                        arithStmt = op_code + the_expr[0] + " " + the_expr[1] + " $" + register;
                        codeList.add(arithStmt);
                    }
                    break;

                case "/":

                    String store = null;
                    op_code = ";DIV" + t;

                    System.out.println("LINE 237: Need to figure out division algorithm for test3\n");
                    if (isLit().compareTo("int") == 0) {
                        store = ";STOREI " + "$" + register + " " + arith_val +
                                "\n" + op_code + "$" + register + " $T";
                        int reg = reg_count+1;
                        store += reg;
                        codeList.add(store);
                    } else if (isLit().compareTo("float") == 0) {
                        store = ";STOREF " + "$" + register + " " + arith_val +
                                "\n"+ op_code + "$" + register + " $T";
                        int reg = reg_count+1;
                        store += reg;
                        codeList.add(store);
                    }
                    else {
                        expr = ctx.expr().getText();
                        the_expr = expr.split("[/]");

                        if (the_expr.length == 2) {
                            arithStmt = op_code + the_expr[0] + " " + the_expr[1] + " $" + register;
                            codeList.add(arithStmt);
                        }
                    }
                    break;
            }
            // There is a store at the end of every arithmetic operation
            storeVars(ctx, register, op, leftHandSide);
        }
    }

    public String VarType() {
        if (type.compareTo("FLOAT") == 0) {
            return "F ";
        } else if (type.compareTo("INT") == 0) {
            return "I ";
        }

        return " ";
    }

    public void storeVars(LittleParser.Assign_exprContext ctx, String register, String op, String lhs) {
        String t = VarType();
        String op_code = null;
        String store = null;
        op_code = ";STORE" + t;

        if (op == null) {
            store = op_code + ctx.expr().getText() + " $" + register + "\n" +
                    op_code + "$" + register + " " + lhs;
            codeList.add(store);
        } else { // for end of arithmetic operations
            store = op_code + "$" + register + " " + lhs;
            codeList.add(store);
        }


    }

    public String isLit() {

        try {
            float val = Float.parseFloat(arith_val);
            if(val % 1 != 0){
                return "float";
            }
            else{
                return "int";
            }
        }
        catch(Exception e) {
            return " ";
        }
    }

    @Override
    public void enterString_decl(LittleParser.String_declContext ctx) {

    }

    public void prettyPrint() {
        //print all symbol tables in the order they were created
        //print out symlist in order tables were made

//            for (int i=0; i<tableList.size(); i++) {
//                HashMap<String,ArrayList> curr = tableList.get(i);
//
//                curr.entrySet().forEach(entry->{
//                    System.out.println(entry.getKey() + " " + entry.getValue());
//                });
//            }// end for

        for (int i = 0; i < codeList.size(); i++) {
            String code = codeList.get(i);
            System.out.println(code);
        }

    }// end print
}