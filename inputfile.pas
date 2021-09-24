program parserTest ;
const gun = 5 ;
var x , i , j , sum : integer ;
     hun : array [ 1 .. 3 ] of integer ;
     c : char ;
function test ( x : integer ) : integer ;

begin
	if ( x <= 1 ) then
		begin
			test := x ;
		end
	else 
		begin
			test := test ( x - 1 ) + test ( x - 2 ) ;
		end ;
end ;

begin
	fh := 5 ;
end .

