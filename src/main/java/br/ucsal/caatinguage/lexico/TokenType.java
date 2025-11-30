package br.ucsal.caatinguage.lexico;

/**
 * Tipos de token da linguagem Caatinguage2025-2.
 * Mapeamento oficial baseado no Apêndice A da especificação.
 */
public enum TokenType {

    // --- PALAVRAS RESERVADAS (PRS) ---

    // Tipos de Dados
    INTEGER("PRS01"),
    REAL("PRS02"),
    CHARACTER("PRS03"),
    STRING("PRS04"),
    BOOLEAN("PRS05"),
    VOID("PRS06"),

    // Constantes Booleanas
    TRUE("PRS07"),
    FALSE("PRS08"),

    // Palavras Especiais de Definição
    VAR_TYPE("PRS09"),
    FUNC_TYPE("PRS10"),
    PARAM_TYPE("PRS11"),

    // Estrutura do Programa
    DECLARATIONS("PRS12"),
    END_DECLARATIONS("PRS13"),
    PROGRAM("PRS14"),
    END_PROGRAM("PRS15"),
    FUNCTIONS("PRS16"),
    END_FUNCTIONS("PRS17"),
    END_FUNCTION("PRS18"), // Atenção: Existe END_FUNCTIONS (PRS17) e END_FUNCTION (PRS18)

    // Comandos
    RETURN("PRS19"),
    IF("PRS20"),
    ELSE("PRS21"),
    END_IF("PRS22"), // Na spec: "endif"
    WHILE("PRS23"),
    END_WHILE("PRS24"), // Na spec: "endWhile"
    BREAK("PRS25"),
    PRINT("PRS26"),


    // --- IDENTIFICADORES E CONSTANTES (IDN) ---

    // Identificadores Gerais
    PROGRAM_NAME("IDN01"),
    IDENTIFIER("IDN02"),   // Variable
    FUNCTION_NAME("IDN03"),

    // Constantes Literais
    INT_CONST("IDN04"),
    REAL_CONST("IDN05"),
    STRING_CONST("IDN06"),
    CHAR_CONST("IDN07"),


    // --- SÍMBOLOS RESERVADOS (SRS) ---

    SEMICOLON("SRS01"), // ;
    COMMA("SRS02"),     // ,
    COLON("SRS03"),     // :

    // SRS04 é "=" na spec; na gramática vocês usam ":=". Aqui Lexer devolve ASSIGN para ":=".
    ASSIGN("SRS04"),

    QUESTION("SRS05"),  // ?

    LPAREN("SRS06"),    // (
    RPAREN("SRS07"),    // )
    LBRACKET("SRS08"),  // [
    RBRACKET("SRS09"),  // ]
    LBRACE("SRS10"),    // {
    RBRACE("SRS11"),    // }

    // Operadores Aritméticos
    PLUS("SRS12"),      // +
    MINUS("SRS13"),     // -
    STAR("SRS14"),      // *
    SLASH("SRS15"),     // /
    PERCENT("SRS16"),   // %

    // Operadores Relacionais
    EQ("SRS17"),        // == (igualdade)
    NEQ("SRS18"),       // != (diferente)
    HASH("SRS18"),      // #  (alias para diferente)

    LT("SRS19"),        // <
    LE("SRS20"),        // <=
    GT("SRS21"),        // >
    GE("SRS22"),        // >=


    // --- ESPECIAIS ---
    EOF("EOF"),
    ERROR("ERRO");

    private final String codigo;

    TokenType(String codigo) {
        this.codigo = codigo;
    }

    /**
     * Retorna o código oficial do átomo (ex: PRS01, IDN02).
     */
    public String getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return codigo;
    }
}
