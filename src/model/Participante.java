package model;

import java.sql.*;
import exceptions.*;

public abstract class Participante {
    protected int id;
    protected String nome;
    protected String email;
    protected String usuario;
    protected String senha;
    protected String tipo;

    public Participante(String nome, String email, String usuario, String senha, String tipo) {
        this.nome = nome;
        this.email = email;
        this.usuario = usuario;
        this.senha = senha;
        this.tipo = tipo;
    }

    public static Participante login(String usuario, String senha) throws SQLException {
        String sql = "SELECT * FROM participantes WHERE usuario = ? AND senha = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Participante participante;
                String tipo = rs.getString("tipo");
                switch (tipo) {
                    case "ALUNO":
                        participante = new Aluno(rs.getString("nome"), rs.getString("email"), usuario, senha);
                        break;
                    case "PROFESSOR":
                        participante = new Professor(rs.getString("nome"), rs.getString("email"), usuario, senha);
                        break;
                    case "PROFISSIONAL":
                        participante = new Profissional(rs.getString("nome"), rs.getString("email"), usuario, senha);
                        break;
                    default:
                        return null;
                }
                participante.id = rs.getInt("id");
                return participante;
            }
        }
        return null;
    }

    public void cadastrar() throws SQLException {
        String sql = "INSERT INTO participantes (nome, email, usuario, senha, tipo) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nome);
            stmt.setString(2, email);
            stmt.setString(3, usuario);
            stmt.setString(4, senha);
            stmt.setString(5, tipo);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
        }
    }

    public void listarMinhasInscricoes() throws SQLException {
        String sql = "SELECT i.id, e.nome as evento, a.nome as atividade, i.dataInscricao, " +
                "p.status, p.valor " +
                "FROM inscricoes i " +
                "LEFT JOIN eventos e ON i.eventoId = e.id " +
                "LEFT JOIN atividades a ON i.atividadeId = a.id " +
                "LEFT JOIN pagamentos p ON i.id = p.inscricaoId " +
                "WHERE i.participanteId = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, this.id);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n=== MINHAS INSCRIÇÕES ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Evento: " + rs.getString("evento"));
                System.out.println("Atividade: " + rs.getString("atividade"));
                System.out.println("Data: " + rs.getString("dataInscricao"));
                System.out.println("Status Pagamento: " +
                        (rs.getString("status") == null ? "PENDENTE" : rs.getString("status")));
                System.out.println("Valor: " + rs.getDouble("valor"));
                System.out.println("----------------------");
            }
        }
    }

    public void listarMinhasInscricoesComPagamentoPendente() throws SQLException {
        String sql = "SELECT i.id, e.nome as evento, a.nome as atividade " +
                "FROM inscricoes i " +
                "LEFT JOIN eventos e ON i.eventoId = e.id " +
                "LEFT JOIN atividades a ON i.atividadeId = a.id " +
                "LEFT JOIN pagamentos p ON i.id = p.inscricaoId " +
                "WHERE i.participanteId = ? AND (p.status IS NULL OR p.status = 'PENDENTE')";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, this.id);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n=== INSCRIÇÕES COM PAGAMENTO PENDENTE ===");
            while (rs.next()) {
                System.out.println("ID Inscrição: " + rs.getInt("id"));
                System.out.println("Evento: " + rs.getString("evento"));
                System.out.println("Atividade: " + rs.getString("atividade"));
                System.out.println("----------------------");
            }
        }
    }

    public void registrarPagamento(int inscricaoId, double valor) throws SQLException, PagamentoInvalidoException {
        if (valor <= 0) {
            throw new PagamentoInvalidoException("Valor de pagamento inválido");
        }

        String sql = "INSERT INTO pagamentos (inscricaoId, valor, dataPagamento, status) VALUES (?, ?, datetime('now'), 'PENDENTE')";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, inscricaoId);
            stmt.setDouble(2, valor);
            stmt.executeUpdate();
        }
    }

    public int getId() {
        return id;
    }
}