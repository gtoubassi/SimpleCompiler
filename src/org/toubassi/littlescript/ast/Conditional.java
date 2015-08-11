package org.toubassi.littlescript.ast;

import org.toubassi.littlescript.compiler.AssemblyWriter;
import org.toubassi.littlescript.compiler.SymbolTable;
import org.toubassi.littlescript.interpreter.Env;
import org.toubassi.littlescript.ast.expr.Expression;

/**
 * Created by gtoubassi on 7/17/15.
 */
public class Conditional extends Node {

    private Expression expression;

    public Conditional(Expression expression) {
        this.expression = expression;
    }

    public Node eval(Env env) {
        if (expression.evalExpression(env) == 1) {
            return children.get(0);
        }
        return getNextExecutionNode(env);
    }

    @Override
    public void compile(SymbolTable symbols, AssemblyWriter writer) {
        expression.compile(symbols, writer);
        // result in %edi

        writer.emit("cmpl", "$0", "%edi");
        String falseLabel = writer.getLabel();
        writer.emit("je", falseLabel);

        children.get(0).compile(symbols, writer);

        writer.emitLabel(falseLabel);
    }
}
