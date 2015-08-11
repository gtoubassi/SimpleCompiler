package org.toubassi.littlescript.compiler;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by gtoubassi on 7/20/15.
 */
public class AssemblyWriterTest {

    @Test
    public void testEmptyProgram() {
        AssemblyWriter writer = new AssemblyWriter();
        writer.begin();
        String asm = writer.end();
        System.out.print(asm);
        assertTrue(true);
    }

}
