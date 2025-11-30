package br.ucsal.caatinguage.tabela;

import br.ucsal.caatinguage.lexico.TokenType;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class SimboloManager {

    private final Map<String, SymbolEntry> tabelaSimbolos = new LinkedHashMap<>();
    private static final Map<String, TokenType> PALAVRAS_RESERVADAS = new HashMap<>();

    private int contadorEntradas = 1;

    static {
        // Mapeamento de palavras reservadas -> TokenType (sempre em UPPERCASE)
        PALAVRAS_RESERVADAS.put("INTEGER", TokenType.INTEGER);
        PALAVRAS_RESERVADAS.put("REAL", TokenType.REAL);
        PALAVRAS_RESERVADAS.put("CHARACTER", TokenType.CHARACTER);
        PALAVRAS_RESERVADAS.put("STRING", TokenType.STRING);
        PALAVRAS_RESERVADAS.put("BOOLEAN", TokenType.BOOLEAN);
        PALAVRAS_RESERVADAS.put("VOID", TokenType.VOID);

        PALAVRAS_RESERVADAS.put("TRUE", TokenType.TRUE);
        PALAVRAS_RESERVADAS.put("FALSE", TokenType.FALSE);

        PALAVRAS_RESERVADAS.put("VARTYPE", TokenType.VAR_TYPE);
        PALAVRAS_RESERVADAS.put("FUNCTYPE", TokenType.FUNC_TYPE);
        PALAVRAS_RESERVADAS.put("PARAMTYPE", TokenType.PARAM_TYPE);

        PALAVRAS_RESERVADAS.put("DECLARATIONS", TokenType.DECLARATIONS);
        PALAVRAS_RESERVADAS.put("ENDDECLARATIONS", TokenType.END_DECLARATIONS);
        PALAVRAS_RESERVADAS.put("PROGRAM", TokenType.PROGRAM);
        PALAVRAS_RESERVADAS.put("ENDPROGRAM", TokenType.END_PROGRAM);
        PALAVRAS_RESERVADAS.put("FUNCTIONS", TokenType.FUNCTIONS);
        PALAVRAS_RESERVADAS.put("ENDFUNCTIONS", TokenType.END_FUNCTIONS);
        PALAVRAS_RESERVADAS.put("ENDFUNCTION", TokenType.END_FUNCTION);

        PALAVRAS_RESERVADAS.put("RETURN", TokenType.RETURN);
        PALAVRAS_RESERVADAS.put("IF", TokenType.IF);
        PALAVRAS_RESERVADAS.put("ELSE", TokenType.ELSE);
        PALAVRAS_RESERVADAS.put("ENDIF", TokenType.END_IF);
        PALAVRAS_RESERVADAS.put("WHILE", TokenType.WHILE);
        PALAVRAS_RESERVADAS.put("ENDWHILE", TokenType.END_WHILE);
        PALAVRAS_RESERVADAS.put("BREAK", TokenType.BREAK);
        PALAVRAS_RESERVADAS.put("PRINT", TokenType.PRINT);
    }

    public TokenType buscarTipoReservado(String lexema) {
        return PALAVRAS_RESERVADAS.get(lexema.toUpperCase());
    }

    /**
     * Insere ou atualiza símbolo (identificador) na tabela.
     * Faz:
     *  - upper case
     *  - truncagem em 35 chars
     *  - registra código do átomo (IDN02)
     *  - registra ocorrência (linha)
     */
    public SymbolEntry inserirOuAtualizar(String lexemaOriginal, TokenType tipoToken, int linha) {
        String lexemaUpper = lexemaOriginal.toUpperCase();

        String lexemaTruncado = lexemaUpper;
        if (lexemaUpper.length() > 35) {
            lexemaTruncado = lexemaUpper.substring(0, 35);
        }

        String codigoAtomo = tipoToken.getCodigo();

        if (tabelaSimbolos.containsKey(lexemaTruncado)) {
            SymbolEntry existente = tabelaSimbolos.get(lexemaTruncado);
            existente.atualizar(lexemaUpper.length(), linha);
            return existente;
        } else {
            SymbolEntry novo = new SymbolEntry(
                    contadorEntradas++,
                    codigoAtomo,
                    lexemaTruncado,
                    lexemaUpper.length(),
                    linha
            );
            tabelaSimbolos.put(lexemaTruncado, novo);
            return novo;
        }
    }

    public String gerarRelatorioTABComoTexto() {
        StringBuilder sb = new StringBuilder();
        for (SymbolEntry s : tabelaSimbolos.values()) {
            sb.append(s.toString()).append("\n");
        }
        return sb.toString();
    }

    /**
     * Gera arquivo .TAB no formato tabular:
     * INDEX  ATOM  LEXEME  LEN_BEFORE  LEN_AFTER  TYPE  LINES
     */
    public void gerarArquivoTAB(String baseName) throws IOException {
        Path path = Path.of(baseName + ".TAB");
        try (PrintWriter out = new PrintWriter(
                java.nio.file.Files.newBufferedWriter(path, StandardCharsets.UTF_8))) {

            out.println("INDEX\tATOM\tLEXEME\tLEN_BEFORE\tLEN_AFTER\tTYPE\tLINES");

            for (SymbolEntry s : tabelaSimbolos.values()) {
                out.printf(
                        "%d\t%s\t%s\t%d\t%d\t%s\t%s%n",
                        s.getNumeroEntrada(),
                        s.getCodigoAtomo(),
                        s.getLexeme(),
                        s.getQtdAntesTrunc(),
                        s.getQtdDepoisTrunc(),
                        s.getTipoSimbolo().getCode(),
                        s.getLinhasFormatadas()
                );
            }
        }
    }
}
