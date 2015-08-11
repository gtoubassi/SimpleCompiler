package org.toubassi.littlescript.ast;

import org.toubassi.littlescript.compiler.AssemblyWriter;
import org.toubassi.littlescript.compiler.SymbolTable;
import org.toubassi.littlescript.interpreter.Env;
import org.toubassi.littlescript.ast.expr.CallExpr;
import org.toubassi.littlescript.ast.expr.Expression;

/**
 * Created by gtoubassi on 7/17/15.
 */
public class CallStatement extends Node {
    private Expression expression;

    public CallStatement(CallExpr expression) {
        this.expression = expression;
    }

    public Node eval(Env env) {
        expression.evalExpression(env);
        return getNextExecutionNode(env);
    }

    @Override
    public void compile(SymbolTable symbols, AssemblyWriter writer) {
        expression.compile(symbols, writer);
    }
}
