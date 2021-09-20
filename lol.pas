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

begin
    value := (5 * 1) + (5 * 5 / 1 + 3 + (4 + (5 * 3)))/2.0 * 8.0;
    testOne();
end.
