package model;

public class Aluno extends Participante {
    public Aluno(String nome, String email, String usuario, String senha) {
        super(nome, email, usuario, senha, "ALUNO");
    }
}