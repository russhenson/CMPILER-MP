public class Token {
    public int line;
    public String lexeme;
    public String token_type;

    public Token(String lexeme, String token_type, int line) {
        this.line = line;
        this.lexeme = lexeme;
        this.token_type = token_type;
    }

    public static Token c(String lexeme, String token_type, int line){
        return new Token(lexeme, token_type, line);
    }

}
