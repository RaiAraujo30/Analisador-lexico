package compilador.lexico;

import java.util.Map;
import java.util.HashMap;

public class AnalisadorLexico {
    private final String codigoFonte;
    private int posicaoAtual = 0;
    private int linha = 1;

    private static final Map<String, TipoToken> PALAVRAS_CHAVE;

    static {
        PALAVRAS_CHAVE = new HashMap<>();
        PALAVRAS_CHAVE.put("programa", TipoToken.PROGRAMA);
        PALAVRAS_CHAVE.put("int", TipoToken.INT);
        PALAVRAS_CHAVE.put("bool", TipoToken.BOOL);
        PALAVRAS_CHAVE.put("procedimento", TipoToken.PROCEDIMENTO);
        PALAVRAS_CHAVE.put("funcao", TipoToken.FUNCAO);
        PALAVRAS_CHAVE.put("se", TipoToken.SE);
        PALAVRAS_CHAVE.put("entao", TipoToken.ENTAO);
        PALAVRAS_CHAVE.put("senao", TipoToken.SENAO);
        PALAVRAS_CHAVE.put("enquanto", TipoToken.ENQUANTO);
        PALAVRAS_CHAVE.put("leia", TipoToken.LEIA);
        PALAVRAS_CHAVE.put("escreva", TipoToken.ESCREVA);
        PALAVRAS_CHAVE.put("verdadeiro", TipoToken.VERDADEIRO);
        PALAVRAS_CHAVE.put("falso", TipoToken.FALSO);
        PALAVRAS_CHAVE.put("pare", TipoToken.PARE);
        PALAVRAS_CHAVE.put("continue", TipoToken.CONTINUE);
        PALAVRAS_CHAVE.put("retorne", TipoToken.RETORNE);
    }

    public AnalisadorLexico(String codigoFonte) {
        this.codigoFonte = codigoFonte;
    }

    private char charAtual() {
        if (posicaoAtual >= codigoFonte.length()) {
            return '\0'; // Caractere nulo para representar o fim do arquivo
        }
        return codigoFonte.charAt(posicaoAtual);
    }

    public void avancar() {
        posicaoAtual++;
    }

    private char proximoChar() {
        if (posicaoAtual + 1 >= codigoFonte.length()) {
            return '\0';
        }
        return codigoFonte.charAt(posicaoAtual + 1);
    }
    
    // Método principal para obter o próximo token
    public Token proximoToken() {
        while (charAtual() != '\0') {
            char c = charAtual();

            // Ignorar espaços em branco e quebras de linha
            if (Character.isWhitespace(c)) {
                if (c == '\n') linha++;
                avancar();
                continue;
            }

            // Ignorar comentários /* ... */
            if (c == '/' && proximoChar() == '*') {
                avancar(); // Pula o '/'
                avancar(); // Pula o '*'
                while (charAtual() != '\0' && !(charAtual() == '*' && proximoChar() == '/')) {
                    if (charAtual() == '\n') linha++;
                    avancar();
                }
                avancar(); // Pula o '*'
                avancar(); // Pula o '/'
                continue;
            }

            // Identificadores e Palavras-chave
            if (Character.isLetter(c)) {
                return identificador();
            }

            // Números
            if (Character.isDigit(c)) {
                return numero();
            }

            // Operadores e pontuação
            switch (c) {
                case ';': avancar(); return new Token(TipoToken.PONTO_E_VIRGULA, ";", linha);
                case ',': avancar(); return new Token(TipoToken.VIRGULA, ",", linha);
                case '(': avancar(); return new Token(TipoToken.ABRE_PARENTESES, "(", linha);
                case ')': avancar(); return new Token(TipoToken.FECHA_PARENTESES, ")", linha);
                case '{': avancar(); return new Token(TipoToken.ABRE_CHAVES, "{", linha);
                case '}': avancar(); return new Token(TipoToken.FECHA_CHAVES, "}", linha);
                case ':': avancar(); return new Token(TipoToken.DOIS_PONTOS, ":", linha);
                case '+': avancar(); return new Token(TipoToken.MAIS, "+", linha);
                case '-': avancar(); return new Token(TipoToken.MENOS, "-", linha);
                case '*': avancar(); return new Token(TipoToken.VEZES, "*", linha);
                case '/': avancar(); return new Token(TipoToken.DIVISAO, "/", linha);
                case '=':
                    avancar();
                    if (charAtual() == '=') {
                        avancar();
                        return new Token(TipoToken.IGUAL, "==", linha);
                    }
                    return new Token(TipoToken.ATRIBUICAO, "=", linha);
                case '!':
                    avancar();
                    if (charAtual() == '=') {
                        avancar();
                        return new Token(TipoToken.DIFERENTE, "!=", linha);
                    }
                    return new Token(TipoToken.NAO_LOGICO, "!", linha);
                case '<':
                    avancar();
                    if (charAtual() == '=') {
                        avancar();
                        return new Token(TipoToken.MENOR_IGUAL, "<=", linha);
                    }
                    return new Token(TipoToken.MENOR, "<", linha);
                case '>':
                    avancar();
                    if (charAtual() == '=') {
                        avancar();
                        return new Token(TipoToken.MAIOR_IGUAL, ">=", linha);
                    }
                    return new Token(TipoToken.MAIOR, ">", linha);
                case '&':
                    avancar();
                    if (charAtual() == '&') {
                        avancar();
                        return new Token(TipoToken.E_LOGICO, "&&", linha);
                    }
                    return new Token(TipoToken.ERRO, "&", linha);
                case '|':
                    avancar();
                    if (charAtual() == '|') {
                        avancar();
                        return new Token(TipoToken.OU_LOGICO, "||", linha);
                    }
            }
            
            // Se chegou até aqui, o caractere é inválido.
            String caractereInvalido = String.valueOf(c);
            avancar(); // Pula o caractere inválido para não entrar em loop infinito
            return new Token(TipoToken.ERRO, caractereInvalido, linha);
        }
        return new Token(TipoToken.EOF, "", linha); // Fim do arquivo
    }

    private Token identificador() {
        int inicio = posicaoAtual;
        while (Character.isLetterOrDigit(charAtual()) || charAtual() == '_') {
            avancar();
        }
        String lexema = codigoFonte.substring(inicio, posicaoAtual);
        TipoToken tipo = PALAVRAS_CHAVE.getOrDefault(lexema, TipoToken.IDENTIFICADOR);
        return new Token(tipo, lexema, linha);
    }

    private Token numero() {
        int inicio = posicaoAtual;
        while (Character.isDigit(charAtual())) {
            avancar();
        }
        String lexema = codigoFonte.substring(inicio, posicaoAtual);
        return new Token(TipoToken.NUMERO, lexema, linha);
    }
}