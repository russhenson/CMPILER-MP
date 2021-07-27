import java.util.Arrays;

public class TokenType {
    private String lexeme;
    private Type type;
    enum Type{
        RESERVED,
        IDENTIFIER,
        PREDECLARED,
        DATA_TYPE,
        COMMA,
        SEMICOLON,
        COLON,
        COMMENT,
        PLUS,
        MINUS,
        MULTIPLY,
        DIVIDE,
        EQUALS,
        GREATER_THAN,
        LESS_THAN,
        OPEN_BRACKET,
        CLOSE_BRACKET,
        PERIOD,
        COLON_EQUALS,
        OPEN_PAREN,
        CLOSE_PAREN,
        NOT_EQUAL,
        GREATER_EQUAL,
        LESS_EQUAL,
        DOUBLE_PERIOD,
        HAT,
        SINGLE_QUOTE,
        DOUBLE_QUOTE,
        ERROR
    }

    String[] reserved = {"program", "begin", "end", "function", "procedure", "and", "or", "not", "var", "const", "for", "to", "downto", "repeat", "until", "while",
                        "do", "mod", "div", "return", "array", "of", "if", "then", "else"};

    String[] predeclared = {"boolean", "real", "true", "read", "write", "char", "integer", "false", "readln", "writeln", "string"};

    String[] dataTypes = {"Character", "String", "Integer", "Real", "Boolean", "Array"};

    public TokenType(String lexeme){
        this.lexeme = lexeme;
        this.type = tokenize(this.lexeme);
    }

    private Type tokenize(String lexeme){
        Type token = Type.ERROR;

        if(Arrays.asList(reserved).contains(lexeme))
            token = Type.RESERVED;
        else if(Arrays.asList(predeclared).contains(lexeme))
            token = Type.PREDECLARED;
        else if(Arrays.asList(dataTypes).contains(lexeme))
            token = Type.DATA_TYPE;
        else if(lexeme.equals(","))
            token = Type.COMMA;
        else if(lexeme.equals(";"))
            token = Type.SEMICOLON;
        else if(lexeme.equals(":"))
            token = Type.COLON;
        else if(lexeme.equals("+"))
            token = Type.PLUS;
        else if(lexeme.equals("-"))
            token = Type.MINUS;
        else if(lexeme.equals("*"))
            token = Type.MULTIPLY;
        else if(lexeme.equals("/"))
            token = Type.DIVIDE;
        else if(lexeme.equals("="))
            token = Type.EQUALS;
        else if(lexeme.equals("<"))
            token = Type.GREATER_THAN;
        else if(lexeme.equals(">"))
            token = Type.LESS_THAN;
        else if(lexeme.equals("*"))
            token = Type.MULTIPLY;
        else if(lexeme.equals("["))
            token = Type.OPEN_BRACKET;
        else if(lexeme.equals("]"))
            token = Type.CLOSE_BRACKET;
        else if(lexeme.equals("."))
            token = Type.PERIOD;
        else if(lexeme.equals(":="))
            token = Type.COLON_EQUALS;
        else if(lexeme.equals("("))
            token = Type.OPEN_PAREN;
        else if(lexeme.equals(")"))
            token = Type.CLOSE_PAREN;
        else if(lexeme.equals("<>"))
            token = Type.NOT_EQUAL;
        else if(lexeme.equals("<="))
            token = Type.GREATER_EQUAL;
        else if(lexeme.equals(">="))
            token = Type.LESS_EQUAL;
        else if(lexeme.equals(".."))
            token = Type.DOUBLE_PERIOD;
        else if(lexeme.equals("^"))
            token = Type.HAT;
        else if(lexeme.matches("[a-zA-Z][a-zA-z0-9]*"))
            token = Type.IDENTIFIER;
        else if(lexeme.matches("\\{[a-zA-Z0-9]*\\}"))
            token = Type.COMMENT;
        else if(lexeme.equals("‘") | lexeme.equals("\'"))
            token = Type.SINGLE_QUOTE;
        else if(lexeme.equals("\""))
            token = Type.DOUBLE_QUOTE;
        else
            token = Type.ERROR;

        return token;
    }

    public String get_lexeme(){
        return this.lexeme;
    }

    public String getTokenType(){
        return this.type.name();
    }
}
//TOKEN TYPE
    /**
     * RESERVED - program | begin | end | function | procedure |
     *            and | or | not | var | const |
     *            for | to | downto | repeat | until |
     *            while | do | mod | div | return
     * 
     * IDENTIFIERS - [a-zA-Z][a-zA-z0-9]*
     * 
     * PREDECLARED - boolean | real | true | read | write |
     *               char | integer | false | readln | writeln | string
     * 
     * COMMA - \,
     * 
     * SEMICOLON - \;
     * 
     * COLON - \:
     * 
     *
     * 
     */