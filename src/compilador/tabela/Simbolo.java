package compilador.tabela;

import compilador.lexico.TipoToken;

// Uma classe simples para guardar informações do símbolo
public class Simbolo {
    String nome;
    TipoToken tipo; // int, bool, etc.
    // Você pode adicionar mais informações, como categoria (variável, função)
    
    public Simbolo(String nome, TipoToken tipo) {
        this.nome = nome;
        this.tipo = tipo;
    }
}
