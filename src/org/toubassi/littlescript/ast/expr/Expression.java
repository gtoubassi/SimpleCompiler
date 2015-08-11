package org.toubassi.littlescript.ast.expr;

import org.toubassi.littlescript.interpreter.Env;
import org.toubassi.littlescript.compiler.AssemblyWriter;
import org.toubassi.littlescript.compiler.SymbolTable;

/**
 * Created by gtoubassi on 7/17/15.
 */
public abstract class Expression {

    public abstract int evalExpression(Env env);

    public void compile(SymbolTable symbols, AssemblyWriter writer) {
    }
}
