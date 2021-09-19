import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.ListIterator;

public class Parser {
    enum G {
        PROGRAM_STRUC, program, IDENTIFIER, SEMICOLON, VARIABLE_DEC, DOT,
        BLOCK, VARIABLE, COLON, DATA_TYPE, var, VARIABLE_LIST, FUNCTION_LIST, FUNCTION_DEC,
        LPAREN, RPAREN, begin, end, STATEMENT, IF_STATEMENT, FOR_LOOP, EXPRESSION, ASSIGNMENT,
        FUNC_CALL, IF, ELSE, THEN, INTEGER, REAL, OPERATION, ASSIGN, FOR, TO, STRING, BOOLEAN,
        STATEMENT_LIST, COMMA, ARITHMETIC, RELATIONAL, DO, EXPRESSION_LIST, FUNC_PREFIX, readln, writeln, READ, WRITE
    }

    String error = "";

    public AST tree = null;
    public ArrayList<Token> tokens;
    int current = 0;
    ListIterator<Token> it;
    Token t;
    boolean match_parenthesis = false;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
        this.it = tokens.listIterator();
    }


    public static void print_nodes(AST tree, int depth) {
        String s = "";
        for (int i = 0; i < depth; i++) {
            s += "|";
        }
        s += "-";
        System.out.println(s + tree.rule);
        for (AST ast : tree.nodeList) {
            print_nodes(ast, depth + 1);
        }
    }

    public AST VARIABLE_DEC() {
        String rule = G.VARIABLE_DEC.name();
        AST node = AST.c(rule);
        //System.out.println("VARIABLE");
        AST var = var();

        if(!isEmptyNode(var)){
            node.addNode(var);
            next();

            AST vl = VARIABLE_LIST();
            node.addNode(vl);

            return node;
        }else
            return node;
    }


    public AST VARIABLE_LIST() {
        String rule = G.VARIABLE_LIST.name();
        AST node = AST.c(rule);
        AST v = VARIABLES();
        while (!isEmptyNode(v)) {
            node.addNode(v);
            AST s = SEMICOLON();
            if(!isEmptyNode(s)){
                node.addNode(s);
                next();
                v = VARIABLES();
            }else{
                v = empty();
            }
        }

        return node;
    }

    public AST VARIABLES() {
        String rule = G.VARIABLE.name();
        AST node = AST.c(rule);
        AST id = IDENTIFIER();
        if (!isEmptyNode(id)){
            //System.out.println("variables");
            node.addNode(id);
            next();
            while (!isEmptyNode(COMMA())) {
                next();
                id = IDENTIFIER();
                if(!isEmptyNode(id)) {
                    node.addNode(id);
                    next();
                }else break;
            }

            AST c = COLON();
            if (isEmptyNode(c)) {
                System.out.println("Line " + t.line + ": Missing :");
                error = error.concat("Line " + t.line + ": Missing :\n");
                AST d = DATATYPE();
                if (isEmptyNode(d)) {
                    System.out.println("Line " + t.line + ": Missing datatype");
                    error = error.concat("Line " + t.line + ": Missing datatype\n");
                    return empty();
                }

                return empty();
            } else {
                node.addNode(c);
                next();

                AST d = DATATYPE();
                if (isEmptyNode(d)) {
                    System.out.println("Line" + t.line + ": Missing datatype");
                    error = error.concat("Line" + t.line + ": Missing datatype\n");
                    return empty();
                } else {
                    node.addNode(d);
                    next();
                }
            }



        }else
            return empty();


        return node;
    }

    public AST FUNCTION_LIST() {
        String rule = G.FUNCTION_LIST.name();
        AST node = AST.c(rule);
        AST f = FUNCTION_DEC();
        while (!isEmptyNode(f)) {
          //  System.out.println("Has function");
            node.addNode(f);
            f = FUNCTION_DEC();
        }
        return node;
    }

    public AST FUNCTION_DEC() {
        String rule = G.FUNCTION_DEC.name();
        AST node = AST.c(rule);

        AST proc = FUNC_PREFIX();
        //System.out.println("no");
        if (isEmptyNode(proc)) {
            return empty();
        } else {
            node.addNode(proc);
           // System.out.println("function keyword");
            next();
        }

        AST id = IDENTIFIER();
        if (isEmptyNode(id)) {
            System.out.println("Line " + t.line + " Missing function name ");
            error = error.concat("Line" + t.line + " Missing function name\n");
            return empty();
        } else {
            //System.out.println("FUNC NAME: " + t.lexeme);
            node.addNode(id);
            next();
        }

        AST l = LPAREN();
        if (isEmptyNode(l)) {
            System.out.println("Line " + t.line + ": Missing (");
            error = error.concat("Line" + t.line + ": Missing (\n");
        } else {
            node.addNode(l);
            next();
        }

        AST p = VARIABLE_LIST();
        node.addNode(p);

        AST r = RPAREN();
        if (isEmptyNode(r)) {
            System.out.println("Line " + t.line + ": Missing )");
            error = error.concat("Line" + t.line + ": Missing )\n");
            return empty();
        } else {
            node.addNode(r);
            next();
        }

        AST c = COLON();
        if (!isEmptyNode(c)) {
            node.addNode(c);
            next();
            AST d = DATATYPE();
            if (!isEmptyNode(d)) {
                node.addNode(d);
                next();
            } else {
                System.out.println("Line " + t.line + ": Missing return type for function");
                error = error.concat("Line" + t.line + ": Missing return type for function\n");
            }
        } else {
            System.out.println("Line " + t.line + ": Missing colon for return type");
            error = error.concat("Line" + t.line + ": Missing colon for return type\n");
            return empty();
        }


        AST v = VARIABLE_DEC();
        node.addNode(v);


        AST b = BLOCK();
        if (isEmptyNode(b)) {
            System.out.println("Line " + t.line + ": Missing block statement");
            error = error.concat("Line" + t.line + ": Missing block statement\n");
            //next();
        } else {
            node.addNode(b);
            next();
            AST ss = SEMICOLON();
            if (isEmptyNode(ss)) {
                System.out.println("Line " + t.line + ": Missing ;");
                error = error.concat("Line" + t.line + ": Missing ;\n");
            } else {
                node.addNode(ss);
                next();
            }
        }
        return node;

    }

    public AST FUNC_PREFIX() {
        String rule = G.FUNC_PREFIX.name();
        AST node = AST.c(rule);
       // System.out.println("FUNC_PREFIX:" + t.lexeme);
        if (t.lexeme.equals("function")) {
            node.token = t;
            return node;
        } else return empty();

    }

    public AST PROC_PREFIX() {
        String rule = G.FUNC_PREFIX.name();
        AST node = AST.c(rule);

        if (t.lexeme.equals("procedure")) {
            node.token = t;
            return node;
        } else return empty();
    }


    public AST DATATYPE() {
        String rule = G.DATA_TYPE.name();
        AST node = AST.c(rule);
        // System.out.println("DATATYPE:" + t.lexeme);
        if (t.lexeme.equals("integer") || t.lexeme.equals("string") || t.lexeme.equals("boolean") || t.lexeme.equals("real") || t.lexeme.equals("character") || t.lexeme.equals("void")) {
            node.token = t;
            return node;
        } else
            return empty();

    }

    public AST READ() {
        String rule = G.READ.name();
        AST node = AST.c(rule);

        AST a = READLN();
        if (isEmptyNode(a)) {
            return empty();
        } else {
            //System.out.println("Keyword readln");
            node.addNode(a);
            next();
        }


        ;
        a = LPAREN();
        if (isEmptyNode(a)) {
            System.out.println("Line " + t.line + ": Missing (");
            error = error.concat("Line" + t.line + ": Missing (\n");
            return empty();
        } else {
            node.addNode(a);
            next();
        }
        match_parenthesis = true;
        a = EXPRESSION_LIST();
        node.addNode(a);
        match_parenthesis = false;

        a = RPAREN();
        if (isEmptyNode(a)) {
            System.out.println("Line " + t.line + ": missing )");
            error = error.concat("Line" + t.line + ": Missing )\n");
            return empty();
        } else {
            node.addNode(a);
            next();
        }

        return node;
    }

    public AST WRITE() {
        String rule = G.WRITE.name();
        AST node = AST.c(rule);

        AST a = WRITELN();
        if (isEmptyNode(a)) {
            return empty();
        } else {
            node.addNode(a);
            next();
        }
        ;
        AST l = LPAREN();
        if (isEmptyNode(l)) {
            System.out.println("Line" + t.line +": Missing (");
            error = error.concat("Line" + t.line +": Missing (\n");
            return empty();
        } else {
            node.addNode(l);
            next();
        }

        match_parenthesis = true;
        AST ex = EXPRESSION_LIST();
        node.addNode(ex);
        match_parenthesis = false;
        ;
        AST r = RPAREN();
        if (!isEmptyNode(r)) {
            node.addNode(r);
            next();
        } else{
            System.out.println("Line " + t.line + ": Parenthesis mismatch");
            error = error.concat("Line" + t.line +": Parenthesis mismatch\n");
            return empty();
        }

        return node;
    }


    public AST Expression() {
        String rule = G.EXPRESSION.name();
        AST node = AST.c(rule);
        AST rh = RHS();
        while (!isEmptyNode(rh)) {
           // System.out.println("RHS " + rh.token.lexeme);
            node.addNode(rh);
            rh = RHS();
        }

        //if (node.nodeList.isEmpty()) return empty();

        return node;
    }

    public AST RHS() {
        AST s;
        //System.out.println("mmm: " + match_parenthesis);
            s = LPAREN();
            if(!isEmptyNode(s) && match_parenthesis == true){

                return empty();
             }
            if (!isEmptyNode(s)) {
                next();
                return s;
            }

            s = RPAREN();
            if(!isEmptyNode(s) && match_parenthesis == true){
              //  System.out.println("REJECTED RP");
                return empty();
            }
            if (!isEmptyNode(s)) {
               // System.out.println("ACCEOTED RP");
                next();
                return s;
            }

            s = INVALID();
        if (!isEmptyNode(s)) {
            // System.out.println("idee");
            next();
            return s;
        }


        s = IDENTIFIER();
        if (!isEmptyNode(s)) {
           // System.out.println("idee");
            next();
            return s;
        }
        s = REAL();
        if (!isEmptyNode(s)) {
            next();
            return s;
        }
        s = STRING();
        if (!isEmptyNode(s)) {
           // System.out.println("STRRING");
            next();
            return s;
        }
        s = INTEGER();
        if (!isEmptyNode(s)) {
            next();
            return s;
        }
        s = BOOLEAN();
        if (!isEmptyNode(s)) {
            next();
            return s;
        }
        s = TRUE();
        if (!isEmptyNode(s)) {
            next();
            return s;
        }
        s = FAlSE();
        if (!isEmptyNode(s)) {
            next();
            return s;
        }
        s = RELATIONAL();
        if (!isEmptyNode(s)) {
            next();
            return s;
        }
        s = ARITHMETIC();
        if (!isEmptyNode(s)) {
            next();
            return s;
        }
        s = FUNC_CALL();
        if (!isEmptyNode(s)) {
            next();
            return s;
        }

        return empty();
    }

    public AST ASSIGNMENT() {
        String rule = G.ASSIGNMENT.name();
        AST node = AST.c(rule);
        int save = current;
        AST id = IDENTIFIER();
        if (isEmptyNode(id)) {  /*System.out.println("SKIP ASSIGNMENT No identifier");*/
            return empty();
        } else {
            node.addNode(id);
            next();
        }

        AST as = ASSIGN();
        if (isEmptyNode(as)) {  /*System.out.println("SKIP ASSIGNMENT No ASSIGN");*/
            previous();
            return empty();
        } else {
           // System.out.println(t.line + " - ASSIGGNMENTT");
           // System.out.println(t.line + " - ASSIGGNMENTT");
            node.addNode(as);
            next();
        }

        AST rh = Expression();
        if (isEmptyNode(rh)) {
            System.out.println("line " + t.line + ": No value assignment");
            error = error.concat("Line" + t.line +": No value assignment\n");
        } else {
            node.addNode(rh);
        }

        return node;
    }

    public AST FOR_LOOP() {
        String rule = G.FOR_LOOP.name();
        AST node = AST.c(rule);

        /*System.out.println("FOR_LOOP");*/

        AST f = FOR();
        if (isEmptyNode(f)) {  /*System.out.println("SKIP FOR No FOR"); */
            return empty();
        } else {
            node.addNode(f);
            next();
        }

        AST a = ASSIGNMENT();
        if (isEmptyNode(a)) {
            System.out.println("line " + t.line + ": Missing Loop initialization");
            error = error.concat("Line" + t.line + ": Missing Loop initialization\n");
            next();
        } else {
            node.addNode(a);
        }

        AST down = DOWN();
        if (isEmptyNode(down)) {
        } else {
            node.addNode(down);
            next();
        }

        AST t = TO();
        if (isEmptyNode(t)) {
        } else {
            node.addNode(t);
            next();
        }

        AST e = Expression();
        if (isEmptyNode(e)) {
            System.out.println("line " + t.line + ": Missing loop condition");
            error = error.concat("Line" + t.line + ": Missing loop condition\n");
           // next();
        } else {
            node.addNode(e);
            //next();
        }

        AST d = DO();
        if (isEmptyNode(d)) {
            System.out.println("line " + t.line + ": Missing Keyword DO");
            error = error.concat("Line" + t.line + ": Missing Keyword DO\n");
        } else {
            node.addNode(d);
            next();
        }

        AST b = BLOCK();
        if (isEmptyNode(b)) {
            System.out.println("line " + t.line + " Missing BLOCK STATEMENT");
            error = error.concat("Line" + t.line + ": Missing BLOCK STATEMENT\n");
        } else {
            node.addNode(b);
            next();
            AST s = SEMICOLON();
            if (isEmptyNode(s)) {
//                System.out.println("line " + t.line + ": MISSING ;");
//                error = error.concat("Line " + t.line + ": MISSING ;\n");
            } else {
                node.addNode(s);
                next();
            }
        }

        return node;
    }

    public AST DOWN() {
        if (t.lexeme.equals("downto"))
            return constructAST("downto");
        else return empty();
    }


    public AST IF_THEN() {
        String rule = G.IF_STATEMENT.name();
        AST node = AST.c(rule);

        //System.out.println("IF_THEN");
        AST id = IF();
        if (isEmptyNode(id)) {  /*System.out.println("SKIP IF No if"); */
            return empty();
        } else {
            node.addNode(id);
            next();
        }

        AST l = LPAREN();
        if (isEmptyNode(l)) {
            System.out.println("line " + t.line + ": MISSING ( for condition");
            error = error.concat("Line" + t.line +": MISSING ( for condition\n");
        } else {
            node.addNode(l);
            next();
        }
        match_parenthesis = true;
        AST ex = Expression();
        if (isEmptyNode(ex)) {
            System.out.println("line " + t.line + " : Missing conditional statement");
            error = error.concat("Line" + t.line +": Missing conditional statement\n");
        } else {
            node.addNode(ex);
           // next();
        }

        match_parenthesis = false;
        AST r = RPAREN();
        if (isEmptyNode(r)) {
            System.out.println("line " + t.line + ": MISSING ( for condition");
            error = error.concat("Line" + t.line +": MISSING ( for condition\n");
        } else {
            node.addNode(r);
            next();
        }

        AST t = THEN();
        if (isEmptyNode(t)) {
            System.out.println("line " + t.line + "MISSING THEN");
            error = error.concat("Line" + t.line +": MISSING THEN\n");
        } else {
            node.addNode(t);
            next();
        }

        AST b = BLOCK();
        if (isEmptyNode(b)) {
            System.out.println("line " + t.line + ": MISSING BLOCK");
            error = error.concat("Line" + t.line +": MISSING BLOCK\n");
        } else {
            node.addNode(b);
            next();

        }

        AST e = ELSE();
        if (isEmptyNode(e)) { /*System.out.println("NO ELSE");*/ } else {
            node.addNode(e);
            next();
            b = BLOCK();
            if (isEmptyNode(b)) {
                System.out.println("line " + t.line + "MISSING BLOCK");
                error = error.concat("Line" + t.line +": MISSING BLOCK\n");
            } else {
                node.addNode(b);
                next();
            }
        }

        AST s = SEMICOLON();
        if (isEmptyNode(s)) {
            System.out.println("line " + t.line + "MISSING SEMICOLON");
            error = error.concat("Line" + t.line +": MISSING SEMICOLON\n");
        } else {
            node.addNode(s);
            next();
        }

        return node;
    }


    public void next() {
        if (this.current < tokens.size() - 1) {
            current++;
            //System.out.println(current);
            //System.out.println(tokens.size());
            t = tokens.get(current);
        }
    }

    public void previous() {
        if (this.current > 0) {
            current--;
            //System.out.println(current);
            //System.out.println(tokens.size());
            t = tokens.get(current);
        }
    }

    public void skip_line(){
        //int line = t.line;
        int current_line = t.line;
        while(t.line == current_line){
            next();
        }
    }

    public void backtrack(int save) {
        current = save;
        t = tokens.get(current);
    }

    public AST PROGRAM_STRUC() {
        String rule = G.PROGRAM_STRUC.name();
        AST node = AST.c(rule);
        boolean error = true;
        int save = current;
        t = tokens.get(current);

        AST r = program();
        node.addNode(r);
        next();
        AST id = IDENTIFIER();
        node.addNode(id);
        next();
        AST s = SEMICOLON();
        node.addNode(s);
        next();

        AST vs = VARIABLE_DEC();
        node.addNode(vs);


        AST fs = FUNCTION_LIST();
        node.addNode(fs);
        //next();

        AST b = BLOCK();
        node.addNode(b);
        next();

        AST e;
        if (current < tokens.size()) {
            e = DOT();
            if (!isEmptyNode(e)) {   node.addNode(e);}
            else System.out.println( "Missing " + t.line  + " : '.'");
        }

        return node;
    }

    public AST STATEMENT_LIST() {
        String rule = G.STATEMENT_LIST.name();
        AST node = AST.c(rule);
        AST temp = STATEMENT();
        //int save = current;
       // if(isEmptyNode(temp))System.out.println("no statement");
        while (!isEmptyNode(temp)) {
            node.addNode(temp);
            temp = STATEMENT();
        }
        return node;
    }

    public AST STATEMENT() {
        String rule = G.STATEMENT.name();
        AST node = AST.c(rule);
        AST s = ASSIGNMENT();
        if (!isEmptyNode(s)) {
            node.addNode(s);
            s = SEMICOLON();
            if (isEmptyNode(s)) {
                System.out.println("Line " + t.line + " : Missing ;");
                error = error.concat("Line " + t.line + ": Missing ;\n");
            } else {
                node.addNode(s);
                next();
            }
            return node;
        }
        //s = READ(); if(!isEmptyNode(s)){  node.addNode(s); return node;  }
        s = WRITE();
        if (!isEmptyNode(s)) {
           // System.out.println("WRITE");
            node.addNode(s);
            s = SEMICOLON();
            if (isEmptyNode(s)) {
                skip_line();
            } else {
                node.addNode(s);
                next();
            }
            return node;
        }

        s = READ();
        if(!isEmptyNode(s)){
            node.addNode(s);
            s = SEMICOLON();
            if (isEmptyNode(s)) {
                System.out.println("line " + t.line + ": MISSING ;");
                error = error.concat("Line " + t.line +": MISSING ;\n");
            } else {
                node.addNode(s);
                next();
            }
            return node;
        }

        s = FUNC_CALL();
        if (!isEmptyNode(s)) {
           // System.out.println("FUnction call");
           // System.out.println("FUnction call");
            node.addNode(s);

            s = SEMICOLON();
            if (isEmptyNode(s)) {
                System.out.println("line " + t.line + ": Syntax Error");
                error = error.concat("Line" + t.line +":  Syntax Error\n");
            } else {
                node.addNode(s);
                next();
            }
            return node;
        }

        s = IF_THEN();
        if (!isEmptyNode(s)) {
            node.addNode(s);
            return node;
        }
        s = FOR_LOOP();
        if (!isEmptyNode(s)) {
            node.addNode(s);
            return node;
        }

        s = Expression();
        if (s.getNodeList().size() > 0) {
            //System.out.println("EXXPRSION");
            node.addNode(s);
            next();
            s = SEMICOLON();
            if(!isEmptyNode(s)){
                node.addNode(s);
                next();
            }else{
                 //   System.out.println("expr statement Line " + t.line + " Missing ;");
            }
            return node;
        }

        return empty();
    }

    public AST FUNC_CALL() {
        String rule = G.FUNC_CALL.name();
        AST node = AST.c(rule);
        int save = current;
        //System.out.println("TRY THIS" + t.lexeme);
        AST i = IDENTIFIER();
        if (isEmptyNode(i)) {
            return empty();
        } else {
            node.addNode(i);
            next();
        }
        AST s = LPAREN();
        if (isEmptyNode(s)) {
            current = save;
            return empty();
        } else {
            node.addNode(i);
            node.addNode(s);
            next();
        }

        match_parenthesis = true;
        s = EXPRESSION_LIST();
        node.addNode(s);

        match_parenthesis = false;

        s = RPAREN();
        if (isEmptyNode(s)) {
            System.out.println("Line " + t.line + ": Missing )");
            error = error.concat("Line " + t.line + ": Missing )\n");
        } else {
            node.addNode(s);
            next();
        }

        return node;
    }

    public AST EXPRESSION_LIST() {
        String rule = G.EXPRESSION_LIST.name();
        AST node = AST.c(rule);
        AST e = Expression();

        if (!isEmptyNode(e)) {
           // System.out.println("Expression " + e.getNodeList().get(0).token.lexeme);
            node.addNode(e);
            while(!isEmptyNode(COMMA())){
                next();
                e = Expression();
                if(!isEmptyNode(e)){
                    if(e.getNodeList().isEmpty()){
                        System.out.println("Line " + t.line + " : Expecting expression after comma");
                        error = error.concat("Line " + t.line + " : Expecting expression after comma\n");
                    }

                    node.addNode(e);
                } else{ break;};
            }

        }

        return node;
    }

    public AST BLOCK() {
        String rule = G.BLOCK.name();
        AST node = AST.c(rule);
        int save = current;
        t = tokens.get(current);
        AST b = begin();
        node.addNode(b);
        next();
        AST s = STATEMENT_LIST();
        node.addNode(s);

        AST e = end();
        if(isEmptyNode(e)){
            next();
            System.out.println("Line " + t.line + " : missing end");
            error = error.concat("Line " + t.line + " : missing end\n");
        }else{node.addNode(e);
        }

        // next();
        //System.out.println("YOO==" + t.lexeme);
        return node;
    }


    public boolean isEmptyNode(AST ast) {
        return ast.rule.equals("EMPTY");
    }

    public AST IDENTIFIER() {
        String rule = G.IDENTIFIER.name();
        //System.out.println("IDENTIFER:" + t.lexeme );
        if (t.token_type.equals(G.IDENTIFIER.name()))
            return constructAST(rule, t);
        else return empty();
    }

    public AST var() {
        String rule = G.var.name();
        if (t.lexeme.equals(G.var.name()))
            return constructAST(rule, t);
        else return empty();
    }

    public AST cons() {
        String rule = "CONS";
        if (t.lexeme.equals("const"))
            return constructAST(rule, t);
        else return empty();
    }

    public AST FOR() {
        String rule = G.FOR.name();
        if (t.lexeme.equals(G.FOR.name().toLowerCase()))
            return constructAST(rule, t);
        else return empty();
    }

    public AST DO() {
        String rule = G.DO.name();
        if (t.lexeme.equals(G.DO.name().toLowerCase()))
            return constructAST(rule, t);
        else return empty();
    }

    public AST TRUE() {
        if (t.lexeme.equals("true"))
            return constructAST("true", t);
        else return empty();
    }

    public AST FAlSE() {
        if (t.lexeme.equals("false"))
            return constructAST("false", t);
        else return empty();
    }


    public AST TO() {
        String rule = G.TO.name();
        if (t.lexeme.equals(G.TO.name().toLowerCase()))
            return constructAST(rule, t);
        else return empty();
    }

    public AST program() {
        String rule = G.program.name();
        //System.out.println("Lexeme:" + t.lexeme );
        if (t.lexeme.equals(G.program.name()))
            return constructAST(rule, t);
        else return empty();
    }

    public AST COMMA() {
        String rule = G.COMMA.name();
        if (t.lexeme.equals(",")){
            //System.out.println("COMMA");
            return constructAST(rule, t);
        }

        else return empty();
    }

    public AST LPAREN() {
        String rule = G.LPAREN.name();
        if (t.token_type.equals(G.LPAREN.name()))
            return constructAST(rule, t);
        else return empty();
    }

    public AST RPAREN() {
        String rule = G.RPAREN.name();
        if (t.token_type.equals(G.RPAREN.name()))
            return constructAST(rule, t);
        else return empty();
    }

    public AST SEMICOLON() {
        String rule = G.SEMICOLON.name();
        //System.out.println("SEMICOLON:" + t.lexeme );
        if (t.token_type.equals(G.SEMICOLON.name()))
            return constructAST(rule, t);
        else return empty();
    }

    public AST COLON() {
        String rule = G.COLON.name();
        //System.out.println("COLON:" + t.lexeme );
        if (t.lexeme.equals(":"))
            return constructAST(rule, t);
        else return empty();
    }

    public AST OPERATION() {
        String rule = G.OPERATION.name();
        if (t.token_type.equals(G.OPERATION.name()))
            return constructAST(rule, t);
        else return empty();
    }

    public AST ASSIGN() {
        String rule = G.ASSIGN.name();
        //System.out.println("ASSIGN:" + t.lexeme );
        if (t.token_type.equals(G.ASSIGN.name()))
            return constructAST(rule, t);
        else return empty();
    }

    public AST IF() {
        String rule = G.IF.name();
        if (t.lexeme.equals(G.IF.name().toLowerCase()))
            return constructAST(rule, t);
        else return empty();
    }

    public AST ELSE() {
        String rule = G.ELSE.name();
        if (t.lexeme.equals(G.ELSE.name().toLowerCase()))
            return constructAST(rule, t);
        else return empty();
    }

    public AST THEN() {
        String rule = G.THEN.name();
        if (t.lexeme.equals(G.THEN.name().toLowerCase()))
            return constructAST(rule, t);
        else return empty();
    }

    public AST begin() {
        String rule = G.begin.name();
        if (t.lexeme.equals(G.begin.name()))
            return constructAST(rule, t);
        else return empty();
    }

    public AST end() {
        // this.t = it.next();
        String rule = G.end.name();
        if (t.lexeme.equals(G.end.name()))
            return constructAST(rule, t);
        else return empty();
    }

    public AST DOT() {
        // this.t = it.next();
        String rule = G.DOT.name();
        if (t.token_type.equals(rule))
            return constructAST(rule, t);
        else return empty();
    }

    public AST REAL() {
        // this.t = it.next();
        String rule = G.REAL.name();
        if (t.token_type.equals(rule)) return constructAST(rule, t);
        else return empty();
    }

    public AST INTEGER() {
        // this.t = it.next();
        String rule = G.INTEGER.name();
        //.out.println("INTEGER:"+ t.lexeme);
        if (t.token_type.equals(rule)) {
            return constructAST(rule, t);
        } else return empty();
    }


    public AST READLN() {
        // this.t = it.next();
        String rule = G.readln.name();
        if (t.lexeme.equals("readln")) {
            return constructAST(rule, t);
        } else return empty();
    }

    public AST WRITELN() {
        // this.t = it.next();
        String rule = G.writeln.name();
        if (t.lexeme.equals(rule)) {
            return constructAST(rule, t);
        } else return empty();
    }

    public AST ARITHMETIC() {
        // this.t = it.next();
        String rule = G.ARITHMETIC.name();
        if (t.lexeme.equals("+") || t.lexeme.equals("-") || t.lexeme.equals("/") ||  t.lexeme.equals("*")) {
            return constructAST(rule, t);
        } else return empty();
    }

    public AST RELATIONAL() {
        // this.t = it.next();
        String rule = G.RELATIONAL.name();
        if (t.lexeme.equals("=") || t.lexeme.equals("<=") || t.lexeme.equals("<") || t.lexeme.equals(">") || t.lexeme.equals(">=")
        || t.lexeme.equals("and") || t.lexeme.equals("or") || t.lexeme.equals("not") ) {
            return constructAST(rule, t);
        } else return empty();
    }

    public AST INVALID() {
        // this.t = it.next();
        String rule = "INVALID";
        if (t.token_type.equals("INVALID EXPRESSION/IDENTIFIER") ) {
            return constructAST(rule, t);
        } else return empty();
    }

    public AST BOOLEAN() {
        // this.t = it.next();
        String rule = G.BOOLEAN.name();
        if (t.lexeme.equals("true") || t.lexeme.equals("false")) {
            return constructAST(rule, t);
        } else return empty();
    }

    public AST STRING() {
        // this.t = it.next();
        if (t.token_type.equals(G.STRING.name())) {
            return constructAST(G.STRING.name(), t);
        } else return empty();
    }

    public AST empty() {
        return AST.c("EMPTY");
    }

    public AST errorNode(String message) {
        AST node = AST.c("ERROR");
        node.message = message;
        return node;
    }

    public AST constructAST(String rule, Token t) {
        AST node = AST.c(rule);
        node.token = t;
        return node;
    }

    public AST constructAST(String rule) {
        AST node = AST.c(rule);
        return node;
    }

    public void addASTChild(AST parent, AST child) {
        parent.nodeList.add(child);
    }

    public AST run_parser(){
        AST tree = this.PROGRAM_STRUC();
        File path = new File("error.txt");
        BufferedWriter wr;
        try { wr = new BufferedWriter(new FileWriter(path, true));
            wr.write(error);
            wr.close();

        } catch (IOException ex) {
            System.out.println("error");
        }

        return tree;
    }

    public static Parser c(ArrayList<Token> tokens) {
        return new Parser(tokens);
    }
}