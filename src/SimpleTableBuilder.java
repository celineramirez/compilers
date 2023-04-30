import java.sql.Array;
import java.util.*;

public class SimpleTableBuilder extends LittleBaseListener {

    ArrayList<String> codeList = new ArrayList<>(); // list of IR Code

    // global constants
    String type = null; // Get the type of the variables collected in varlist
    int reg_count = 0; // register counter
    String arith_val = null; // gets the current id/literal being parsed
    HashMap<String,String[]> varmap = new HashMap<String, String[]>(); // map of all declared variables and their values
    HashMap<String,String> storedVars = new HashMap<>(); // all variables where register values were stored

    public void enterProgram(LittleParser.ProgramContext ctx) {
    }
    @Override
    public void enterFunc_decl(LittleParser.Func_declContext ctx) {
    }

    @Override
    public void exitFunc_decl(LittleParser.Func_declContext ctx) {
        codeList.add("sys halt");
    }

    @Override
    public void enterVar_decl(LittleParser.Var_declContext ctx) {
        type = ctx.var_type().getText();
        String [] varlist = ctx.id_list().getText().split(",");

        if (varmap.containsKey(type)) {
            String[] array = varmap.get(type);
            String[] newArray = new String[array.length + varlist.length];
            System.arraycopy(array, 0, newArray, 0, array.length);
            for (int i = 0; i < varlist.length; i++) {
                newArray[array.length+i] = varlist[i];
            }
            for (String s : varlist) {
                codeList.add("var " + s);
            }
            varmap.put(type, newArray);
        }
        else {
            for (String s : varlist) {
                codeList.add("var " + s);
            }
            varmap.put(type, varlist); // put the type and variable list into a hashmap
        }
    }

