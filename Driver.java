import java.io.File;

public class Driver {
    public static void main(String[] args){
        File inputFile = new File("inputfile.pas");
        File outputFile = new File("outputfile.tok");
        File errorFile = new File("error.txt");

        Scanner2 scanner = new Scanner2();

        
        scanner.read_line(inputFile, 2);
        String line = scanner.get_line();
        String[] lexemesPerLine = scanner.get_lexeme(line);

        scanner.console_dump(line, lexemesPerLine);
        //scanner.get_lexeme("var x , y , sum : integer ;");
        //System.out.println(scanner.classify_lexeme("{0123asd}"));

        

    }
}
