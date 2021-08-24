import javax.swing.text.html.HTMLEditorKit.Parser;

import java.io.*;
import java.util.*;

public class Parser2 {

    private Stack<String> tokenStack;
    private Scanner2 scanner;
    private ArrayList<String> tokens;


    public Parser2(ArrayList<String> tokens) {
        tokenStack = new Stack<>();
        scanner = new Scanner2();


        for(int i = tokens.size()-1; i >= 0; i--){
            //System.out.println(tokens.get(i));
            tokenStack.push(tokens.get(i));
        }
        

    }

    // <PROGRAM> ::= program <PROGNAME> ;
    void programHeading() {
        
        System.out.println(Arrays.toString(tokenStack.toArray()));

        String lookAhead = tokenStack.peek();
        System.out.println("LookAhead: " + lookAhead);
        if(lookAhead.equals("program")){
           //pop it from the stack then check if the next one is a program name then
            //System.out.println("Popping: " + lookAhead);
            tokenStack.pop();
            lookAhead = tokenStack.peek();
            //System.out.println("New lookAhead: " + lookAhead);
            if(lookAhead.matches("[a-zA-Z][a-zA-z0-9]*")){ // program name
                //System.out.println("Popping: " + lookAhead);
                tokenStack.pop();
                lookAhead = tokenStack.peek();
                //System.out.println("New lookAhead: " + lookAhead);
                if(lookAhead.equals(";")){
                    System.out.println("Valid program heading");
                }
                else
                    System.out.println("Missing a comma");
            }
            else
                System.out.println("Invalid ID");
       }

       
       
    }
    
}
