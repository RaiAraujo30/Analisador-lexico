package compilador.lexico;

public class TestadorLexico {

    public static void main(String[] args) {
        String codigoDeTeste = 
            "programa meu_teste; /* Comentário inicial */\n" +
            "int var_1, var_2;\n" +
            "bool flag;\n" +
            "{\n" +
            "   /* Bloco de comandos\n" +
            "      com múltiplas linhas */\n" +
            "   var_1 = 123 + 45;\n" +
            "   flag = verdadeiro;\n" +
            "   se (var_1 >= 100) {\n" +
            "      escreva(var_1 != var_2);\n" +
            "   }\n" +
            "}";

        System.out.println("--- INICIANDO ANÁLISE LÉXICA ---");
        AnalisadorLexico lexico = new AnalisadorLexico(codigoDeTeste);
        
        Token token;
        do {
            token = lexico.proximoToken();
            System.out.println(token); // O toString() da classe Token será útil aqui
        } while (token.tipo != TipoToken.EOF);

        System.out.println("--- FIM DA ANÁLISE LÉXICA ---");
        
        // Testando um caractere inválido
        System.out.println("\n--- TESTANDO ERRO ---");
        String codigoComErro = "int a = 5 @ 4;";
        try {
            AnalisadorLexico lexicoErro = new AnalisadorLexico(codigoComErro);
            while (lexicoErro.proximoToken().tipo != TipoToken.EOF);
        } catch (Exception e) {
            System.err.println("Erro capturado com sucesso: " + e.getMessage());
        }
    }
}
