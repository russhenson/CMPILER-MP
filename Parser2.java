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

    void popper() {
    	tokenStack.pop();
        tokenTypeStack.pop();
    }

    void peeker() {
    	if(!tokenStack.empty()){ // proceed to peek at the next token
            tokenLookAhead = tokenStack.peek();
            typeLookAhead = tokenTypeStack.peek();
        }

    }

    // <program> ::= program *IDENTIFIER* ;
    boolean program() {
        boolean isValid = false;
        // Check if the first token is "program"
        if(tokenLookAhead.equals("program")){
            popper();
            peeker();
            
            // Checks the program name if it's valid
            if(typeLookAhead.equals("IDENTIFIER")){ 
                popper();
                peeker();

                // Check if it ends with semi colon
                if(typeLookAhead.equals("SEMICOLON")){
                    System.out.println("Valid program heading"); // No error
                    popper();
                    peeker();
                    isValid = true;

                }
                else {
                    System.out.println("Missing a semicolon"); // Missing a semicolon
                    // get the error message from error.txt
                    newcount++;
                	errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
                   
                }

            }
            else {
                System.out.println("Invalid ID"); // Missing or invalid name
                // get the error message from error.txt
                newcount++;
            	errparser.error_checker(5, "error.txt" , newcount, tokenLookAhead);
                
            }
       }
       // get the error message from error.txt
        else {
        	newcount++;
        	errparser.error_checker(8, "error.txt" , newcount, tokenLookAhead);
        }

       return isValid;

    }
    
    
    // Syntax: l-value := r-value ;
    boolean assignment(){ 
        boolean isValid = true;

        if(typeLookAhead.equals("IDENTIFIER")){ 
            popper();
            peeker();

            if(typeLookAhead.equals("COLON_EQUALS")){
                popper();
                peeker();

                // Check the r-value
                if( typeLookAhead.equals("STRING") || 
                    typeLookAhead.equals("REAL") || 
                    typeLookAhead.equals("INTEGER") || 
                    arithmeticExpr()){

                    popper();
                    peeker();

                    // Check if it ends with semi colon
                    if(typeLookAhead.equals("SEMICOLON")){
                        System.out.println("Valid assignment"); // No error
                        // get the error message from error.txt
                        isValid =  true;
                    }
                    else {
                        System.out.println("Missing a comma"); // Missing a comma
                        // get the error message from error.txt
                        
                    }
                }
                else {
                    System.out.println("Invalid assignment value");
                    // get the error message from error.txt
                    
                }

            }
            else {
                System.out.println("Missing an assignment operator");
                // get the error message from error.txt
               
            }

        }
        
        return isValid;
    }

    boolean variableDeclaration(){
    	boolean isGoing = false;
    	boolean iscorrect = false;

        boolean isValid = false;

        // Check if the first token is "var"
    	System.out.println("Checking " + this.tokenLookAhead);
        if(tokenLookAhead.equals("var")){
            popper();
            peeker();

            while (typeLookAhead.equals("IDENTIFIER")) {
            	iscorrect = true;
            	isGoing = true;
            	popper();
            	peeker();
            	System.out.println("Grun " + this.tokenLookAhead + " " + this.typeLookAhead);
            	while(isGoing) {
            		//if it is a comma
            		System.out.println("ZAP " + this.tokenLookAhead + " " + this.typeLookAhead);
            		if (typeLookAhead.equals("COMMA")) {
            			popper();
                    	peeker();
                    	if (!(typeLookAhead.equals("IDENTIFIER"))) {
                    		iscorrect = false;
                    		newcount++;
                    		errparser.error_checker(11, "error.txt" , newcount, tokenLookAhead);
                    		return false;
                    	}
                    	else {
                    		popper();
                        	peeker();
                    	}
            		}
            		//if it is a colon
            		else if (typeLookAhead.equals("COLON")) {
            			popper();
                    	peeker();
                    	isGoing = false;
            		}
            		
            		else {
            			newcount++;
            			errparser.error_checker(10, "error.txt" , newcount, tokenLookAhead);
            			return false;
            		}
            	}
            	//if it is a keyword
            	if (typeLookAhead.equals("DATA_TYPE")) {
            		popper();
            		peeker();
            	}
            	else {
            		newcount++;
            		errparser.error_checker(12, "error.txt" , newcount, tokenLookAhead);
            		return false;
            	}
            	//if it ends with a semicolon
            	if (typeLookAhead.equals("SEMICOLON")) {
            		popper();
            		peeker();
            		System.out.println("One line valid");
            	}
            	else {
            		newcount++;
            		errparser.error_checker(7, "error.txt" , newcount, tokenLookAhead);
            		
            		return false;
            	}
            }
            
            /*while(typeLookAhead.equals("SEMICOLON")){
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
*/
            System.out.println("Valid Variable Declaration");

            //return iscorrect;
            isValid = true;


        }

        return isValid;
    }

    boolean forStatement(){
        boolean isValid = false;
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

                                    if(compoundStatement()){

                                        if(typeLookAhead.equals("SEMICOLON")){
                                            tokenStack.pop();
                                            tokenTypeStack.pop();
                                            if(!tokenStack.empty()){ // proceed to peek at the next token
                                                tokenLookAhead = tokenStack.peek();
                                                typeLookAhead = tokenTypeStack.peek();
                                            }

                                            System.out.println("Valid for-loop");
                                            isValid = true;
                                        }
                                            
                                        
                                    }                          
                                } 
                            }
                        }
                    }
                }
            }
        }

        

        return isValid;
    }

    // Syntax: 
    boolean arithmeticExpr(){

        return false;
    }

    // <ifStatement> ::= <ifThen> | <ifThenElse>
    boolean ifStatement(){
        boolean isValid = false;

        if(ifThen() || ifThenElse()){
            isValid = true;
        }

        return isValid;
    }

    // <ifThen> ::= if <expression> then <compoundStatement>
    boolean ifThen() {
        boolean isValid = false;

        if(tokenLookAhead.equals("if")){
            popper();
            peeker();

            if(typeLookAhead.equals("OPEN_PAREN")){
                popper();
                peeker();

                if(expression()){

                    if(typeLookAhead.equals("CLOSE_PAREN")){
                        popper();
                        peeker();
                    }

                    if(tokenLookAhead.equals("then")){
                        popper();
                        peeker();

                        if(compoundStatement()){

                            isValid = true;
                        }
                    }
                }
            }
        }

        return isValid;
    }

    // <ifThenElse> ::= if <expression> then <compoundStatement> else <compoundStatement>
    boolean ifThenElse() {
        boolean isValid = false;

        if(tokenLookAhead.equals("if")){
            popper();
            peeker();

            if(typeLookAhead.equals("OPEN_PAREN")){
                popper();
                peeker();

                if(expression()){

                    if(typeLookAhead.equals("CLOSE_PAREN")){
                        popper();
                        peeker();
                    }

                    if(tokenLookAhead.equals("then")){
                        popper();
                        peeker();

                        if(compoundStatement()){

                            if(tokenLookAhead.equals("else")){
                                popper();
                                peeker();

                                if(compoundStatement()){
                                    isValid = true;
                                }
                            }
                            
                        }
                    }
                }
            }
        }

        return isValid;
    }

    // <expression> ::= <simpleExpression> | <relationalExpression>
    boolean expression() {
        Boolean isValid = false;

        if(simpleExpression() || relationalExpression()){
            isValid = true;
        }

        return isValid;
    }

    // <relationalExpression> ::= <simpleExpression> <relationalOperator> <simpleExpression>
    boolean relationalExpression() {
        Boolean isValid = false;

        if(simpleExpression()){
            if(relationalOperator()){
                if(simpleExpression()){
                    isValid = true;
                }
            }
        }

        return isValid;
    }

    // <simpleExpression> ::= <term> | <term> <addingOperator> <term>
    boolean simpleExpression() {
        boolean isValid = false;

        if(term()){

            if(addingOperator()){

                if(term()){
                    isValid = true;
                }

            }
            else 
                isValid = true; // just the term is acceptable
            
        }

        return isValid;
    }

    // <term> ::= <factor> | <factor> <multiOperator> <factor>
    boolean term(){
        boolean isValid = false;

        if(factor()){

            if(multiOperator()){

                if(factor()){
                    isValid = true;
                }
            }
        }


        return isValid;
    }

    // <factor> ::= *IDENTIFIER* | *INTEGER* | ( expression )
    boolean factor() {
        boolean isValid = false;

        if(typeLookAhead.equals("IDENTIFIER") || typeLookAhead.equals("INTEGER")){
            popper();
            peeker();
            isValid = true;
        }

        return isValid;
    }

    // <addingOperator> ::= *PLUS* | *MINUS*
    boolean addingOperator(){
        boolean isValid = false;

        if(typeLookAhead.equals("PLUS") || typeLookAhead.equals("MINUS")){
            popper();
            peeker();
            isValid = true;
        }

        return isValid;
    }

    // <multiOperator> ::= *MULTIPLY* | *DIVIDE*
    boolean multiOperator(){
        boolean isValid = false;

        if(typeLookAhead.equals("MULTIPLY") || typeLookAhead.equals("DIVIDE")){
            popper();
            peeker();
            isValid = true;
        }
            
        return isValid;
    }

    // <relationalOperator> ::= *NOT_EQUAL* | *LESS_THAN* | *LESS_EQUAL* | *GREATER_EQUAL* | *GREATER_THAN*
    boolean relationalOperator() {
        boolean isValid = false;

        if( typeLookAhead.equals("NOT_EQUAL") || 
            typeLookAhead.equals("LESS_THAN") || 
            typeLookAhead.equals("LESS_EQUAL") ||
            typeLookAhead.equals("GREATER_THAN") ||
            typeLookAhead.equals("GREATER_EQUAL")){

            isValid = true;
        }

        return isValid;
    }

    // <statement> ::= <simpleStatement> | <structuredStatement>
    boolean statement() { 
        boolean isValid = false;
        if(simpleStatement() | structuredStatement())
            isValid = true;
            
        return isValid;
    }

    // <simpleStatement> ::= <assignment> | <readStatement> | <writeStatement>
    boolean simpleStatement() {
        boolean isValid = false;

        if(assignment() | readStatement() | writeStatement())
            isValid = true;

        return isValid;
    }

    // <structuredStatement> ::= <compoundStatement> | <ifStatement> | <whileStatement> | forStatement
    boolean structuredStatement() {
        boolean isValid = false;

        if(compoundStatement() | ifStatement() /* | whileStatement() */ | forStatement())
            isValid = true;

        return isValid;
    }

    boolean whileStatement() {
    	return false;
    }
    // <compoundStatement> ::= begin <statement> end
    boolean compoundStatement() {
        boolean isValid = false;

        if(tokenLookAhead.equals("begin")){
            popper();
            peeker();

            if(statement()){
                if(tokenLookAhead.equals("end")){
                    popper();
                     peeker();

                    isValid = true;
                }
            }
        }

        return isValid;
    }

    // <readStatement> ::= read ( *IDENTIFIER* , *IDENTIFIER* ) | readln ( *IDENTIFIER* , *IDENTIFIER* )
    boolean readStatement() {
        boolean isValid = false;

        if(tokenLookAhead.equals("read") || tokenLookAhead.equals("readln")){
            popper();
            peeker();

            if(typeLookAhead.equals("OPEN_PAREN")){
                popper();
                peeker();

                if(typeLookAhead.equals("IDENTIFIER")){
                    popper();
                    peeker();

                    if(typeLookAhead.equals("COMMA")){
                        popper();
                        peeker();

                        if(typeLookAhead.equals("IDENTIFIER")){
                            popper();
                            peeker();

                            if(typeLookAhead.equals("CLOSE_PAREN")){
                                popper();
                                peeker();

                                if(typeLookAhead.equals("SEMICOLON")){
                                    popper();
                                    peeker();
            
                                    System.out.println("Valid read statement");
                                    isValid = true;
                                }
        
                                
                            }
                        }

                    }
                    else if(typeLookAhead.equals("CLOSE_PAREN")){
                        popper();
                        peeker();

                        if(typeLookAhead.equals("SEMICOLON")){
                            popper();
                            peeker();
    
                            System.out.println("Valid read statement");
                            isValid = true;
                        }
                    }
                }

            }

        }


        return isValid;
    }

    // <writeStatement> ::= write ( *IDENTIFIER* , *IDENTIFIER* ) | writeln ( *IDENTIFIER* , *IDENTIFIER* )
    boolean writeStatement() {
        return false;
    }



}