    @Override public void enterString_decl(LittleParser.String_declContext ctx) {
        String [] string_list = ctx.id().getText().split(",");
        type = "STRING";

        if(varmap.containsKey(type)){
            String[] array = varmap.get(type);
            String[] newArray = new String[array.length + string_list.length];
            System.arraycopy(array, 0, newArray, 0, array.length);
            for(int i = 0; i < string_list.length; i++) {
                newArray[array.length+i] = string_list[i];
            }
            for (int i = 0; i < string_list.length; i++) {
                codeList.add("str " + ctx.id().getText() + " " + ctx.str().getText());
            }
            varmap.put(type, newArray);
        }
        else {
            for (int i = 0; i < string_list.length; i++) {
                codeList.add("str " + ctx.id().getText() + " " + ctx.str().getText());
            }
            varmap.put(type, string_list); // put the type and variable list into a hashmap
        }
    }

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
            if (the_type != null && the_type.compareTo("FLOAT") == 0) {
                codeList.add("sys readr " + find);
            }
            else if (the_type != null && the_type.compareTo("INT") == 0) {
                codeList.add("sys readi " + find);
            }
            else if (the_type != null && the_type.compareTo("STRING") == 0) {
                codeList.add("sys reads " + find);
            }
        }
    }

    @Override
    public void enterWrite_stmt(LittleParser.Write_stmtContext ctx) {
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
            if (the_type != null && the_type.compareTo("FLOAT") == 0) {
                codeList.add("sys writer " + find);
            }
            else if (the_type != null && the_type.compareTo("INT") == 0) {
                codeList.add("sys writei " + find);
            }
            else if (the_type != null && the_type.compareTo("STRING") == 0) {
                codeList.add("sys writes " + find);
            }
        }
    }

    @Override public void enterPrimary(LittleParser.PrimaryContext ctx) {
        arith_val = ctx.getText();
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
        register = "r" + reg_count;
        reg_count++;
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
            arith_val = ctx.expr().getText();
            storeVars(ctx, register, op, leftHandSide);
        }

        // operator exists, binary assignment a := b + c
        else {

            String arithStmt; // IR code to enter in codeList
            String t = VarType(); // type of variable currently being parsed
            String m = "move ";

            switch (op) {

                case "+":
                    t = VarType();
                    op_code = "\nadd" + t;

                    if (storedVars.containsKey(the_expr[0])) {

                        String new_reg = storedVars.get(the_expr[0]);
                        arithStmt = op_code + the_expr[1] + " " + new_reg +
                                "\n" + m + new_reg + " " + leftHandSide;

                        if(regExists(new_reg) != null && !Objects.equals(regExists(new_reg), leftHandSide)){
                            String k = regExists(new_reg);
                            storedVars.remove(k);
                        }

                        storedVars.put(leftHandSide, new_reg);

                        codeList.add(arithStmt);
                    }

                    else if(storedVars.containsKey(the_expr[1])){

                        String new_reg = storedVars.get(the_expr[0]);
                        arithStmt = op_code + the_expr[1] + " " + new_reg +
                                "\n" + m + new_reg + " " + leftHandSide;

                        if(regExists(new_reg) != null && !Objects.equals(regExists(new_reg), leftHandSide)){
                            String k = regExists(new_reg);
                            storedVars.remove(k);
                        }

                        storedVars.put(leftHandSide, new_reg);

                        codeList.add(arithStmt);

                    }

                    else {
                        if (the_expr.length == 2) {
                            arithStmt = m + the_expr[0] + " " + register
                                    + op_code + the_expr[1] + " " + register +
                                    "\n" + m + register + " " + leftHandSide;

                            storedVars.put(leftHandSide, register);

                            codeList.add(arithStmt);
                        }
                    }
                    break;
                case "-":
                    t = VarType();
                    op_code = "\nsub" + t;

                    if (storedVars.containsKey(the_expr[0])) {

                        String new_reg = storedVars.get(the_expr[0]);
                        arithStmt = op_code + the_expr[1] + " " + new_reg +
                                "\n" + m + new_reg + " " + leftHandSide;

                        if(regExists(new_reg) != null && !Objects.equals(regExists(new_reg), leftHandSide)){
                            String k = regExists(new_reg);
                            storedVars.remove(k);
                        }

                        storedVars.put(leftHandSide, new_reg);

                        codeList.add(arithStmt);

                    }

                    else if(storedVars.containsKey(the_expr[1])){

                        String new_reg = storedVars.get(the_expr[0]);
                        arithStmt = op_code + the_expr[1] + " " + new_reg +
                                "\n" + m + new_reg + " " + leftHandSide;

                        if(regExists(new_reg) != null && !Objects.equals(regExists(new_reg), leftHandSide)){
                            String k = regExists(new_reg);
                            storedVars.remove(k);
                        }

                        storedVars.put(leftHandSide, new_reg);

                        codeList.add(arithStmt);

                    }

                    else {
                        if (the_expr.length == 2) {
                            arithStmt = m + the_expr[0] + " " + register
                                    + op_code + the_expr[1] + " " + register +
                                    "\n" + m + register + " " + leftHandSide;

                            storedVars.put(leftHandSide, register);

                            codeList.add(arithStmt);
                        }
                    }
                    break;
                case "*":

                    arith_val = the_expr[1];
                    t = VarType();
                    op_code = "\nmul"+t;

                    if (storedVars.containsKey(the_expr[0])) {

                        if (the_expr.length == 2) {

                            String new_reg = storedVars.get(the_expr[0]);
                            arithStmt = op_code + the_expr[1] + " " + new_reg +
                                    "\n" + m + new_reg + " " + leftHandSide;

                            if(regExists(new_reg) != null && !Objects.equals(regExists(new_reg), leftHandSide)){
                                String k = regExists(new_reg);
                                storedVars.remove(k);
                            }

                            storedVars.put(leftHandSide, new_reg);

                            codeList.add(arithStmt);

                        }
                    }

                    else if(storedVars.containsKey(the_expr[1])){

                        String new_reg = storedVars.get(the_expr[0]);
                        arithStmt = op_code + the_expr[1] + " " + new_reg +
                                "\n" + m + new_reg + " " + leftHandSide;

                        if(regExists(new_reg) != null && !Objects.equals(regExists(new_reg), leftHandSide)){
                            String k = regExists(new_reg);
                            storedVars.remove(k);
                        }

                        storedVars.put(leftHandSide, new_reg);

                        codeList.add(arithStmt);
                    }

                    else {
                        if (the_expr.length == 2) {
                            arithStmt = m + the_expr[0] + " " + register
                                    + op_code + the_expr[1] + " " + register +
                                    "\n" + m + register + " " + leftHandSide;

                            storedVars.put(leftHandSide, register);

                            codeList.add(arithStmt);
                        }
                    }
                    break;

                case "/":

                    arith_val = the_expr[1];
                    t = VarType();
                    String store = null;
                    op_code = "\ndiv"+t;

                        if (storedVars.containsKey(the_expr[0])) {

                            if (the_expr.length == 2) {

                                String new_reg = storedVars.get(the_expr[0]);
                                arithStmt = op_code + the_expr[1] + " " + new_reg +
                                        "\n" + m + new_reg + " " + leftHandSide;

                                if(regExists(new_reg) != null && !Objects.equals(regExists(new_reg), leftHandSide)){
                                    String k = regExists(new_reg);
                                    storedVars.remove(k);
                                }

                                storedVars.put(leftHandSide, new_reg);

                                codeList.add(arithStmt);
                            }
                        } else if (storedVars.containsKey(the_expr[1])) {

                            String new_reg = storedVars.get(the_expr[0]);
                            arithStmt = op_code + the_expr[1] + " " + new_reg +
                                    "\n" + m + new_reg + " " + leftHandSide;

                            if(regExists(new_reg) != null && !Objects.equals(regExists(new_reg), leftHandSide)){
                                String k = regExists(new_reg);
                                storedVars.remove(k);
                            }

                            storedVars.put(leftHandSide, new_reg);

                            codeList.add(arithStmt);

                        } else {
                            if (the_expr.length == 2) {
                                arithStmt = m + the_expr[0] + " " + register
                                        + op_code + the_expr[1] + " " + register +
                                        "\n" + m + register + " " + leftHandSide;

                                storedVars.put(leftHandSide, register);

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
        // find the variables used in the write statement in the hashmap, and return their type
        String find = arith_val;
        String the_type = null;

        //if it is a literal
        if(isLit()){
            if(arith_val.contains(".")){
                return "r "; // is float
            }
            else{

                return "i "; // is int
            }
        }
        else {
            // find the variable in the hashmap then return its type
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
                    return "r ";
                } else if (the_type.compareTo("INT") == 0) {
                    return "i ";
                } else if (the_type.compareTo("STRING") == 0) {
                    return "s ";
                }
            }
        }
        return " ";
    }

    // check if the register is already being used for another variable
    public String regExists(String reg){
        // iterate each entry of hashmap
        for(Map.Entry<String, String> entry: storedVars.entrySet()) {

            // if register is already being used for a variable
            if(Objects.equals(entry.getValue(), reg)) {
                return entry.getKey(); // return the variable name
            }
        }
        return null;
    }

    // loads and stores
    public void storeVars(LittleParser.Assign_exprContext ctx, String register, String op, String lhs) {
        String t = VarType();
        String op_code;
        String store;
        op_code = "move ";

        if (op == null) {
            store = op_code + ctx.expr().getText() + " " + lhs;
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