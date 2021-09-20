
program parserTest ;
var value , nextValue : real ;
	blue : integer ;
function testOne ( num1 , num2 : integer ; val1 , val2 : string ) : integer ;
var 
	x , y , z , value  : integer ;
	blue , red : real ;

begin
	value := 5 ;
	writeln ( "Hyuga tiger" , value ) ;
	testOne := 24 ;
end ;


begin
	value := 20 ;
	blue := testOne ( 1 , 5 , "hello" , "world" )  ;
	writeln ( " Value " ,  blue ) ;
end .