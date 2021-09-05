import java.util.Arrays;

public class TokenType {
    private String lexeme;
    private Type type;
    private boolean isCurrComment;
    private Scanner2 lAnalyzer = new Scanner2();
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
        REAL,
        INTEGER,
        OPEN_CURLY_BRACE,
        CLOSE_CURLY_BRACE,
        BOOL_AND,
        BOOL_OR,
        BOOL_NOT,
		STRING,
		ARRAY,
        ERROR,
        VOID,
        UP_UNTIL,
        STRING_SINGLE
    }

    String[] reserved = {"program", "begin", "end", "function", "procedure", "and", "or", "not", "var", "const", "for", "to", "downto", "repeat", "until", "while",
                        "do", "mod", "div", "return", "array", "of", "if", "then", "else"};

    String[] predeclared = {"true", "read", "write",  "false", "readln", "writeln"/* , "boolean", "real", "char", "integer", "string" */};

    String[] dataTypes = {"char", "string", "integer", "real", "boolean", "array"};

    public TokenType(String lexeme, boolean isnotcomm, boolean isString){
        this.lexeme = lexeme;
        this.type = tokenize(this.lexeme, isnotcomm, isString);
    }

    private Type tokenize(String lexeme, boolean isnotcomm, boolean isString){
        Type token = Type.ERROR;
        if (isnotcomm && !isString) {
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
	        else if (lexeme.equals("...")) {
	        	token = Type.UP_UNTIL;
	        }
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
	        else if(lexeme.equals(">"))
	            token = Type.GREATER_THAN;
	        else if(lexeme.equals("<"))
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
	        else if(lexeme.equals(">="))
	            token = Type.GREATER_EQUAL;
	        else if(lexeme.equals("<="))
	            token = Type.LESS_EQUAL;
	        else if(lexeme.equals(".."))
	            token = Type.DOUBLE_PERIOD;
			else if(lexeme.matches("\\[[0-9]+..[1-9][0-9]*\\]"))
				token = Type.ARRAY;
	        else if(lexeme.equals("^"))
	            token = Type.HAT;
	        else if (lexeme.equals("void")) {
	        	token = Type.VOID;
	        }
	        else if(lexeme.matches("[a-zA-Z][a-zA-z0-9]*") && !lAnalyzer.isComment)
	            token = Type.IDENTIFIER;
			else if(lexeme.matches("\\{") || lexeme.startsWith("{"))
				token = Type.OPEN_CURLY_BRACE;
	        else if(lexeme.equals("‘") | lexeme.equals("\'") || lexeme.startsWith("\'"))
	            token = Type.SINGLE_QUOTE;
	        else if (lexeme.startsWith("‘") || lexeme.startsWith("'")) {
	        	token = Type.STRING_SINGLE;
	        }
	        else if(lexeme.equals("\""))
	            token = Type.DOUBLE_QUOTE;
	        else if(lexeme.startsWith("\""))
	            token = Type.STRING;
	        else if(lexeme.matches("[0-9]+"))
	            token = Type.INTEGER;
	        else if(lexeme.matches("[0-9]*.[0-9]+"))
	            token = Type.REAL;
	        else if(lexeme.matches("and:"))
	            token = Type.BOOL_AND;
	        else if(lexeme.matches("or:"))
	            token = Type.BOOL_OR;
	        else if(lexeme.matches("not:"))
	            token = Type.BOOL_NOT;
	        
	        else
	            token = Type.ERROR;
        }
		else if(isString){
			if(lexeme.matches("\""))
				token = Type.DOUBLE_QUOTE;
			else
				token = Type.STRING;
		}
        else {
        	if(lexeme.matches("\\}"))
				token = Type.CLOSE_CURLY_BRACE;
			else
				token = Type.COMMENT;
        }
        return token;
    }

    public String get_lexeme(){
        return this.lexeme;
    }

    public String getTokenType(){
        return this.type.name();
    }

    public boolean getIsComment(){
        return this.isCurrComment;
    }
}
