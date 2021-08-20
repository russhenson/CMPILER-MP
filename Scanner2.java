import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Scanner;

public class Scanner2 {
    File inputFile, outputFile, errFile;
    ArrayList<String> inputStrings = new ArrayList<String>();
    private String tokenresult = "";
    BufferedReader bReader;
    FileReader fileR;

    BufferedWriter bWriter;
    FileWriter fileW;

    StringBuilder sb = new StringBuilder();
    StringBuilder result = new StringBuilder();
    ErrorScanner errs;
    Boolean isComment;
    boolean isString;

    //ArrayList<Token> tokenList = new ArrayList<Token>();
    public ArrayList<String> tokenType;

    public Scanner2(){
        this.inputFile = new File("inputfile.pas");
        this.outputFile = new File("outputfile.tok");
        this.errs = new ErrorScanner();
        isComment = false;
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
    String classify_lexeme(String lexeme, boolean isnotcomm, boolean isString){
        TokenType tokenType = new TokenType(lexeme, isnotcomm, isString);
        return tokenType.getTokenType();
    }

    void setIsComment(boolean isComment){
        this.isComment = isComment;
    }

    boolean isComment(){
        return this.isComment;
    }

    /**
     * Write token on standard console.
     * @param {String} line
     * @param {String[]} lexemesPerLine */  
    String console_dump(String line, String lexeme, boolean isnotcomm, boolean isString){
    	String result = classify_lexeme(lexeme, isnotcomm, isString);
        
    	this.tokenresult = result;
        String output = lexeme + "\t" + result + "\n";
        return output;
    }

    /**
     * Write token to output file.
     * @param {FILE*} outputFile
     * @param {char*} lexeme
     * @param {char*} token_class */  
    void file_dump(File outputFile, String outputLine){
        try{
            this.fileW = new FileWriter(outputFile, true);
            this.bWriter = new BufferedWriter(fileW);

            bWriter.write(outputLine);

            bWriter.close();  
            //System.out.println("Success");  

        }catch(IOException e){
            e.printStackTrace();


        }
    }
    void delete_line(String filename) {
    	byte b;
    	try {
    		RandomAccessFile f = new RandomAccessFile(filename, "rw");
    		long length = f.length() - 1;
    		do {                     
    		  length -= 1;
    		  f.seek(length);
    		  b = f.readByte();
    		} while(b != 10 && length > 0);
    		if (length == 0) { 
    			f.setLength(length);
    			}
    		else {
    			f.setLength(length + 1);
    		}
    		f.close();
    	}
    	catch(Exception e) {
    		
    	}
    }

    /** Displays error on standard console. Error codes and descriptions are 
        retrieved from error.txt.
        @param code - An integer corresponding to appropriate error. */
    void print_error(String outputfile)
    {
    	try {
    		File myobj = new File(outputfile);
    		Scanner myreader = new Scanner(myobj);
    		while(myreader.hasNextLine()) {
    			String texter = myreader.nextLine().trim();
    			System.out.println(texter);
    		}
    	}
    	catch (Exception e) {
    		
    	}
    	//System.out.print
    }
    void lex_error(String lex, String filecatch, int counter) {
    	errs.error_checker(lex, filecatch, counter);
    }
    ArrayList<String> acquire_errorlist()
    {
    	return errs.listval();
    }
    String get_tokenresult() {
    	return this.tokenresult;
    }

    
}







