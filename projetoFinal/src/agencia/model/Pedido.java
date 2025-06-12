package agencia.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Pedido {
    protected int id;
    private Cliente cliente;
    private List<PacoteViagem> pacotes = new ArrayList<>();
    private List<ServicoAdicional> servicos = new ArrayList<>();
    private Date data;

    public Pedido(int id, Cliente cliente, Date data) {
        this.id = id;
        this.cliente = cliente;
        this.data = data;
    }

    public void adicionarPacote(PacoteViagem pacote) {
        pacotes.add(pacote);
    }

    public void adicionarServico(ServicoAdicional servico) {
        servicos.add(servico);
    }

    public String getResumo() {
        StringBuilder sb = new StringBuilder("Data do Pedido: " + new SimpleDateFormat("dd/MM/yyyy HH:mm").format(data) + "\n");
        
        // MUDANÇA AQUI: Trocamos getResumo() por toString(), que já existe
        sb.append("Cliente: ").append(cliente.toString()).append("\n\n");
        
        sb.append("---------- PACOTES ----------\n");
        
        // MUDANÇA AQUI: Trocamos getResumo() por toString()
        for (PacoteViagem p : pacotes) {
            sb.append(" - ").append(p.toString()).append("\n");
        }

        if (!servicos.isEmpty()) {
            sb.append("\n---------- SERVIÇOS ADICIONAIS ----------\n");
            // MUDANÇA AQUI TAMBÉM: Trocamos getResumo() por toString()
            for (ServicoAdicional s : servicos) {
                sb.append(" - ").append(s.toString()).append("\n");
            }
        }

        sb.append("\n====================================\n");
        sb.append("TOTAL DO PEDIDO: R$").append(String.format("%.2f", getTotal())).append("\n");
        sb.append("====================================\n");
        
        return sb.toString();
    }

    public double getTotal() {
        return pacotes.stream().mapToDouble(PacoteViagem::getPreco).sum() +
               servicos.stream().mapToDouble(ServicoAdicional::getPreco).sum();
    }

    // Getters e Setters
    public List<PacoteViagem> getPacotes() { return pacotes; }
    public Cliente getCliente() { return cliente; }
    public List<ServicoAdicional> getServicos() { return servicos; }
    public int getId() { return id; }
    public Date getData() { return data; }
    public void setId(int id) { this.id = id; }
}