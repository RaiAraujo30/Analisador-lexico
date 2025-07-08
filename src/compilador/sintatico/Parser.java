package compilador.sintatico;

import compilador.lexico.AnalisadorLexico;
import compilador.lexico.TipoToken;
import compilador.lexico.Token;
import compilador.sintatico.exceptions.SyntaxError;
import compilador.tabela.Simbolo;
import compilador.tabela.TabelaDeSimbolos;

public class Parser {
    private final AnalisadorLexico lexer;
    private Token lookahead;
    private final TabelaDeSimbolos tabela;

    public Parser(AnalisadorLexico lexer) {
        this.lexer = lexer;
        this.lookahead = lexer.proximoToken();
        this.tabela = new TabelaDeSimbolos();
    }

    private void match(TipoToken esperado) throws SyntaxError {
        if (lookahead.tipo == esperado) {
            lookahead = lexer.proximoToken();
        } else {
            error("Esperado " + esperado + " mas veio " + lookahead.tipo);
        }
    }

    private void error(String msg) throws SyntaxError {
        throw new SyntaxError("Erro sintático na linha " + lookahead.linha + ": " + msg);
    }

    public void parsePrograma() throws SyntaxError {

        match(TipoToken.PROGRAMA);
        match(TipoToken.IDENTIFICADOR);
        match(TipoToken.PONTO_E_VIRGULA);

        tabela.entrarEscopo();

        while (lookahead.tipo != TipoToken.EOF) {
            if (lookahead.tipo == TipoToken.INT || lookahead.tipo == TipoToken.BOOL) {
                parseDeclVar();
            } else if (lookahead.tipo == TipoToken.PROCEDIMENTO
                    || lookahead.tipo == TipoToken.FUNCAO) {
                parseDecl();
            } else if (isStartCmd(lookahead.tipo)) {
                parseCmd(); // qualquer comando que existe (atribuição, if...
            } else {
                error("Esperado declVar, declRotina ou comando mas veio " + lookahead.tipo);
            }
        }

        tabela.sairEscopo();

        match(TipoToken.EOF);
    }


    private void parseDecl() throws SyntaxError {
        if (lookahead.tipo == TipoToken.INT || lookahead.tipo == TipoToken.BOOL) {
            parseDeclVar();
        } else if (lookahead.tipo == TipoToken.PROCEDIMENTO) {
            parseDeclProcedimento();
        } else if (lookahead.tipo == TipoToken.FUNCAO) {
            parseDeclFuncao();
        } else {
            error("Esperado declaração mas veio " + lookahead.tipo);
        }
    }


    private void parseDeclVar() throws SyntaxError {
        TipoToken tipo = lookahead.tipo;
        match(tipo);
        String nome = lookahead.lexema;
        match(TipoToken.IDENTIFICADOR);
        tabela.adicionar(new Simbolo(nome, tipo));
        while (lookahead.tipo == TipoToken.VIRGULA) {
            match(TipoToken.VIRGULA);
            nome = lookahead.lexema;
            match(TipoToken.IDENTIFICADOR);
            tabela.adicionar(new Simbolo(nome, tipo));
        }
        match(TipoToken.PONTO_E_VIRGULA);
    }

    private void parseDeclProcedimento() throws SyntaxError {
        match(TipoToken.PROCEDIMENTO);
        String nome = lookahead.lexema;
        match(TipoToken.IDENTIFICADOR);
        tabela.adicionar(new Simbolo(nome, TipoToken.PROCEDIMENTO));
        match(TipoToken.ABRE_PARENTESES);
        tabela.entrarEscopo(); // escopo de parâmetros e corpo
        if (lookahead.tipo == TipoToken.INT || lookahead.tipo == TipoToken.BOOL) {
            parseParametros();
        }
        match(TipoToken.FECHA_PARENTESES);
        parseCorpo();
        tabela.sairEscopo();
    }


    private void parseDeclFuncao() throws SyntaxError {
        match(TipoToken.FUNCAO);
        TipoToken tipoRetorno = lookahead.tipo;
        match(tipoRetorno);
        String nome = lookahead.lexema;
        match(TipoToken.IDENTIFICADOR);
        tabela.adicionar(new Simbolo(nome, tipoRetorno));
        match(TipoToken.ABRE_PARENTESES);
        tabela.entrarEscopo(); // escopo de parâmetros e corpo
        if (lookahead.tipo == TipoToken.INT || lookahead.tipo == TipoToken.BOOL) {
            parseParametros();
        }
        match(TipoToken.FECHA_PARENTESES);
        parseCorpo();
        tabela.sairEscopo();
    }


