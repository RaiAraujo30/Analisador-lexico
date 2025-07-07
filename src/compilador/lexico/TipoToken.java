package compilador.lexico;

public enum TipoToken {
    // Palavras-chave
    PROGRAMA, INT, BOOL, PROCEDIMENTO, FUNCAO, SE, ENTAO, ENQUANTO,
    LEIA, ESCREVA, VERDADEIRO, FALSO, PARE, CONTINUE, RETORNE,

    // Identificadores e Números
    IDENTIFICADOR, NUMERO,

    // Operadores e Pontuação
    PONTO_E_VIRGULA, VIRGULA, ABRE_PARENTESES, FECHA_PARENTESES, ABRE_CHAVES, FECHA_CHAVES,
    DOIS_PONTOS, ATRIBUICAO, // := e = (vamos usar = para atribuição conforme a gramática)

    // Operadores Relacionais
    IGUAL, DIFERENTE, MENOR, MENOR_IGUAL, MAIOR, MAIOR_IGUAL,

    // Operadores Aritméticos e Lógicos
    MAIS, MENOS, VEZES, DIVISAO, E_LOGICO, OU_LOGICO, NAO_LOGICO,

    // Fim de Arquivo
    EOF
}
