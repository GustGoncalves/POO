package model;

public class Profissional extends Participante {
    public Profissional(String nome, String email, String usuario, String senha) {
        super(nome, email, usuario, senha, "PROFISSIONAL");
    }
}