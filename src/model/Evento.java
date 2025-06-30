package model;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Evento {
    private int id;
    private String nome;
    private String descricao;
    private String dataInicio;
    private String dataFim;

    public Evento(String nome, String descricao, String dataInicio, String dataFim) {
        this.nome = nome;
        this.descricao = descricao;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    public void salvar() throws SQLException {
        String sql = "INSERT INTO eventos (nome, descricao, dataInicio, dataFim) VALUES (?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, nome);
            stmt.setString(2, descricao);
            stmt.setString(3, dataInicio);
            stmt.setString(4, dataFim);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                this.id = rs.getInt(1);
            }
        }
    }

    public static void listarEventos() throws SQLException {
        String sql = "SELECT * FROM eventos";
        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n=== LISTA DE EVENTOS ===");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Nome: " + rs.getString("nome"));
                System.out.println("Descrição: " + rs.getString("descricao"));
                System.out.println("Data Início: " + rs.getString("dataInicio"));
                System.out.println("Data Fim: " + rs.getString("dataFim"));
                System.out.println("----------------------");
            }
        }
    }

    public int getId() {
        return id;
    }
}