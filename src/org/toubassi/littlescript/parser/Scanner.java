package org.toubassi.littlescript.parser;

import java.io.InputStream;
import java.io.StringReader;

/**
 * Created by gtoubassi on 7/17/15.
 */
public class Scanner {
    private String text;
    private int length;
    private int index;
    private int currentTokenStartOffset;
    private Token pushedbackToken;

    public Scanner(String text) {
        this.text = text;
        this.length = text.length();
    }

    public void pushbackToken(Token token) {
        pushedbackToken = token;
    }

    public Token nextToken() {
        if (pushedbackToken != null) {
            Token token = pushedbackToken;
            pushedbackToken = null;
            return token;
        }

        skipWhite();
        if (index >= length) {
            return null;
        }

        currentTokenStartOffset = index;

        switch (curr()) {
            case '(':
                index++;
                return newToken(Token.Type.LeftParen);
            case ')':
                index++;
                return newToken(Token.Type.RightParen);
            case '{':
                index++;
                return newToken(Token.Type.LeftBrace);
            case '}':
                index++;
                return newToken(Token.Type.RightBrace);
            case '+':
                index++;
                return newToken(Token.Type.OperatorPlus);
            case '-':
                index++;
                return newToken(Token.Type.OperatorMinus);
            case '/':
                index++;
                return newToken(Token.Type.OperatorDivide);
            case '*':
                index++;
                return newToken(Token.Type.OperatorTimes);
            case ';':
                index++;
                return newToken(Token.Type.Semicolon);
            case ',':
                index++;
                return newToken(Token.Type.Comma);
            default:
                return tokenizeNonSimple();
        }

    }

    private Token tokenizeNonSimple() {
        if (Character.isDigit(curr())) {
            Token token = newToken(Token.Type.Literal);
            token.value = 0;
            while (Character.isDigit(curr())) {
                token.value = token.value * 10 + curr() - '0';
                index++;
            }
            return token;
        }
        else if (Character.isLetter(curr())){
            StringBuilder builder = new StringBuilder();
            while (Character.isLetter(curr())) {
                builder.append(curr());
                index++;
            }

            String str = builder.toString();

            if (str.equals("if")) {
                return newToken(Token.Type.KeywordIf);
            }
            if (str.equals("while")) {
                return newToken(Token.Type.KeywordWhile);
            }
            if (str.equals("return")) {
                return newToken(Token.Type.KeywordReturn);
            }
            if (str.equals("func")) {
                return newToken(Token.Type.KeywordFunc);
            }

            Token token = newToken(Token.Type.Identifier);
            token.identifier = str;
            return token;
        }
        else if (curr() == '=') {
            if (peek() == '=') {
                index += 2;
                return newToken(Token.Type.OperatorEqual);
            }
            index++;
            return newToken(Token.Type.Assignment);
        }
        else if (curr() == '!') {
            if (peek() == '=') {
                index += 2;
                return newToken(Token.Type.OperatorNotEqual);
            }
            throw new IllegalArgumentException("Scanning error: expected '=' after '!'");
        }
        else if (curr() == '>') {
            if (peek() == '=') {
                index += 2;
                return newToken(Token.Type.OperatorGreaterThanOrEqual);
            }
            index++;
            return newToken(Token.Type.OperatorGreaterThan);
        }
        else if (curr() == '<') {
            if (peek() == '=') {
                index += 2;
                return newToken(Token.Type.OperatorLessThanOrEqual);
            }
            index++;
            return newToken(Token.Type.OperatorLessThan);
        }
        throw new IllegalArgumentException("Unexpected character " + curr());
    }

    private void skipWhite() {
        while (index < length && Character.isWhitespace(curr())) {
            index++;
        }
    }

    private char curr() {
        return text.charAt(index);
    }

    private char peek() {
        if (index + 1 < text.length()) {
            return text.charAt(index + 1);
        }
        return 0;
    }

    private Token newToken(Token.Type type) {
        Token token = new Token(type);
        token.debugCharacterOffset = currentTokenStartOffset;
        return token;
    }

    private String getInputLineForIndex(int index) {
        if (index < 0 || index >= text.length()) {
            return "<out of range>";
        }

        // Scan backward
        int lineStart = index;
        while (lineStart > 0 && text.charAt(lineStart) != '\n') {
            lineStart--;
        }
        if (lineStart > 0) {
            lineStart++; // Don't include the '\n'
        }

        // Scan forward
        int lineEnd = index;
        while (lineEnd < text.length() - 1 && text.charAt(lineEnd) != '\n') {
            lineEnd++;
        }
        // substring is exclusive of the endIndex so we won't include the \n

        return text.substring(lineStart, lineEnd);
    }

    public static void main(String[] args) throws Exception {
        InputStream in = Scanner.class.getResourceAsStream("script1.lscript");
        int nextByte;
        StringBuilder builder = new StringBuilder();
        while ((nextByte = in.read()) >= 0) {
            builder.append((char)nextByte);
        }
        in.close();

        String programText = builder.toString();

        Scanner scanner = new Scanner(programText);
        Token token;
        while ((token = scanner.nextToken()) != null) {
            System.out.println(token + "    '" + scanner.getInputLineForIndex(token.debugCharacterOffset) + "'");
        }
    }
}
