package agencia.view;

import agencia.controller.DadosController;
import agencia.model.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class PedidosFrame extends JFrame {
    private final DadosController controller;
    private JTable table;
    private DefaultTableModel model;
    private List<Pedido> pedidos;

    public PedidosFrame(DadosController controller) {
        this.controller = controller;
        initComponents();
    }

    private void initComponents() {
        setTitle("Pedidos");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1000, 600);
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        String[] colunas = {"ID", "Cliente", "Data", "Pacotes", "Serviços", "Total (R$)"};
        model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        configurarTabela();
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnNovo = new JButton("Novo Pedido");
        JButton btnEditar = new JButton("Editar");
        JButton btnDetalhes = new JButton("Ver Detalhes");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar");
        botoesPanel.add(btnNovo);
        botoesPanel.add(btnEditar);
        botoesPanel.add(btnDetalhes);
        botoesPanel.add(btnExcluir);
        botoesPanel.add(btnAtualizar);
        mainPanel.add(botoesPanel, BorderLayout.SOUTH);

        btnNovo.addActionListener(e -> criarPedidoDialog(null));
        btnEditar.addActionListener(e -> {
            int linha = table.getSelectedRow();
            if (linha >= 0) {
                criarPedidoDialog(pedidos.get(table.convertRowIndexToModel(linha)));
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Selecione um pedido para editar", 
                    "Aviso", JOptionPane.WARNING_MESSAGE);
            }
        });
        btnDetalhes.addActionListener(e -> verDetalhesPedido());
        btnExcluir.addActionListener(e -> excluirPedido());
        btnAtualizar.addActionListener(e -> atualizarTabela());

        setContentPane(mainPanel);
        atualizarTabela();
    }

    private void configurarTabela() {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(28);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void atualizarTabela() {
        this.pedidos = controller.carregarPedidos();
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (Pedido p : this.pedidos) {
            model.addRow(new Object[]{
                p.getId(), 
                p.getCliente().getNome(), 
                sdf.format(p.getData()), 
                p.getPacotes().size(), 
                p.getServicos().size(), 
                String.format("%.2f", p.getTotal())
            });
        }
    }

    private void verDetalhesPedido() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Pedido pedido = pedidos.get(table.convertRowIndexToModel(selectedRow));
            JTextArea textArea = new JTextArea(pedido.getResumo());
            textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            textArea.setEditable(false);
            textArea.setMargin(new Insets(10, 10, 10, 10));
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setPreferredSize(new Dimension(600, 400));
            JOptionPane.showMessageDialog(this, scrollPane, 
                "Detalhes do Pedido #" + pedido.getId(), 
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Selecione um pedido para ver os detalhes.", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirPedido() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Pedido pedido = pedidos.get(table.convertRowIndexToModel(selectedRow));
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Excluir o pedido do cliente '" + pedido.getCliente().getNome() + "'?", 
                "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.excluirPedido(pedido.getId());
                atualizarTabela();
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Selecione um pedido para excluir.", 
                "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void criarPedidoDialog(Pedido pedidoExistente) {
        boolean isEdit = pedidoExistente != null;
        String titulo = isEdit ? "Editar Pedido #" + pedidoExistente.getId() : "Novo Pedido";

        List<Cliente> clientes = controller.carregarClientes();
        List<PacoteViagem> pacotes = controller.carregarPacotes();
        List<ServicoAdicional> servicos = controller.carregarServicos();

        if (clientes.isEmpty() || pacotes.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "É necessário cadastrar clientes e pacotes
