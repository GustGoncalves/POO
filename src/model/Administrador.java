package model;

import java.sql.*;

public class Administrador {
    private int id;
    private String usuario;
    private String senha;

    public Administrador(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
    }

    public static Administrador login(String usuario, String senha) throws SQLException {
        String sql = "SELECT * FROM administradores WHERE usuario = ? AND senha = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Administrador admin = new Administrador(usuario, senha);
                admin.id = rs.getInt("id");
                return admin;
            }
        }
        return null;
    }

    public int getId() {
        return id;
    }
}