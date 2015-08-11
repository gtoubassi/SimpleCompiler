package org.toubassi.littlescript.parser;

import java.nio.file.Files;

/**
 * Created by gtoubassi on 7/17/15.
 */
public class Token {

    public enum Type {
        Literal,
        Identifier,
        Assignment,

        OperatorPlus,
        OperatorMinus,
        OperatorTimes,
        OperatorDivide,
        OperatorEqual,
        OperatorNotEqual,
        OperatorGreaterThan,
        OperatorLessThan,
        OperatorGreaterThanOrEqual,
        OperatorLessThanOrEqual,

        LeftParen,
        RightParen,
        LeftBrace,
        RightBrace,

        Comma,
        Semicolon,

        KeywordReturn,
        KeywordFunc,
        KeywordIf,
        KeywordWhile
    }

    public Type type;
    public String identifier;
    public int value;
    public int debugCharacterOffset;
    public int debugLineNumber;
    public int debugOffsetInLine;

    public Token(Type type) {
        this.type = type;
    }

    public String toString() {
        if (type == Type.Identifier) {
            return "identifier " + identifier;
        }
        if (type == Type.Literal) {
            return "literal " + value;
        }
        return type.toString();
    }
}
