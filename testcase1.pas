program sample ;

var
value, nextValue  : real;
y : boolean;
z , p , input : integer;

function testOne(): void
var x, i: integer;
begin
  readln('Hello. I need your number: ' ,x);
  for i to 10 do         #error 1 no assign syntaxx error
   begin
     writeln('Yes' +x+); #error 2 syntax error
                         #error 3 no end
   x := 5x;              #error 4 no operator
end;

function testTwo(x: real): void
begin
  if( x := 4 )  then                     #error 5 syntax error
    begin
     writeln(x is a very huge number); #error 6 syntax error
    end;
end;

function testThree(x, y: integer): void
var i: integer;
begin
    for i:= 0 to x do
        begin
           writeln(y);
        end;
end;

function testFour(x: integer;  y:integer; z):void  #error 7 syntax error
var sum: integer;
begin
  sum = x + y + z;
end;

function testFive(): #error 8
begin
  writeln('Hello');
end;

begin
    value := (5 * 1) + (5 * 5 / 1 + 3 + (4 + (5 * 3)))/2.0 * 8.0;
    testOne()(x); //syntax error #9
	testTwo(123 ++value);  //syntax error #10
	nextValue = 5 **5 + 5 ++ 5 ** 5; //syntax error #11, 12, 13

	testTwo(5.0)); //syntax error #14
	testTwo(5.0; //syntax error #15
	testThree(25 13); //syntax error #16
	testThree((12 * 10), (54 * 5); //syntax error #17
	testThree(4,,5); //syntax error #18
end.
