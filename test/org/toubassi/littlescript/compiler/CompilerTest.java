package org.toubassi.littlescript.compiler;

import org.junit.Test;
import org.toubassi.littlescript.parser.Parser;

import static org.junit.Assert.*;

/**
 * Created by gtoubassi on 7/20/15.
 */
public class CompilerTest {

    @Test
    public void assignLiteral() throws Parser.Exception {
        Compiler compiler = new Compiler("a = 25 / 2;");
        String program = compiler.compile();
        System.out.print(program);
        assertTrue(true);
    }

}
