import javax.swing.text.html.HTMLEditorKit.Parser;

import java.io.*;
import java.util.*;

public class Parser2 {

    private Stack<String> tokenStack;
    private Scanner2 scanner;
    private ArrayList<String> tokens;


    public Parser2() {
        tokenStack = new Stack<>();
        scanner = new Scanner2();

        tokens = scanner.token_dump();

        for(int i = tokens.size()-1; i >= 0; i--){
            System.out.println(tokens.get(i));
            tokenStack.push(tokens.get(i));
        }

    }

    // <PROGRAM> ::= program <PROGNAME> ;
    void program() {
        String lookAhead = tokenStack.peek();
       /* if(lookAhead == "program"){
           pop it from the stack then check if the next one is a program name then
       } */

       
       System.out.println("look ahead : " + lookAhead);
    }
    
}
