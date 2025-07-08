package compilador.lexico;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class TestadorLexico {
    public static void main(String[] args) {
        // 1. Especifique o nome do arquivo a ser lido.
        String nomeDoArquivo = "codigo.txt";
        String codigoFonte;

        try {
            codigoFonte = new String(Files.readAllBytes(Paths.get(nomeDoArquivo)));
            System.out.println("--- ARQUIVO '" + nomeDoArquivo + "' LIDO COM SUCESSO ---");
        } 
        catch (IOException e) {
            System.err.println("Erro ao ler o arquivo '" + nomeDoArquivo + "': " + e.getMessage());
            return; 
        }

        // --- FIM DA MODIFICAÇÃO ---

        System.out.println("--- INICIANDO ANÁLISE LÉXICA ---");
        AnalisadorLexico lexico = new AnalisadorLexico(codigoFonte);
        
        Token token;
        List<String> erros = new ArrayList<>();
 
        do {
            token = lexico.proximoToken();
            if (token.tipo == TipoToken.ERRO) {
                String mensagemDeErro = "--> Erro Léxico Encontrado: Caractere inesperado '" + token.lexema + "' na linha " + token.linha;
                erros.add(mensagemDeErro);
            } else if (token.tipo != TipoToken.EOF) {
                System.out.println(token);
            }
        } while (token.tipo != TipoToken.EOF);
 
        System.out.println("--- FIM DA ANÁLISE LÉXICA ---");

        // 3. VERIFIQUE SE HÁ ERROS E IMPRIMA O RELATÓRIO NO FINAL
        if (erros.isEmpty()) {
            System.out.println("\nNenhum erro léxico encontrado.");
        } else {
            System.err.println("\n--- RELATÓRIO DE ERROS LÉXICOS ---");
            for (String erro : erros) {
                System.err.println(erro);
            }
            System.err.println("A análise encontrou " + erros.size() + " erro(s).");
        }
    }
}