    private void parseParametros() throws SyntaxError {
        TipoToken tipo = lookahead.tipo;
        match(tipo);
        String nome = lookahead.lexema;
        match(TipoToken.IDENTIFICADOR);
        tabela.adicionar(new Simbolo(nome, tipo));
        while (lookahead.tipo == TipoToken.VIRGULA) {
            match(TipoToken.VIRGULA);
            tipo = lookahead.tipo;
            match(tipo);
            nome = lookahead.lexema;
            match(TipoToken.IDENTIFICADOR);
            tabela.adicionar(new Simbolo(nome, tipo));
        }
    }


    private void parseCorpo() throws SyntaxError {
        match(TipoToken.ABRE_CHAVES);
        while (lookahead.tipo == TipoToken.INT || lookahead.tipo == TipoToken.BOOL || isStartCmd(lookahead.tipo)) {
            if (lookahead.tipo == TipoToken.INT || lookahead.tipo == TipoToken.BOOL) {
                parseDeclVar();
            } else {
                parseCmd();
            }
        }
        match(TipoToken.FECHA_CHAVES);
    }

    /**
     * <comando> ::= (<comando atribuição>|
        *       <chamada de procedimento> |
        *       <comando condicional>     |
        *       <comando enquanto>        |
        *       <comando leitura>         |
        *       <comando escrita>)
     * 
     * <comando atribuição> ::= <identificador> = <expressão>;
     * <chamada de procedimento> ::= <identificador> ([<expressão>{, <expressão>}]);
     * <chamada de função> ::= <identificador> ([<expressão> {, <expressão>}]);
     * <comando condicional> ::= se (<expressão> ) { <comandos> } [entao {
     * <comandos> }]
     * <comando enquanto> ::= enquanto (<expressão>) { <comandos> [<comando de parada>] [<comando de continuação>]}
     * 
     * <comando leitura> ::= leia ( <identificador >);
     * <comando escrita> ::= escreva (<expressão> );
     * <comando de parada> ::= pare;
     * <comando de continuação> ::= continue;
     */
    private void parseCmd() throws SyntaxError {
        switch (lookahead.tipo) {
            case SE -> {
                match(TipoToken.SE);
                match(TipoToken.ABRE_PARENTESES);
                parseExpr();
                match(TipoToken.FECHA_PARENTESES);
                match(TipoToken.ENTAO);
                parseCorpo();
                if (lookahead.tipo == TipoToken.SENAO) {
                    match(TipoToken.SENAO);
                    parseCorpo();
                }
            }
            case ENQUANTO -> {
                match(TipoToken.ENQUANTO);
                match(TipoToken.ABRE_PARENTESES);
                parseExpr();
                match(TipoToken.FECHA_PARENTESES);
                parseCorpo();
            }
            case LEIA -> {
                match(TipoToken.LEIA);
                match(TipoToken.ABRE_PARENTESES);
                match(TipoToken.IDENTIFICADOR);
                match(TipoToken.FECHA_PARENTESES);
                match(TipoToken.PONTO_E_VIRGULA);
            }
            case ESCREVA -> {
                match(TipoToken.ESCREVA);
                match(TipoToken.ABRE_PARENTESES);
                parseExpr();
                match(TipoToken.FECHA_PARENTESES);
                match(TipoToken.PONTO_E_VIRGULA);
            }
            case PARE -> {
                match(TipoToken.PARE);
                match(TipoToken.PONTO_E_VIRGULA);
            }
            case CONTINUE -> {
                match(TipoToken.CONTINUE);
                match(TipoToken.PONTO_E_VIRGULA);
            }
            case RETORNE -> {
                match(TipoToken.RETORNE);
                parseExpr();
                match(TipoToken.PONTO_E_VIRGULA);
            }
            case IDENTIFICADOR -> {
                String id = lookahead.lexema;
                match(TipoToken.IDENTIFICADOR);
                if (lookahead.tipo == TipoToken.ATRIBUICAO) {
                    match(TipoToken.ATRIBUICAO);
                    parseExpr();
                    match(TipoToken.PONTO_E_VIRGULA);
                } else {
                    match(TipoToken.ABRE_PARENTESES);
                    if (lookahead.tipo != TipoToken.FECHA_PARENTESES) {
                        parseListaExpressao();
                    }
                    match(TipoToken.FECHA_PARENTESES);
                    match(TipoToken.PONTO_E_VIRGULA);
                }
            }
            default -> error("Esperado comando mas veio " + lookahead.tipo);
        }
    }

