program parserTest;
var value, nextValue : float;

function testOne();
var x:=0;

begin
	read("Hello. I need your number: ", x);

	for i := 1 to 10 do
		[begin]
			write("Yes", x);
		[end;]

	x := 5x; //syntax error
end;

function testTwo(x : float);
begin
	if(x = 4) then
	write(x, "is a very huge number");
end;

function testThree(x, y : integer);
var i:=0;

begin
	while i == x do
	begin
		i := i+1;
	end;
end;

function testFour(x, y, z : integer);
var sum : integer;
begin
	sum = x + y + z;
end;

function testFive();
begin
	writeln("Hello");
end;

begin
	value := (5 * 1) + (5 * 5 / 1 + 3 + (4 + (5 * 3)))/2.0f * 8.0f;

	testOne()(x);
	testTwo(123 ++ value);

	nextValue := 5 ** 5 + 5 ++ 5 ** 5;

	testTwo(5.0f));
	testTwo(5.0f;
	testThree(25, 13);
	testThree((12 * 10), (54 * 5);
	testThree( 4 ,, 5);
	testThree(4, 5);
end.