package org.toubassi.littlescript.ast;

import org.toubassi.littlescript.compiler.AssemblyWriter;
import org.toubassi.littlescript.compiler.SymbolTable;
import org.toubassi.littlescript.interpreter.Env;
import org.toubassi.littlescript.ast.expr.Expression;

/**
 * Created by gtoubassi on 7/17/15.
 */
public class Return extends Node {
    private Expression expression;

    public Return(Expression expression) {
        this.expression = expression;
    }

    @Override
    public Node eval(Env env) {
        env.setReturnValue(expression.evalExpression(env));
        return null;
    }

    @Override
    public void compile(SymbolTable symbols, AssemblyWriter writer) {
        expression.compile(symbols, writer);
        writer.emit("movl", "%edi", "%eax");
        emitFunctionReturn(symbols, writer);
    }

    /**
     * Its assumed the return value is already in eax
     */
    public static void emitFunctionReturn(SymbolTable symbols, AssemblyWriter writer) {
        symbols.popLocals(writer);
        writer.emit("popq", "%rbp");
        writer.emit("retq");
    }
}
