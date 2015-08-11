package org.toubassi.littlescript.ast;

import org.toubassi.littlescript.interpreter.Env;
import org.toubassi.littlescript.compiler.AssemblyWriter;
import org.toubassi.littlescript.compiler.SymbolTable;

/**
 * Created by gtoubassi on 7/17/15.
 */
public class Block extends Node {

    private String name;
    private String[] formalParams;

    public Block(String name, String... params) {
        this.name = name;
        this.formalParams = params;
    }

    public String getName() {
        return name;
    }

    public String[] getFormalParams() {
        return formalParams;
    }

    public Block getBlockWithName(String name) {
        if (name.equals(this.name)) {
            return this;
        }
        for (Node child : children) {
            if (child instanceof Block) {
                Block b = ((Block)child).getBlockWithName(name);
                if (b != null) {
                    return b;
                }
            }
        }
        return null;
    }

    @Override
    public Node eval(Env env) {
        // This will cause execution to skip over the block.
        return getNextExecutionNode(env);
    }

    @Override
    public void compile(SymbolTable symbols, AssemblyWriter writer) {

        if (name != null) {
            writer.beginFunction();
        }

        SymbolTable localSymbols = new SymbolTable();

        String identifier = name == null ? "_main" : "_" + name;
        writer.emitComment("function prologue for " + identifier);
        writer.emit(".globl\t" + identifier);
        writer.emit(".align\t4, 0x90");
        writer.emitLabel(identifier);
        writer.emit(".cfi_startproc");
        writer.emit("pushq", "%rbp");
        writer.emit(".cfi_def_cfa_offset 16");
        writer.emit(".cfi_offset %rbp, -16");
        writer.emit("movq", "%rsp", "%rbp");
        writer.emit(".cfi_def_cfa_register %rbp");

        // Establish parameters as locals
        String registers[] = {"%rdi", "%rsi", "%rdx", "%rcx", "%r8", "%r9"};
        if (formalParams.length > registers.length) {
            throw new IllegalArgumentException("Too many formal params in " + identifier + " (max of " + registers.length + ")");
        }
        for (int i = 0; i < formalParams.length; i++) {
            int offset = localSymbols.getFrameOffsetFor(formalParams[i], writer);
            writer.emit("movq", registers[i], offset + "(%rbp)");
        }

        for (Node child : children) {
            child.compile(localSymbols, writer);
        }

        writer.emit("movl", "$0", "%eax");
        Return.emitFunctionReturn(localSymbols, writer);
        writer.emit(".cfi_endproc");

        if (name != null) {
            writer.endFunction();
        }
    }
}
