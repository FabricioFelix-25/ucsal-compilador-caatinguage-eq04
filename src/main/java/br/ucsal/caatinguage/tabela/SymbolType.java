package br.ucsal.caatinguage.tabela;

public enum SymbolType {
    NO_TYPE("-"); // por enquanto "sem tipo", depois vocês podem ter VARIABLE, FUNCTION, PARAM, etc.

    private final String code;

    SymbolType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
