import model.*;
import exceptions.*;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static Administrador adminLogado = null;
    private static Participante participanteLogado = null;

    public static void main(String[] args) {
        try {
            Database.initializeDatabase();
            exibirMenuPrincipal();
        } catch (Exception e) {
            System.err.println("Erro no sistema: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }

    private static void exibirMenuPrincipal() throws SQLException {
        while (true) {
            System.out.println("\n=== SISTEMA DE GERENCIAMENTO DE EVENTOS ACADÊMICOS ===");
            System.out.println("1. Login Administrador");
            System.out.println("2. Login Participante");
            System.out.println("3. Cadastrar Participante");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine(); // Limpar buffer

            switch (opcao) {
                case 1:
                    loginAdministrador();
                    break;
                case 2:
                    loginParticipante();
                    break;
                case 3:
                    cadastrarParticipante();
                    break;
                case 0:
                    System.out.println("Saindo do sistema...");
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void loginAdministrador() throws SQLException {
        System.out.print("\nUsuário: ");
        String usuario = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        adminLogado = Administrador.login(usuario, senha);
        if (adminLogado != null) {
            System.out.println("Login realizado com sucesso!");
            menuAdministrador();
        } else {
            System.out.println("Usuário ou senha incorretos!");
        }
    }

    private static void loginParticipante() throws SQLException {
        System.out.print("\nUsuário: ");
        String usuario = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        participanteLogado = Participante.login(usuario, senha);
        if (participanteLogado != null) {
            System.out.println("Login realizado com sucesso!");
            menuParticipante();
        } else {
            System.out.println("Usuário ou senha incorretos!");
        }
    }

    private static void cadastrarParticipante() throws SQLException {
        System.out.println("\n=== CADASTRO DE PARTICIPANTE ===");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Usuário: ");
        String usuario = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        System.out.println("Tipo de participante:");
        System.out.println("1. Aluno");
        System.out.println("2. Professor");
        System.out.println("3. Profissional");
        System.out.print("Escolha: ");
        int tipo = scanner.nextInt();
        scanner.nextLine();

        Participante participante;
        switch (tipo) {
            case 1:
                participante = new Aluno(nome, email, usuario, senha);
                break;
            case 2:
                participante = new Professor(nome, email, usuario, senha);
                break;
            case 3:
                participante = new Profissional(nome, email, usuario, senha);
                break;
            default:
                System.out.println("Tipo inválido!");
                return;
        }

        participante.cadastrar();
        System.out.println("Participante cadastrado com sucesso!");
    }

    private static void menuAdministrador() throws SQLException {
        while (true) {
            System.out.println("\n=== MENU ADMINISTRADOR ===");
            System.out.println("1. Criar Evento");
            System.out.println("2. Listar Eventos");
            System.out.println("3. Adicionar Atividade");
            System.out.println("4. Listar Inscrições");
            System.out.println("5. Confirmar Pagamento");
            System.out.println("0. Logout");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    criarEvento();
                    break;
                case 2:
                    Evento.listarEventos();
                    break;
                case 3:
                    adicionarAtividade();
                    break;
                case 4:
                    Inscricao.listarInscricoes();
                    break;
                case 5:
                    confirmarPagamento();
                    break;
                case 0:
                    adminLogado = null;
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void criarEvento() throws SQLException {
        System.out.println("\n=== CRIAR EVENTO ===");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();
        System.out.print("Data de Início (YYYY-MM-DD): ");
        String dataInicio = scanner.nextLine();
        System.out.print("Data de Fim (YYYY-MM-DD): ");
        String dataFim = scanner.nextLine();

        Evento evento = new Evento(nome, descricao, dataInicio, dataFim);
        evento.salvar();
        System.out.println("Evento criado com sucesso!");
    }

    private static void adicionarAtividade() throws SQLException {
        System.out.println("\n=== ADICIONAR ATIVIDADE ===");
        Evento.listarEventos();
        System.out.print("ID do Evento: ");
        int eventoId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Nome da Atividade: ");
        String nome = scanner.nextLine();
        System.out.print("Descrição: ");
        String descricao = scanner.nextLine();
        System.out.print("Data (YYYY-MM-DD): ");
        String data = scanner.nextLine();
        System.out.print("Limite de Inscritos: ");
        int limite = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Tipo (palestra, simpósio, curso, etc.): ");
        String tipo = scanner.nextLine();

        Atividade atividade = new Atividade(eventoId, nome, descricao, data, limite, tipo);
        atividade.salvar();
        System.out.println("Atividade adicionada com sucesso!");
    }

    private static void confirmarPagamento() throws SQLException {
        System.out.println("\n=== CONFIRMAR PAGAMENTO ===");
        Inscricao.listarInscricoesComPagamentoPendente();
        System.out.print("ID da Inscrição: ");
        int inscricaoId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Valor Pago: ");
        double valor = scanner.nextDouble();
        scanner.nextLine();

        Pagamento.confirmarPagamento(inscricaoId, valor);
        System.out.println("Pagamento confirmado com sucesso!");
    }

    private static void menuParticipante() throws SQLException {
        while (true) {
            System.out.println("\n=== MENU PARTICIPANTE ===");
            System.out.println("1. Listar Eventos");
            System.out.println("2. Inscrever-se em Evento");
            System.out.println("3. Inscrever-se em Atividade");
            System.out.println("4. Minhas Inscrições");
            System.out.println("5. Registrar Pagamento");
            System.out.println("0. Logout");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    Evento.listarEventos();
                    break;
                case 2:
                    inscreverEmEvento();
                    break;
                case 3:
                    inscreverEmAtividade();
                    break;
                case 4:
                    participanteLogado.listarMinhasInscricoes();
                    break;
                case 5:
                    registrarPagamento();
                    break;
                case 0:
                    participanteLogado = null;
                    return;
                default:
                    System.out.println("Opção inválida!");
            }
        }
    }

    private static void inscreverEmEvento() throws SQLException {
        System.out.println("\n=== INSCRIÇÃO EM EVENTO ===");
        Evento.listarEventos();
        System.out.print("ID do Evento: ");
        int eventoId = scanner.nextInt();
        scanner.nextLine();

        try {
            Inscricao.inscreverEmEvento(participanteLogado.getId(), eventoId);
            System.out.println("Inscrição realizada com sucesso!");
        } catch (InscricaoDuplicadaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void inscreverEmAtividade() throws SQLException {
        System.out.println("\n=== INSCRIÇÃO EM ATIVIDADE ===");
        Evento.listarEventos();
        System.out.print("ID do Evento: ");
        int eventoId = scanner.nextInt();
        scanner.nextLine();

        Atividade.listarAtividadesDoEvento(eventoId);
        System.out.print("ID da Atividade: ");
        int atividadeId = scanner.nextInt();
        scanner.nextLine();

        try {
            Inscricao.inscreverEmAtividade(participanteLogado.getId(), eventoId, atividadeId);
            System.out.println("Inscrição realizada com sucesso!");
        } catch (InscricaoDuplicadaException | AtividadeLotadaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void registrarPagamento() throws SQLException {
        System.out.println("\n=== REGISTRAR PAGAMENTO ===");
        participanteLogado.listarMinhasInscricoesComPagamentoPendente();
        System.out.print("ID da Inscrição: ");
        int inscricaoId = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Valor Pago: ");
        double valor = scanner.nextDouble();
        scanner.nextLine();

        try {
            participanteLogado.registrarPagamento(inscricaoId, valor);
            System.out.println("Pagamento registrado. Aguarde confirmação do administrador.");
        } catch (PagamentoInvalidoException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}