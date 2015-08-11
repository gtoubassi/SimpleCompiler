package org.toubassi.littlescript.ast.expr;

import org.toubassi.littlescript.interpreter.Env;
import org.toubassi.littlescript.compiler.AssemblyWriter;
import org.toubassi.littlescript.compiler.SymbolTable;

/**
 * Created by gtoubassi on 7/17/15.
 */
public class LiteralNumber extends Expression {

    private int value;

    public LiteralNumber(int value) {
        this.value = value;
    }

    public int evalExpression(Env env) {
        return value;
    }

    @Override
    public void compile(SymbolTable symbols, AssemblyWriter writer) {
        writer.emitComment("Load constant " + value);
        writer.emit("movl", "$" + value, "%edi");
    }
}
