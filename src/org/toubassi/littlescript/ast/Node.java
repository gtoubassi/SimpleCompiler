package org.toubassi.littlescript.ast;

import org.toubassi.littlescript.interpreter.Env;
import org.toubassi.littlescript.ast.expr.*;
import org.toubassi.littlescript.compiler.AssemblyWriter;
import org.toubassi.littlescript.compiler.SymbolTable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gtoubassi on 7/17/15.
 */
public abstract class Node {

    protected List<Node> children = new ArrayList();
    protected Node parent;

    public void addChild(Node child) {
        child.parent = this;
        children.add(child);
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getNextExecutionNode(Env env) {
        if (parent == null) {
            return null;
        }

        int index = parent.children.indexOf(this);
        if (index < parent.children.size() - 1) {
            return parent.children.get(index + 1);
        }
        return parent.getNextExecutionNode(env);
    }

    public abstract Node eval(Env env);

    public void compile(SymbolTable symbols, AssemblyWriter writer) {
    }

    public static void main(String[] args) {

        Block program = new Block(null);

        /*
        func sum(a, b) {
            return a + b;
        }
        */
        {
            Block function = new Block("sum", "a", "b");
            Expression expr = new BinaryOp(new Variable("a"), BinaryOp.Type.Plus, new Variable("b"));
            Return ret = new Return(expr);
            function.addChild(ret);
            program.addChild(function);
        }

        // a = 2;
        {
            Assignment assignA = new Assignment();
            assignA.setLValue("a");
            assignA.setRValue(new LiteralNumber(2));
            program.addChild(assignA);
        }

        // b = 4 * (a + 3);
        {
            Assignment assignB = new Assignment();
            assignB.setLValue("b");
            BinaryOp add = new BinaryOp(
                    new Variable("a"),
                    BinaryOp.Type.Plus,
                    new LiteralNumber(3));

            BinaryOp mult = new BinaryOp(
                    new LiteralNumber(4),
                    BinaryOp.Type.Times,
                    add);

            assignB.setRValue(mult);
            program.addChild(assignB);
        }


        //if (b > 10) {
        //    a = 10000 - b;
        //}
        {
            Expression expr = new BinaryOp(new Variable("b"),
                    BinaryOp.Type.GreaterThan,
                    new LiteralNumber(10));
            Conditional condition = new Conditional(expr);

            Assignment assignA = new Assignment();
            assignA.setLValue("a");
            BinaryOp operator = new BinaryOp(
                    new LiteralNumber(10000),
                    BinaryOp.Type.Minus,
                    new Variable("b"));
            assignA.setRValue(operator);

            condition.addChild(assignA);

            program.addChild(condition);
        }

        // c = 0;
        // d = 1;
        {
            Assignment assignA = new Assignment();
            assignA.setLValue("c");
            assignA.setRValue(new LiteralNumber(0));
            program.addChild(assignA);

            Assignment assignB = new Assignment();
            assignB.setLValue("d");
            assignB.setRValue(new LiteralNumber(1));
            program.addChild(assignB);
        }

        // while (c < 10) {
        {
            Expression expr = new BinaryOp(new Variable("c"),
                    BinaryOp.Type.LessThan,
                    new LiteralNumber(10));
            While whileStatement = new While(expr);
            program.addChild(whileStatement);

            //    c = c + 1;
            {
                Assignment assignC = new Assignment();
                assignC.setLValue("c");
                BinaryOp operator = new BinaryOp(
                        new Variable("c"),
                        BinaryOp.Type.Plus,
                        new LiteralNumber(1));
                assignC.setRValue(operator);
                whileStatement.addChild(assignC);
            }

            //    d = 2 * d;
            {
                Assignment assignD = new Assignment();
                assignD.setLValue("d");
                BinaryOp operator = new BinaryOp(
                        new Variable("d"),
                        BinaryOp.Type.Times,
                        new LiteralNumber(2));
                assignD.setRValue(operator);
                whileStatement.addChild(assignD);
            }
        }

        // e = sum(c, d);
        {
            Assignment assign = new Assignment();
            assign.setLValue("e");
            CallExpr callExpr = new CallExpr("sum");
            callExpr.addChild(new Variable("c"));
            callExpr.addChild(new Variable("d"));
            assign.setRValue(callExpr);
            program.addChild(assign);
        }


        Env env = new Env(program);
        env.run();
        env.dump();
    }
}
