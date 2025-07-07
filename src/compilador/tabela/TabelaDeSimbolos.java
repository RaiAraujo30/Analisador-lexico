package compilador.tabela;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class TabelaDeSimbolos {
    private Stack<Map<String, Simbolo>> escopos;

    public TabelaDeSimbolos() {
        this.escopos = new Stack<>();
        entrarEscopo(); // Inicia o escopo global
    }

    // Entra em um novo escopo (ex: ao entrar em uma função)
    public void entrarEscopo() {
        escopos.push(new HashMap<>());
    }

    // Sai do escopo atual (ex: ao sair de uma função)
    public void sairEscopo() {
        if (!escopos.isEmpty()) {
            escopos.pop();
        }
    }

    // Adiciona um novo símbolo ao escopo atual
    public void adicionar(Simbolo simbolo) {
        if (escopos.peek().containsKey(simbolo.nome)) {
            // Erro: Símbolo já declarado neste escopo
            throw new RuntimeException("Erro Semântico: Identificador '" + simbolo.nome + "' já foi declarado.");
        }
        escopos.peek().put(simbolo.nome, simbolo);
    }

    // Busca um símbolo em todos os escopos, do mais interno para o mais externo
    public Simbolo buscar(String nome) {
        for (int i = escopos.size() - 1; i >= 0; i--) {
            if (escopos.get(i).containsKey(nome)) {
                return escopos.get(i).get(nome);
            }
        }
        return null; // Símbolo não encontrado
    }
}