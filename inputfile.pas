program parserTest ;
const gun = 5 ;
var x : integer ;

function testOne ( y , z : integer ; gun : string ) : void ;
var 
	g : string ;

begin
	g := gun ;
	writeln ( 'Gun 2 ' , g ) ;
	writeln ( 'Y is ' , y ) ;
	writeln ( 'z is ' , z ) ; 	
		
end ;
function testTwo ( ) : integer ;

begin
	testTwo := 2 ;
end ;

begin
	
	testOne ( testTwo ( ) , 3 + 1 , 'banzai' ) ;
	
end .