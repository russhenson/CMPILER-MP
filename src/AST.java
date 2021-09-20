import java.util.ArrayList;
// abstract syntax tree
// recursive descent
public class AST {
    String rule;
    Token token;
    ArrayList<AST> nodeList;
    public int line = 0;
    public String message;

    private AST(String rule) {
        this.rule = rule;
        nodeList = new ArrayList<>();
    }

    public static AST c(String rule){
        return new AST(rule);
    }

    public void setLine(int line){
        this.line = line;
    }

    public void addNode(AST tree){
        nodeList.add(tree);
    }

    public String getRule() {
        return rule;
    }

    public ArrayList<AST> getNodeList() {
        return nodeList;
    }

    public AST getTreeNode(int index){
        return nodeList.get(index);
    }


    public AST getTreeNode(String rule){
        for(AST node: nodeList){
            if(node.getRule().equals(rule)){
                return node;
            }
        }
        return null;
    }


}
