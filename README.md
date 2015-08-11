# A Simple Compiler

This is a trivially simple interpreter compiler written mostly on an
intercontinental flight.  The code is pretty hacky and not meant for
any other purpose then a diversion.  The language is dead simple:

1. Only integers
2. Simple expressions: + - / * ( )
3. Simple variables that are declared at time of reference.  No arrays
4. if and while statements
5. function calls with zero or more arguments and a mandatory return value.
6. a "print" statement to print the value of an expression/variable.

To run the interpreter:

    % java -classpath out org.toubassi.littlescript.interpreter.Interpreter scripts/simple.lscript
   
To run the compiler:
 
    % java -classpath out org.toubassi.littlescript.compiler.Compiler scripts/simple.lscript > /tmp/t.S ; cc -o t /tmp/t.S; ./t

