package br.ucsal.caatinguage.tabela;

import br.ucsal.caatinguage.lexico.TokenType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

/**
 * Gerenciador da tabela de símbolos e da tabela de reservados.
 */
public class SimboloManager {

    private final Map<String, Integer> indexByLexemeLower = new HashMap<>();
    private final List<SymbolEntry> symbols = new ArrayList<>();
    private final Map<String, TokenType> reserved = new HashMap<>();

    /**
     * Inicializa a tabela de reservados e esvazia a tabela de símbolos.
     */
    public void initTables() {
        symbols.clear();
        indexByLexemeLower.clear();
        reserved.clear();

        // Carrega palavras reservadas básicas (complete conforme necessário)
        reserved.put("program", TokenType.PROGRAM);
        reserved.put("declarations", TokenType.DECLARATIONS);
        reserved.put("enddeclarations", TokenType.END_DECLARATIONS);
        reserved.put("functions", TokenType.FUNCTIONS);
        reserved.put("endfunctions", TokenType.END_FUNCTIONS);
        reserved.put("endprogram", TokenType.END_PROGRAM);

        reserved.put("if", TokenType.IF);
        reserved.put("else", TokenType.ELSE);
        reserved.put("endif", TokenType.END_IF);
        reserved.put("while", TokenType.WHILE);
        reserved.put("endwhile", TokenType.END_WHILE);
        reserved.put("return", TokenType.RETURN);
        reserved.put("break", TokenType.BREAK);
        reserved.put("print", TokenType.PRINT);
        reserved.put("true", TokenType.TRUE);
        reserved.put("false", TokenType.FALSE);
    }

    public boolean isReserved(String lexeme) {
        return reserved.containsKey(lexeme.toLowerCase(Locale.ROOT));
    }

    public TokenType getReservedTokenType(String lexeme) {
        return reserved.get(lexeme.toLowerCase(Locale.ROOT));
    }

    public int findSymbol(String lexeme) {
        String key = lexeme.toLowerCase(Locale.ROOT);
        return indexByLexemeLower.getOrDefault(key, -1);
    }

    /**
     * Insere um novo símbolo na tabela.
     *
     * @param lexeme          lexema original
     * @param atomCode        tipo do átomo (geralmente IDENTIFIER)
     * @param lenBeforeTrunc  tamanho antes da truncagem
     * @param maxLen          limite de armazenamento (ex. 35)
     * @param firstLine       linha da primeira ocorrência
     * @return índice do símbolo
     */
    public int insertSymbol(String lexeme,
                            TokenType atomCode,
                            int lenBeforeTrunc,
                            int maxLen,
                            int firstLine) {

        String validChars = lexeme;
        String truncated = validChars;
        if (validChars.length() > maxLen) {
            truncated = validChars.substring(0, maxLen);
        }

        int lenAfterTrunc = truncated.length();
        int index = symbols.size() + 1; // começa em 1

        SymbolEntry entry = new SymbolEntry(
                index,
                atomCode,
                truncated,
                lenBeforeTrunc,
                lenAfterTrunc,
                SymbolType.UNKNOWN
        );
        entry.registerOccurrence(firstLine);

        symbols.add(entry);
        indexByLexemeLower.put(truncated.toLowerCase(Locale.ROOT), index);

        return index;
    }

    public void registerOccurrence(int index, int line) {
        if (index <= 0 || index > symbols.size()) {
            return;
        }
        symbols.get(index - 1).registerOccurrence(line);
    }

    public void setSymbolType(int index, SymbolType type) {
        if (index <= 0 || index > symbols.size()) {
            return;
        }
        symbols.get(index - 1).setType(type);
    }

    /**
     * Gera o arquivo .TAB com o conteúdo da tabela de símbolos.
     */
    public void generateTabReport(String baseName) throws IOException {
        Path path = Path.of(baseName + ".TAB");
        try (PrintWriter out = new PrintWriter(
                java.nio.file.Files.newBufferedWriter(path, StandardCharsets.UTF_8))) {

            out.println("INDEX\tATOM\tLEXEME\tLEN_BEFORE\tLEN_AFTER\tTYPE\tLINES");

            for (SymbolEntry entry : symbols) {
                out.print(entry.getIndex());
                out.print("\t");
                out.print(entry.getAtomCode());
                out.print("\t");
                out.print(entry.getLexeme());
                out.print("\t");
                out.print(entry.getLenBeforeTrunc());
                out.print("\t");
                out.print(entry.getLenAfterTrunc());
                out.print("\t");
                out.print(entry.getType());
                out.print("\t");

                List<Integer> lines = entry.getLines();
                for (int i = 0; i < lines.size(); i++) {
                    if (i > 0) {
                        out.print(",");
                    }
                    out.print(lines.get(i));
                }
                out.println();
            }
        }
    }
}
