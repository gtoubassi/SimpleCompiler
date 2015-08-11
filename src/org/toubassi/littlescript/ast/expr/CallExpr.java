package org.toubassi.littlescript.ast.expr;

import org.toubassi.littlescript.compiler.AssemblyWriter;
import org.toubassi.littlescript.compiler.SymbolTable;
import org.toubassi.littlescript.interpreter.Env;
import org.toubassi.littlescript.ast.Block;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gtoubassi on 7/17/15.
 */
public class CallExpr extends Expression {
    private String identifier;
    private List<Expression> children = new ArrayList();

    public CallExpr(String identifier) {
        this.identifier = identifier;
    }

    public void addChild(Expression expression) {
        children.add(expression);
    }

    public String getIdentifier() {
        return identifier;
    }

    public int evalExpression(Env env) {
        // Evaluate the actual parameters to be passed
        int[] actualParameterValues = new int[children.size()];
        for (int i = 0; i < children.size(); i++) {
            Expression child = children.get(i);
            actualParameterValues[i] = child.evalExpression(env);
        }

        if ("print".equals(identifier)) {
            System.out.println(actualParameterValues[0]);
            return 0;
        }
        else {
            Block block = env.getBlockWithName(identifier);

            if (children.size() != block.getFormalParams().length) {
                throw new IllegalArgumentException("formal/actual parameter count mismatch");
            }

            // Push a new frame and set the parameter values.
            Env e = new Env(block);
            for (int i = 0; i < actualParameterValues.length; i++) {
                e.setVariable(block.getFormalParams()[i], actualParameterValues[i]);
            }

            // Call the function
            return e.run();
        }
    }


    @Override
    public void compile(SymbolTable symbols, AssemblyWriter writer) {

        if ("print".equals(identifier)) {
            children.get(0).compile(symbols, writer);
            writer.emit("movl", "%edi", "%esi");
            writer.emit("leaq", "L_.printfmt(%rip)", "%rax");
            writer.emit("movq", "%rax", "%rdi");
            writer.emit("movb", "$0", "%al"); // whut dis for?
            writer.emit("callq", "_printf");
            // Leave zero in the return value just in case someone does 1 + print(a)
            writer.emit("movl", "$0", "%edi");
        }
        else {
            String registers[] = {"%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9"};
            if (children.size() > registers.length) {
                throw new IllegalArgumentException("Can't pass more then " + registers.length + " arguments to a function");
            }
            for (int i = children.size() - 1; i >= 0; i--) {
                Expression child = children.get(i);
                child.compile(symbols, writer);
                writer.emit("pushq", "%rdi");
            }
            for (int i = 0; i < children.size(); i++) {
                writer.emit("popq", registers[i]);
            }
            writer.emit("movb", "$0", "%al"); // whut dis for?
            writer.emit("callq", "_" + identifier);
            writer.emit("movl", "%eax", "%edi");
        }

    }

}
