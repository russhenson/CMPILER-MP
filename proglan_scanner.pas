program sample ;

var 
sampleIdentifier123 : real ;
y : boolean ;
z , p , input : integer ;

function performFunction ( const x : integer ) : integer ;
begin
	sampleIdentifier123 := 999.9 ;
	y := ( 5 > 4 and: true ) or: ( false and: not: ( x = 0 ) ) ;
	
	writeln ( " Please input a number : " ) ; 
	readln ( input ) ;
	
	x := input ;
	z := input ;
	
	if ( x <> 0 ) then
		begin
		
		end ;
	else
		begin
		
		end ;
	
	for n := 0 to x do
		begin
			for i := 0 to x do
				begin
					writeln ( " Hello World " , input ) ;
				end ;
		end ;
	
	p = 0 ;
	performFunction := z ;
end ;

begin
	x = performFunction ( x ) ;
	writeln ( ' Value of X : ' , x ) ;
	writeln ( ' Value of P : ' , p ) ;
end .