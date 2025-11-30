package br.ucsal.caatinguage.lexico;

import br.ucsal.caatinguage.tabela.SimboloManager;

import java.io.IOException;
import java.io.Reader;

public class Lexer implements AutoCloseable {
    private final Reader reader;
    private final SimboloManager tabela;
    private int linhaAtual = 1;
    private int colunaAtual = 0;
    private int nextChar = -1; // Buffer de lookahead

    public Lexer(Reader reader, SimboloManager tabela) throws IOException {
        this.reader = reader;
        this.tabela = tabela;
        lerCaractere(); // Inicializa o buffer
    }

    private void lerCaractere() throws IOException {
        nextChar = reader.read();
        colunaAtual++;
    }

    public Token proximoToken() throws IOException, Exception {
        while (nextChar != -1) {
            char c = (char) nextChar;

            // 1. Filtro de Espaços e Quebras de Linha
            if (c == ' ' || c == '\t' || c == '\r') {
                lerCaractere();
                continue;
            }
            if (c == '\n') {
                linhaAtual++;
                colunaAtual = 0;
                lerCaractere();
                continue;
            }

            // 2. Comentários (// e /*) e Divisão (/)
            if (c == '/') {
                lerCaractere();
                if (nextChar == '/') { // Comentário de Linha
                    while (nextChar != '\n' && nextChar != -1) {
                        lerCaractere();
                    }
                    continue;
                } else if (nextChar == '*') { // Comentário de Bloco
                    lerCaractere();
                    boolean fechar = false;
                    while (nextChar != -1) {
                        if (nextChar == '*') {
                            lerCaractere();
                            if (nextChar == '/') {
                                lerCaractere();
                                fechar = true;
                                break;
                            }
                        } else {
                            if (nextChar == '\n') {
                                linhaAtual++;
                                colunaAtual = 0;
                            }
                            lerCaractere();
                        }
                    }
                    if (!fechar) {
                        // Segundo a spec, EOF em comentário não é erro fatal
                        return new Token(TokenType.EOF, "EOF", linhaAtual, colunaAtual);
                    }
                    continue;
                } else {
                    return new Token(TokenType.SLASH, "/", linhaAtual, colunaAtual); // SRS15
                }
            }

            // 3. Identificadores e Palavras Reservadas
            if (Character.isLetter(c) || c == '_') {
                StringBuilder sb = new StringBuilder();
                while (Character.isLetterOrDigit((char) nextChar) || nextChar == '_') {
                    sb.append((char) nextChar);
                    lerCaractere();
                }
                String lexemaOriginal = sb.toString();

                // Verifica Palavra Reservada (Case Insensitive)
                TokenType tipoReservado = tabela.buscarTipoReservado(lexemaOriginal);

                if (tipoReservado != null) {
                    return new Token(tipoReservado, lexemaOriginal.toUpperCase(), linhaAtual, colunaAtual);
                } else {
                    // É identificador. O SimboloManager trata a truncagem e inserção.
                    tabela.inserirOuAtualizar(lexemaOriginal, TokenType.IDENTIFIER, linhaAtual);

                    // Retorna o token com o lexema truncado e maiúsculo (limite 35)
                    String lexemaFinal = lexemaOriginal.toUpperCase();
                    if (lexemaFinal.length() > 35) lexemaFinal = lexemaFinal.substring(0, 35);

                    return new Token(TokenType.IDENTIFIER, lexemaFinal, linhaAtual, colunaAtual);
                }
            }

            // 4. Números (Inteiros e Reais)
            if (Character.isDigit(c)) {
                StringBuilder sb = new StringBuilder();
                while (Character.isDigit((char) nextChar)) {
                    sb.append((char) nextChar);
                    lerCaractere();
                }
                // Se vier ponto, é Real
                if (nextChar == '.') {
                    sb.append('.');
                    lerCaractere();
                    while (Character.isDigit((char) nextChar)) {
                        sb.append((char) nextChar);
                        lerCaractere();
                    }
                    // Parte exponencial (ex: 10.5e-2)
                    if (nextChar == 'e' || nextChar == 'E') {
                        sb.append((char) nextChar);
                        lerCaractere();
                        if (nextChar == '+' || nextChar == '-') {
                            sb.append((char) nextChar);
                            lerCaractere();
                        }
                        while (Character.isDigit((char) nextChar)) {
                            sb.append((char) nextChar);
                            lerCaractere();
                        }
                    }
                    return new Token(TokenType.REAL_CONST, sb.toString(), linhaAtual, colunaAtual);
                }
                return new Token(TokenType.INT_CONST, sb.toString(), linhaAtual, colunaAtual);
            }

            // 5. Strings ("...")
            if (c == '"') {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                lerCaractere();
                while (nextChar != '"' && nextChar != -1 && nextChar != '\n') {
                    sb.append((char) nextChar);
                    lerCaractere();
                }
                if (nextChar == '"') {
                    sb.append('"');
                    lerCaractere();
                    return new Token(TokenType.STRING_CONST, sb.toString(), linhaAtual, colunaAtual);
                } else {
                    throw new Exception("String não fechada na linha " + linhaAtual);
                }
            }

            // 6. Caractere ('...')
            if (c == '\'') {
                StringBuilder sb = new StringBuilder();
                sb.append(c);
                lerCaractere(); // Lê o conteúdo
                if (nextChar != -1 && nextChar != '\'') {
                    sb.append((char) nextChar);
                    lerCaractere();
                }
                if (nextChar == '\'') {
                    sb.append('\'');
                    lerCaractere();
                    return new Token(TokenType.CHAR_CONST, sb.toString(), linhaAtual, colunaAtual);
                } else {
                    throw new Exception("Caractere mal formado na linha " + linhaAtual);
                }
            }

            // 7. Símbolos Especiais (Apêndice A)
            lerCaractere(); // Consome o símbolo atual

            switch (c) {
                case ';': return new Token(TokenType.SEMICOLON, ";", linhaAtual, colunaAtual);
                case ',': return new Token(TokenType.COMMA, ",", linhaAtual, colunaAtual);
                case '(': return new Token(TokenType.LPAREN, "(", linhaAtual, colunaAtual);
                case ')': return new Token(TokenType.RPAREN, ")", linhaAtual, colunaAtual);
                case '[': return new Token(TokenType.LBRACKET, "[", linhaAtual, colunaAtual);
                case ']': return new Token(TokenType.RBRACKET, "]", linhaAtual, colunaAtual);
                case '{': return new Token(TokenType.LBRACE, "{", linhaAtual, colunaAtual);
                case '}': return new Token(TokenType.RBRACE, "}", linhaAtual, colunaAtual);
                case '+': return new Token(TokenType.PLUS, "+", linhaAtual, colunaAtual);
                case '-': return new Token(TokenType.MINUS, "-", linhaAtual, colunaAtual);
                case '*': return new Token(TokenType.STAR, "*", linhaAtual, colunaAtual);
                case '%': return new Token(TokenType.PERCENT, "%", linhaAtual, colunaAtual);
                case '#': return new Token(TokenType.HASH, "#", linhaAtual, colunaAtual);
                case '?': return new Token(TokenType.QUESTION, "?", linhaAtual, colunaAtual);

                case ':':
                    if (nextChar == '=') {
                        lerCaractere();
                        return new Token(TokenType.ASSIGN, ":=", linhaAtual, colunaAtual);
                    }
                    return new Token(TokenType.COLON, ":", linhaAtual, colunaAtual);

                case '<':
                    if (nextChar == '=') {
                        lerCaractere();
                        return new Token(TokenType.LE, "<=", linhaAtual, colunaAtual);
                    }
                    return new Token(TokenType.LT, "<", linhaAtual, colunaAtual);

                case '>':
                    if (nextChar == '=') {
                        lerCaractere();
                        return new Token(TokenType.GE, ">=", linhaAtual, colunaAtual);
                    }
                    return new Token(TokenType.GT, ">", linhaAtual, colunaAtual);

                case '=':
                    if (nextChar == '=') {
                        lerCaractere();
                        return new Token(TokenType.EQ, "==", linhaAtual, colunaAtual);
                    }
                    return new Token(TokenType.ASSIGN, "=", linhaAtual, colunaAtual);

                case '!':
                    if (nextChar == '=') {
                        lerCaractere();
                        return new Token(TokenType.NEQ, "!=", linhaAtual, colunaAtual);
                    }
                    return new Token(TokenType.ERROR, "!", linhaAtual, colunaAtual);

                default:
                    // Caractere inválido (filtro de 1º nível) - Ignora e continua
                    continue;
            }
        }
        return new Token(TokenType.EOF, "EOF", linhaAtual, colunaAtual);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
