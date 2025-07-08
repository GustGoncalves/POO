package model;

public class Inscricao {
    private int id;
    private int participanteId;
    private Integer eventoId;
    private Integer atividadeId;
    private String dataInscricao;

    public Inscricao(int participanteId, Integer eventoId, Integer atividadeId, String dataInscricao) {
        this.participanteId = participanteId;
        this.eventoId = eventoId;
        this.atividadeId = atividadeId;
        this.dataInscricao = dataInscricao;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getParticipanteId() { return participanteId; }
    public void setParticipanteId(int participanteId) { this.participanteId = participanteId; }
    public Integer getEventoId() { return eventoId; }
    public void setEventoId(Integer eventoId) { this.eventoId = eventoId; }
    public Integer getAtividadeId() { return atividadeId; }
    public void setAtividadeId(Integer atividadeId) { this.atividadeId = atividadeId; }
    public String getDataInscricao() { return dataInscricao; }
    public void setDataInscricao(String dataInscricao) { this.dataInscricao = dataInscricao; }
}