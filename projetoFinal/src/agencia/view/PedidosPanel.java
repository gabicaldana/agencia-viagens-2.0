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

public class PedidosPanel extends JPanel {
    private final DadosController controller;
    private final JFrame parentFrame;
    private JTable table;
    private DefaultTableModel model;
    private List<Pedido> pedidos;

    public PedidosPanel(JFrame parentFrame, DadosController controller) {
        this.parentFrame = parentFrame;
        this.controller = controller;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Tabela
        String[] colunas = {"ID", "Cliente", "Data", "Pacotes", "Serviços", "Total (R$)"};
        model = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(model);
        configurarTabela();
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Botões
        JPanel botoesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnNovo = new JButton("Novo Pedido");
        JButton btnDetalhes = new JButton("Ver Detalhes");
        JButton btnExcluir = new JButton("Excluir");
        JButton btnAtualizar = new JButton("Atualizar");
        botoesPanel.add(btnNovo);
        botoesPanel.add(btnDetalhes);
        botoesPanel.add(btnExcluir);
        botoesPanel.add(btnAtualizar);
        add(botoesPanel, BorderLayout.SOUTH);

        // Ações
        btnNovo.addActionListener(e -> criarPedidoDialog());
        btnDetalhes.addActionListener(e -> verDetalhesPedido());
        btnExcluir.addActionListener(e -> excluirPedido());
        btnAtualizar.addActionListener(e -> atualizarTabela());

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
            model.addRow(new Object[]{p.getId(), p.getCliente().getNome(), sdf.format(p.getData()), p.getPacotes().size(), p.getServicos().size(), String.format("%.2f", p.getTotal())});
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
            JOptionPane.showMessageDialog(this, scrollPane, "Detalhes do Pedido #" + pedido.getId(), JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um pedido para ver os detalhes.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void excluirPedido() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Pedido pedido = pedidos.get(table.convertRowIndexToModel(selectedRow));
            int confirm = JOptionPane.showConfirmDialog(this, "Excluir o pedido do cliente '" + pedido.getCliente().getNome() + "'?", "Confirmação", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.excluirPedido(pedido.getId());
                atualizarTabela();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Selecione um pedido para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void criarPedidoDialog() {
        List<Cliente> clientes = controller.carregarClientes();
        List<PacoteViagem> pacotes = controller.carregarPacotes();
        
        if (clientes.isEmpty() || pacotes.isEmpty()) {
            JOptionPane.showMessageDialog(this, "É necessário cadastrar clientes e pacotes antes de criar um pedido.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(parentFrame, "Novo Pedido", true);
        dialog.setSize(700, 600);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setLocationRelativeTo(parentFrame);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Cliente
        JComboBox<Cliente> clienteCombo = new JComboBox<>(clientes.toArray(new Cliente[0]));
        JPanel clientePanel = new JPanel(new BorderLayout(0, 5));
        clientePanel.setBorder(new TitledBorder("1. Selecione o Cliente"));
        clientePanel.add(clienteCombo, BorderLayout.CENTER);

        // Pacotes
        JList<PacoteViagem> pacotesList = new JList<>(pacotes.toArray(new PacoteViagem[0]));
        pacotesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JPanel pacotesPanel = new JPanel(new BorderLayout(0, 5));
        pacotesPanel.setBorder(new TitledBorder("2. Selecione os Pacotes (pelo menos um)"));
        pacotesPanel.add(new JScrollPane(pacotesList), BorderLayout.CENTER);

        // Serviços
        JList<ServicoAdicional> servicosList = new JList<>(controller.carregarServicos().toArray(new ServicoAdicional[0]));
        servicosList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JPanel servicosPanel = new JPanel(new BorderLayout(0, 5));
        servicosPanel.setBorder(new TitledBorder("3. Selecione Serviços Adicionais (opcional)"));
        servicosPanel.add(new JScrollPane(servicosList), BorderLayout.CENTER);

        contentPanel.add(clientePanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(pacotesPanel);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        contentPanel.add(servicosPanel);

        JButton btnSalvar = new JButton("Salvar Pedido");
        btnSalvar.addActionListener(e -> {
            Cliente cliente = (Cliente) clienteCombo.getSelectedItem();
            List<PacoteViagem> pacotesSelecionados = pacotesList.getSelectedValuesList();
            
            if (pacotesSelecionados.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Selecione pelo menos um pacote!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Pedido novoPedido = new Pedido(0, cliente, new Date());
            pacotesSelecionados.forEach(novoPedido::adicionarPacote);
            servicosList.getSelectedValuesList().forEach(novoPedido::adicionarServico);

            controller.salvarPedido(novoPedido);
            atualizarTabela();
            
            JOptionPane.showMessageDialog(dialog, "Pedido criado com sucesso!\nTotal: R$ " + String.format("%.2f", novoPedido.getTotal()), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        JPanel botoesDialogPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        botoesDialogPanel.setBorder(new EmptyBorder(0, 15, 15, 15));
        botoesDialogPanel.add(btnSalvar);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(botoesDialogPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
}