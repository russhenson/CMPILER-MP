import java.util.ArrayList;

public class Driver {
    public static void main(String args[]){
//        ArrayList<Token> tokens = Scanner.c("lol.pas").tokens;
//        Parser p = Parser.c(tokens);
//        AST tree = p.run_parser();
//       //Parser.print_nodes(tree, 0);
//
//        Interpreter i = Interpreter.c(tree);
//        i.clear_log();
//        i.walk_tree(tree);
        IDE ide = new IDE();
        ide.setVisible(true);
    }
}
