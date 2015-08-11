package org.toubassi.littlescript.interpreter;

import org.toubassi.littlescript.ast.Block;
import org.toubassi.littlescript.parser.Parser;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by gtoubassi on 7/20/15.
 */
public class Interpreter {

    public static void main(String args[]) throws Parser.Exception, IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(args[0]));
        String program = new String(bytes, Charset.defaultCharset());

        Parser parser = new Parser(program);
        Block block = parser.parse();

        Env env = new Env(block);
        env.run();
        env.dump();
    }

}
