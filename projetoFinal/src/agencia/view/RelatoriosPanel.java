package agencia.view;

import agencia.controller.DadosController;
import agencia.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class RelatoriosPanel extends JPanel {
    private final DadosController controller;
    private final JTextArea textArea;

    public RelatoriosPanel(DadosController controller) {
        this.controller = controller;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        textArea = new JTextArea();
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        textArea.setEditable(false);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton btnResumo = new JButton("Resumo Geral");
        JButton btnVendas = new JButton("Vendas por Data");
        JButton btnPacotesPopulares = new JButton("Pacotes Populares");
        botoesPanel.add(btnResumo);
        botoesPanel.add(btnVendas);
        botoesPanel.add(btnPacotesPopulares);
        add(botoesPanel, BorderLayout.SOUTH);

        // Ações
        btnResumo.addActionListener(e -> gerarResumoGeral());
        btnVendas.addActionListener(e -> gerarVendasPorData());
        btnPacotesPopulares.addActionListener(e -> gerarPacotesPopulares());
    }

    private void gerarResumoGeral() {
        List<Cliente> clientes = controller.carregarClientes();
        List<PacoteViagem> pacotes = controller.carregarPacotes();
        List<ServicoAdicional> servicos = controller.carregarServicos();
        List<Pedido> pedidos = controller.carregarPedidos();

        double faturamentoTotal = pedidos.stream().mapToDouble(Pedido::getTotal).sum();

        StringBuilder sb = new StringBuilder("=== RESUMO GERAL ===\n\n");
        sb.append(String.format("%-25s %d\n", "Total de Clientes:", clientes.size()));
        sb.append(String.format("%-25s %d\n", "Total de Pacotes:", pacotes.size()));
        sb.append(String.format("%-25s %d\n", "Total de Serviços:", servicos.size()));
        sb.append(String.format("%-25s %d\n\n", "Total de Pedidos:", pedidos.size()));
        sb.append("----------------------------------------\n");
        sb.append(String.format("%-25s R$ %.2f\n", "FATURAMENTO TOTAL:", faturamentoTotal));
        
        textArea.setText(sb.toString());
    }

    private void gerarVendasPorData() {
        List<Pedido> pedidos = controller.carregarPedidos();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Map<String, Double> vendasPorData = new TreeMap<>();

        for (Pedido p : pedidos) {
            String data = sdf.format(p.getData());
            vendasPorData.put(data, vendasPorData.getOrDefault(data, 0.0) + p.getTotal());
        }
        
        StringBuilder sb = new StringBuilder("=== VENDAS POR DATA ===\n\n");
        for (Map.Entry<String, Double> entry : vendasPorData.entrySet()) {
            sb.append(String.format("%-12s R$ %.2f\n", entry.getKey() + ":", entry.getValue()));
        }

        textArea.setText(sb.toString());
    }

    private void gerarPacotesPopulares() {
        List<Pedido> pedidos = controller.carregarPedidos();
        Map<String, Integer> contagemPacotes = new HashMap<>();

        for (Pedido p : pedidos) {
            for (PacoteViagem pacote : p.getPacotes()) {
                String nomePacote = pacote.getNome();
                contagemPacotes.put(nomePacote, contagemPacotes.getOrDefault(nomePacote, 0) + 1);
            }
        }

        List<Map.Entry<String, Integer>> lista = new ArrayList<>(contagemPacotes.entrySet());
        lista.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        StringBuilder sb = new StringBuilder("=== PACOTES MAIS VENDIDOS ===\n\n");
        for (Map.Entry<String, Integer> entry : lista) {
            sb.append(String.format("%-30s %d venda(s)\n", entry.getKey() + ":", entry.getValue()));
        }

        textArea.setText(sb.toString());
    }
}