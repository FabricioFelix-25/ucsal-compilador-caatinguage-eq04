package br.ucsal.caatinguage.lexico;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Analisador léxico da linguagem Caatinguage2025-2.
 * Implementação inicial (esqueleto).
 *
 * TODO: implementar reconhecimento completo de tokens conforme a gramática.
 */
public class Lexer implements AutoCloseable {

    @Override
    public void close() throws IOException {
        reader.close();
    }

    private static final int EOF_CHAR = -1;

    private final BufferedReader reader;
    private int currentChar;
    private int currentLine = 1;
    private boolean initialized = false;

    public Lexer(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    public void init() throws IOException {
        if (!initialized) {
            advance();
            initialized = true;
        }
    }

    private void advance() throws IOException {
        currentChar = reader.read();
        if (currentChar == '\n') {
            currentLine++;
        }
    }

    private boolean isEOF() {
        return currentChar == EOF_CHAR;
    }

    private boolean isWhitespace(int ch) {
        return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n';
    }

    private boolean isLetter(int ch) {
        return (ch >= 'A' && ch <= 'Z') || (ch >= 'a' && ch <= 'z') || ch == '_';
    }

    private boolean isDigit(int ch) {
        return (ch >= '0' && ch <= '9');
    }

    /**
     * Retorna o próximo token do fluxo ou EOF quando acabar.
     */
    public Token nextToken() throws IOException {
        if (!initialized) {
            init();
        }

        // Pular espaços e comentários
        skipWhitespaceAndComments();

        if (isEOF()) {
            return new Token(TokenType.EOF, "<EOF>", currentLine);
        }

        // Identificadores / palavras reservadas
        if (isLetter(currentChar)) {
            return readIdentifierOrReserved();
        }

        // Números
        if (isDigit(currentChar)) {
            return readNumber();
        }

        // Strings (simples: abre com aspas duplas)
        if (currentChar == '"') {
            return readString();
        }

        // Símbolos e operadores de um ou dois caracteres
        return readSymbolOrOperator();
    }

    private void skipWhitespaceAndComments() throws IOException {
        boolean again;
        do {
            again = false;

            // Espaços em branco
            while (!isEOF() && isWhitespace(currentChar)) {
                advance();
            }

            // Comentário de linha: //
            if (!isEOF() && currentChar == '/') {
                reader.mark(1);
                int next = reader.read();
                if (next == '/') {
                    // consumir até fim da linha
                    currentChar = next;
                    while (!isEOF() && currentChar != '\n') {
                        advance();
                    }
                    again = true;
                    advance();
                } else {
                    // não era comentário, volta
                    reader.reset();
                }
            }

            // Comentário de bloco: /* ... */
            if (!isEOF() && currentChar == '/') {
                reader.mark(1);
                int next = reader.read();
                if (next == '*') {
                    currentChar = next;
                    boolean endFound = false;
                    while (!isEOF()) {
                        advance();
                        if (currentChar == '*') {
                            reader.mark(1);
                            int next2 = reader.read();
                            if (next2 == '/') {
                                currentChar = next2;
                                advance(); // consome depois de */
                                endFound = true;
                                break;
                            } else {
                                reader.reset();
                            }
                        }
                    }
                    again = true;
                } else {
                    reader.reset();
                }
            }

        } while (again);
    }

    private Token readIdentifierOrReserved() throws IOException {
        StringBuilder sb = new StringBuilder();
        int startLine = currentLine;

        while (!isEOF() && (isLetter(currentChar) || isDigit(currentChar))) {
            sb.append((char) currentChar);
            advance();
        }

        String lexeme = sb.toString();
        String lower = lexeme.toLowerCase();

        // Mapear palavras reservadas básicas (complete conforme necessário)
        return switch (lower) {
            case "program" -> new Token(TokenType.PROGRAM, lexeme, startLine);
            case "declarations" -> new Token(TokenType.DECLARATIONS, lexeme, startLine);
            case "enddeclarations" -> new Token(TokenType.END_DECLARATIONS, lexeme, startLine);
            case "functions" -> new Token(TokenType.FUNCTIONS, lexeme, startLine);
            case "endfunctions" -> new Token(TokenType.END_FUNCTIONS, lexeme, startLine);
            case "endprogram" -> new Token(TokenType.END_PROGRAM, lexeme, startLine);
            case "if" -> new Token(TokenType.IF, lexeme, startLine);
            case "else" -> new Token(TokenType.ELSE, lexeme, startLine);
            case "endif" -> new Token(TokenType.END_IF, lexeme, startLine);
            case "while" -> new Token(TokenType.WHILE, lexeme, startLine);
            case "endwhile" -> new Token(TokenType.END_WHILE, lexeme, startLine);
            case "return" -> new Token(TokenType.RETURN, lexeme, startLine);
            case "break" -> new Token(TokenType.BREAK, lexeme, startLine);
            case "print" -> new Token(TokenType.PRINT, lexeme, startLine);
            case "true" -> new Token(TokenType.TRUE, lexeme, startLine);
            case "false" -> new Token(TokenType.FALSE, lexeme, startLine);
            case "integer"  -> new Token(TokenType.INTEGER,  lexeme, startLine);
            case "real"     -> new Token(TokenType.REAL,     lexeme, startLine);
            case "string"   -> new Token(TokenType.STRING,   lexeme, startLine);
            case "boolean"  -> new Token(TokenType.BOOLEAN,  lexeme, startLine);
            case "character"-> new Token(TokenType.CHARACTER,lexeme, startLine);
            case "void"     -> new Token(TokenType.VOID,     lexeme, startLine);

            default -> new Token(TokenType.IDENTIFIER, lexeme, startLine);
        };
    }

    private Token readNumber() throws IOException {
        StringBuilder sb = new StringBuilder();
        int startLine = currentLine;
        boolean hasDot = false;

        while (!isEOF() && (isDigit(currentChar) || currentChar == '.')) {
            if (currentChar == '.') {
                if (hasDot) break;
                hasDot = true;
            }
            sb.append((char) currentChar);
            advance();
        }

        String lexeme = sb.toString();
        TokenType type = hasDot ? TokenType.REAL_CONST : TokenType.INT_CONST;
        return new Token(type, lexeme, startLine);
    }

    private Token readString() throws IOException {
        int startLine = currentLine;
        StringBuilder sb = new StringBuilder();
        advance(); // consumir a aspas inicial

        while (!isEOF() && currentChar != '"') {
            // TODO: tratar escapes se necessário
            sb.append((char) currentChar);
            advance();
        }

        if (currentChar == '"') {
            advance(); // consumir aspas final
            return new Token(TokenType.STRING_CONST, sb.toString(), startLine);
        }

        // EOF sem fechar aspas
        return new Token(TokenType.ERROR, "Unterminated string literal", startLine);
    }

    private Token readSymbolOrOperator() throws IOException {
        int ch = currentChar;
        int startLine = currentLine;
        advance();

        switch (ch) {
            case '{': return new Token(TokenType.LBRACE, "{", startLine);
            case '}': return new Token(TokenType.RBRACE, "}", startLine);
            case '(': return new Token(TokenType.LPAREN, "(", startLine);
            case ')': return new Token(TokenType.RPAREN, ")", startLine);
            case '[': return new Token(TokenType.LBRACKET, "[", startLine);
            case ']': return new Token(TokenType.RBRACKET, "]", startLine);
            case ';': return new Token(TokenType.SEMICOLON, ";", startLine);
            case ',': return new Token(TokenType.COMMA, ",", startLine);
            case '+': return new Token(TokenType.PLUS, "+", startLine);
            case '-': return new Token(TokenType.MINUS, "-", startLine);
            case '*': return new Token(TokenType.STAR, "*", startLine);
            case '/': return new Token(TokenType.SLASH, "/", startLine);
            case '%': return new Token(TokenType.PERCENT, "%", startLine);
            case ':':
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.ASSIGN, ":=", startLine);
                }
                return new Token(TokenType.COLON, ":", startLine);
            case '<':
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.LE, "<=", startLine);
                }
                return new Token(TokenType.LT, "<", startLine);
            case '>':
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.GE, ">=", startLine);
                }
                return new Token(TokenType.GT, ">", startLine);
            case '=':
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.EQ, "==", startLine);
                }
                break;
            case '!':
                if (currentChar == '=') {
                    advance();
                    return new Token(TokenType.NEQ, "!=", startLine);
                }
                break;
            case '#':
                return new Token(TokenType.HASH, "#", startLine);
        }

        return new Token(TokenType.ERROR, "Invalid character: " + (char) ch, startLine);
    }

    public void finalizeLexer() throws IOException {
        reader.close();
    }
}
