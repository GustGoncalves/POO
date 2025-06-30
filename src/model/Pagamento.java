package model;

import java.sql.*;

public class Pagamento {
    public static void confirmarPagamento(int inscricaoId, double valor) throws SQLException {
        String sql = "UPDATE pagamentos SET status = 'CONFIRMADO' WHERE inscricaoId = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, inscricaoId);
            stmt.executeUpdate();
        }
    }
}