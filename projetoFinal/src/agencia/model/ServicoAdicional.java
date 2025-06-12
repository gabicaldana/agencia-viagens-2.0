package agencia.model;

public class ServicoAdicional {
    protected int id;
    protected String descricao;
    protected double preco;

    public ServicoAdicional(int id, String descricao, double preco) {
        this.id = id;
        this.descricao = descricao;
        this.preco = preco;
    }

    // Getters
    public double getPreco() { return preco; }
    public String getDescricao() { return descricao; }
    public int getId() { return id; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public void setPreco(double preco) { this.preco = preco; }

    public String getResumo() {
        return String.format("%s (R$%.2f)", descricao, preco);
    }

    @Override
    public String toString() {
        return getResumo();
    }
}