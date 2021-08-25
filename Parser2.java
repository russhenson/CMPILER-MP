import javax.swing.text.html.HTMLEditorKit.Parser;

import java.io.*;
import java.util.*;

public class Parser2 {

    private Stack<String> tokenStack;
    private Stack<String> tokenTypeStack;
    
    private String tokenLookAhead;
    private String typeLookAhead;

    public Parser2(ArrayList<String> tokens, ArrayList<String> tokenType) {
        this.tokenStack = new Stack<>();
        this.tokenTypeStack = new Stack<>();

        for(int i = tokens.size()-1; i >= 0; i--){
            tokenStack.push(tokens.get(i));
            tokenTypeStack.push(tokenType.get(i));
        }

        tokenLookAhead = tokenStack.peek();
        typeLookAhead = tokenTypeStack.peek();
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
                    System.out.println("Missing a comma"); // Missing a comma
                    // get the error message from error.txt
                    return false;
                }

            }
            else {
                System.out.println("Invalid ID"); // Missing or invalid name
                // get the error message from error.txt
                return false;
            }
       }
       // get the error message from error.txt
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
        return false;
    }

    // Syntax: 
    boolean arithmeticExpr(){

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
            
        }

        return false;
    }

    boolean forLoop(){

        return false;
    }

    boolean ifThen(){

        return false;
    }

    boolean ifThenElse(){

        return false;
    }


}
