import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Scanner {

    int codeline = 0;
    String errors = "";
    public ArrayList<Token> tokens = new ArrayList<>();
    private String filename;
    String text;

    private Scanner(String filename){
        this.filename = filename;
        run_scanner();
    }

    private Scanner(String text, int temp){
        this.text = text;
        run_scanner();
    }

    public static Scanner c(String filename){
        return new Scanner(filename);
    }

    StringTokenizer get_lexeme(String strline) {
        int i;
        String temp = "";
        String delim = "₪";
        String lexemes = "";
        char[] line = strline.toCharArray();
        String errors = "";

        for (i = 0; i < line.length; i++) {
            char c = line[i];
            //System.out.println(c);
            temp = "";
            if (c == '{') { //comments
                //	strncat(str, &c, 1);
                while (line[i] != '}' && i < line.length) {
                    i += 1;
                }
                if(line[i] == '}') i++;
                // temp += delim;
            }else if (c == ':') {
                temp += c;
                if(i < line.length-1)
                if (line[i + 1] == '=') {
                    i += 1;
                    c = line[i];
                    temp += c;
                }
                temp += delim;
            }else if (c == '(' || c == ')' || c == ',' || c == ';' || c == '[' || c == ']' || c == '.') {
                temp += c;
                temp += delim;
            }  else if ((c == '-' &&  Character.isDigit((line[i + 1]))) || Character.isDigit(c)){
                temp += c;
                String stop = "<>=;+-*/' ():,\n\t";
                while( i < line.length -1){
//                    if(Character.isDigit(line[i+1]) || line[i + 1] == '.'){
////                        c = line[i + 1];
////                        i += 1;
////                        temp += c;
////                    }else break;
                    if(stop.indexOf(line[i+1]) == -1){
                        c = line[i + 1];
                        i += 1;
                        temp += c;
                    }else break;
                }
                temp += delim;
            } else if (Character.isLetter(c)) {
                temp += c;
                String stop = "<>=;+*/' .():-,\n\t";
                //System.out.println(i);
                while( i < line.length -1){
                    if(stop.indexOf(line[i+1]) == -1){
                        i += 1;
                        c = line[i];
                        temp += c;

                    }else
                        break;
                }

                temp += delim;
            }else if( c == '<' || c == '>' || c == '=' || c == '-' ||  c == '+'  || c == '*' || c == '/'  ){
                temp += c;
                if( (c == '<' || c == '>') && line[i + 1] == '='){
                    i += 1;
                    c = line[i];
                    temp += c;
                }else if(  line[i + 1] == '+'  ||  line[i + 1] == '*' ){
                    i += 1;
                    c = line[i];
                    temp += c;
                }
                temp += delim;
            }
            else if (c == '\'') {
                temp += c;
                if(i < tokens.size()-1){
                    while (line[i + 1] != '\'') {
                        i += 1;
                        if (i < line.length) {
                            c = line[i];
                            if (c == ' ') c = ' ';
                            temp += c;
                            if(i < line.length-1)
                                if (line[i + 1] == '\'') {
                                    i += 1;
                                    c = line[i];
                                    temp += c;
                                    break;
                                }
                        } else {
                            break;
                        }
                    }



                }

                temp += delim;
            }else if(c!= ' ' && c!= '\n' && c!= '\t'){
                temp += c;
                String stop = "<>=;+*-/' .():,\n\t";
                //System.out.println(i);
                while( i < line.length -1){
                    if(stop.indexOf(line[i+1]) != -1 && !Character.isAlphabetic(line[i+1]) &&  !Character.isDigit(line[i+1])){
                        i += 1;
                        c = line[i];
                        temp += c;
                    }else break;
                }

                temp += delim;

            }
            lexemes+=temp;
        }
        return new StringTokenizer(lexemes, delim);
    }

    private boolean strcmp(String x, String y){
        if(x.equals(y)){
            return false;
        }else
            return true;
    }

    private boolean isidentifier(String lexeme){
        int i;
        if(!Character.isAlphabetic(lexeme.charAt(0))){
            return false;
        }
        for (i = 1; i < lexeme.length(); i++) {
            if( !Character.isAlphabetic(lexeme.charAt(i)) && !Character.isDigit(lexeme.charAt(i))  )
                return false;
        }
        return true;
    }

    private boolean isInvalidIdentifier(String lexeme){
        if(lexeme.length() > 1)
            return true;
        else return false;
    }

    private boolean isUnknownSymbol(String lexeme){
        if(lexeme.length() == 1)
            return true;
        else return false;
    }

    private boolean isstring(String lexeme){
        if(lexeme.charAt(0) == '\'' && lexeme.charAt(lexeme.length()-1)== '\'' && lexeme.length() > 3)
        {
            return true;
        }
        else return false;
    }

    private boolean ischaracter(String lexeme){
        if(lexeme.charAt(0) == '\'' && lexeme.charAt(lexeme.length()-1 ) == '\'' && lexeme.length() == 3) {
            return true;
        }else return false;
    }

    private boolean isinteger(String lexeme){
        int i;
        for (i = 0; i < lexeme.length(); i++) {
            if( !Character.isDigit(lexeme.charAt(i))) return false;
        }
        return true;
    }

    boolean isreal(String lexeme){
        int i;
        for (i = 0; i < lexeme.length(); i++) {
            if( !Character.isDigit(lexeme.charAt(i)) && lexeme.charAt(i) != '.'){
                return false;
            }
        }
        return true;
    }


    String classify_lexeme( String lexeme ){
        //reserved words
        //puts(lexeme);
        if (!strcmp(lexeme, "program") ||  !strcmp(lexeme, "and")  || !strcmp(lexeme, "array")  || !strcmp(lexeme, "for")
                || !strcmp(lexeme, "while")  || !strcmp(lexeme, "begin")  || !strcmp(lexeme, "or")  ||!strcmp(lexeme, "of")
                || !strcmp(lexeme, "to")  || !strcmp(lexeme, "do")  || !strcmp(lexeme, "end")  || !strcmp(lexeme, "not")
                || !strcmp(lexeme, "if")   || !strcmp(lexeme, "mod")  || !strcmp(lexeme, "function")
                || !strcmp(lexeme, "var")  || !strcmp(lexeme, "then")  || !strcmp(lexeme, "repeat")  || !strcmp(lexeme, "div")
                || !strcmp(lexeme, "procedure")  || !strcmp(lexeme, "const")  || !strcmp(lexeme, "else")  || !strcmp(lexeme, "until")
                || !strcmp(lexeme, "return"))
            return "RESERVED";
        else if (!strcmp(lexeme, "boolean") ||  !strcmp(lexeme, "real")  || !strcmp(lexeme, "true")  || !strcmp(lexeme, "read")
                || !strcmp(lexeme, "write")  || !strcmp(lexeme, "char")  || !strcmp(lexeme, "integer")  ||!strcmp(lexeme, "false")
                || !strcmp(lexeme, "readln")  || !strcmp(lexeme, "writeln")  || !strcmp(lexeme, "string") || !strcmp(lexeme, "void"))
            return "PREDECLARED";
        else if (!strcmp(lexeme, ";"))
            return "SEMICOLON";
        else if (!strcmp(lexeme, "."))
            return "DOT";
        else if(!strcmp(lexeme, "downto")){
            return "DOWN";
        }
        else if (!strcmp(lexeme, ","))
            return "COMMA";
        else if (!strcmp(lexeme, ":"))
            return "COLON";
        else if (!strcmp(lexeme, "["))
            return "RBRACKET";
        else if (!strcmp(lexeme, "]"))
            return "LBRACKET";
        else if (!strcmp(lexeme, "+") || !strcmp(lexeme, "-") || !strcmp(lexeme, "/") || !strcmp(lexeme, "*") ||
                !strcmp(lexeme, "mod") )
            return "ARITHMETIC";
        else if( !strcmp(lexeme, "<") || !strcmp(lexeme, ">") || !strcmp(lexeme, "<=") || !strcmp(lexeme, ">=")
                || !strcmp(lexeme, "=")  ){
            return "RELATIONAL";
        }else if( !strcmp(lexeme, ":=") ){
            return "ASSIGN";
        }else if( !strcmp(lexeme, "and") || !strcmp(lexeme, "or") || !strcmp(lexeme, "not")   ){
            return "BOOLEAN";
        }
        else if(isidentifier(lexeme))
            return "IDENTIFIER";
        else if (!strcmp(lexeme, "("))
            return "LPAREN";
        else if (!strcmp(lexeme, ")"))
            return "RPAREN";
        else if(isinteger(lexeme)){
            return "INTEGER";
        }else if(isreal(lexeme) ){
            return "REAL";
        }else if(isstring(lexeme)){
            return "STRING";
        }else if(ischaracter(lexeme)){
            return "CHARACTER";
        }else if(isInvalidIdentifier(lexeme)){
            errors += "Line " + String.valueOf(codeline) + ": ";
            errors += " invalid expression / identifier\n";
            return "INVALID EXPRESSION/IDENTIFIER";
        }
        else{
            errors += "Line " + String.valueOf(codeline) + ": ";
            errors += " invalid expression\n";
            return "UNKNOWN SYMBOL";
        }
    }

    private void console_dump(String lexeme, String token_class){
        System.out.println(lexeme + " : " + token_class);
    }

    private void file_dump(String lexeme, String token_class){
        File path = new File("out.txt");
        BufferedWriter wr;
        try { wr = new BufferedWriter(new FileWriter(path, true));
            wr.append(lexeme + " : " + token_class + "\n");
            wr.close();
        } catch (IOException ex) {
            System.out.println("error");
        }
    }

    private void error_log(){
        File path = new File("error.txt");
        BufferedWriter wr;
        try { wr = new BufferedWriter(new FileWriter(path));
            wr.append(errors);
            wr.close();
        } catch (IOException ex) {
            System.out.println("error");
        }
    }



    public void run_scanner(){
        BufferedWriter wr;
        try { wr = new BufferedWriter(new FileWriter("out.txt"));
            wr.write("");
            wr.close();
        } catch (IOException ex) {
            System.out.println("error");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                codeline++;
                StringTokenizer tokenizer = get_lexeme(line);
                while(tokenizer.hasMoreElements()){
                    String token = tokenizer.nextToken();
                    String token_type = classify_lexeme(token);
                    console_dump(token,token_type);
                    tokens.add(Token.c(token, token_type, codeline));
                    file_dump( token, token_type);
                }
            } System.out.println(errors);
        } catch (Exception e) {
            e.printStackTrace();
        }

        error_log();
    }

}

