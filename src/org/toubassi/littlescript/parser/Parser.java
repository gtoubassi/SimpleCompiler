package org.toubassi.littlescript.parser;

import org.toubassi.littlescript.ast.*;
import org.toubassi.littlescript.ast.expr.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gtoubassi on 7/19/15.
 */
public class Parser {

    public static class Exception extends java.lang.Exception {
        public Exception(String message) {
            super(message);
        }
    }

    private Scanner scanner;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
    }

    public Parser(String text) {
        this(new Scanner(text));
    }

    public Block parse() throws Exception {
        return parseBlock(null);
    }

    private Block parseBlock(String identifier, String... params) throws Exception {
        Block block = new Block(identifier, params);

        Token token;
        while ((token = nextToken()) != null) {
            scanner.pushbackToken(token);
            if (token.type == Token.Type.RightBrace) {
                break;
            }

            Node statement = parseStatement();
            if (statement == null) {
                break;
            }

            block.addChild(statement);
        }

        return block;
    }

    private Node parseStatement() throws Exception {
        Token token = nextToken();
        if (token == null) {
            // End of program
            return null;
        }

        if (token.type == Token.Type.Identifier) {
            Node statement = parseAssignmentOrCall(token.identifier);
            consumeExpected(Token.Type.Semicolon);
            return statement;
        }
        else if (token.type == Token.Type.KeywordFunc) {
            return parseFunc();
        }
        else if (token.type == Token.Type.KeywordIf) {
            return parseConditional();
        }
        else if (token.type == Token.Type.KeywordWhile) {
            return parseWhile();
        }
        else if (token.type == Token.Type.KeywordReturn) {
            Node statement = parseReturn();
            consumeExpected(Token.Type.Semicolon);
            return statement;
        }

        error("Expected assignment statement, if, while, return, or func declaration");
        return null;
    }

    private Node parseAssignmentOrCall(String identifier) throws Exception {
        Token token = nextToken();

        if (token.type == Token.Type.Assignment) {
            return parseAssignment(identifier);
        }
        else if (token.type == Token.Type.LeftParen) {
            return parseCall(identifier);
        }
        error("Expected either an assignment or function call associated with identifier " + identifier);
        return null;
    }

    private Assignment parseAssignment(String identifier) throws Exception {
        Assignment assignment = new Assignment();
        assignment.setLValue(identifier);
        assignment.setRValue(parseExpression());
        return assignment;
    }

    private Expression parseExpression() throws Exception {
        // parseExpression < > <= >= == !=
        // parsePlusOrMinusTerm + -
        // parseDivideOrTimesTerm / *
        // parsePrimitiveTerm ( ) literal, identifier, function call

        Expression current = parsePlusOrMinusTerm();

        Token token;
        while ((token = nextToken()).type == Token.Type.OperatorLessThan ||
                token.type == Token.Type.OperatorLessThanOrEqual ||
                token.type == Token.Type.OperatorGreaterThan ||
                token.type == Token.Type.OperatorGreaterThanOrEqual ||
                token.type == Token.Type.OperatorEqual ||
                token.type == Token.Type.OperatorNotEqual) {

            current = new BinaryOp(current, convertOp(token.type), parsePlusOrMinusTerm());
        }

        scanner.pushbackToken(token);
        return current;
    }

    private Expression parsePlusOrMinusTerm() throws Exception {
        Expression current = parseDivideOrTimesTerm();

        Token token;
        while ((token = nextToken()).type == Token.Type.OperatorPlus ||
                token.type == Token.Type.OperatorMinus) {

            current = new BinaryOp(current, convertOp(token.type), parseDivideOrTimesTerm());
        }

        scanner.pushbackToken(token);
        return current;
    }

    private Expression parseDivideOrTimesTerm() throws Exception {
        Expression current = parsePrimitiveTerm();

        Token token;
        while ((token = nextToken()).type == Token.Type.OperatorDivide ||
                token.type == Token.Type.OperatorTimes) {

            current = new BinaryOp(current, convertOp(token.type), parsePrimitiveTerm());
        }

        scanner.pushbackToken(token);
        return current;
    }

    private Expression parsePrimitiveTerm() throws Exception {
        Token token = nextToken();
        if (token.type == Token.Type.LeftParen) {
            Expression expression =  parseExpression();
            consumeExpected(Token.Type.RightParen);
            return expression;
        }
        else if (token.type == Token.Type.Identifier) {
            // Either a variable reference or a function call.
            String identifier = token.identifier;
            token = nextToken();

            if (token.type == Token.Type.LeftParen) {
                // Functional Call
                Expression expression =  parseCallExpression(identifier);
                return expression;
            }
            else {
                scanner.pushbackToken(token);
                return new Variable(identifier);
            }
        }
        else if (token.type == Token.Type.Literal) {
            return new LiteralNumber(token.value);
        }
        error("Expected left paren, right paren, number, identifier, or function call but got " + token);
        return null;
    }

    private CallExpr parseCallExpression(String identifier) throws Exception {
        // Left paren already consumed
        CallExpr expr = new CallExpr(identifier);
        Token token;

        while ((token = nextToken()).type != Token.Type.RightParen) {
            scanner.pushbackToken(token);
            expr.addChild(parseExpression());
            token = nextToken();
            if (token.type != Token.Type.RightParen && token.type != Token.Type.Comma) {
                error("Missing , or right paren in argument list for call to function " + identifier);
            }
            if (token.type != Token.Type.Comma) {
                scanner.pushbackToken(token);
            }
        }
        return expr;
    }

    private CallStatement parseCall(String identifier) throws Exception {
        return new CallStatement(parseCallExpression(identifier));
    }

    private Block parseFunc() throws Exception {
        Token token = consumeExpected(Token.Type.Identifier);
        String functionName = token.identifier;

        consumeExpected(Token.Type.LeftParen);

        List<String> formalParamNames = new ArrayList();

        while ((token = nextToken()).type != Token.Type.RightParen) {
            if (token.type != Token.Type.Identifier) {
                error("Expected identifier in declaration of parameters for function " + functionName + " got " + token);
            }
            formalParamNames.add(token.identifier);
            token = nextToken();
            if (token.type != Token.Type.RightParen && token.type != Token.Type.Comma) {
                error("Missing , or right paren in parameter list for declaration of function " + functionName);
            }
            if (token.type != Token.Type.Comma) {
                scanner.pushbackToken(token);
            }
        }

        consumeExpected(Token.Type.LeftBrace);
        Block block = parseBlock(functionName, formalParamNames.toArray(new String[formalParamNames.size()]));
        consumeExpected(Token.Type.RightBrace);

        return block;
    }

    private Conditional parseConditional() throws Exception {
        consumeExpected(Token.Type.LeftParen);
        Conditional conditional = new Conditional(parseExpression());
        consumeExpected(Token.Type.RightParen);
        consumeExpected(Token.Type.LeftBrace);

        Block block = parseBlock(null);
        for (Node child : block.getChildren()) {
            conditional.addChild(child);
        }

        consumeExpected(Token.Type.RightBrace);
        return conditional;
    }

    private Return parseReturn() throws Exception {
        return new Return(parseExpression());
    }

    private While parseWhile() throws Exception {
        consumeExpected(Token.Type.LeftParen);
        While whileStatement = new While(parseExpression());
        consumeExpected(Token.Type.RightParen);
        consumeExpected(Token.Type.LeftBrace);

        Block block = parseBlock(null);
        for (Node child : block.getChildren()) {
            whileStatement.addChild(child);
        }

        consumeExpected(Token.Type.RightBrace);
        return whileStatement;
    }

    private Token nextToken() {
        return scanner.nextToken();
    }

    private Token consumeExpected(Token.Type type) throws Exception {
        Token token = nextToken();
        if (token == null || token.type != type) {
            error("Expected token " + type + " but got " + token);
        }
        return token;
    }

    private void error(String errorString) throws Exception {
        throw new Exception(errorString);
    }

    private BinaryOp.Type convertOp(Token.Type tokenType) throws Exception {
        BinaryOp.Type binOpType;

        switch (tokenType) {
            case OperatorLessThan:
                binOpType = BinaryOp.Type.LessThan;
                break;
            case OperatorGreaterThan:
                binOpType = BinaryOp.Type.GreaterThan;
                break;
            case OperatorLessThanOrEqual:
                binOpType = BinaryOp.Type.LessThanOrEqual;
                break;
            case OperatorGreaterThanOrEqual:
                binOpType = BinaryOp.Type.GreaterThanOrEqual;
                break;
            case OperatorEqual:
                binOpType = BinaryOp.Type.Equal;
                break;
            case OperatorNotEqual:
                binOpType = BinaryOp.Type.NotEqual;
                break;
            case OperatorPlus:
                binOpType = BinaryOp.Type.Plus;
                break;
            case OperatorMinus:
                binOpType = BinaryOp.Type.Minus;
                break;
            case OperatorDivide:
                binOpType = BinaryOp.Type.Divide;
                break;
            case OperatorTimes:
                binOpType = BinaryOp.Type.Times;
                break;
            default:
                throw new Exception("Unexpected operator type " + tokenType);
        }

        return binOpType;
    }
}
