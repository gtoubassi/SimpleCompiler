package org.toubassi.littlescript.interpreter;

import org.toubassi.littlescript.ast.Block;
import org.toubassi.littlescript.ast.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gtoubassi on 7/17/15.
 */
public class Env {

    public static class Frame {
        private Map<String, Integer> variables = new HashMap<String, Integer>();
        private Frame parent;
        private int returnValue;

        public Frame(Frame parent) {
            this.parent = parent;
        }

        public void setVariable(String identifier, int value) {
            variables.put(identifier, value);
        }

        public int getVariable(String identifier) {
            Integer i = variables.get(identifier);
            return i == null ? 0 : i;
        }

        public void setReturnValue(int value) { returnValue = value;}

        public void dump() {
            for (Map.Entry entry : variables.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue());
            }
        }
    }

    private Block block;
    private Frame currentFrame;

    public Env(Block block) {
        this.block = block;
        currentFrame = new Frame(null);
    }

    public void setVariable(String identifier, int value) {
        currentFrame.setVariable(identifier, value);
    }

    public int getVariable(String identifier) {
        return currentFrame.getVariable(identifier);
    }

    public void setReturnValue(int value) {
        currentFrame.setReturnValue(value);
    }

    public Block getBlockWithName(String name) {
        return block.getBlockWithName(name);
    }

    public int run() {
        Node programCounter = block.getChildren().get(0);
        while (programCounter != null) {
            programCounter = programCounter.eval(this);
        }

        return currentFrame.returnValue;
    }

    public void dump() {
        currentFrame.dump();
    }
}
