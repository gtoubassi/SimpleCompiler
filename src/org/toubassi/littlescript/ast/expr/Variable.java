package org.toubassi.littlescript.ast.expr;

import org.toubassi.littlescript.interpreter.Env;
import org.toubassi.littlescript.compiler.AssemblyWriter;
import org.toubassi.littlescript.compiler.SymbolTable;

/**
 * Created by gtoubassi on 7/17/15.
 */
public class Variable extends Expression {

    private String identifier;

    public Variable(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int evalExpression(Env env) {
        return env.getVariable(identifier);
    }

    @Override
    public void compile(SymbolTable symbols, AssemblyWriter writer) {
        int offset = symbols.getFrameOffsetFor(identifier, writer);
        writer.emitComment("Load " + identifier);
        writer.emit("movl", offset + "(%rbp)", "%edi");
    }
}
