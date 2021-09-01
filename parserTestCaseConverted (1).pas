program parserTest;
var value, nextValue : float;

function testOne( ) : void ;
var 
	x : integer ;

begin
	x := 0 ;
	read ( " Hello. I need your number: " , x ) ;

	for i := 1 to 10 do
		begin
			write( " Yes ", x );
		end;

	x := 5x ; 
end ;

function testTwo ( x :real ) : void ;
begin
	if( x = 4 ) then
	write ( x , " is a very huge number " ) ;
end ;

function testThree ( x , y : integer ) : void ;
var 
	i : integer ;

begin
	i = 0 ;
	while i = x do
	begin
		i := i + 1 ;
	end;
end ;

function testFour ( x , y , z : integer ) void ;
var 
	sum : integer ;
begin
	sum = x + y + z ;
end ;

function testFive () void ;
begin
	writeln ( "Hello" ) ;
end ;

begin
	value := (5 * 1) + (5 * 5 / 1 + 3 + (4 + (5 * 3 ) ) ) / 2.0 * 8.0 ;

	testOne ( x ) ;
	testTwo ( 123 ++ value ) ;

	nextValue := 5 ** 5 + 5 ++ 5 ** 5 ;

	testTwo (5.0 ) ) ;
	testTwo ( 5.0 ;
	testThree ( 25 , 13 ) ;
	testThree ( ( 12 * 10) ,  (54 * 5 ) ;
	testThree ( 4 , , 5 ) ;
	testThree (4 , 5 ) ;
end .