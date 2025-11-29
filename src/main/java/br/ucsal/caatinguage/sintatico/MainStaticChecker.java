package br.ucsal.caatinguage.sintatico;

import br.ucsal.caatinguage.lexico.Lexer;
import br.ucsal.caatinguage.lexico.Token;
import br.ucsal.caatinguage.lexico.TokenType;
import br.ucsal.caatinguage.tabela.SimboloManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Programa principal do Static Checker.
 * Controla a chamada ao léxico e à tabela de símbolos e gera .LEX e .TAB.
 */
public class MainStaticChecker {

    public static void main(String[] args) {
        try {
            new MainStaticChecker().run(args);
        } catch (Exception e) {
            System.err.println("Erro na execução do Static Checker: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run(String[] args) throws IOException {
        String fileName = (args != null && args.length > 0)
                ? args[0]
                : askFileNameFromUser();

        if (fileName == null || fileName.isBlank()) {
            System.err.println("Nenhum arquivo .252 informado.");
            return;
        }

        File sourceFile = new File(fileName);
        if (!sourceFile.exists()) {
            System.err.println("Arquivo não encontrado: " + sourceFile.getAbsolutePath());
            return;
        }

        String baseName = stripExtension(sourceFile.getName());

        try (Reader reader = new InputStreamReader(
                new FileInputStream(sourceFile), StandardCharsets.UTF_8);
             Lexer lexer = new Lexer(reader)) {

            SimboloManager simbolos = new SimboloManager();
            simbolos.initTables();

            Path lexPath = Path.of(baseName + ".LEX");
            try (PrintWriter lexOut = new PrintWriter(
                    java.nio.file.Files.newBufferedWriter(lexPath, StandardCharsets.UTF_8))) {

                lexOut.println("TYPE\tLEXEME\tLINE");

                while (true) {
                    Token token = lexer.nextToken();
                    TokenType type = token.getType();

                    lexOut.printf("%s\t%s\t%d%n",
                            type,
                            token.getLexeme(),
                            token.getLine());

                    if (type == TokenType.IDENTIFIER) {
                        handleIdentifier(simbolos, token);
                    }

                    if (type == TokenType.EOF) {
                        break;
                    }
                }

                simbolos.generateTabReport(baseName);
            }

         // aqui o reader.close() e o lexer.close() são chamados automaticamente
            System.out.println("Análise concluída. Arquivos gerados: "
                    + baseName + ".LEX e " + baseName + ".TAB");
        }
    }

    private String askFileNameFromUser() {
        System.out.print("Informe o nome do arquivo fonte (.252): ");
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine().trim();
    }

    private String stripExtension(String name) {
        int dot = name.lastIndexOf('.');
        if (dot > 0) {
            return name.substring(0, dot);
        }
        return name;
    }

    private void handleIdentifier(SimboloManager simbolos, Token token) {
        String lexeme = token.getLexeme();
        int existingIndex = simbolos.findSymbol(lexeme);
        if (existingIndex == -1) {
            int lenBefore = lexeme.length();
            int maxLen = 35; // conforme especificação
            int index = simbolos.insertSymbol(
                    lexeme,
                    TokenType.IDENTIFIER,
                    lenBefore,
                    maxLen,
                    token.getLine()
            );
            // se quiser fazer algo com o índice, pode usar aqui
        } else {
            simbolos.registerOccurrence(existingIndex, token.getLine());
        }
    }
}

