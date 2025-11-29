package br.ucsal.caatinguage.lexico;

/**
 * Enum com os tipos de tokens da linguagem Caatinguage2025-2.
 * Ajuste/complete conforme a gramática.
 */
public enum TokenType {

    // Palavras reservadas principais
    PROGRAM,           // program
    DECLARATIONS,      // declarations
    END_DECLARATIONS,  // endDeclarations
    FUNCTIONS,         // functions
    END_FUNCTIONS,     // endFunctions
    END_PROGRAM,       // endProgram

    VAR_TYPE,          // varType (int, real, etc. - pode refinar depois)
    FUNC_TYPE,         // funcType
    IF,                // if
    ELSE,              // else
    END_IF,            // endIf
    WHILE,             // while
    END_WHILE,         // endWhile
    RETURN,            // return
    BREAK,             // break
    PRINT,             // print

    TRUE,              // true
    FALSE,             // false

    // Identificadores e literais
    IDENTIFIER,        // programName, functionName, variable, etc.
    INT_CONST,
    REAL_CONST,
    STRING_CONST,
    CHAR_CONST,

    // Operadores aritméticos
    PLUS,      // +
    MINUS,     // -
    STAR,      // *
    SLASH,     // /
    PERCENT,   // %

    // Operadores relacionais
    LT,        // <
    LE,        // <=
    GT,        // >
    GE,        // >=
    EQ,        // ==
    NEQ,       // !=
    HASH,      // #

    // Símbolos
    LBRACE,    // {
    RBRACE,    // }
    LPAREN,    // (
    RPAREN,    // )
    LBRACKET,  // [
    RBRACKET,  // ]
    SEMICOLON, // ;
    COMMA,     // ,
    COLON,     // :
    ASSIGN,    // :=

    // Fim de arquivo / erro
    EOF,
    INTEGER, REAL, STRING, BOOLEAN, CHARACTER, VOID, ERROR
}
