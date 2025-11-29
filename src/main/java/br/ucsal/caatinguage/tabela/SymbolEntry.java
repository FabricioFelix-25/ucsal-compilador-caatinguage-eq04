package br.ucsal.caatinguage.tabela;

import br.ucsal.caatinguage.lexico.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * Entrada da tabela de símbolos, conforme projetado na Etapa 1.
 */
public class SymbolEntry {

    private final int index;
    private final TokenType atomCode;
    private final String lexeme;        // truncado (até 35 chars)
    private final int lenBeforeTrunc;
    private final int lenAfterTrunc;
    private SymbolType type;
    private final List<Integer> lines = new ArrayList<>(5);

    public SymbolEntry(int index,
                       TokenType atomCode,
                       String lexeme,
                       int lenBeforeTrunc,
                       int lenAfterTrunc,
                       SymbolType type) {
        this.index = index;
        this.atomCode = atomCode;
        this.lexeme = lexeme;
        this.lenBeforeTrunc = lenBeforeTrunc;
        this.lenAfterTrunc = lenAfterTrunc;
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public TokenType getAtomCode() {
        return atomCode;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getLenBeforeTrunc() {
        return lenBeforeTrunc;
    }

    public int getLenAfterTrunc() {
        return lenAfterTrunc;
    }

    public SymbolType getType() {
        return type;
    }

    public void setType(SymbolType type) {
        this.type = type;
    }

    public List<Integer> getLines() {
        return lines;
    }

    public void registerOccurrence(int line) {
        if (lines.size() < 5 && !lines.contains(line)) {
            lines.add(line);
        }
    }

    @Override
    public String toString() {
        return "SymbolEntry{" +
                "index=" + index +
                ", atomCode=" + atomCode +
                ", lexeme='" + lexeme + '\'' +
                ", lenBeforeTrunc=" + lenBeforeTrunc +
                ", lenAfterTrunc=" + lenAfterTrunc +
                ", type=" + type +
                ", lines=" + lines +
                '}';
    }
}
