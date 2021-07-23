import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Driver {
    public static void main(String[] args) throws FileNotFoundException{
        File inputFile = new File("inputfile.pas");
        File outputFile = new File("outputfile.tok");
        File errorFile = new File("error.txt");
        Scanner2 scanner = new Scanner2();


        try(BufferedReader br = new BufferedReader(new FileReader(inputFile))){
            String line;
            int lineNum = 1;
            while((line = br.readLine())!= null){
                scanner.read_line(inputFile, lineNum);
                String oneLine = scanner.get_line();
                String[] lexemesPerLine = scanner.get_lexeme(oneLine);
                
                for(int i = 0; i < lexemesPerLine.length; i++){
                    scanner.console_dump(oneLine, lexemesPerLine[i]);
                }
                
                lineNum++;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }



    
        //scanner.get_lexeme("var x , y , sum : integer ;");
        //System.out.println(scanner.classify_lexeme("{0123asd}"));

        

    }
}
