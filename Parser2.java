import javax.swing.text.html.HTMLEditorKit.Parser;

import java.io.*;
import java.util.*;

public class Parser2 {

    private Stack<String> tokenStack;
    private Stack<String> tokenTypeStack;
    private ErrorParser errparser;
    private String tokenLookAhead;
    private String typeLookAhead;
    private ArrayList<String> token_name, type_name;
    private int newcount = 0;

    public Parser2(ArrayList<String> tokens, ArrayList<String> tokenType, int counter) {
        this.tokenStack = new Stack<>();
        this.tokenTypeStack = new Stack<>();
        token_name = new ArrayList<String>();
        type_name = new ArrayList<String>();
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

    String tokenPopper() {
        return tokenStack.pop();
    }

    String tokenTypePopper() {
        return tokenTypeStack.pop();
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
            
            tokenPopper();
            tokenTypePopper();
            peeker();
            
            // Checks the program name if it's valid
            if(typeLookAhead.equals("IDENTIFIER")){ 
                
                tokenPopper();
                tokenTypePopper();
                peeker();

                // Check if it ends with semi colon
                if(typeLookAhead.equals("SEMICOLON")){
                    System.out.println("Valid program heading"); // No error
                    
                    tokenPopper();
                    tokenTypePopper();
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
            	//panic
            	panicmode("IDENTIFIER", 1);
                
            }
       }
       // get the error message from error.txt
        else {
        	newcount++;
        	errparser.error_checker(8, "error.txt" , newcount, tokenLookAhead);
        	panicmode("program", 0);
        }

       return isValid;

    }
    void panicmode(String type, int mode) {
    	boolean cango = true, colongo = true;
    	if (mode == 0) {
    		//program
    		while (cango) {
        		this.tokenPopper();
            	this.tokenTypePopper();
            	peeker();
            	if (this.tokenLookAhead.equals(type)) {
            		cango = false;
            		System.out.println("Stopped at " + this.tokenLookAhead + " type " + this.typeLookAhead);
            		program();
            		
            	}
        	}
    		
    	}
    	else if (mode == 1) {
    		//types for program
    		while (cango) {
    			System.out.println("Token " + this.tokenLookAhead + " type " +  this.typeLookAhead);
        		this.tokenPopper();
            	this.tokenTypePopper();
            	
            	peeker();
            	if (this.typeLookAhead.equals(type)) {
            		System.out.println(type + " Stopped at " + this.tokenLookAhead + " type " + this.typeLookAhead);
            		while(colongo) {
            			this.tokenPopper();
                    	this.tokenTypePopper();
                    	peeker();
                    	if (this.typeLookAhead.equals("SEMICOLON")) {
                    		System.out.println("SEMICOLON " + " Stopped at " + this.tokenLookAhead + " type " + this.typeLookAhead);
                    		colongo = false;
                    		cango = false;
                    	}
            		}
            	}
        	}
    	}
    	else if (mode == 2) {
    		
    	}
    	
    	
    }
    // Syntax: l-value := r-value ;
    boolean assignment(){ 
        boolean isValid = true;

        if(typeLookAhead.equals("IDENTIFIER")){ 
            
            tokenPopper();
            tokenTypePopper();
            peeker();

            if(typeLookAhead.equals("COLON_EQUALS")){
                
                tokenPopper();
                tokenTypePopper();
                peeker();

                // Check the r-value
                if( typeLookAhead.equals("STRING") || 
                    typeLookAhead.equals("REAL") || 
                    typeLookAhead.equals("INTEGER") || 
                    expression()){ // supposed to be arithmetic

                    
                        tokenPopper();
                    tokenTypePopper();
                    peeker();

                    // Check if it ends with semi colon
                    if(typeLookAhead.equals("SEMICOLON")){
                        
                        tokenPopper();
                        tokenTypePopper();
                        peeker();
                        System.out.println("Valid assignment"); // No error
                        isValid =  true;
                    }
                    else {
                        System.out.println("Missing a semicolon"); 
                        // Error: Missing a semicolon 
                        // get the error message from error.txt
                        
                    }
                }
                else {
                    System.out.println("Invalid assignment value"); 
                    // Error: Wrong assignment
                    // get the error message from error.txt
                    
                }

            }
            else {
                System.out.println("Missing an assignment operator"); 
                // Error: No := operator
                // get the error message from error.txt
               
            }

        }
        // Error: Incorrect or Missing Identifier
        // geth the error message from error.txt
        
        return isValid;
    }

    boolean variableDeclaration(){
    	boolean isGoing = false;
    	boolean iscorrect = false;

        boolean isValid = false;

        // Check if the first token is "var"
    	System.out.println("Checking " + this.tokenLookAhead);
        if(tokenLookAhead.equals("var")){
            
            tokenPopper();
            tokenTypePopper();
            peeker();

            while (typeLookAhead.equals("IDENTIFIER")) {
            	iscorrect = true;
            	isGoing = true;
            	
                tokenPopper();
                tokenTypePopper();
            	peeker();
            	System.out.println("Grun " + this.tokenLookAhead + " " + this.typeLookAhead);
            	while(isGoing) {
            		//if it is a comma
            		System.out.println("ZAP " + this.tokenLookAhead + " " + this.typeLookAhead);
            		if (typeLookAhead.equals("COMMA")) {
            			
                        tokenPopper();
                        tokenTypePopper();
                    	peeker();
                    	if (!(typeLookAhead.equals("IDENTIFIER"))) {
                    		iscorrect = false;
                    		newcount++;
                    		errparser.error_checker(11, "error.txt" , newcount, tokenLookAhead);
                    		return false;
                    	}
                    	else {
                    		
                            tokenPopper();
                            tokenTypePopper();
                        	peeker();
                    	}
            		}
            		//if it is a colon
            		else if (typeLookAhead.equals("COLON")) {
            			
                        tokenPopper();
                        tokenTypePopper();
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
            		
                    tokenPopper();
                    tokenTypePopper();
            		peeker();
            	}
            	else {
            		newcount++;
            		errparser.error_checker(12, "error.txt" , newcount, tokenLookAhead);
            		return false;
            	}
            	//if it ends with a semicolon
            	if (typeLookAhead.equals("SEMICOLON")) {
            		
                    tokenPopper();
                    tokenTypePopper();
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
    void clearlists() {
    	token_name.clear();
    	type_name.clear();
    }
    boolean forStatement(){
        boolean isValid = false;
        // Check if the first token is "for"
        if(tokenLookAhead.equals("for")){
            
            tokenPopper();
            tokenTypePopper();
            peeker();

            if(typeLookAhead.equals("IDENTIFIER")){
                
                tokenPopper();
                tokenTypePopper();
                peeker();

                if(typeLookAhead.equals("COLON_EQUALS")){
                    
                    tokenPopper();
                    tokenTypePopper();
                    peeker();

                    if(typeLookAhead.equals("INTEGER")){
                        
                        tokenPopper();
                        tokenTypePopper();
                        peeker();

                        if(tokenLookAhead.equals("to")){
                            
                            tokenPopper();
                            tokenTypePopper();
                            peeker();

                            if(typeLookAhead.equals("INTEGER")){
                                
                                tokenPopper();
                                tokenTypePopper();
                                peeker();

                                if(tokenLookAhead.equals("do")){
                                    
                                    tokenPopper();
                                    tokenTypePopper();
                                    peeker();

                                    if(compoundStatement(0)){
                                        System.out.println("Valid for-loop");
                                        isValid = true;
                                        
                                    }
                                    // this function has error handling already no need for one here                          
                                }
                                // Error: Expected a "do"
                            }
                            // Error: Expected an Integer
                        }
                        // Error: Expected a "to"
                    }
                    // Error: Expected an Integer
                }
                // Error: No := operator
            }
            // Error: Incorrect or Missing Identifier
        }
        // Error: Expected a "for"

        

        return isValid;
    }

    // <ifStatement> ::= <ifThen> | <ifThenElse>
    boolean ifStatement(){
        boolean isValid = false;

        System.out.println("ifStatement function called.");

        if(ifThen() || ifThenElse()){
            isValid = true;
        }

        return isValid;
    }

    // <ifThen> ::= if <expression> then <compoundStatement>
    void returntokens() {
    	//returning of tokens
    	for(int i = token_name.size()-1; i >= 0; i--){
    		this.tokenTypeStack.push(type_name.get(i));
    		this.tokenStack.push(token_name.get(i));
    	}
    	clearlists();
    }
    boolean ifThen() {
        boolean isValid = false;
       
        System.out.println("ifThen function called.");

        if(tokenLookAhead.equals("if")){
            token_name.add(this.tokenLookAhead);
            type_name.add(this.typeLookAhead);
            tokenPopper();
            tokenTypePopper();
            peeker();

            if(typeLookAhead.equals("OPEN_PAREN")){
            	token_name.add(this.tokenLookAhead);
                type_name.add(this.typeLookAhead);
            	token_name.add(this.tokenLookAhead);
                type_name.add(this.typeLookAhead);
                tokenPopper();
                tokenTypePopper();
                peeker();

                if(expression()){
                	
                    if(typeLookAhead.equals("CLOSE_PAREN")){
                    	token_name.add(this.tokenLookAhead);
                        type_name.add(this.typeLookAhead);
                    	token_name.add(this.tokenLookAhead);
                        type_name.add(this.typeLookAhead);
                        tokenPopper();
                        tokenTypePopper();
                        peeker();
                
                        if(tokenLookAhead.equals("then")){
                        	token_name.add(this.tokenLookAhead);
                            type_name.add(this.typeLookAhead);
                        	token_name.add(this.tokenLookAhead);
                            type_name.add(this.typeLookAhead);
                            tokenPopper();
                            tokenTypePopper();
                            peeker();
                            //if(compoundStatement
                            if(tokenLookAhead.equals("begin") && !tokenLookAhead.equals("else")){
                            	token_name.add(this.tokenLookAhead);
                                type_name.add(this.typeLookAhead);
                                System.out.println("Valid if-then statement");
                                isValid = true;
                                clearlists();
                            }
                            else {
                            	//there is an else
                            	//function for returning of stack
                            	this.returntokens();
                            }
                        }
                        // Error: Missing a then
                    }
                    // Error: Missing a )
                }
            }
            // Error: Missing a (
        }
        // Error: Missing if
        
        

        return isValid;
    }
    
    // <ifThenElse> ::= if <expression> then <compoundStatement> else <compoundStatement>
    boolean ifThenElse() {
        boolean isValid = false;

        System.out.println("ifThenElse function called.");

        if(tokenLookAhead.equals("if")){
            
            tokenPopper();
            tokenTypePopper();
            peeker();

            if(typeLookAhead.equals("OPEN_PAREN")){
                
                tokenPopper();
                tokenTypePopper();
                peeker();

                if(expression()){

                    if(typeLookAhead.equals("CLOSE_PAREN")){
                        
                        tokenPopper();
                        tokenTypePopper();
                        peeker();
                    

                        if(tokenLookAhead.equals("then")){
                            
                            tokenPopper();
                            tokenTypePopper();
                            peeker();

                            if(compoundStatement(0)){

                                if(tokenLookAhead.equals("else")){
                                    
                                    tokenPopper();
                                    tokenTypePopper();
                                    peeker();

                                    if(compoundStatement(0)){
                                        isValid = true;
                                        System.out.println("Valid if-then-else statement");
                                    }
                                }
                                // Error: Missing an "else"
                                
                            }
                        }
                        // Error: Missing a then
                    }
                    // Error: Missing a )
                }
            }
            // Error: Missing a ()
        }
        // Error: Missing if

        return isValid;
    }

    // <expression> ::= <simpleExpression> | <relationalExpression>
    boolean expression() {
        Boolean isValid = false;

        System.out.println("expression function called.");

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

        System.out.println("simpleExpression function called.");

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

        System.out.println("term function called.");

        if(factor()){

            if(multiOperator()){
            	
                if(factor()){
                    isValid = true;
                }
            }
            else 
                isValid = true;
        }


        return isValid;
    }

    // <factor> ::= *IDENTIFIER* | *INTEGER* | <expressionParen>
    boolean factor() {
        boolean isValid = false;

        System.out.println("factor function called.");

        if(typeLookAhead.equals("IDENTIFIER") || typeLookAhead.equals("INTEGER")){ 
            token_name.add(tokenLookAhead);
            type_name.add(this.typeLookAhead);
            tokenPopper();
            tokenTypePopper();
            peeker();
            isValid = true;
        }
        else if (expressionParen()) {
        	tokenPopper();
            tokenTypePopper();
            peeker();
            isValid = true;
        }

        return isValid;
    }

    // <expressionParen> ::= ( expression )
    boolean expressionParen() {
        boolean isValid = false;

        if(typeLookAhead.equals("OPEN_PAREN")){
        	token_name.add(tokenLookAhead);
            type_name.add(this.typeLookAhead);
            tokenPopper();
            tokenTypePopper();
            peeker();
            if(expression()){
                if(typeLookAhead.equals("CLOSE_PAREN")){
                	token_name.add(tokenLookAhead);
                    type_name.add(this.typeLookAhead);
                    tokenPopper();
                    tokenTypePopper();
                    peeker();

                    isValid = true;
                }
                // Error: Missing )
            }
        }
        // Error: Missing a (

        return isValid;
    }

    // <addingOperator> ::= *PLUS* | *MINUS*
    boolean addingOperator(){
        boolean isValid = false;

        if(typeLookAhead.equals("PLUS") || typeLookAhead.equals("MINUS")){
        	token_name.add(tokenLookAhead);
            type_name.add(this.typeLookAhead);
            tokenPopper();
            tokenTypePopper();
            peeker();
            isValid = true;
        }
        // Error: Expected a + or -

        return isValid;
    }

    // <multiOperator> ::= *MULTIPLY* | *DIVIDE*
    boolean multiOperator(){
        boolean isValid = false;

        if(typeLookAhead.equals("MULTIPLY") || typeLookAhead.equals("DIVIDE")){
        	token_name.add(tokenLookAhead);
            type_name.add(this.typeLookAhead);
            tokenPopper();
            tokenTypePopper();
            peeker();
            isValid = true;
        }
        // Error: Expected a * or /
            
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
        	token_name.add(tokenLookAhead);
            type_name.add(this.typeLookAhead);
            isValid = true;
        }
        // Error: Invalid operator

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

        if(compoundStatement(1) | ifStatement() /* | whileStatement() */ | forStatement())
            isValid = true;

        return isValid;
    }

    // <compoundStatement> ::= begin <statement> end
    boolean compoundStatement(int mode) {
        boolean isValid = false;

        if(tokenLookAhead.equals("begin")){
            
            tokenPopper();
            tokenTypePopper();
            peeker();

            if(statement()){
                if(tokenLookAhead.equals("end")){
                    
                    tokenPopper();
                    tokenTypePopper();
                    peeker();
                    //if statement
                    if (mode == 0) {
                    	if(typeLookAhead.equals("SEMICOLON")){
                            
                            tokenPopper();
                            tokenTypePopper();
                            peeker();

                            isValid = true;
                            System.out.println("Valid compound statement");

                        }
                        // Error: Missing a semicolon
                    	else {
                    		
                    	}
                    }
					else if (mode == 1) {
						if (typeLookAhead.equals("PERIOD")) {

							tokenPopper();
							tokenTypePopper();
							peeker();

							isValid = true;
							System.out.println("Valid compound statement");

						}
						// Error: Missing a semicolon
						else {
							
						}
					}
                    
                    
                }
                // Error: Expected end
            }
        }
        // Error: Expected begin


        return isValid;
    }

    // <readStatement> ::= read ( *IDENTIFIER* , *IDENTIFIER* ) | readln ( *IDENTIFIER* , *IDENTIFIER* )
    boolean readStatement() {
        boolean isValid = false;

        if(tokenLookAhead.equals("read") || tokenLookAhead.equals("readln")){
            
            tokenPopper();
            tokenTypePopper();
            peeker();

            if(typeLookAhead.equals("OPEN_PAREN")){
                
                tokenPopper();
                tokenTypePopper();
                peeker();

                if(typeLookAhead.equals("IDENTIFIER")){
                    
                    tokenPopper();
                    tokenTypePopper();
                    peeker();

                    if(typeLookAhead.equals("COMMA")){
                        
                        tokenPopper();
                        tokenTypePopper();
                        peeker();

                        if(typeLookAhead.equals("IDENTIFIER")){
                            
                            tokenPopper();
                            tokenTypePopper();
                            peeker();

                            if(typeLookAhead.equals("CLOSE_PAREN")){
                                
                                tokenPopper();
                                tokenTypePopper();
                                peeker();

                                if(typeLookAhead.equals("SEMICOLON")){
                                    
                                    tokenPopper();
                                    tokenTypePopper();
                                    peeker();
            
                                    System.out.println("Valid read statement");
                                    isValid = true;
                                }
                                // Error: Missing a ;
        
                                
                            }
                            // Error: Missing )
                        }
                        // Error: Invalid Identifier

                    }
                    else if(typeLookAhead.equals("CLOSE_PAREN")){
                        
                        tokenPopper();
                        tokenTypePopper();
                        peeker();

                        if(typeLookAhead.equals("SEMICOLON")){
                            
                            tokenPopper();
                            tokenTypePopper();
                            peeker();
    
                            System.out.println("Valid read statement");
                            isValid = true;
                        }
                        // Error: Missing a ;
                    }
                    // Error: Missing )
                }
                // Error: Invalid Identifier

            }
            // Error: Missing (


        }
        // Error: Expected read or readln


        return isValid;
    }

    // <writeStatement> ::= write ( *IDENTIFIER* , *IDENTIFIER* ) | writeln ( *IDENTIFIER* , *IDENTIFIER* )
    boolean writeStatement() {
        boolean isValid = false;

        if(tokenLookAhead.equals("write") || tokenLookAhead.equals("writeln")){
            
            tokenPopper();
            tokenTypePopper();
            peeker();

            if(typeLookAhead.equals("OPEN_PAREN")){
                
                tokenPopper();
                tokenTypePopper();
                peeker();

                if(typeLookAhead.equals("IDENTIFIER") || typeLookAhead.equals("STRING")){
                    
                    tokenPopper();
                    tokenTypePopper();
                    peeker();

                    if(typeLookAhead.equals("COMMA")){
                        
                        tokenPopper();
                        tokenTypePopper();
                        peeker();

                        if(typeLookAhead.equals("IDENTIFIER") || typeLookAhead.equals("STRING")){
                            
                            tokenPopper();
                            tokenTypePopper();
                            peeker();

                            if(typeLookAhead.equals("CLOSE_PAREN")){
                                
                                tokenPopper();
                                tokenTypePopper();
                                peeker();

                                if(typeLookAhead.equals("SEMICOLON")){
                                    
                                    tokenPopper();
                                    tokenTypePopper();
                                    peeker();
            
                                    System.out.println("Valid write statement");
                                    isValid = true;
                                }
        
                                
                            }
                        }

                    }
                    else if(typeLookAhead.equals("CLOSE_PAREN")){
                        
                        tokenPopper();
                        tokenTypePopper();
                        peeker();

                        if(typeLookAhead.equals("SEMICOLON")){
                            
                            tokenPopper();
                            tokenTypePopper();
                            peeker();
    
                            System.out.println("Valid write statement");
                            isValid = true;
                        }
                         // Error: Missing a ;
                    }
                    // Error: Missing )
                
                }
                // Error: Invalid Identifier or String

            }
            // Error: Missing (

        }
        // Error: Expected read or readln

        return isValid;
    }



}
