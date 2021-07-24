import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Scanner2 {
    File inputFile, outputFile;
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
        this.outputFile = new File("outputfile.tok");

    }


    /**
     * Returns 1 if success, 0 otherwise.
     * @param {FILE*} inputFile - Source code character stream.
     * @param {int} lineNum - The line number
     */
    
    int read_line(File inputFile, int lineNum){
        int result = 1;

        try{
            this.fileR = new FileReader(inputFile);
            this.bReader = new BufferedReader(fileR);
            this.sb.delete(0, this.sb.length());

            for(int i=0; i<lineNum-1; i++){
                this.bReader.readLine();
            }
            this.sb.append(this.bReader.readLine());

        }catch(IOException e){
            e.printStackTrace();

            result = 0;
        }

        //System.out.println("Result: "+ result);
        //System.out.println("Output: "+ sb);

        return result;
    }

    String get_line(){

        return this.sb.toString();
    }

    /**
     *  Returns all lexemes from line.
     * @param {String[]} line - A line of code. */
    ArrayList<String> get_lexeme(String line){

        String[] lexemesTmp = line.split("\\s+");
        ArrayList<String> lexemes = new ArrayList<>();

        for(int i = 0; i < lexemesTmp.length; i++){
            lexemes.add(lexemesTmp[i]);
        }
        for(int i = 0; i < lexemes.size(); i++){
            if(lexemes.get(i).length() == 0){
                lexemes.remove(i);
            }
        }

        return lexemes;
    }

    /**
     * Returns the token class of a lexeme.
     * @param {String} lexeme */  
    String classify_lexeme(String lexeme){
        TokenType tokenType = new TokenType(lexeme);
        return tokenType.getTokenType();
    }

    /**
     * Write token on standard console.
     * @param {String} line
     * @param {String[]} lexemesPerLine */  
    String console_dump(String line, String lexeme){
        String output = lexeme + "  " + classify_lexeme(lexeme) + "\n";
        return output;
    }

    /**
     * Write token to output file.
     * @param {FILE*} outputFile
     * @param {char*} lexeme
     * @param {char*} token_class */  
    void file_dump(File outputFile, String outputLine){
        try{
            this.fileW = new FileWriter(outputFile);
            this.bWriter = new BufferedWriter(fileW);

            bWriter.write(outputLine);

            bWriter.close();  
            //System.out.println("Success");  

        }catch(IOException e){
            e.printStackTrace();


        }
    }

    /*  Displays error on standard console. Error codes and descriptions are 
        retrieved from error.txt.
        @param code - An integer corresponding to appropriate error.
    */
    //void print_error( code );
 

    
}







