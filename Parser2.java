import javax.swing.text.html.HTMLEditorKit.Parser;

import java.io.*;
import java.util.*;

public class Parser2 {

    private Stack<String> tokenStack;
    private Stack<String> tokenTypeStack;
    private ErrorParser errparser;
    private String tokenLookAhead;
    private String typeLookAhead;
    private int newcount = 0;
    public Parser2(ArrayList<String> tokens, ArrayList<String> tokenType, int counter) {
        this.tokenStack = new Stack<>();
        this.tokenTypeStack = new Stack<>();
        this.newcount = counter;
        errparser = new ErrorParser();
        for(int i = tokens.size()-1; i >= 0; i--){
            tokenStack.push(tokens.get(i));
            tokenTypeStack.push(tokenType.get(i));
        }

        tokenLookAhead = tokenStack.peek();
        typeLookAhead = tokenTypeStack.peek();
    }
    ArrayList<String> get_errparselist() {
    	return this.errparser.get_errparselist();
    }
    int getcounter() {
    	return newcount;
    }
    // Syntax: program <IDENTIFIER> ;
    boolean programHeading() {
        // Check if the first token is "program"
        if(tokenLookAhead.equals("program")){
            tokenStack.pop();
            tokenTypeStack.pop();
            if(!tokenStack.empty()){ // proceed to peek at the next token
                tokenLookAhead = tokenStack.peek();
                typeLookAhead = tokenTypeStack.peek();
            }
            
            // Checks the program name if it's valid
            if(typeLookAhead.equals("IDENTIFIER")){ 
                tokenStack.pop();
                tokenTypeStack.pop();
                if(!tokenStack.empty()){
                    tokenLookAhead = tokenStack.peek();
                    typeLookAhead = tokenTypeStack.peek();
                }

                // Check if it ends with semi colon
                if(typeLookAhead.equals("SEMICOLON")){
                    System.out.println("Valid program heading"); // No error
                    return true;
                }
                else {
                    System.out.println("Missing a semicolon"); // Missing a comma
                    // get the error message from error.txt
                    newcount++;
                	errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
                    return false;
                }

            }
            else {
                System.out.println("Invalid ID"); // Missing or invalid name
                // get the error message from error.txt
                newcount++;
            	errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
                return false;
            }
       }
       // get the error message from error.txt
        else {
        	newcount++;
        	errparser.error_checker(8, "error.txt" , newcount, tokenLookAhead);
        }
       return false;

    }

    // Syntax: l-value := r-value ;
    boolean assignment(){ 
        if(typeLookAhead.equals("IDENTIFIER")){ 
            tokenStack.pop();
            tokenTypeStack.pop();
            if(!tokenStack.empty()){
                tokenLookAhead = tokenStack.peek();
                typeLookAhead = tokenTypeStack.peek();
            }

            if(typeLookAhead.equals("COLON_EQUALS")){
                tokenStack.pop();
                tokenTypeStack.pop();
                if(!tokenStack.empty()){
                    tokenLookAhead = tokenStack.peek();
                    typeLookAhead = tokenTypeStack.peek();
                }

                // Check the r-value
                if( typeLookAhead.equals("STRING") || 
                    typeLookAhead.equals("REAL") || 
                    typeLookAhead.equals("INTEGER") || 
                    arithmeticExpr()){

                    tokenStack.pop();
                    tokenTypeStack.pop();
                    if(!tokenStack.empty()){
                        tokenLookAhead = tokenStack.peek();
                        typeLookAhead = tokenTypeStack.peek();
                    }

                    // Check if it ends with semi colon
                    if(typeLookAhead.equals("SEMICOLON")){
                        System.out.println("Valid assignment"); // No error
                        // get the error message from error.txt
                        return true;
                    }
                    else {
                        System.out.println("Missing a comma"); // Missing a comma
                        // get the error message from error.txt
                        return false;
                    }
                }
                else {
                    System.out.println("Invalid assignment value");
                    // get the error message from error.txt
                    return false;
                }

            }
            else {
                System.out.println("Missing an assignment operator");
                // get the error message from error.txt
                return false;
            }

        }
        // get the error message from error.txt
        else {
        	
        }
        return false;
    }

    boolean variableDeclaration(){
        // Check if the first token is "var"
        if(tokenLookAhead.equals("var")){
            tokenStack.pop();
            tokenTypeStack.pop();
            if(!tokenStack.empty()){ // proceed to peek at the next token
                tokenLookAhead = tokenStack.peek();
                typeLookAhead = tokenTypeStack.peek();
            }

            while(typeLookAhead.equals("SEMICOLON")){
                while(typeLookAhead.equals("COLON")){
                    if(typeLookAhead.equals("IDENTIFIER")){
                        tokenStack.pop();
                        tokenTypeStack.pop();
                        if(!tokenStack.empty()){ // proceed to peek at the next token
                            tokenLookAhead = tokenStack.peek();
                            typeLookAhead = tokenTypeStack.peek();
                        }

                        if(typeLookAhead.equals("COMMA")){
                            tokenStack.pop();
                            tokenTypeStack.pop();
                            if(!tokenStack.empty()){ // proceed to peek at the next token
                                tokenLookAhead = tokenStack.peek();
                                typeLookAhead = tokenTypeStack.peek();
                            }
                        }
                    }
                    // error for invalid identifier
                    // error if colon is not found
                }
                if(typeLookAhead.equals("DATA_TYPE")){
                    tokenStack.pop();
                    tokenTypeStack.pop();
                    if(!tokenStack.empty()){ // proceed to peek at the next token
                        tokenLookAhead = tokenStack.peek();
                        typeLookAhead = tokenTypeStack.peek();
                    }
                }

                // error if the next token is a colon,  it should be a semicolon after the data type
            }

            System.out.println("Valid Variable Declaration");

        }

        return false;
    }

