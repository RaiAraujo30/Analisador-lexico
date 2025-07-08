package compilador.sintatico;

import compilador.lexico.AnalisadorLexico;
import compilador.sintatico.exceptions.SyntaxError;


public class TestadorSintatico {
    public static void main(String[] args) {
        String codigoFonte =
            "programa exemplo;\n" +
            "int x, y;\n" +
            "funcao void soma(int a, int b) {\n" +
            "    retorne a + b;\n" +
            "}\n" +
            "procedimento mostra(bool f) {\n" +
            "    se (f) entao {\n" +
            "        escreva(x);\n" +
            "    } senao {\n" +
            "        escreva(y);\n" +
            "    }\n" +
            "}\n" +
            "{\n" +
            "    x = soma(3, 4);\n" +
            "    mostra(x > 5);\n" +
            "}\n";

        System.out.println("--- INICIANDO ANÁLISE SINTÁTICA ---");
        AnalisadorLexico lexico = new AnalisadorLexico(codigoFonte);
        Parser parser = new Parser(lexico);
        try {
            parser.parsePrograma();
            System.out.println("Sucesso: análise sintática completada sem erros.");
        } catch (SyntaxError e) {
            System.err.println("Erro sintático: " + e.getMessage());
        }
    }
}
