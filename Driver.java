

public class Driver {
    public static void main(String[] args)/*  throws FileNotFoundException */{
        /* File inputFile = new File("inputfile.pas");
        File outputFile = new File("outputfile.tok");
        File errorFile = new File("error.txt");
        Scanner2 scanner = new Scanner2();

        try(BufferedReader br = new BufferedReader(new FileReader(inputFile))){
            String line;
            int lineNum = 1;
            while((line = br.readLine())!= null){
                scanner.read_line(inputFile, lineNum);
                String oneLine = scanner.get_line();
                ArrayList<String> lexemesPerLine = scanner.get_lexeme(oneLine);
                
                for(int i = 0; i < lexemesPerLine.size(); i++){
                    System.out.print(scanner.console_dump(oneLine, lexemesPerLine.get(i)));
                }
                
                lineNum++;
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
 */
        
        TextEditorGUI editor = new TextEditorGUI();
        

    }
}
