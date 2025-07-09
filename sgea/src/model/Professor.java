package model;

public class Professor extends Participante {
    public Professor(String nome, String email, String usuario, String senha) {
        super(nome, email, usuario, senha, "PROFESSOR");
    }
}