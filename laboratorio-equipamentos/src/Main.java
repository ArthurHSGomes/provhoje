import exception.EquipamentoIndisponivelException;
import exception.EquipamentoNaoEncontradoException;
import model.Equipamento;
import model.Laboratorio;
import repository.Repositorio;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Main {

    private static final Repositorio<Equipamento> repositorio = new Repositorio<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        popularDados();

        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opção");
            tratarOpcao(opcao);
        } while (opcao != 0);

        scanner.close();
        System.out.println("\nSistema encerrado. Até logo!");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Carga inicial automática de 5 equipamentos
    // ─────────────────────────────────────────────────────────────────────────
    private static void popularDados() {
        repositorio.adicionar(new Equipamento("EQ001", "Microscópio Óptico",       Laboratorio.BIOLOGIA,   5,  5000.00));
        repositorio.adicionar(new Equipamento("EQ002", "Centrífuga Clínica",        Laboratorio.QUIMICA,    3,  8500.00));
        repositorio.adicionar(new Equipamento("EQ003", "Osciloscópio Digital",      Laboratorio.FISICA,     2, 12000.00));
        repositorio.adicionar(new Equipamento("EQ004", "Servidor Dell PowerEdge",   Laboratorio.COMPUTACAO, 1, 25000.00));
        repositorio.adicionar(new Equipamento("EQ005", "Balança Analítica",         Laboratorio.QUIMICA,    4,  3200.00));
        System.out.println(">>> 5 equipamentos carregados automaticamente.\n");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Menu principal
    // ─────────────────────────────────────────────────────────────────────────
    private static void exibirMenu() {
        System.out.println("\n╔══════════════════════════════════════════╗");
        System.out.println("║    SISTEMA DE CONTROLE DE EQUIPAMENTOS   ║");
        System.out.println("╠══════════════════════════════════════════╣");
        System.out.println("║  1. Cadastrar equipamento                ║");
        System.out.println("║  2. Remover equipamento                  ║");
        System.out.println("║  3. Filtrar por laboratório              ║");
        System.out.println("║  4. Filtrar por quantidade mínima        ║");
        System.out.println("║  5. Adicionar unidades ao estoque        ║");
        System.out.println("║  6. Ordenar equipamentos                 ║");
        System.out.println("║  7. Gerar relatório resumido             ║");
        System.out.println("║  8. Retirar unidades                     ║");
        System.out.println("║  9. Exibir todos os equipamentos         ║");
        System.out.println("║  0. Sair                                 ║");
        System.out.println("╚══════════════════════════════════════════╝");
    }

    private static void tratarOpcao(int opcao) {
        switch (opcao) {
            case 1 -> cadastrarEquipamento();
            case 2 -> removerEquipamento();
            case 3 -> filtrarPorLaboratorio();
            case 4 -> filtrarPorQuantidade();
            case 5 -> adicionarUnidades();
            case 6 -> ordenarEquipamentos();
            case 7 -> gerarRelatorio();
            case 8 -> retirarUnidades();
            case 9 -> exibirTodos();
            case 0 -> { /* encerramento tratado no main */ }
            default -> System.out.println("[AVISO] Opção inválida. Tente novamente.");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Funcionalidade 1 — Cadastrar equipamento
    // ─────────────────────────────────────────────────────────────────────────
    private static void cadastrarEquipamento() {
        System.out.println("\n--- CADASTRO DE EQUIPAMENTO ---");
        String codigo = lerString("Código       : ");

        List<Equipamento> existente = repositorio.buscar(e -> e.getCodigo().equalsIgnoreCase(codigo));
        if (!existente.isEmpty()) {
            System.out.println("[ERRO] Já existe um equipamento com o código: " + codigo);
            return;
        }

        String nome      = lerString("Nome         : ");
        Laboratorio lab  = escolherLaboratorio();
        int qtd          = lerInteiro("Quantidade   ");
        double valor     = lerDouble("Valor (R$)   ");

        repositorio.adicionar(new Equipamento(codigo, nome, lab, qtd, valor));
        System.out.println("[OK] Equipamento cadastrado com sucesso!");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Funcionalidade 2 — Remover equipamento
    // ─────────────────────────────────────────────────────────────────────────
    private static void removerEquipamento() {
        System.out.println("\n--- REMOÇÃO DE EQUIPAMENTO ---");
        String codigo = lerString("Código do equipamento: ");
        try {
            Equipamento eq = buscarPorCodigo(codigo);
            repositorio.remover(eq);
            System.out.println("[OK] Equipamento \"" + eq.getNome() + "\" removido com sucesso!");
        } catch (EquipamentoNaoEncontradoException e) {
            System.out.println("[ERRO] " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Funcionalidade 3 — Filtrar por laboratório  [usa Predicate + Consumer]
    // ─────────────────────────────────────────────────────────────────────────
    private static void filtrarPorLaboratorio() {
        System.out.println("\n--- FILTRAR POR LABORATÓRIO ---");
        Laboratorio lab = escolherLaboratorio();

        Predicate<Equipamento> filtroPorLab = e -> e.getLaboratorio() == lab;
        List<Equipamento> resultado = repositorio.buscar(filtroPorLab);

        System.out.println("\nEquipamentos do laboratório de " + lab.getDescricao() + ":");
        exibirLista(resultado);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Funcionalidade 4 — Filtrar por quantidade mínima  [usa Predicate + Consumer]
    // ─────────────────────────────────────────────────────────────────────────
    private static void filtrarPorQuantidade() {
        System.out.println("\n--- FILTRAR POR QUANTIDADE MÍNIMA ---");
        int qtdMinima = lerInteiro("Quantidade mínima disponível");

        Predicate<Equipamento> filtroPorQtd = e -> e.getQuantidadeDisponivel() >= qtdMinima;
        List<Equipamento> resultado = repositorio.buscar(filtroPorQtd);

        System.out.println("\nEquipamentos com quantidade disponível >= " + qtdMinima + ":");
        exibirLista(resultado);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Funcionalidade 5 — Adicionar unidades ao estoque
    // ─────────────────────────────────────────────────────────────────────────
    private static void adicionarUnidades() {
        System.out.println("\n--- ADICIONAR UNIDADES AO ESTOQUE ---");
        String codigo = lerString("Código do equipamento: ");
        try {
            Equipamento eq = buscarPorCodigo(codigo);
            int qtd = lerInteiro("Quantidade a adicionar");
            eq.setQuantidadeDisponivel(eq.getQuantidadeDisponivel() + qtd);
            System.out.println("[OK] Estoque atualizado! Nova quantidade: " + eq.getQuantidadeDisponivel());
        } catch (EquipamentoNaoEncontradoException e) {
            System.out.println("[ERRO] " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Funcionalidade 6 — Ordenar equipamentos  [usa Comparator + Consumer]
    // ─────────────────────────────────────────────────────────────────────────
    private static void ordenarEquipamentos() {
        System.out.println("\n--- ORDENAR EQUIPAMENTOS ---");
        System.out.println("  1. Por valor patrimonial (menor → maior)");
        System.out.println("  2. Por nome (A → Z)");
        int tipo = lerInteiro("Critério de ordenação");

        Comparator<Equipamento> comparador;
        String criterio;

        if (tipo == 2) {
            comparador = Comparator.comparing(Equipamento::getNome, String.CASE_INSENSITIVE_ORDER);
            criterio = "nome";
        } else {
            comparador = Comparator.comparingDouble(Equipamento::getValorPatrimonial);
            criterio = "valor patrimonial";
        }

        List<Equipamento> ordenados = repositorio.listar().stream()
                .sorted(comparador)
                .collect(Collectors.toList());

        System.out.println("\nEquipamentos ordenados por " + criterio + ":");
        exibirLista(ordenados);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Funcionalidade 7 — Relatório resumido  [usa Function + Consumer]
    // ─────────────────────────────────────────────────────────────────────────
    private static void gerarRelatorio() {
        System.out.println("\n--- RELATÓRIO RESUMIDO DOS EQUIPAMENTOS ---");
        List<Equipamento> todos = repositorio.listar();

        if (todos.isEmpty()) {
            System.out.println("Nenhum equipamento cadastrado.");
            return;
        }

        // Function: converte Equipamento → String formatada
        Function<Equipamento, String> formatador = e ->
                e.getNome() + " - Laboratório de " + e.getLaboratorio().getDescricao()
                + " - R$ " + String.format("%,.2f", e.getValorPatrimonial());

        double valorTotal = todos.stream().mapToDouble(Equipamento::getValorPatrimonial).sum();
        int qtdTotal      = todos.stream().mapToInt(Equipamento::getQuantidadeDisponivel).sum();

        System.out.println("Total de tipos de equipamentos : " + todos.size());
        System.out.printf( "Total de unidades disponíveis  : %d%n", qtdTotal);
        System.out.printf( "Valor patrimonial total        : R$ %,.2f%n%n", valorTotal);
        System.out.println("Listagem:");
        System.out.println("─".repeat(65));
        todos.stream()
                .map(formatador)
                .forEach(System.out::println);
        System.out.println("─".repeat(65));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Funcionalidade 8 — Retirar unidades  [lança EquipamentoIndisponivelException]
    // ─────────────────────────────────────────────────────────────────────────
    private static void retirarUnidades() {
        System.out.println("\n--- RETIRADA DE UNIDADES ---");
        String codigo = lerString("Código do equipamento: ");
        try {
            Equipamento eq = buscarPorCodigo(codigo);

            System.out.println("Quantidade disponível: " + eq.getQuantidadeDisponivel());
            int qtd = lerInteiro("Quantidade a retirar");

            if (qtd > eq.getQuantidadeDisponivel()) {
                throw new EquipamentoIndisponivelException(
                        "Quantidade solicitada (" + qtd + ") superior ao disponível ("
                        + eq.getQuantidadeDisponivel() + ").");
            }

            eq.setQuantidadeDisponivel(eq.getQuantidadeDisponivel() - qtd);
            System.out.println("[OK] Retirada realizada! Quantidade restante: " + eq.getQuantidadeDisponivel());

        } catch (EquipamentoNaoEncontradoException | EquipamentoIndisponivelException e) {
            System.out.println("[ERRO] " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Funcionalidade 9 — Exibir todos os equipamentos  [usa Consumer]
    // ─────────────────────────────────────────────────────────────────────────
    private static void exibirTodos() {
        System.out.println("\n--- TODOS OS EQUIPAMENTOS ---");
        List<Equipamento> todos = repositorio.listar();
        System.out.println("Total cadastrado: " + todos.size() + " equipamento(s)\n");
        exibirLista(todos);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Métodos auxiliares
    // ─────────────────────────────────────────────────────────────────────────

    /** Busca por código e lança exceção se não encontrado. */
    private static Equipamento buscarPorCodigo(String codigo) throws EquipamentoNaoEncontradoException {
        List<Equipamento> resultado = repositorio.buscar(e -> e.getCodigo().equalsIgnoreCase(codigo));
        if (resultado.isEmpty()) {
            throw new EquipamentoNaoEncontradoException(
                    "Equipamento com código \"" + codigo + "\" não encontrado.");
        }
        return resultado.get(0);
    }

    /** Consumer padrão para exibir um equipamento. */
    private static Consumer<Equipamento> criarExibidor() {
        return e -> {
            System.out.println("┌─────────────────────────────────────────────");
            System.out.println("│ Código              : " + e.getCodigo());
            System.out.println("│ Nome                : " + e.getNome());
            System.out.println("│ Laboratório         : " + e.getLaboratorio().getDescricao());
            System.out.println("│ Qtd. disponível     : " + e.getQuantidadeDisponivel());
            System.out.printf( "│ Valor patrimonial   : R$ %,.2f%n", e.getValorPatrimonial());
            System.out.println("└─────────────────────────────────────────────");
        };
    }

    /** Exibe uma lista usando o Consumer; mostra mensagem se vazia. */
    private static void exibirLista(List<Equipamento> lista) {
        if (lista.isEmpty()) {
            System.out.println("Nenhum equipamento encontrado.");
            return;
        }
        Consumer<Equipamento> exibidor = criarExibidor();
        lista.forEach(exibidor);
    }

    /** Permite ao usuário escolher um laboratório via menu numerado. */
    private static Laboratorio escolherLaboratorio() {
        Laboratorio[] labs = Laboratorio.values();
        System.out.println("Laboratórios:");
        for (int i = 0; i < labs.length; i++) {
            System.out.println("  " + (i + 1) + ". " + labs[i].getDescricao());
        }
        int escolha;
        do {
            escolha = lerInteiro("Escolha (1-" + labs.length + ")");
            if (escolha < 1 || escolha > labs.length) {
                System.out.println("[AVISO] Opção inválida.");
            }
        } while (escolha < 1 || escolha > labs.length);
        return labs[escolha - 1];
    }

    /** Lê um inteiro do console com validação de formato. */
    private static int lerInteiro(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem + ": ");
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("[ERRO] Digite um número inteiro válido.");
            }
        }
    }

    /** Lê um double do console com validação de formato (aceita vírgula). */
    private static double lerDouble(String mensagem) {
        while (true) {
            try {
                System.out.print(mensagem + ": ");
                return Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
            } catch (NumberFormatException e) {
                System.out.println("[ERRO] Digite um valor numérico válido (ex: 1500.00).");
            }
        }
    }

    /** Lê uma String não vazia do console. */
    private static String lerString(String mensagem) {
        String valor;
        do {
            System.out.print(mensagem);
            valor = scanner.nextLine().trim();
            if (valor.isEmpty()) {
                System.out.println("[AVISO] Campo obrigatório. Tente novamente.");
            }
        } while (valor.isEmpty());
        return valor;
    }
}
