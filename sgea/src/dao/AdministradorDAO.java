package dao;

import connection.*;
import model.*;
import exceptions.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdministradorDAO {
    public Administrador login(String usuario, String senha) throws SQLException {
        String sql = "SELECT * FROM administradores WHERE usuario = ? AND senha = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Administrador admin = new Administrador(usuario, senha);
                admin.setId(rs.getInt("id"));
                return admin;
            }
        }
        return null;
    }

    public List<Participante> listarParticipantesPorEvento(int eventoId) throws SQLException {
        List<Participante> participantes = new ArrayList<>();
        String sql = "SELECT p.* FROM participantes p " +
                "JOIN inscricoes i ON p.id = i.participanteId " +
                "WHERE i.eventoId = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Participante participante;
                String tipo = rs.getString("tipo");

                switch (tipo) {
                    case "ALUNO":
                        participante = new Aluno(rs.getString("nome"), rs.getString("email"),
                                rs.getString("usuario"), rs.getString("senha"));
                        break;
                    case "PROFESSOR":
                        participante = new Professor(rs.getString("nome"), rs.getString("email"),
                                rs.getString("usuario"), rs.getString("senha"));
                        break;
                    case "PROFISSIONAL":
                        participante = new Profissional(rs.getString("nome"), rs.getString("email"),
                                rs.getString("usuario"), rs.getString("senha"));
                        break;
                    default:
                        continue;
                }
                participante.setId(rs.getInt("id"));
                participantes.add(participante);
            }
        }
        return participantes;
    }

    public List<Participante> listarParticipantesPorAtividade(int atividadeId) throws SQLException {
        List<Participante> participantes = new ArrayList<>();
        String sql = "SELECT p.* FROM participantes p " +
                "JOIN inscricoes i ON p.id = i.participanteId " +
                "WHERE i.atividadeId = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, atividadeId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Participante participante;
                String tipo = rs.getString("tipo");

                switch (tipo) {
                    case "ALUNO":
                        participante = new Aluno(rs.getString("nome"), rs.getString("email"),
                                rs.getString("usuario"), rs.getString("senha"));
                        break;
                    case "PROFESSOR":
                        participante = new Professor(rs.getString("nome"), rs.getString("email"),
                                rs.getString("usuario"), rs.getString("senha"));
                        break;
                    case "PROFISSIONAL":
                        participante = new Profissional(rs.getString("nome"), rs.getString("email"),
                                rs.getString("usuario"), rs.getString("senha"));
                        break;
                    default:
                        continue;
                }
                participante.setId(rs.getInt("id"));
                participantes.add(participante);
            }
        }
        return participantes;
    }

    public List<Evento> listarTodosEventos() throws SQLException {
        List<Evento> eventos = new ArrayList<>();
        String sql = "SELECT * FROM eventos";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Evento evento = new Evento(
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getString("dataInicio"),
                        rs.getString("dataFim")
                );
                evento.setId(rs.getInt("id"));
                eventos.add(evento);
            }
        }
        return eventos;
    }

    public void atualizarEvento(Evento evento) throws SQLException, EntidadeNaoEncontradaException {
        if (!entidadeExiste("eventos", evento.getId())) {
            throw new EntidadeNaoEncontradaException("Evento não encontrado para atualização");
        }

        String sql = "UPDATE eventos SET nome = ?, descricao = ?, dataInicio = ?, dataFim = ? WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, evento.getNome());
            stmt.setString(2, evento.getDescricao());
            stmt.setString(3, evento.getDataInicio());
            stmt.setString(4, evento.getDataFim());
            stmt.setInt(5, evento.getId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Atualização falhou, nenhum registro modificado");
            }
        }
    }

    public void excluirEvento(int id) throws SQLException {
        String sql = "DELETE FROM eventos WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void confirmarPagamento(int inscricaoId) throws SQLException, EntidadeNaoEncontradaException {
        new PagamentoDAO().confirmarPagamento(inscricaoId);
    }

    private boolean entidadeExiste(String tabela, int id) throws SQLException {
        String sql = "SELECT id FROM " + tabela + " WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeQuery().next();
        }
    }
}