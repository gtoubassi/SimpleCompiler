package org.toubassi.littlescript.ast.expr;

import org.toubassi.littlescript.interpreter.Env;
import org.toubassi.littlescript.compiler.AssemblyWriter;
import org.toubassi.littlescript.compiler.SymbolTable;

/**
 * Created by gtoubassi on 7/17/15.
 */
public class BinaryOp extends Expression {

    public enum Type {
        Plus, Minus, Divide, Times,
        Equal, NotEqual, LessThan, GreaterThan, LessThanOrEqual, GreaterThanOrEqual,
    };

    private Type type;
    private Expression left;
    private Expression right;

    public BinaryOp(Expression left, Type type, Expression right) {
        this.left = left;
        this.type = type;
        this.right = right;
    }

    public Type getType() {
        return type;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public int evalExpression(Env env) {
        int value = 0;
        int leftValue = left.evalExpression(env);
        int rightValue = right.evalExpression(env);
        switch (type) {
            case Plus:
                value = leftValue + rightValue;
                break;
            case Minus:
                value = leftValue - rightValue;
                break;
            case Divide:
                value = leftValue / rightValue;
                break;
            case Times:
                value = leftValue * rightValue;
                break;
            case Equal:
                value = leftValue == rightValue ? 1 : 0;
                break;
            case NotEqual:
                value = leftValue != rightValue ? 1 : 0;
                break;
            case LessThan:
                value = leftValue < rightValue ? 1 : 0;
                break;
            case GreaterThan:
                value = leftValue > rightValue ? 1 : 0;
                break;
            case LessThanOrEqual:
                value = leftValue <= rightValue ? 1 : 0;
                break;
            case GreaterThanOrEqual:
                value = leftValue >= rightValue ? 1 : 0;
                break;
        }
        return value;
    }

    @Override
    public void compile(SymbolTable symbols, AssemblyWriter writer) {
        right.compile(symbols, writer);
        writer.emit("pushq", "%rdi");
        //writer.emit("movl", "%edi", "%ebx");
        left.compile(symbols, writer);
        writer.emit("popq", "%rbx");

        // the args are in ebx and edi, and the

        writer.emitComment(type.toString());

        switch (type) {
            case Plus:
                writer.emit("addl", "%ebx", "%edi");
                break;
            case Minus:
                writer.emit("subl", "%ebx", "%edi");
                break;
            case Divide:
                writer.emit("movl", "%edi", "%eax");
                writer.emit("cltd");
                writer.emit("idivl", "%ebx");
                writer.emit("movl", "%eax", "%edi");
                break;
            case Times:
                writer.emit("imull", "%ebx", "%edi");
                break;
            case Equal:
                emitConditionalExpression(writer, "jne");
                break;
            case NotEqual:
                emitConditionalExpression(writer, "je");
                break;
            case GreaterThan:
                emitConditionalExpression(writer, "jle");
                break;
            case LessThan:
                emitConditionalExpression(writer, "jge");
                break;
            case GreaterThanOrEqual:
                emitConditionalExpression(writer, "jl");
                break;
            case LessThanOrEqual:
                emitConditionalExpression(writer, "jg");
                break;
            default:
                throw new RuntimeException("unsupported binary operator " + type);
        }
    }

    private void emitConditionalExpression(AssemblyWriter writer, String branchOp) {
        writer.emit("cmpl", "%ebx", "%edi");
        String falseLabel = writer.getLabel();
        String doneLabel = writer.getLabel();
        writer.emit(branchOp, falseLabel);
        writer.emit("movl", "$1", "%edi");
        writer.emit("jmp", doneLabel);
        writer.emitLabel(falseLabel);
        writer.emit("movl", "$0", "%edi");
        writer.emitLabel(doneLabel);
    }
}
