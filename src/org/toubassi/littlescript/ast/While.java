package org.toubassi.littlescript.ast;

import org.toubassi.littlescript.compiler.AssemblyWriter;
import org.toubassi.littlescript.compiler.SymbolTable;
import org.toubassi.littlescript.interpreter.Env;
import org.toubassi.littlescript.ast.expr.Expression;

/**
 * Created by gtoubassi on 7/17/15.
 */
public class While extends Node {
    private Expression expression;

    public While(Expression expression) {
        this.expression = expression;
    }

    public Node eval(Env env) {
        return getNextExecutionNode(env);
    }

    public Node getNextExecutionNode(Env env) {
        if (expression.evalExpression(env) == 1) {
            return children.get(0);
        }
        return super.getNextExecutionNode(env);
    }

    @Override
    public void compile(SymbolTable symbols, AssemblyWriter writer) {
        String loopStartLabel = writer.getLabel();
        writer.emitLabel(loopStartLabel);
        expression.compile(symbols, writer);
        // result in %edi

        writer.emit("cmpl", "$0", "%edi");
        String doneLabel = writer.getLabel();
        writer.emit("je", doneLabel);

        for (Node child : children) {
            child.compile(symbols, writer);
        }

        writer.emit("jmp", loopStartLabel);

        writer.emitLabel(doneLabel);
    }
}
