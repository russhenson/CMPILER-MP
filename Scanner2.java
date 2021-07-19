public class Scanner2 {
    
}


/*
Returns 1 if success, 0 otherwise.
@param {FILE*} inputFile - Source code character stream.
@param {char*} line - One line of source code.
*/
//int read_line( FILE* inputFile, char* line );

/*
Returns a lexeme from line.
@param {char*} line - A line of code.
*/
// char* get_lexeme( char* line );

/*
Returns the token class of a lexeme.
@param {char*} lexeme 
*/
//char* classify_lexeme( char* lexeme );

/* 
Write token on standard console.
@param {char*} lexeme
@param {char*} token_class
*/
//int console_dump( char* lexeme, char* token_class );

/*
Write token to output file.
@param {FILE*} outputFile@param {char*} lexeme
@param {char*} token_class
*/
//int file_dump( FILE* outputFile, char* lexeme, char* token_class, char* );

/* 
Displays error on standard console. Error codes and descriptions are 
retrieved from error.txt.
@param code - An integer corresponding to appropriate error.
*/
//void print_error( code );