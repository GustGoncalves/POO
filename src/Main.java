import connection.Database;
import model.*;
import dao.*;
import exceptions.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static Administrador adminLogado = null;
    private static Participante participanteLogado = null;

    // Instâncias dos DAOs
    private static AdministradorDAO administradorDAO = new AdministradorDAO();
    private static ParticipanteDAO participanteDAO = new ParticipanteDAO();
    private static EventoDAO eventoDAO = new EventoDAO();
    private static AtividadeDAO atividadeDAO = new AtividadeDAO();
    private static InscricaoDAO inscricaoDAO = new InscricaoDAO();
    private static PagamentoDAO pagamentoDAO = new PagamentoDAO();
    private static ValorInscricaoDAO valorInscricaoDAO = new ValorInscricaoDAO();

    public static void main(String[] args) {
        try {
            Database.initializeDatabase();
            exibirMenuPrincipal();
        } catch (Exception e) {
            System.err.println("Erro no sistema: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }

    private static void exibirMenuPrincipal() throws SQLException, EntidadeNaoEncontradaException, SenhaFracaException, EmailInvalidoException, EmailDuplicadoException {
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

    private static void loginAdministrador() throws SQLException, EntidadeNaoEncontradaException {
        System.out.print("\nUsuário: ");
        String usuario = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        adminLogado = administradorDAO.login(usuario, senha);
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

        participanteLogado = participanteDAO.login(usuario, senha);
        if (participanteLogado != null) {
            System.out.println("Login realizado com sucesso!");
            menuParticipante();
        } else {
            System.out.println("Usuário ou senha incorretos!");
        }
    }

    private static void cadastrarParticipante() throws SQLException, SenhaFracaException, EmailInvalidoException, EmailDuplicadoException {
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

        participanteDAO.cadastrar(participante);
        System.out.println("Participante cadastrado com sucesso!");
    }

    private static void menuAdministrador() throws SQLException, EntidadeNaoEncontradaException {
        while (true) {
            System.out.println("\n=== MENU ADMINISTRADOR ===");
            System.out.println("1. Criar Evento");
            System.out.println("2. Listar Eventos");
            System.out.println("3. Editar Evento");
            System.out.println("4. Excluir Evento");
            System.out.println("5. Adicionar Atividade");
            System.out.println("6. Listar Participantes por Evento");
            System.out.println("7. Listar Participantes por Atividade");
            System.out.println("8. Listar Todas Inscrições com Status");
            System.out.println("9. Confirmar Pagamento");
            System.out.println("10. Definir Valores de Inscrição");
            System.out.println("0. Logout");
            System.out.print("Escolha uma opção: ");

            int opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1:
                    criarEvento();
                    break;
                case 2:
                    listarEventos();
                    break;
                case 3:
                    editarEvento();
                    break;
                case 4:
                    excluirEvento();
                    break;
                case 5:
                    adicionarAtividade();
                    break;
                case 6:
                    listarParticipantesPorEvento();
                    break;
                case 7:
                    listarParticipantesPorAtividade();
                    break;
                case 8:
                    listarInscricoesComStatus();
                    break;
                case 9:
                    confirmarPagamentoAdmin();
                    break;
                case 10:
                    definirValoresInscricao();
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
        eventoDAO.salvar(evento);
        System.out.println("Evento criado com sucesso!");
    }

    private static void listarEventos() throws SQLException {
        List<Evento> eventos = eventoDAO.listarTodos();

        System.out.println("\n=== LISTA DE EVENTOS ===");
        for (Evento evento : eventos) {
            System.out.println("ID: " + evento.getId());
            System.out.println("Nome: " + evento.getNome());
            System.out.println("Descrição: " + evento.getDescricao());
            System.out.println("Data Início: " + evento.getDataInicio());
            System.out.println("Data Fim: " + evento.getDataFim());
            System.out.println("----------------------");
        }
    }

    private static void editarEvento() throws SQLException {
        System.out.println("\n=== EDITAR EVENTO ===");

        // Lista todos os eventos disponíveis
        listarEventos();

        System.out.print("ID do Evento a editar: ");
        int id = scanner.nextInt();
        scanner.nextLine(); // Limpar buffer

        try {
            // Busca o evento existente no banco de dados
            Evento eventoExistente = eventoDAO.buscarPorId(id);

            System.out.println("\nEditando Evento ID: " + eventoExistente.getId());
            System.out.println("[Deixe em branco para manter o valor atual]");

            // Solicita novos valores, mostrando os atuais como referência
            System.out.print("Novo Nome (" + eventoExistente.getNome() + "): ");
            String nome = scanner.nextLine();

            System.out.print("Nova Descrição (" + eventoExistente.getDescricao() + "): ");
            String descricao = scanner.nextLine();

            System.out.print("Nova Data de Início (" + eventoExistente.getDataInicio() + "): ");
            String dataInicio = scanner.nextLine();

            System.out.print("Nova Data de Fim (" + eventoExistente.getDataFim() + "): ");
            String dataFim = scanner.nextLine();

            // Cria objeto com os valores atualizados (mantém os antigos se novos forem vazios)
            Evento eventoAtualizado = new Evento(
                    nome.isEmpty() ? eventoExistente.getNome() : nome,
                    descricao.isEmpty() ? eventoExistente.getDescricao() : descricao,
                    dataInicio.isEmpty() ? eventoExistente.getDataInicio() : dataInicio,
                    dataFim.isEmpty() ? eventoExistente.getDataFim() : dataFim
            );
            eventoAtualizado.setId(id);

            // Atualiza no banco de dados
            eventoDAO.atualizar(eventoAtualizado);
            System.out.println("\nEvento atualizado com sucesso!");

            // Mostra os dados atualizados
            System.out.println("\nDados atualizados:");
            System.out.println("Nome: " + eventoAtualizado.getNome());
            System.out.println("Descrição: " + eventoAtualizado.getDescricao());
            System.out.println("Data Início: " + eventoAtualizado.getDataInicio());
            System.out.println("Data Fim: " + eventoAtualizado.getDataFim());

        } catch (EntidadeNaoEncontradaException e) {
            System.out.println("\nErro: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("\nErro ao atualizar evento: " + e.getMessage());
        }
    }

    private static void excluirEvento() throws SQLException {
        System.out.println("\n=== EXCLUIR EVENTO ===");
        listarEventos();
        System.out.print("ID do Evento a excluir: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        eventoDAO.excluir(id);
        System.out.println("Evento excluído com sucesso!");
    }

    private static void adicionarAtividade() throws SQLException {
        System.out.println("\n=== ADICIONAR ATIVIDADE ===");
        listarEventos();
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
        atividadeDAO.salvar(atividade);
        System.out.println("Atividade adicionada com sucesso!");
    }

    private static void listarParticipantesPorEvento() throws SQLException {
        System.out.println("\n=== LISTAR PARTICIPANTES POR EVENTO ===");
        listarEventos();
        System.out.print("ID do Evento: ");
        int eventoId = scanner.nextInt();
        scanner.nextLine();

        List<Participante> participantes = administradorDAO.listarParticipantesPorEvento(eventoId);

        System.out.println("\n=== PARTICIPANTES INSCRITOS ===");
        for (Participante p : participantes) {
            System.out.println("ID: " + p.getId());
            System.out.println("Nome: " + p.getNome());
            System.out.println("Email: " + p.getEmail());
            System.out.println("Tipo: " + p.getTipo());
            System.out.println("----------------------");
        }
    }

    private static void listarParticipantesPorAtividade() throws SQLException {
        System.out.println("\n=== LISTAR PARTICIPANTES POR ATIVIDADE ===");
        listarEventos();
        System.out.print("ID do Evento: ");
        int eventoId = scanner.nextInt();
        scanner.nextLine();

        listarAtividadesDoEvento(eventoId);
        System.out.print("ID da Atividade: ");
        int atividadeId = scanner.nextInt();
        scanner.nextLine();

        List<Participante> participantes = administradorDAO.listarParticipantesPorAtividade(atividadeId);

        System.out.println("\n=== PARTICIPANTES INSCRITOS ===");
        for (Participante p : participantes) {
            System.out.println("ID: " + p.getId());
            System.out.println("Nome: " + p.getNome());
            System.out.println("Email: " + p.getEmail());
            System.out.println("Tipo: " + p.getTipo());
            System.out.println("----------------------");
        }
    }

    private static void listarInscricoesComStatus() throws SQLException {
        List<InscricaoDAO.InscricaoInfo> inscricoes = inscricaoDAO.listarTodas();

        System.out.println("\n=== TODAS AS INSCRIÇÕES ===");
        for (InscricaoDAO.InscricaoInfo i : inscricoes) {
            System.out.println("ID: " + i.getId());
            System.out.println("Participante: " + i.getParticipante());
            System.out.println("Evento: " + i.getEvento());
            System.out.println("Atividade: " + i.getAtividade());
            System.out.println("Data: " + i.getDataInscricao());
            System.out.println("Status: " + (i.getStatusPagamento() == null ? "PENDENTE" : i.getStatusPagamento()));
            System.out.println("Valor: " + i.getValor());
            System.out.println("----------------------");
        }
    }

    private static void confirmarPagamentoAdmin() throws SQLException, EntidadeNaoEncontradaException {
        System.out.println("\n=== CONFIRMAR PAGAMENTO ===");
        List<InscricaoDAO.InscricaoInfo> inscricoesPendentes = inscricaoDAO.listarComPagamentoPendente();

        System.out.println("\n=== INSCRIÇÕES COM PAGAMENTO PENDENTE ===");
        for (InscricaoDAO.InscricaoInfo inscricao : inscricoesPendentes) {
            System.out.println("ID: " + inscricao.getId());
            System.out.println("Participante: " + inscricao.getParticipante());
            System.out.println("Evento: " + inscricao.getEvento());
            System.out.println("Atividade: " + inscricao.getAtividade());
            System.out.println("Status: " + (inscricao.getStatusPagamento() == null ? "PENDENTE" : inscricao.getStatusPagamento()));
            System.out.println("----------------------");
        }

        System.out.print("ID da Inscrição: ");
        int inscricaoId = scanner.nextInt();
        scanner.nextLine();

        pagamentoDAO.confirmarPagamento(inscricaoId);
        System.out.println("Pagamento confirmado com sucesso!");
    }

    private static void definirValoresInscricao() throws SQLException {
        System.out.println("\n=== DEFINIR VALORES DE INSCRIÇÃO ===");
        System.out.println("1. Definir valor para Alunos");
        System.out.println("2. Definir valor para Professores");
        System.out.println("3. Definir valor para Profissionais");
        System.out.print("Escolha: ");
        int tipo = scanner.nextInt();
        scanner.nextLine();

        System.out.print("Valor da Inscrição: ");
        double valor = scanner.nextDouble();
        scanner.nextLine();

        String tipoParticipante = "";
        switch (tipo) {
            case 1:
                tipoParticipante = "ALUNO";
                break;
            case 2:
                tipoParticipante = "PROFESSOR";
                break;
            case 3:
                tipoParticipante = "PROFISSIONAL";
                break;
            default:
                System.out.println("Tipo inválido!");
                return;
        }

        valorInscricaoDAO.atualizarValor(tipoParticipante, valor);
        System.out.println("Valor definido com sucesso!");
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
                    listarEventos();
                    break;
                case 2:
                    inscreverEmEvento();
                    break;
                case 3:
                    inscreverEmAtividade();
                    break;
                case 4:
                    listarMinhasInscricoes();
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
        listarEventos();
        System.out.print("ID do Evento: ");
        int eventoId = scanner.nextInt();
        scanner.nextLine();

        try {
            inscricaoDAO.inscreverEmEvento(participanteLogado.getId(), eventoId);
            System.out.println("Inscrição realizada com sucesso!");
        } catch (InscricaoDuplicadaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void inscreverEmAtividade() throws SQLException {
        System.out.println("\n=== INSCRIÇÃO EM ATIVIDADE ===");
        listarEventos();
        System.out.print("ID do Evento: ");
        int eventoId = scanner.nextInt();
        scanner.nextLine();

        listarAtividadesDoEvento(eventoId);
        System.out.print("ID da Atividade: ");
        int atividadeId = scanner.nextInt();
        scanner.nextLine();

        try {
            inscricaoDAO.inscreverEmAtividade(participanteLogado.getId(), eventoId, atividadeId);
            System.out.println("Inscrição realizada com sucesso!");
        } catch (InscricaoDuplicadaException | AtividadeLotadaException e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void listarAtividadesDoEvento(int eventoId) throws SQLException {
        List<Atividade> atividades = atividadeDAO.listarPorEvento(eventoId);

        System.out.println("\n=== ATIVIDADES DO EVENTO ===");
        for (Atividade atividade : atividades) {
            System.out.println("ID: " + atividade.getId());
            System.out.println("Nome: " + atividade.getNome());
            System.out.println("Descrição: " + atividade.getDescricao());
            System.out.println("Data: " + atividade.getData());
            System.out.println("Vagas: " + atividade.getLimiteInscritos());
            System.out.println("Tipo: " + atividade.getTipo());
            System.out.println("----------------------");
        }
    }

    private static void listarMinhasInscricoes() throws SQLException {
        List<InscricaoDAO.InscricaoInfo> inscricoes = inscricaoDAO.listarPorParticipante(participanteLogado.getId());

        System.out.println("\n=== MINHAS INSCRIÇÕES ===");
        for (InscricaoDAO.InscricaoInfo inscricao : inscricoes) {
            System.out.println("ID: " + inscricao.getId());
            System.out.println("Evento: " + inscricao.getEvento());
            System.out.println("Atividade: " + inscricao.getAtividade());
            System.out.println("Data: " + inscricao.getDataInscricao());
            System.out.println("Status Pagamento: " +
                    (inscricao.getStatusPagamento() == null ? "PENDENTE" : inscricao.getStatusPagamento()));
            System.out.println("Valor: " + inscricao.getValor());
            System.out.println("----------------------");
        }
    }

    private static void registrarPagamento() throws SQLException {
        System.out.println("\n=== REGISTRAR PAGAMENTO ===");
        List<InscricaoDAO.InscricaoInfo> inscricoesPendentes =
                inscricaoDAO.listarComPagamentoPendentePorParticipante(participanteLogado.getId());

        System.out.println("\n=== INSCRIÇÕES COM PAGAMENTO PENDENTE ===");
        for (InscricaoDAO.InscricaoInfo inscricao : inscricoesPendentes) {
            System.out.println("ID Inscrição: " + inscricao.getId());
            System.out.println("Evento: " + inscricao.getEvento());
            System.out.println("Atividade: " + inscricao.getAtividade());
            System.out.println("----------------------");
        }

        System.out.print("ID da Inscrição: ");
        int inscricaoId = scanner.nextInt();
        scanner.nextLine();

        try {
            // Obtém o valor automaticamente baseado no tipo de participante
            double valor = valorInscricaoDAO.getValorPorTipo(participanteLogado.getTipo());
            System.out.println("Valor a pagar: " + valor);

            System.out.print("Confirmar pagamento? (S/N): ");
            String confirmacao = scanner.nextLine();

            if (confirmacao.equalsIgnoreCase("S")) {
                pagamentoDAO.registrarPagamento(inscricaoId, valor);
                System.out.println("Pagamento registrado. Aguarde confirmação do administrador.");
            } else {
                System.out.println("Pagamento cancelado.");
            }
        } catch (PagamentoInvalidoException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (EntidadeNaoEncontradaException e) {
            throw new RuntimeException(e);
        }
    }
}