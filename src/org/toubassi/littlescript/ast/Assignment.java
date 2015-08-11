package org.toubassi.littlescript.ast;

import org.toubassi.littlescript.interpreter.Env;
import org.toubassi.littlescript.ast.expr.Expression;
import org.toubassi.littlescript.compiler.AssemblyWriter;
import org.toubassi.littlescript.compiler.SymbolTable;

/**
 * Created by gtoubassi on 7/17/15.
 */
public class Assignment extends Node {
    private String lValue;
    private Expression rValue;

    public String getLValue() {
        return lValue;
    }

    public void setLValue(String lValue) {
        this.lValue = lValue;
    }

    public Expression getRValue() {
        return rValue;
    }

    public void setRValue(Expression rValue) {
        this.rValue = rValue;
    }

    @Override
    public Node eval(Env env) {
        env.setVariable(lValue, rValue.evalExpression(env));
        return getNextExecutionNode(env);
    }

    @Override
    public void compile(SymbolTable symbols, AssemblyWriter writer) {
        int offset = symbols.getFrameOffsetFor(lValue, writer);
        rValue.compile(symbols, writer);
        writer.emitComment("assign to " + lValue);
        writer.emit("movl", "%edi", offset + "(%rbp)");
    }

}