    /**
     * <listaExpressao> ::= <expr> { ',' <expr> }
     */
    private void parseListaExpressao() throws SyntaxError {
        parseExpr();
        while (lookahead.tipo == TipoToken.VIRGULA) {
            match(TipoToken.VIRGULA);
            parseExpr();
        }
    }

    private boolean isStartCmd(TipoToken t) {
        return switch (t) {
            case SE, ENQUANTO, LEIA, ESCREVA, PARE, CONTINUE, RETORNE, IDENTIFICADOR -> true;
            default -> false;
        };
    }

    private void parseExpr() throws SyntaxError {
        parseExprOr();
    }

    private void parseExprOr() throws SyntaxError {
        parseExprAnd();
        while (lookahead.tipo == TipoToken.OU_LOGICO) {
            match(TipoToken.OU_LOGICO);
            parseExprAnd();
        }
    }


    private void parseExprAnd() throws SyntaxError {
        parseExprRelacional();
        while (lookahead.tipo == TipoToken.E_LOGICO) {
            match(TipoToken.E_LOGICO);
            parseExprRelacional();
        }
    }


    private void parseExprRelacional() throws SyntaxError {
        parseExprAdd();
        while (lookahead.tipo == TipoToken.IGUAL
                || lookahead.tipo == TipoToken.DIFERENTE
                || lookahead.tipo == TipoToken.MENOR
                || lookahead.tipo == TipoToken.MENOR_IGUAL
                || lookahead.tipo == TipoToken.MAIOR
                || lookahead.tipo == TipoToken.MAIOR_IGUAL) {
            TipoToken op = lookahead.tipo;
            match(op);
            parseExprAdd();
        }
    }

    /**
     * <exprAdd> ::= <exprMul> { ('+' | '-') <exprMul> }
     */
    private void parseExprAdd() throws SyntaxError {
        parseExprMul();
        while (lookahead.tipo == TipoToken.MAIS || lookahead.tipo == TipoToken.MENOS) {
            TipoToken op = lookahead.tipo;
            match(op);
            parseExprMul();
        }
    }

    private void parseExprMul() throws SyntaxError {
        parseExprUnary();
        while (lookahead.tipo == TipoToken.VEZES || lookahead.tipo == TipoToken.DIVISAO) {
            TipoToken op = lookahead.tipo;
            match(op);
            parseExprUnary();
        }
    }

    private void parseExprUnary() throws SyntaxError {
        if (lookahead.tipo == TipoToken.NAO_LOGICO) {
            match(TipoToken.NAO_LOGICO);
            parseExprUnary();
        } else if (lookahead.tipo == TipoToken.MENOS) {
            match(TipoToken.MENOS);
            parseExprUnary();
        } else {
            parseExprPrimary();
        }
    }

    private void parseExprPrimary() throws SyntaxError {
        if (lookahead.tipo == TipoToken.IDENTIFICADOR) {
            String nome = lookahead.lexema;
            match(TipoToken.IDENTIFICADOR);
            // novo: chamada de função
            if (lookahead.tipo == TipoToken.ABRE_PARENTESES) {
                match(TipoToken.ABRE_PARENTESES);
                if (lookahead.tipo != TipoToken.FECHA_PARENTESES) {
                    parseListaExpressao();
                }
                match(TipoToken.FECHA_PARENTESES);
            }
        } else if (lookahead.tipo == TipoToken.NUMERO) {
            match(TipoToken.NUMERO);
        } else if (lookahead.tipo == TipoToken.VERDADEIRO) {
            match(TipoToken.VERDADEIRO);
        } else if (lookahead.tipo == TipoToken.FALSO) {
            match(TipoToken.FALSO);
        } else if (lookahead.tipo == TipoToken.ABRE_PARENTESES) {
            match(TipoToken.ABRE_PARENTESES);
            parseExpr();
            match(TipoToken.FECHA_PARENTESES);
        } else {
            error("Esperado expressão mas veio " + lookahead.tipo);
        }
    }
}
