package br.ucsal.caatinguage.sintatico;

import br.ucsal.caatinguage.lexico.Lexer;
import br.ucsal.caatinguage.lexico.Token;
import br.ucsal.caatinguage.lexico.TokenType;
import br.ucsal.caatinguage.tabela.SimboloManager;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

public class MainStaticChecker {

    public static void main(String[] args) {
        try {
            new MainStaticChecker().run(args);
        } catch (Exception e) {
            System.err.println("Erro na execução do Static Checker: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void run(String[] args) throws Exception {
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

        SimboloManager simbolos = new SimboloManager();

        try (Reader reader = new InputStreamReader(
                new FileInputStream(sourceFile), StandardCharsets.UTF_8);
             Lexer lexer = new Lexer(reader, simbolos)) {

            Path lexPath = Path.of(baseName + ".LEX");
            try (PrintWriter lexOut = new PrintWriter(
                    java.nio.file.Files.newBufferedWriter(lexPath, StandardCharsets.UTF_8))) {

                lexOut.println("ATOM\tLEXEME\tLINE");

                while (true) {
                    Token token = lexer.proximoToken();
                    TokenType type = token.getType();

                    lexOut.printf("%s\t%s\t%d%n",
                            type.getCodigo(),          // PRSxx / IDNxx / SRSxx
                            token.getLexeme(),
                            token.getLine());

                    if (type == TokenType.EOF) {
                        break;
                    }
                }
            }

            // Gera .TAB com base na tabela de símbolos interna do SimboloManager
            simbolos.gerarArquivoTAB(baseName);
        }

        System.out.println("Análise concluída. Arquivos gerados: "
                + baseName + ".LEX e " + baseName + ".TAB");
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
}
