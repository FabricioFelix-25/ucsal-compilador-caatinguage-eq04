package br.ucsal.caatinguage.tabela;

public enum SymbolType {
    NO_TYPE("-");

    private final String code;

    SymbolType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
