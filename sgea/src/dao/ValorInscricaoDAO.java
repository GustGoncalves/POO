package dao;

import connection.*;
import java.sql.*;

public class ValorInscricaoDAO {
    public double getValorPorTipo(String tipoParticipante) throws SQLException {
        String sql = "SELECT valor FROM valores_inscricao WHERE tipo_participante = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipoParticipante.toUpperCase());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("valor");
            } else {
                throw new SQLException("Tipo de participante não encontrado: " + tipoParticipante);
            }
        }
    }

    public void atualizarValor(String tipoParticipante, double valor) throws SQLException {
        if (valor <= 0) {
            throw new SQLException("O valor deve ser maior que zero");
        }

        String sql = "INSERT OR REPLACE INTO valores_inscricao (tipo_participante, valor) VALUES (?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, tipoParticipante.toUpperCase());
            stmt.setDouble(2, valor);
            stmt.executeUpdate();
        }
    }

    public void inicializarValoresPadrao() throws SQLException {
        try {
            // Verifica se já existem valores
            if (!tabelaPossuiRegistros()) {
                atualizarValor("ALUNO", 50.0);
                atualizarValor("PROFESSOR", 30.0);
                atualizarValor("PROFISSIONAL", 100.0);
            }
        } catch (SQLException e) {
            throw new SQLException("Falha ao inicializar valores padrão: " + e.getMessage());
        }
    }

    private boolean tabelaPossuiRegistros() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM valores_inscricao";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() && rs.getInt("total") > 0;
        }
    }
}