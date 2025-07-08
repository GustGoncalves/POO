package model;

public class Pagamento {
    private int id;
    private int inscricaoId;
    private double valor;
    private String dataPagamento;
    private String status;

    public Pagamento(int inscricaoId, double valor, String dataPagamento, String status) {
        this.inscricaoId = inscricaoId;
        this.valor = valor;
        this.dataPagamento = dataPagamento;
        this.status = status;
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getInscricaoId() { return inscricaoId; }
    public void setInscricaoId(int inscricaoId) { this.inscricaoId = inscricaoId; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
    public String getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(String dataPagamento) { this.dataPagamento = dataPagamento; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}