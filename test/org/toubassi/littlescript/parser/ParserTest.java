package org.toubassi.littlescript.parser;

import org.junit.Test;
import org.toubassi.littlescript.interpreter.Env;
import org.toubassi.littlescript.ast.Assignment;
import org.toubassi.littlescript.ast.Block;
import org.toubassi.littlescript.ast.expr.LiteralNumber;

import static org.junit.Assert.*;

/**
 * Created by gtoubassi on 7/19/15.
 */
public class ParserTest {

    @Test
    public void assignLiteral() throws Parser.Exception {
        Parser parser = new Parser("a = 10;");
        Block b = parser.parse();
        assertNotNull(b);
        assertEquals(b.getChildren().size(), 1);
        assertTrue(b.getChildren().get(0) instanceof Assignment);
        Assignment assignment = (Assignment)b.getChildren().get(0);
        assertEquals(assignment.getLValue(), "a");
        assertTrue(assignment.getRValue() instanceof LiteralNumber);

        Env env = new Env(b);
        env.run();
        assertEquals(10, env.getVariable("a"));
    }

    @Test
    public void assignLiteralExpression() throws Parser.Exception {
        Parser parser = new Parser("a = 10 + 1;");
        Block b = parser.parse();
        assertNotNull(b);

        Env env = new Env(b);
        env.run();
        assertEquals(11, env.getVariable("a"));
    }

    @Test
    public void expressionParens() throws Parser.Exception {
        Parser parser = new Parser("a = 2*(3 + 10);");
        Block b = parser.parse();
        assertNotNull(b);

        Env env = new Env(b);
        env.run();
        assertEquals(26, env.getVariable("a"));
    }

    @Test
    public void leftToRightExpressionExecution() throws Parser.Exception {
        Parser parser = new Parser("a = 5 * 10 / 6 / 2;");
        Block b = parser.parse();
        assertNotNull(b);

        Env env = new Env(b);
        env.run();
        assertEquals(4, env.getVariable("a"));
    }

    @Test
    public void localsInExpressions() throws Parser.Exception {
        Parser parser = new Parser("b = 10; a = 2 * b;");
        Block b = parser.parse();
        assertNotNull(b);

        Env env = new Env(b);
        env.run();
        assertEquals(20, env.getVariable("a"));
        assertEquals(10, env.getVariable("b"));
    }

    @Test
    public void conditional() throws Parser.Exception {
        Parser parser = new Parser("a = 10; if (a == 10) { b = 1; c = 2;} if (a > 10) { d = 3; }");
        Block b = parser.parse();
        assertNotNull(b);

        Env env = new Env(b);
        env.run();
        assertEquals(10, env.getVariable("a"));
        assertEquals(1, env.getVariable("b"));
        assertEquals(2, env.getVariable("c"));
        assertEquals(0, env.getVariable("d"));
    }

    @Test
    public void whileStatement() throws Parser.Exception {
        Parser parser = new Parser("a = 1; while (a < 10) { b = b + a; a = a + 1; }");
        Block b = parser.parse();
        assertNotNull(b);

        Env env = new Env(b);
        env.run();
        assertEquals(10, env.getVariable("a"));
        assertEquals(45, env.getVariable("b"));
    }

    @Test
    public void functionDeclAndCall() throws Parser.Exception {
        Parser parser = new Parser("func sum(total) { a = 1; while (a < total) { b = b + a; a = a + 1; } return b;} s=sum(10);");
        Block b = parser.parse();
        assertNotNull(b);

        Env env = new Env(b);
        env.run();
        assertEquals(45, env.getVariable("s"));
    }


    @Test
    public void print() throws Parser.Exception {
        Parser parser = new Parser("a = 25 / 2;\nprint(a);");
        Block b = parser.parse();
        assertNotNull(b);

        Env env = new Env(b);
        env.run();
        assertEquals(12, env.getVariable("a"));
    }


}
