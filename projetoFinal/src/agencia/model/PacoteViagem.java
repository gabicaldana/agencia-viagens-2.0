package agencia.model;

// A classe agora não é mais "abstract"
public class PacoteViagem {

    private int id;
    private String nome;
    private String destino;
    private int duracao;
    private double preco;
    private String tipo; // "Aventura", "Luxo", "Cultural"

    public PacoteViagem(int id, String nome, String destino, int duracao, double preco, String tipo) {
        this.id = id;
        this.nome = nome;
        this.destino = destino;
        this.duracao = duracao;
        this.preco = preco;
        this.tipo = tipo;
    }

    // Getters
    public int getId() { return id; }
    public String getNome() { return nome; }
    public String getDestino() { return destino; }
    public int getDuracao() { return duracao; }
    public double getPreco() { return preco; }
    public String getTipo() { return tipo; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setNome(String nome) { this.nome = nome; }
    public void setDestino(String destino) { this.destino = destino; }
    public void setDuracao(int duracao) { this.duracao = duracao; }
    public void setPreco(double preco) { this.preco = preco; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    @Override
    public String toString() {
        return String.format("%s - %s (%s, %d dias, R$%.2f)", nome, tipo, destino, duracao, preco);
    }
}