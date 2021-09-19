public class Symbol {
    public  String type;
    public  String name;
    public  String scope;

    public  AST tree;
    public  Token t;
    public String val = "";

    private Symbol(String name, String type, String scope, AST ast){
        this.tree = ast;
        this.name = name;
        this.type = type;
        this.scope = scope;
    }

    private Symbol(String name, String type, String scope, Token t){
        this.t = t;
        this.name = name;
        this.type = type;
        this.scope = scope;
    }


    public boolean hasValue(){
        return !val.isEmpty();
    }



    public static Symbol c(String name, String type, String scope, AST ast){
        return new Symbol(name, type, scope, ast);
    }

    public static Symbol c(String name, String type, String scope, Token t){
        return new Symbol(name, type, scope, t);
    }

    public String toString(){
        return name + " : " + val;
    }

}
