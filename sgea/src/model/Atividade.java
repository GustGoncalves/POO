package model;

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

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getEventoId() { return eventoId; }
    public void setEventoId(int eventoId) { this.eventoId = eventoId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getData() { return data; }
    public void setData(String data) { this.data = data; }
    public int getLimiteInscritos() { return limiteInscritos; }
    public void setLimiteInscritos(int limiteInscritos) { this.limiteInscritos = limiteInscritos; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        return nome + " - " + data + " (" + tipo + ")";
    }
}