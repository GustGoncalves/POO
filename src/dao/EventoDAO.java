package dao;

import connection.*;
import model.Evento;
import exceptions.EntidadeNaoEncontradaException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EventoDAO {
    public void salvar(Evento evento) throws SQLException {
        String sql = "INSERT INTO eventos (nome, descricao, dataInicio, dataFim) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, evento.getNome());
            stmt.setString(2, evento.getDescricao());
            stmt.setString(3, evento.getDataInicio());
            stmt.setString(4, evento.getDataFim());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                evento.setId(rs.getInt(1));
            }
        }
    }

    public void atualizar(Evento evento) throws SQLException, EntidadeNaoEncontradaException {
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

    public Evento buscarPorId(int id) throws SQLException, EntidadeNaoEncontradaException {
        String sql = "SELECT * FROM eventos WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Evento evento = new Evento(
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getString("dataInicio"),
                        rs.getString("dataFim")
                );
                evento.setId(rs.getInt("id"));
                return evento;
            }
        }
        throw new EntidadeNaoEncontradaException("Evento não encontrado");
    }

    public List<Evento> listarTodos() throws SQLException {
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

    public void excluir(int id) throws SQLException {
        String sql = "DELETE FROM eventos WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
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