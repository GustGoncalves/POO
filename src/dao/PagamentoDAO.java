package dao;

import connection.*;
import exceptions.*;
import java.sql.*;

public class PagamentoDAO {
    public void registrarPagamento(int inscricaoId, double valor)
            throws SQLException, PagamentoInvalidoException, EntidadeNaoEncontradaException {

        if (valor <= 0) {
            throw new PagamentoInvalidoException("Valor de pagamento inválido");
        }

        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            // Verifica se a inscrição existe
            if (!inscricaoExiste(conn, inscricaoId)) {
                throw new EntidadeNaoEncontradaException("Inscrição não encontrada");
            }

            // Usa datetime('now') em vez de NOW()
            String sql = "INSERT INTO pagamentos (inscricaoId, valor, dataPagamento, status) " +
                    "VALUES (?, ?, datetime('now'), 'PENDENTE')";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, inscricaoId);
                stmt.setDouble(2, valor);
                stmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Erro ao fechar conexão: " + e.getMessage());
                }
            }
        }
    }

    private boolean inscricaoExiste(Connection conn, int inscricaoId) throws SQLException {
        String sql = "SELECT id FROM inscricoes WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, inscricaoId);
            return stmt.executeQuery().next();
        }
    }

    public void confirmarPagamento(int inscricaoId) throws SQLException, EntidadeNaoEncontradaException {
        // Verificar se existe pagamento para a inscrição
        String sqlVerifica = "SELECT id FROM pagamentos WHERE inscricaoId = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlVerifica)) {

            stmt.setInt(1, inscricaoId);
            if (!stmt.executeQuery().next()) {
                throw new EntidadeNaoEncontradaException("Nenhum pagamento encontrado para esta inscrição");
            }
        }

        // Atualizar status
        String sqlAtualiza = "UPDATE pagamentos SET status = 'CONFIRMADO' WHERE inscricaoId = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sqlAtualiza)) {

            stmt.setInt(1, inscricaoId);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Confirmação de pagamento falhou");
            }
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