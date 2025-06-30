package model;

import java.sql.*;
import exceptions.*;

public class Inscricao {
    public static void listarInscricoes() throws SQLException {
        String sql = "SELECT i.id, p.nome as participante, e.nome as evento, " +
                "a.nome as atividade, i.dataInscricao, pg.status " +
                "FROM inscricoes i " +
                "JOIN participantes p ON i.participanteId = p.id " +
                "LEFT JOIN eventos e ON i.eventoId = e.id " +
                "LEFT JOIN atividades a ON i.atividadeId = a.id " +
                "LEFT JOIN pagamentos pg ON i.id = pg.inscricaoId";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== TODAS AS INSCRIÇÕES ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Participante: " + rs.getString("participante"));
                System.out.println("Evento: " + rs.getString("evento"));
                System.out.println("Atividade: " + rs.getString("atividade"));
                System.out.println("Data: " + rs.getString("dataInscricao"));
                System.out.println("Status: " + (rs.getString("status") == null ? "PENDENTE" : rs.getString("status")));
                System.out.println("----------------------");
            }
        }
    }

    public static void inscreverEmEvento(int participanteId, int eventoId)
            throws SQLException, InscricaoDuplicadaException {

        // Verificar se já está inscrito
        String checkSql = "SELECT id FROM inscricoes WHERE participanteId = ? AND eventoId = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, participanteId);
            checkStmt.setInt(2, eventoId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                throw new InscricaoDuplicadaException("Você já está inscrito neste evento");
            }
        }

        // Fazer a inscrição
        String sql = "INSERT INTO inscricoes (participanteId, eventoId, dataInscricao) " +
                "VALUES (?, ?, datetime('now'))";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, participanteId);
            stmt.setInt(2, eventoId);
            stmt.executeUpdate();
        }
    }

    public static void listarInscricoesComPagamentoPendente() throws SQLException {
        String sql = "SELECT i.id, p.nome as participante, e.nome as evento, " +
                "a.nome as atividade, pg.status " +
                "FROM inscricoes i " +
                "JOIN participantes p ON i.participanteId = p.id " +
                "LEFT JOIN eventos e ON i.eventoId = e.id " +
                "LEFT JOIN atividades a ON i.atividadeId = a.id " +
                "LEFT JOIN pagamentos pg ON i.id = pg.inscricaoId " +
                "WHERE pg.status = 'PENDENTE' OR pg.status IS NULL";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== INSCRIÇÕES COM PAGAMENTO PENDENTE ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Participante: " + rs.getString("participante"));
                System.out.println("Evento: " + rs.getString("evento"));
                System.out.println("Atividade: " + rs.getString("atividade"));
                System.out.println("Status: " + (rs.getString("status") == null ? "PENDENTE" : rs.getString("status")));
                System.out.println("----------------------");
            }
        }
    }

    public static void inscreverEmAtividade(int participanteId, int eventoId, int atividadeId)
            throws SQLException, InscricaoDuplicadaException, AtividadeLotadaException {

        // Verificar se já está inscrito
        String checkSql = "SELECT id FROM inscricoes WHERE participanteId = ? AND atividadeId = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {

            checkStmt.setInt(1, participanteId);
            checkStmt.setInt(2, atividadeId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                throw new InscricaoDuplicadaException("Você já está inscrito nesta atividade");
            }
        }

        // Verificar se há vagas
        String vagasSql = "SELECT COUNT(*) as total, a.limiteInscritos " +
                "FROM inscricoes i " +
                "JOIN atividades a ON i.atividadeId = a.id " +
                "WHERE i.atividadeId = ? " +
                "GROUP BY a.limiteInscritos";

        try (Connection conn = Database.getConnection();
             PreparedStatement vagasStmt = conn.prepareStatement(vagasSql)) {

            vagasStmt.setInt(1, atividadeId);
            ResultSet rs = vagasStmt.executeQuery();

            if (rs.next() && rs.getInt("total") >= rs.getInt("limiteInscritos")) {
                throw new AtividadeLotadaException("Esta atividade já está lotada");
            }
        }

        // Fazer a inscrição
        String sql = "INSERT INTO inscricoes (participanteId, eventoId, atividadeId, dataInscricao) " +
                "VALUES (?, ?, ?, datetime('now'))";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, participanteId);
            stmt.setInt(2, eventoId);
            stmt.setInt(3, atividadeId);
            stmt.executeUpdate();
        }
    }
}