    boolean forStatement(){
        // Check if the first token is "for"
        if(tokenLookAhead.equals("for")){
            tokenStack.pop();
            tokenTypeStack.pop();
            if(!tokenStack.empty()){ // proceed to peek at the next token
                tokenLookAhead = tokenStack.peek();
                typeLookAhead = tokenTypeStack.peek();
            }

            if(typeLookAhead.equals("IDENTIFIER")){
                tokenStack.pop();
                tokenTypeStack.pop();
                if(!tokenStack.empty()){ // proceed to peek at the next token
                    tokenLookAhead = tokenStack.peek();
                    typeLookAhead = tokenTypeStack.peek();
                }

                if(typeLookAhead.equals("COLON_EQUALS")){
                    tokenStack.pop();
                    tokenTypeStack.pop();
                    if(!tokenStack.empty()){ // proceed to peek at the next token
                        tokenLookAhead = tokenStack.peek();
                        typeLookAhead = tokenTypeStack.peek();
                    }

                    if(typeLookAhead.equals("INTEGER")){
                        tokenStack.pop();
                        tokenTypeStack.pop();
                        if(!tokenStack.empty()){ // proceed to peek at the next token
                            tokenLookAhead = tokenStack.peek();
                            typeLookAhead = tokenTypeStack.peek();
                        }

                        if(tokenLookAhead.equals("to")){
                            tokenStack.pop();
                            tokenTypeStack.pop();
                            if(!tokenStack.empty()){ // proceed to peek at the next token
                                tokenLookAhead = tokenStack.peek();
                                typeLookAhead = tokenTypeStack.peek();
                            }

                            if(typeLookAhead.equals("INTEGER")){
                                tokenStack.pop();
                                tokenTypeStack.pop();
                                if(!tokenStack.empty()){ // proceed to peek at the next token
                                    tokenLookAhead = tokenStack.peek();
                                    typeLookAhead = tokenTypeStack.peek();
                                }

                                if(tokenLookAhead.equals("do")){
                                    tokenStack.pop();
                                    tokenTypeStack.pop();
                                    if(!tokenStack.empty()){ // proceed to peek at the next token
                                        tokenLookAhead = tokenStack.peek();
                                        typeLookAhead = tokenTypeStack.peek();
                                    }

                                    if(tokenLookAhead.equals("begin")){
                                        tokenStack.pop();
                                        tokenTypeStack.pop();
                                        if(!tokenStack.empty()){ // proceed to peek at the next token
                                            tokenLookAhead = tokenStack.peek();
                                            typeLookAhead = tokenTypeStack.peek();
                                        }

                                        if(statement()){ // statment func not yet polished
                                            /* tokenStack.pop();
                                            tokenTypeStack.pop();
                                            if(!tokenStack.empty()){ // proceed to peek at the next token
                                                tokenLookAhead = tokenStack.peek();
                                                typeLookAhead = tokenTypeStack.peek();
                                            } */

                                            if(tokenLookAhead.equals("end")){
                                                tokenStack.pop();
                                                tokenTypeStack.pop();
                                                if(!tokenStack.empty()){ // proceed to peek at the next token
                                                    tokenLookAhead = tokenStack.peek();
                                                    typeLookAhead = tokenTypeStack.peek();
                                                }

                                                if(typeLookAhead.equals("SEMICOLON")){
                                                    tokenStack.pop();
                                                    tokenTypeStack.pop();
                                                    if(!tokenStack.empty()){ // proceed to peek at the next token
                                                        tokenLookAhead = tokenStack.peek();
                                                        typeLookAhead = tokenTypeStack.peek();
                                                    }

                                                    System.out.println("Valid for-loop");
                                                    return true;
                                                }
                                            }
                                        } 
                                    }                                    
                                } 
                            }
                        }
                    }
                }
            }
        }

        

        return false;
    }

    boolean whileStatement(){
        return false;
    }
    // Syntax: 
    boolean arithmeticExpr(){

        return false;
    }

    // <ifStatement> :: = if <expression> then <statement> | if <expression> then <statement> else <statement>
    boolean ifStatement(){

        return false;
    }

    boolean expression() {
        return false;
    }

    // <statement> ::= <simpleStatement> | <structuredStatement>
    boolean statement() { 
        if(simpleStatement() | structuredStatement())
            return true;
            
        return false;
    }

    // <simpleStatement> ::= <assignment> | <readStatement> | <writeStatement>
    boolean simpleStatement() {

        if(assignment() | readStatement() | writeStatement())
            return true;

        return false;
    }

    // <structuredStatement> ::= <compoundStatement> | <ifStatement> | <whileStatement> | forStatement
    boolean structuredStatement() {

        if(compoundStatement() | ifStatement() | whileStatement() | forStatement())
            return true;

        return false;
    }

    // <compoundStatement> ::= begin <statement> end
    boolean compoundStatement() {
        return false;
    }

    // <readStatement> ::= read ( *IDENTIFIER* , *IDENTIFIER* ) | readln ( *IDENTIFIER* , *IDENTIFIER* )
    boolean readStatement() {
        return false;
    }

    // <writeStatement> ::= write ( *IDENTIFIER* , *IDENTIFIER* ) | writeln ( *IDENTIFIER* , *IDENTIFIER* )
    boolean writeStatement() {
        return false;
    }



}
