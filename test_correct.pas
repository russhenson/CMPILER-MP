program sample ;

var
value, nextValue : real;
y : boolean;
z , p , input : integer;

function testOne(): void
var x, i: integer;
begin
  readln('Hello. I need your number: ' ,x);
  for i:=0 to 10 do
   begin
     writeln('Yes ', x);
   end;
   x := 5;
end;

function testTwo(x: real): void
begin
  if( x > 4 ) then
    begin
     writeln(x, ' is a very huge number');
    end;
end;

function testThree(x, y: integer): void
var i: integer;
begin
    for i:= 0 to x do
    begin
       writeln('loop ', i, '-> ', y);
    end;
end;

function testFour( x: integer;  y:integer; z: integer ): void
var sum: integer;
begin
  sum := x + y + z;
  writeln('SUM = ', sum);
end;

function testFive(): void
begin
  writeln('Hello');
end;

begin
    value := (5 * 1) + (5 * 5 / 1 + 3 + (4 + (5 * 3)))/2.0 * 8.0;
    testOne();
    testTwo(value);
    nextValue := 5 * 5 + 5 + 5 * 5;

    testTwo(nextValue);
    testTwo(2.0);

    testThree(5, 13);
    testThree(2 * 2, 54 * 5);
    testThree(4,5);

    testFour( 1, 2, 4);
    testFive();

end.
