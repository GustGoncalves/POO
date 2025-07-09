package model;

public class Administrador extends Usuario {
    private int id;
    private String usuario;
    private String senha;

    public Administrador(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsuario() { return usuario; }
    public String getSenha() { return senha; }

    @Override
    public boolean isAdmin() {
        return true;
    }
}