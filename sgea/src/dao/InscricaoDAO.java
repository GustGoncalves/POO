package dao;

import connection.*;
import exceptions.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InscricaoDAO {
    public void inscreverEmEvento(int participanteId, int eventoId) throws SQLException, InscricaoDuplicadaException {
        if (verificarInscricaoExistente(participanteId, eventoId, null)) {
            throw new InscricaoDuplicadaException("Você já está inscrito neste evento");
        }

        String sql = "INSERT INTO inscricoes (participanteId, eventoId, dataInscricao) VALUES (?, ?, datetime('now'))";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, participanteId);
            stmt.setInt(2, eventoId);
            stmt.executeUpdate();
        }
    }

    public void inscreverEmAtividade(int participanteId, int eventoId, int atividadeId)
            throws SQLException, InscricaoDuplicadaException, AtividadeLotadaException {

        if (verificarInscricaoExistente(participanteId, null, atividadeId)) {
            throw new InscricaoDuplicadaException("Você já está inscrito nesta atividade");
        }

        if (verificarAtividadeLotada(atividadeId)) {
            throw new AtividadeLotadaException("Esta atividade já está lotada");
        }

        String sql = "INSERT INTO inscricoes (participanteId, eventoId, atividadeId, dataInscricao) VALUES (?, ?, ?, datetime('now'))";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, participanteId);
            stmt.setInt(2, eventoId);
            stmt.setInt(3, atividadeId);
            stmt.executeUpdate();
        }
    }

    private boolean verificarInscricaoExistente(int participanteId, Integer eventoId, Integer atividadeId)
            throws SQLException {
        String sql;
        if (atividadeId != null) {
            sql = "SELECT id FROM inscricoes WHERE participanteId = ? AND atividadeId = ?";
        } else {
            sql = "SELECT id FROM inscricoes WHERE participanteId = ? AND eventoId = ?";
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, participanteId);
            if (atividadeId != null) {
                stmt.setInt(2, atividadeId);
            } else {
                stmt.setInt(2, eventoId);
            }
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    private boolean verificarAtividadeLotada(int atividadeId) throws SQLException {
        String sql = "SELECT COUNT(*) as total, a.limiteInscritos " +
                "FROM inscricoes i " +
                "JOIN atividades a ON i.atividadeId = a.id " +
                "WHERE i.atividadeId = ? " +
                "GROUP BY a.limiteInscritos";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, atividadeId);
            ResultSet rs = stmt.executeQuery();

            return rs.next() && rs.getInt("total") >= rs.getInt("limiteInscritos");
        }
    }

    public List<InscricaoInfo> listarTodas() throws SQLException {
        List<InscricaoInfo> inscricoes = new ArrayList<>();
        String sql = "SELECT i.id, p.nome as participante, e.nome as evento, " +
                "a.nome as atividade, i.dataInscricao, pg.status, pg.valor " +
                "FROM inscricoes i " +
                "JOIN participantes p ON i.participanteId = p.id " +
                "LEFT JOIN eventos e ON i.eventoId = e.id " +
                "LEFT JOIN atividades a ON i.atividadeId = a.id " +
                "LEFT JOIN pagamentos pg ON i.id = pg.inscricaoId";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                inscricoes.add(new InscricaoInfo(
                        rs.getInt("id"),
                        rs.getString("participante"),
                        rs.getString("evento"),
                        rs.getString("atividade"),
                        rs.getString("dataInscricao"),
                        rs.getString("status"),
                        rs.getObject("valor") != null ? rs.getDouble("valor") : null
                ));
            }
        }
        return inscricoes;
    }

    public List<InscricaoInfo> listarPorParticipante(int participanteId) throws SQLException {
        List<InscricaoInfo> inscricoes = new ArrayList<>();
        String sql = "SELECT i.id, e.nome as evento, a.nome as atividade, i.dataInscricao, " +
                "pg.status, pg.valor " +
                "FROM inscricoes i " +
                "LEFT JOIN eventos e ON i.eventoId = e.id " +
                "LEFT JOIN atividades a ON i.atividadeId = a.id " +
                "LEFT JOIN pagamentos pg ON i.id = pg.inscricaoId " +
                "WHERE i.participanteId = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, participanteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                inscricoes.add(new InscricaoInfo(
                        rs.getInt("id"),
                        null,
                        rs.getString("evento"),
                        rs.getString("atividade"),
                        rs.getString("dataInscricao"),
                        rs.getString("status"),
                        rs.getObject("valor") != null ? rs.getDouble("valor") : null
                ));
            }
        }
        return inscricoes;
    }

    public List<InscricaoInfo> listarComPagamentoPendente() throws SQLException {
        List<InscricaoInfo> inscricoes = new ArrayList<>();
        String sql = "SELECT i.id, p.nome as participante, e.nome as evento, " +
                "a.nome as atividade, pg.status, pg.valor " +
                "FROM inscricoes i " +
                "JOIN participantes p ON i.participanteId = p.id " +
                "LEFT JOIN eventos e ON i.eventoId = e.id " +
                "LEFT JOIN atividades a ON i.atividadeId = a.id " +
                "LEFT JOIN pagamentos pg ON i.id = pg.inscricaoId " +
                "WHERE pg.status = 'PENDENTE' OR pg.status IS NULL";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                inscricoes.add(new InscricaoInfo(
                        rs.getInt("id"),
                        rs.getString("participante"),
                        rs.getString("evento"),
                        rs.getString("atividade"),
                        null,
                        rs.getString("status"),
                        rs.getObject("valor") != null ? rs.getDouble("valor") : null
                ));
            }
        }
        return inscricoes;
    }

    public List<InscricaoInfo> listarComPagamentoPendentePorParticipante(int participanteId) throws SQLException {
        List<InscricaoInfo> inscricoes = new ArrayList<>();
        String sql = "SELECT i.id, e.nome as evento, a.nome as atividade, pg.valor " +
                "FROM inscricoes i " +
                "LEFT JOIN eventos e ON i.eventoId = e.id " +
                "LEFT JOIN atividades a ON i.atividadeId = a.id " +
                "LEFT JOIN pagamentos pg ON i.id = pg.inscricaoId " +
                "WHERE i.participanteId = ? AND (pg.status IS NULL OR pg.status = 'PENDENTE')";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, participanteId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                inscricoes.add(new InscricaoInfo(
                        rs.getInt("id"),
                        null,
                        rs.getString("evento"),
                        rs.getString("atividade"),
                        null,
                        "PENDENTE",
                        rs.getObject("valor") != null ? rs.getDouble("valor") : null
                ));
            }
        }
        return inscricoes;
    }

    public static class InscricaoInfo {
        private int id;
        private String participante;
        private String evento;
        private String atividade;
        private String dataInscricao;
        private String statusPagamento;
        private Double valor;

        public InscricaoInfo(int id, String participante, String evento, String atividade,
                             String dataInscricao, String statusPagamento, Double valor) {
            this.id = id;
            this.participante = participante;
            this.evento = evento;
            this.atividade = atividade;
            this.dataInscricao = dataInscricao;
            this.statusPagamento = statusPagamento;
            this.valor = valor;
        }

        // Getters
        public int getId() { return id; }
        public String getParticipante() { return participante; }
        public String getEvento() { return evento; }
        public String getAtividade() { return atividade; }
        public String getDataInscricao() { return dataInscricao; }
        public String getStatusPagamento() { return statusPagamento; }
        public Double getValor() { return valor; }
    }
}