package br.ucsal.caatinguage.tabela;

import java.util.ArrayList;
import java.util.List;

public class SymbolEntry {
    private final int numeroEntrada;
    private final String codigoAtomo;
    private final String lexeme;
    private int qtdAntesTrunc;
    private final int qtdDepoisTrunc;
    private SymbolType tipoSimbolo;
    private final List<Integer> linhas;

    public SymbolEntry(int numero, String codigo, String lexemeTruncado, int totalChars, int linha) {
        this.numeroEntrada = numero;
        this.codigoAtomo = codigo;
        this.lexeme = lexemeTruncado;
        this.qtdAntesTrunc = totalChars;
        this.qtdDepoisTrunc = lexemeTruncado.length();
        this.tipoSimbolo = SymbolType.NO_TYPE;
        this.linhas = new ArrayList<>();
        this.linhas.add(linha);
    }

    public void atualizar(int totalChars, int linha) {
        if (totalChars > this.qtdAntesTrunc) {
            this.qtdAntesTrunc = totalChars;
        }
        if (this.linhas.size() < 5 && !this.linhas.contains(linha)) {
            this.linhas.add(linha);
        }
    }

    public void setTipoSimbolo(SymbolType tipo) {
        this.tipoSimbolo = tipo;
    }

    public int getNumeroEntrada() {
        return numeroEntrada;
    }

    public String getCodigoAtomo() {
        return codigoAtomo;
    }

    public String getLexeme() {
        return lexeme;
    }

    public int getQtdAntesTrunc() {
        return qtdAntesTrunc;
    }

    public int getQtdDepoisTrunc() {
        return qtdDepoisTrunc;
    }

    public SymbolType getTipoSimbolo() {
        return tipoSimbolo;
    }

    public List<Integer> getLinhas() {
        return linhas;
    }

    public String getLinhasFormatadas() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < linhas.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(linhas.get(i));
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format(
                "Entrada: %d, Codigo: %s, Lexeme: %s, QtdCharAntesTrunc: %d, QtdCharDepoisTrunc: %d, TipoSimb: %s, Linhas: %s",
                numeroEntrada, codigoAtomo, lexeme, qtdAntesTrunc, qtdDepoisTrunc, tipoSimbolo.getCode(), linhas
        );
    }
}
