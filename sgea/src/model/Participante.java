package model;

public class Participante extends Usuario {
    private int id;
    private String nome;
    private String email;
    private String usuario;
    private String senha;
    private String tipo;

    public Participante(String nome, String email, String usuario, String senha, String tipo) {
        this.nome = nome;
        this.email = email;
        this.usuario = usuario;
        this.senha = senha;
        this.tipo = tipo;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override
    public boolean isAdmin() {
        return false;
    }
}