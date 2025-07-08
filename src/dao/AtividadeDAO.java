package dao;

import connection.*;
import model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AtividadeDAO {
    public void salvar(Atividade atividade) throws SQLException {
        String sql = "INSERT INTO atividades (eventoId, nome, descricao, data, limiteInscritos, tipo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, atividade.getEventoId());
            stmt.setString(2, atividade.getNome());
            stmt.setString(3, atividade.getDescricao());
            stmt.setString(4, atividade.getData());
            stmt.setInt(5, atividade.getLimiteInscritos());
            stmt.setString(6, atividade.getTipo());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                atividade.setId(rs.getInt(1));
            }
        }
    }

    public List<Atividade> listarPorEvento(int eventoId) throws SQLException {
        List<Atividade> atividades = new ArrayList<>();
        String sql = "SELECT * FROM atividades WHERE eventoId = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Atividade atividade = new Atividade(
                        rs.getInt("eventoId"),
                        rs.getString("nome"),
                        rs.getString("descricao"),
                        rs.getString("data"),
                        rs.getInt("limiteInscritos"),
                        rs.getString("tipo")
                );
                atividade.setId(rs.getInt("id"));
                atividades.add(atividade);
            }
        }
        return atividades;
    }
}