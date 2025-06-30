package model;

import java.sql.*;

public class Atividade {
    private int id;
    private int eventoId;
    private String nome;
    private String descricao;
    private String data;
    private int limiteInscritos;
    private String tipo;

    public Atividade(int eventoId, String nome, String descricao, String data, int limiteInscritos, String tipo) {
        this.eventoId = eventoId;
        this.nome = nome;
        this.descricao = descricao;
        this.data = data;
        this.limiteInscritos = limiteInscritos;
        this.tipo = tipo;
    }

    public void salvar() throws SQLException {
        String sql = "INSERT INTO atividades (eventoId, nome, descricao, data, limiteInscritos, tipo) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, eventoId);
            stmt.setString(2, nome);
            stmt.setString(3, descricao);
            stmt.setString(4, data);
            stmt.setInt(5, limiteInscritos);
            stmt.setString(6, tipo);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
        }
    }

    public static void listarAtividadesDoEvento(int eventoId) throws SQLException {
        String sql = "SELECT * FROM atividades WHERE eventoId = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, eventoId);
            ResultSet rs = stmt.executeQuery();

            System.out.println("\n=== ATIVIDADES DO EVENTO ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Nome: " + rs.getString("nome"));
                System.out.println("Descrição: " + rs.getString("descricao"));
                System.out.println("Data: " + rs.getString("data"));
                System.out.println("Vagas: " + rs.getInt("limiteInscritos"));
                System.out.println("Tipo: " + rs.getString("tipo"));
                System.out.println("----------------------");
            }
        }
    }
}