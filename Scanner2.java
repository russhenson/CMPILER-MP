import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Scanner2 {
    File inputFile;
    ArrayList<String> inputStrings = new ArrayList<String>();

    BufferedReader bReader;
    FileReader fileR;

    BufferedWriter bWriter;
    FileWriter fileW;

    StringBuilder sb = new StringBuilder();
    StringBuilder result = new StringBuilder();

    //ArrayList<Token> tokenList = new ArrayList<Token>();
    public ArrayList<String> tokenType;

    public Scanner2(){
        this.inputFile = new File("inputfile.pas");

    }


    /**
     * Returns 1 if success, 0 otherwise.
     * @param {FILE*} inputFile - Source code character stream.
     * @param {char*} line - One line of source code.
     */
    
    int read_line(File inputFile, int lineNum){
        int result = 1;

        try{
            this.fileR = new FileReader(inputFile);
            this.bReader = new BufferedReader(fileR);

            for(int i=0; i<lineNum-1; i++){
                this.bReader.readLine();
            }
            sb.append(this.bReader.readLine());

        }catch(IOException e){
            e.printStackTrace();

            result = 0;
        }

        System.out.println("Result: "+ result);
        System.out.println("Output: "+ sb);

        return result;
    }

    /**
     *  Returns all lexemes from line.
     * @param {String[]} line - A line of code. */
    String[] get_lexeme(String line){

        String[] lexemes = line.split("[\\s,]+");

        return lexemes;
    }

    /**
     * Returns the token class of a lexeme.
     * @param {String} lexeme */  
    String classify_lexeme(String lexeme){

        String tokenType;

        return tokenType;
    }

    /**
     * Write token on standard console.
     * @param {char*} lexeme
     * @param {char*} token_class */  
    //int console_dump( char* lexeme, char* token_class );

    /**
     * Write token to output file.
     * @param {FILE*} outputFile@param {char*} lexeme
     * @param {char*} token_class */  
    //int file_dump( FILE* outputFile, char* lexeme, char* token_class, char* );

    /*  Displays error on standard console. Error codes and descriptions are 
        retrieved from error.txt.
        @param code - An integer corresponding to appropriate error.
    */
    //void print_error( code );
 

    
}







