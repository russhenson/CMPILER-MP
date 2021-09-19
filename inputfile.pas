program parserTest ;
var value , ban : integer ;




begin
	value := 2 ;
	if ( value > 5 ) then
		begin
			writeln ( "Greater" ) ;
		end
	else if ( value = 5 ) then
		begin
			writeln ( "Equal" ) ;
		end
	else if ( value = 4 ) then
		begin
			writeln ( "FOUR" ) ;
		end
	else 
		begin
			writeln ( "LESSER" ) ;
		end ;
end .