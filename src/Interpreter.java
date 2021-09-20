import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Interpreter {
    AST tree;

    // hashmap for all variables hashmap of hashmaps
    //stores function parameters
    HashMap<String, HashMap<String, Symbol>> symbol_tables = new HashMap<>();
    Stack<String> current_scope = new Stack<>();

    Stack<HashMap<String, Symbol>> exec_stack = new Stack<>();

    ScriptEngineManager manager = new ScriptEngineManager();
    ScriptEngine engine = manager.getEngineByName("JavaScript");
    public boolean has_ide = false;
    public IDE ide;

    public Interpreter(AST tree) {
        this.tree = tree;
    }

    // constructor
    public static Interpreter c(AST tree){
        return new Interpreter(tree);
    }

    //print out to output.txt
    public void output_log(String output){
        File path = new File("output.txt");
        BufferedWriter wr;
        try { wr = new BufferedWriter(new FileWriter(path, true));
            wr.write(output);
            wr.close();
        } catch (IOException ex) {
            System.out.println("error");
        }
    }

    // print errors to error.txt
    public void error_log(String output){
        File path = new File("error.txt");
        BufferedWriter wr;
        try { wr = new BufferedWriter(new FileWriter(path, true));
            wr.write(output + "\n");
            wr.close();
        } catch (IOException ex) {
            System.out.println("error");
        }
    }

    //clears output.txt
    public void clear_log(){
        File path = new File("output.txt");
        BufferedWriter wr;
        try { wr = new BufferedWriter(new FileWriter(path));
            wr.write("");
            wr.close();
        } catch (IOException ex) {
            System.out.println("error");
        }

    }

    //walks the abstract syntax tree starting with rightmost derivation
    public void walk_tree(AST tree){

        switch(tree.rule){
            case "PROGRAM_STRUC":  program(tree); break;
            case "FUNCTION_LIST":
                tree.nodeList.forEach(tr -> walk_tree(tr));
                break;
            case "FUNCTION_DEC":
                if(current_scope.peek().equals("GLOBAL")){ function_declaration(tree); }
                else System.out.println("Functions must be declared on a global scope");
                break;
            case "VARIABLE_DEC":
                if(current_scope.peek().equals("GLOBAL")){
                    variable_declaration(tree, current_scope.peek());
                }
                else System.out.println("Variables must be declared outside before  ");
                break;
            case "BLOCK":
                tree.getTreeNode("STATEMENT_LIST").getNodeList().forEach(st -> walk_tree(st));
                break;
            case "STATEMENT":// System.out.println("STATEMENT");
                walk_tree(tree.nodeList.get(0)); break;
            case "IF_STATEMENT":  if_statement_then(tree); break;
            case "FOR_LOOP": for_loop(tree); break;
            case "ASSIGNMENT": assignment(tree); break;
            case "WRITE": writeln(tree); break;
            case "READ": readln(tree); break;
            case "FUNC_CALL": func_call(tree); break;

        }
    }

    //start of program block
    public void program(AST tree){
        HashMap<String, Symbol> global_variables = new HashMap<>();
        String scope = "GLOBAL";
        symbol_tables.put(scope, global_variables);
        current_scope.push("GLOBAL");
        tree.nodeList.forEach(tr -> walk_tree(tr));
    }

    //add variable to symbol table
    public void addVariable(AST variable, String scope){
        String datatype = variable.getTreeNode("DATA_TYPE").token.lexeme.toUpperCase();
        List<AST> identifiers = variable.getNodeList().stream().filter(tr->tr.rule.equals("IDENTIFIER")).collect(Collectors.toList());

        for(AST identifier: identifiers){
            String id = identifier.token.lexeme;
            if(!symbol_tables.get(scope).containsKey(id)){
                symbol_tables.get(scope).put(id, Symbol.c(id,datatype, scope, identifier));
            }else{
                System.out.println("Line " + identifier.line + ": variable already declared in scope" );
            }
        }
    }

    //add function to symbol table
    public void addFunction(String id, AST exec_block, AST params, String return_type, String scope){
        List<AST> parameter_list = params.getNodeList().stream().filter(tr->tr.rule.equals("VARIABLE")).collect(Collectors.toList());
        HashMap<String, Symbol> params_map = new HashMap<>();
        //pu function id and block to symbol table
        symbol_tables.get(scope).put(id, Symbol.c(id, return_type, scope, exec_block));
        //add empty symbol table got function parameters
        symbol_tables.put(id, params_map);
        //add variables to the parameter list
        for(AST param: parameter_list){
            addVariable(param, id);
        }
    }

    public void print_function(String id){
        System.out.print("FUNCTTION: ");
        System.out.print(symbol_tables.get("GLOBAL").get(id).name + " ");
        symbol_tables.get(id).values().forEach(s -> System.out.print('-'+ s.name + '-'));
        System.out.println("");
    }

    //checks to see if function has already been declared and if not then call addfunction adn variable declaration
    public void function_declaration(AST tree){

        String scope = "GLOBAL";

        String id = tree.getTreeNode("IDENTIFIER").token.lexeme;
        AST params = tree.getTreeNode("VARIABLE_LIST");
        AST return_type = tree.getTreeNode("DATA_TYPE");
        AST variable_dec = tree.getTreeNode("VARIABLE_DEC");
        AST bt = tree.getTreeNode("BLOCK");

        String type = "";


        if(!symbol_tables.get("GLOBAL").containsKey(id)){

            if(return_type!=null)
                type = return_type.token.lexeme;
            addFunction(id, tree, params, type, scope);
            variable_declaration(variable_dec, id);
            //print_function(id);
        }else{
            System.out.println("Function already declared");
        }
    }

    //checks to see if variable is already declared
    public void variable_declaration(AST tree, String scope){
        if(tree != null){
            AST variable_list = tree.getTreeNode("VARIABLE_LIST");
            if(variable_list != null)
            for(AST var : variable_list.getNodeList().stream().filter(tr -> tr.rule.equals("VARIABLE")).collect(Collectors.toList()) ) {
                addVariable(var, scope);
        }}
    }

    String formatExpression(AST tree){
        String expression = "";
        String type = "none";
        for(AST t: tree.nodeList){
            //System.out.println("formating:  " + t.token.lexeme );
            // Parser.print_nodes(t, 0);
            String rule = t.getRule();
            String lexeme = t.token.lexeme;
            String operation = "none";
           // System.out.println("RULE: " + rule + " token " + lexeme);

            if(rule.equals("INTEGER") || rule.equals("REAL")){
                    type = "number";
                    expression += lexeme;
                   // System.out.println(expression);
            }
            else if(t.getRule().equals("LPAREN") || t.getRule().equals("RPAREN")){
                if(!type.equals("string"))  expression += lexeme;
                else return "inc";
            }
            else if( lexeme.equals("true") || lexeme.equals("false")){
                if(operation.equals("relational") || operation.equals("none") ||operation.equals("boolean") )
                { type = "boolean";  expression += lexeme; } else return "inc";
            }else if(rule.equals("STRING")){
                if(type.equals("string")){ expression += lexeme;
                }else return "inc";
            }
            else if( rule.equals("ARITHMETIC") ){
                operation = "arithmetic";
                if(type.equals("string") && lexeme.equals("+")){ expression += lexeme; }
                else if(type.equals("number")){ expression += lexeme; }else return "inc";
            }else if(rule.equals("RELATIONAL")){
                operation = "relational";
                if(lexeme.equals("=")){ expression += "==";
                }else if(lexeme.equals("<") || lexeme.equals("<=") || lexeme.equals(">=") || lexeme.equals(">")){
                    expression += lexeme;
                    //System.out.println(expression);
                } else return "inc";
            }else if(rule.equals("BOOLEAN")){
                operation = "relational";
                if(type.equals("number") || type.equals("boolean")){
                    if(lexeme.equals("not")){        expression += "!"; }
                    else if(lexeme.equals("and")){  expression += "&&";
                    }else if(lexeme.equals("or")){   expression += "||"; }
                }else return "inc";

            } else if(t.getRule().equals("IDENTIFIER")){
                // System.out.println(t.token.lexeme);
                String id = t.token.lexeme;
                String scope = current_scope.peek();

                if( symbol_tables.get(scope).containsKey(id)){
                    String datatype = symbol_tables.get(scope).get(id).type;
                    String value = symbol_tables.get(scope).get(id).val;

                    if(value.equals("")){ System.out.println("Line " + t.token.line + " : Variable " + id + " in expression has no value");  return "id"; }
                    else if(datatype.equals("string") && type.equals("string") || type.equals("none")) {
                        type = "string";
                    }expression += value;

                }else if( symbol_tables.get("GLOBAL").containsKey(id)){
                    String datatype = symbol_tables.get("GLOBAL").get(id).type;
                    String value = symbol_tables.get("GLOBAL").get(id).val;

                    if(value.equals("")){ System.out.println("Line " + t.token.line + " : Variable " + id + " in expression has no value");  return "id"; }
                    if(datatype.equals("string") && type.equals("string") || type.equals("none")) {
                        type = "string";
                    }expression += value;
                }else{
                    System.out.println("Line " + t.token.line + " : Variable " + id + " not declared");
                    error_log("Line " + t.token.line + ": variable "+  id+ " not declared");
                    return "id";
                }
            }expression += " ";
        }
        //System.out.println(expression);
        return expression.trim();
    }

    String evaluateExpression(AST tree){
        String expr = formatExpression(tree);
        String res = "";
       // System.out.println("EVALUATING: "+  expr);

        if(expr.equals("inc")) {
            System.out.println("Line " + tree.nodeList.get(0).token.line + ": expression has incompatible types");
            return "err";
        }if(expr.equals("id")){
            return "err-idenifier";
        }
        else {
            try { res = String.valueOf(engine.eval(expr)); }catch (ScriptException e) {
                //System.out.println("Line " + tree.nodeList.get(0).token.line + ": invalid expression: " + expr + tree.nodeList.get(0).token.lexeme);
                System.out.println("Line " + tree.nodeList.get(0).token.line + ": invalid expression or incompatible type");
                error_log("Line " + tree.nodeList.get(0).token.line + ": invalid expression or incompatible type");
                return res;
            }
            return res;
        }
    }

    public void for_loop(AST tree){
        AST init = tree.getTreeNode("ASSIGNMENT");
        AST end = tree.getTreeNode("EXPRESSION");
        //AST type = tree.get downto and to
        Token t;
        if(init!= null) {
            t = init.getTreeNode("IDENTIFIER").token;
            String end_expr = evaluateExpression(end);
            String id = t.lexeme;
            String init_val = evaluateExpression(init.getTreeNode("EXPRESSION"));
            AST block = tree.getTreeNode("BLOCK");
            Symbol s;

            String scope = current_scope.peek();


            if (!symbol_tables.get(scope).containsKey(id) && !symbol_tables.get("GLOBAL").containsKey(id)) {
                System.out.println("Line " + t.line + ": VARIABLE " + id + " NOT DECLARED");
            } else {
                if (end_expr.equals("") || end_expr.equals("err") || end_expr.equals("inc")) {
                    System.out.println("Line " + t.line + ": Invalid exit condition");
                } else if (isNumeric(init_val) && isNumeric(end_expr)) {
                    symbol_tables.get(scope).get(id).val = init_val.trim();
                    int a = Integer.parseInt(init_val);
                    int b = Integer.parseInt(end_expr);

                    if (a < b) {
                        for (int i = a; i <= b; i++) {
                            symbol_tables.get(scope).get(id).val = String.valueOf(i);
                            walk_tree(block);
                        }
                    } else if (a >= b) {
                        for (int i = a; i > b; i--) {
                            symbol_tables.get(scope).get(id).val = String.valueOf(i);
                            walk_tree(block);
                        }
                    }
                } else {
                    System.out.println("Line " + t.line + ": Invalid datatype in loop condition");
                    error_log("Line " + t.line + ": Invalid datatype in loop condition");
                }
            }
        }

    }

    public void while_loop(AST tree){
        AST expr = tree.getTreeNode("EXPRESSION");
        AST block = tree.getTreeNode("BLOCK");
        String condition = evaluateExpression(expr);

        while(condition.equals("true")){

        }

    }

    public void if_statement_then(AST tree){
        AST condition = tree.getTreeNode("EXPRESSION");
        List<AST> blocks = tree.nodeList.stream().filter(tr -> tr.rule.equals("BLOCK")).collect(Collectors.toList());
        String expr = evaluateExpression(condition); //System.out.println("EXP IF: " + o);
        int line = tree.getTreeNode("IF").token.line;
        //System.out.println(expr);

        if(expr.equals("true")  || expr.equals("false")){
            if(expr.equals("true") && blocks.size() > 0){
                walk_tree(blocks.get(0));
            }else if( blocks.size() > 1){
                walk_tree(blocks.get(1));
            }
        }else{
            System.out.println("Line " + line + ":  invalid expression in condition");
            error_log("Line " + line + ":  invalid expression in condition");
            return;
        }
       // if(expr.equals("err")) return;

    }

    public void writeln(AST tree){
        // System.out.println("HELLO");
        // ArrayList<AST> ex = tree.getTreeNode("EXPRESSION_LIST").getNodeList();
        String temp = "";
        System.out.print("INTERPRETER OUTPUT: ");
        for(AST ex: tree.getTreeNode("EXPRESSION_LIST").getNodeList().stream().filter(tr -> tr.rule.equals("EXPRESSION")).collect(Collectors.toList())) {
            if(ex.getTreeNode("STRING") != null){
                System.out.print( ex.getTreeNode("STRING").token.lexeme.replaceAll("\'", "") );
                temp += ex.getTreeNode("STRING").token.lexeme.replaceAll("\'", "");
            }else{
                String v = evaluateExpression(ex);
                if(!v.contains("err")){
                    System.out.print(v);
                    temp += v;
                }
            }
        }
        System.out.println("");
        temp += "\n";
        output_log(temp);
    }

    public void readln(AST tree){
        String log = "";
        for(AST ex: tree.getTreeNode("EXPRESSION_LIST").getNodeList().stream().filter(tr -> tr.rule.equals("EXPRESSION")).collect(Collectors.toList())) {
             if(ex.getTreeNode("STRING") != null){
                 String label = ex.getTreeNode("STRING").token.lexeme.replace("'", "");
                System.out.println("INTERPRETER OUTPUT: "  + label);
                if(ide != null){
                    ide.txtOutput.append(label);
                }//output_log( ex.getTreeNode("STRING").token.lexeme + "\n" );
            }else if(ex.getTreeNode("IDENTIFIER") != null){

                String id = ex.getTreeNode("IDENTIFIER").token.lexeme;
                String value = "";
                if(ide == null){
                    Scanner sc = new Scanner(System.in);
                    value = sc.nextLine();
                }else {
                    value = JOptionPane.showInputDialog("Please input a value");
                    ide.txtOutput.append(value + "\n");
                }

                if(symbol_tables.get(current_scope.peek()).containsKey(id)){
                    symbol_tables.get(current_scope.peek()).get(id).val = value;
                }else if(symbol_tables.get("GLOBAL").containsKey(id)){
                    symbol_tables.get("GLOBAL").get(id).val = value;
                }else{
                    System.out.println("Line " + ex.getTreeNode("IDENTIFIER").token.line + " : variable " + id + " not declared");
                    error_log("Line " + ex.getTreeNode("IDENTIFIER").token.line + " : variable " + id + " not declared");

                }
            }
        }
    }


    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    public void func_call(AST tree){
        AST args = tree.getTreeNode("EXPRESSION_LIST");
        String func_id = tree.getTreeNode("IDENTIFIER").token.lexeme;
        int line = tree.getTreeNode("IDENTIFIER").token.line;
        List<AST> list = args.getNodeList().stream().filter(arg-> arg.rule.equals("EXPRESSION")).collect(Collectors.toList());

        //System.out.println("CALLING "+ id);
        // System.out.println("PARAMETERS " + symbol_tables.get(id).keySet().toString());
        // System.out.println("===ARGUMENTS=== ");
        //list.stream().forEach(ar -> System.out.println(evaluateExpression(ar)));
        //System.out.println("============ ");
        if(symbol_tables.get("GLOBAL").containsKey(func_id)){
            //current_scope.push(id);
            List<AST> parameters = symbol_tables.get("GLOBAL").get(func_id).tree.getTreeNode("VARIABLE_LIST").getNodeList().stream().filter(tr -> tr.rule.equals("VARIABLE")).collect(Collectors.toList());
            int param_count = 0;
            for (AST parameter : parameters){
                param_count += parameter.getNodeList().stream().filter(tr -> tr.rule.equals("IDENTIFIER")).collect(Collectors.toList()).size();
            }

            if(list.size() < param_count){
                System.out.println("Line" + line + ": missing argument for function");
                error_log("Line " + line + ": missing argument for function");
            }else if(list.size() == param_count){

                Iterator params = symbol_tables.get(func_id).entrySet().iterator();
                int i = 0;
               // current_scope.push(func_id);

                //load arguments to function
                while (i < param_count) {
                    Map.Entry<String, Symbol> pair = (Map.Entry)params.next();
                    Symbol variable = pair.getValue();
                    String datatype = pair.getValue().type;
                    String variable_name = pair.getKey();

                    variable.val =  evaluateExpression(list.get(i));;
                    symbol_tables.get(func_id).replace(variable_name ,variable);

                    i++;

                    //System.out.println("INPUT ARGUMENT "+ evaluateExpression(list.get(i)));
                    //it.remove(); // avoids a ConcurrentModificationException
                }
            }

            //execute function
            AST function_block = symbol_tables.get("GLOBAL").get(func_id).tree.getTreeNode("BLOCK");
            current_scope.push(func_id);
            walk_tree(function_block);
            current_scope.pop();

        }else{
            System.out.println("function does not exist");
        }

    }

    public void assignment(AST tree){
        String id = tree.getTreeNode("IDENTIFIER").token.lexeme;
        int line = tree.getTreeNode("IDENTIFIER").token.line;
        AST expression = tree.getTreeNode("EXPRESSION");
        String ex = evaluateExpression(expression);

        String scope = current_scope.peek();

        if(symbol_tables.get(scope).containsKey(id)){
            symbol_tables.get(scope).get(id).val = ex;
        }else if(symbol_tables.get("GLOBAL").containsKey(id)){
            symbol_tables.get("GLOBAL").get(id).val = ex;
        }else{
            System.out.println("Line " + line + ": variable "+  id+ " not declared");
            error_log("Line " + line + ": variable "+  id + " not declared");
        }
        // System.out.print("ASSIGNMENT " + id + "=" + ex);
        // System.out.print("SCOPE ASSIGnMENT " + scope);

    }




}
