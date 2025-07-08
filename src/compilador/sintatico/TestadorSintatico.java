package compilador.sintatico;

import compilador.lexico.AnalisadorLexico;
import compilador.sintatico.exceptions.SyntaxError;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestadorSintatico {
    public static void main(String[] args) {
        String nomeDoArquivo = "teste_sintatico.txt";
        String codigoFonte;
        try {
            codigoFonte = Files.readString(Paths.get(nomeDoArquivo));
        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo '" + nomeDoArquivo + "': " + e.getMessage());
            return;
        }

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
