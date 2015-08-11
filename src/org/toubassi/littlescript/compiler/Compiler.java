package org.toubassi.littlescript.compiler;

import org.toubassi.littlescript.ast.Block;
import org.toubassi.littlescript.parser.Parser;
import org.toubassi.littlescript.parser.Scanner;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by gtoubassi on 7/20/15.
 */
public class Compiler {

    private Block block;

    public Compiler(Block block) {
        this.block = block;
    }

    public Compiler(String program) throws Parser.Exception {
        this((new Parser(new Scanner(program))).parse());
    }

    public String compile() {
        AssemblyWriter writer = new AssemblyWriter();
        writer.begin();

        SymbolTable symbols = new SymbolTable();

        block.compile(symbols, writer);

        return writer.end();
    }

    public static void main(String args[]) throws Parser.Exception, IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(args[0]));
        String program = new String(bytes, Charset.defaultCharset());

        Compiler compiler = new Compiler(program);
        System.out.println(compiler.compile());
    }
}
